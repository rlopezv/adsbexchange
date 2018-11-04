/**
 *
 */
package net.upmt.moit.distributed.adsbexchange;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetCommitCallback;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.upmt.moit.distributed.adsbexchange.handler.ConsumerMessageHandler;
import net.upmt.moit.distributed.adsbexchange.model.FlightData;
import net.upmt.moit.distributed.adsbexchange.util.Util;

/**
 * @author ramon
 *
 */
public class SimpleConsumer implements Runnable {

	public static final String PARTITIONS = "partitions";

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleConsumer.class);

	private String clientId;
	private String topic = null;
	private KafkaConsumer<String, FlightData> consumer;
	private Properties config;
	private OffsetCommitCallback commitCallback;
	private boolean sync = false;
	private boolean callback = false;
	private ConsumerMessageHandler messageHandler;

	/**
	 * Constructor
	 * 
	 * @param consumerId, name of the properties file used for configuring it
	 */
	public SimpleConsumer(String consumerId) {
		LOGGER.info("Creating consumer {}", consumerId);
		this.clientId = consumerId;
		config = Util.convertResourceBundleToProperties(ResourceBundle.getBundle(consumerId));
		topic = config.getProperty("topic", "");
		sync = Boolean.valueOf(config.getProperty("commit.async", "true"));
		callback = Boolean.valueOf(config.getProperty("commit.callback", "false"));
		this.consumer = new KafkaConsumer<>(config);
		this.messageHandler = buildMessageHandler(this);
	}

	private ConsumerMessageHandler buildMessageHandler(SimpleConsumer simpleConsumer) {
		ConsumerMessageHandler result = null;
		try {
			Class<?> handlerClass = Class.forName(getConfig().getProperty("handler.class",
					"net.upmt.moit.distributed.adsbexchange.handler.LogMessageHandler"));
			Constructor<?> handlerConstructor = handlerClass.getConstructor(SimpleConsumer.class);
			result = (ConsumerMessageHandler) handlerConstructor.newInstance(simpleConsumer);
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			LOGGER.error("Error creating handler", e);
		}
		return result;
	}

	protected ConsumerMessageHandler getMessageHandler() {
		return messageHandler;
	}

	protected boolean isSync() {
		return sync;
	}

	protected boolean isCallback() {
		return callback;
	}

	protected OffsetCommitCallback getCommitCallback() {
		return commitCallback;
	}

	protected String getClientId() {
		return clientId;
	}

	protected String getTopic() {
		return topic;
	}

	protected KafkaConsumer<String, FlightData> getConsumer() {
		return consumer;
	}

	protected List<TopicPartition> getPartitions() {
		List<TopicPartition> partitions = null;
		String confPartitions = config.getProperty(PARTITIONS);
		if (confPartitions != null) {
			String[] partitionList = confPartitions.split(",");
			for (String partitionId : partitionList) {
				if (partitions == null) {
					partitions = new ArrayList<>();
				}
				partitions.add(new TopicPartition(getTopic(), Integer.parseInt(partitionId)));
			}

		}
		return partitions;
	}

	public Properties getConfig() {
		return config;
	}

	@Override
	public void run() {
		LOGGER.info("Starting the Consumer : {}", clientId);
		// getConsumer().subscribe(Arrays.asList(getTopic()), new
		// PartitionRebalanceListener(getConsumer()));

		// Set<TopicPartition> currentPartitions = getConsumer().assignment();
		List<TopicPartition> currentPartitions = getPartitions();
		if (currentPartitions != null && !currentPartitions.isEmpty()) {
			getConsumer().assign(currentPartitions);
			getConsumer().seekToEnd(currentPartitions);
			// for (TopicPartition partition : getPartitions()) {
			// getConsumer().seekToEnd(partitions);.seek(partition,
			// getPartitionOffset(partition.partition()));
			// }
		} else {
			getConsumer().subscribe(Arrays.asList(getTopic()));
			Set<TopicPartition> allPartitions = getConsumer().assignment();
			getConsumer().seekToEnd(allPartitions);
		}
		// Checking it has subscribers
		while (!getConsumer().subscription().isEmpty() || !getConsumer().assignment().isEmpty()) {
			// Poll time measured in advance.
			ConsumerRecords<String, FlightData> consumerRecords = getConsumer()
					.poll(Duration.ofMillis(Long.parseLong(config.getProperty("poll.time", "1000"))));

			if (!consumerRecords.isEmpty()) {
				LOGGER.info("({}) Records found: {}", getClientId(), consumerRecords.count());
				handleRecords(consumerRecords);
				if (isSync()) {
					getConsumer().commitSync();
				} else {
					if (isCallback()) {
						getConsumer().commitAsync(commitCallback);
					} else {
						getConsumer().commitAsync();
					}
				}
				logMetrics();
			} else {
				LOGGER.info("({})No records found", getClientId());
			}

		}
	}

	public void setConfig(Properties config) {
		this.config = config;
	}

	/**
	 * Must be implemented in the handler
	 * 
	 * @param consumerRecords
	 */
	private void handleRecords(ConsumerRecords<String, FlightData> consumerRecords) {
		for (ConsumerRecord<String, FlightData> record : consumerRecords) {
			getMessageHandler().handleMessage(record);
		}
	}

	public void stop() {
		LOGGER.info("Stopping consumer:{}", getClientId());
		getConsumer().close();
	}

	class ConsumerCommitCallback implements OffsetCommitCallback {

		@Override
		public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception exception) {
			if (exception != null) {
				LOGGER.error("Error comitting", exception);
			} else {
				offsets.entrySet().stream().forEach(entry -> LOGGER.info("Committed: Topic : {},Partition:{}, Info:{} ",
						entry.getKey().topic(), entry.getKey().partition(), entry.getValue().metadata()));
			}
		}

	}

	protected void logMetrics() {
		Map<MetricName, ? extends Metric> metrics = getConsumer().metrics();
		LOGGER.info("***********CONSUMER**********");
		LOGGER.info("***************{}********************", getClientId());
		for (Entry<MetricName, ? extends Metric> metricEntry : metrics.entrySet()) {
			LOGGER.info("{}:{}->{}", metricEntry.getKey().name(), metricEntry.getValue().metricName().name(),
					metricEntry.getValue().metricValue());
		}
		LOGGER.info("*************************************");
	}

}
