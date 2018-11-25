package net.rlopezv.miot.kafka.producer;
/**
 *
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.PartitionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.rlopezv.miot.kafka.experiment.ProducerConfig;
import net.rlopezv.miot.kafka.experiment.TopicConfig;
import net.rlopezv.miot.kafka.producer.callback.LogCallback;
import net.upmt.moit.distributed.adsbexchange.SimpleProducer;

/**
 * 
 * @author ramon
 *
 */
public abstract class AbstractProducer<T> implements Runnable {

	private final Logger LOGGER = LoggerFactory.getLogger(SimpleProducer.class);

	private KafkaProducer<String, T> producer;
	private String clientId;
	private String topic;
	private ProducerConfig config;
	private Callback sentCallback = null;
	private boolean sync = false;
	private boolean callback = false;
	private boolean closed = false;

	/**
	 * Constructor
	 * 
	 * @param consumerId, name of the properties file used for configuring it
	 */
	public AbstractProducer(TopicConfig topicConfig, ProducerConfig config) {
		LOGGER.info("Creating producer {}:{}", config.getName(), config);
		this.clientId = config.getClientId() == null ? config.getName() : config.getClientId();
		this.config = config;
		topic = topicConfig.getName();
		sync = config.getCommitAsync();
		if (config.getCommitCallback()) {
			setSentCallBack(new LogCallback(LOGGER));
		}
		this.producer = new KafkaProducer<>(config.getAdditionalProperties());
	}

	protected void setSentCallBack(Callback logCallback) {
		this.sentCallback = logCallback;
	}

	protected Callback getSentCallback() {
		return sentCallback;
	}

	protected KafkaProducer<String, T> getProducer() {
		return producer;
	}

	protected String getClientId() {
		return clientId;
	}

	protected String getTopic() {
		return topic;
	}

	protected List<PartitionInfo> getPartitions() {
		return getProducer().partitionsFor(getTopic());
	}

	protected ProducerConfig getConfig() {
		return config;
	}

	protected boolean isSync() {
		return sync;
	}

	protected boolean isCallback() {
		return callback;
	}

	protected boolean isClosed() {
		return closed;
	}

	public abstract void run();

	protected void sendRecords(List<T> data) {
		LOGGER.info("Total number of records:{}", data.size());
		long beginTime = System.currentTimeMillis();
		long endTime = System.currentTimeMillis();
		long totalSize = 0;
		List<Future<RecordMetadata>> results = new ArrayList<>();
		for (T datum : data) {
			ProducerRecord<String, T> record = buildRecord(getTopic(), datum);
			if (record != null) {
				Future<RecordMetadata> oMetadata = send(record);
				if (isSync()) {
					try {
						RecordMetadata metadata = oMetadata.get();
						totalSize += metadata.serializedValueSize();
						LOGGER.debug("Topic({}) - Partition({}) - Offset({}) - Message size({})", metadata.topic(),
								metadata.partition(), metadata.offset(), metadata.serializedValueSize());
					} catch (InterruptedException | ExecutionException e) {
						LOGGER.error("Error sending message", e);
					}
				} else {
					results.add(oMetadata);
				}

			} else {
				LOGGER.error("Message discarded {}", datum);
			}
		}
		long publishingTime = System.currentTimeMillis();
		endTime = publishingTime;
		if (results.size() > 0) {
			for (Future<RecordMetadata> result : results) {
				try {
					RecordMetadata metaData = result.get();
					totalSize += metaData.serializedValueSize();
				} catch (InterruptedException | ExecutionException e) {
					LOGGER.error("Error handling futures", e);
				}
				endTime = System.currentTimeMillis();
			}
		}
		LOGGER.info("************************************************");
		long totalTime = endTime - beginTime;
		LOGGER.info("Topic:{} - Partitions:{}", getTopic(), getPartitions().size());
		LOGGER.info("Sync:{}", isSync());
		LOGGER.info("Begin - End ({},{}):{}", beginTime, endTime, totalTime);
		LOGGER.info("Records:{}", data.size());
		LOGGER.info("Size:{}", totalSize);
		LOGGER.info("THroughput Mb/s:{}", ((float) totalSize / totalTime) / 1000);
		LOGGER.info("THroughput msg/s:{}", ((float) data.size() / totalTime) * 1000);
//		LOGGER.info("{},{},{},{}", records, size, ((double) size / totalTime) * 1000,
//				((double) records / totalTime) * 1000);
		LOGGER.info("************************************************");
		LOGGER.info("Total number of records sent:{}", data.size());
	}

	protected Future<RecordMetadata> send(ProducerRecord<String, T> record) {
		Future<RecordMetadata> result = null;
		try {
			// LOGGER.debug("Sending message ({}):{}", getClientId(), record.key());
			if (isSync()) {
				result = getProducer().send(record);
			} else {
				if (isCallback()) {
					result = getProducer().send(record, getSentCallback());
				} else {
					result = getProducer().send(record);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error sending message", e);
		}
		return result;
	}

	protected abstract ProducerRecord<String, T> buildRecord(String topic, T flightData);

//	protected ProducerRecord<String, T> buildRecord(String topic, T flightData) throws JsonProcessingException {
//		ProducerRecord<String, T> result = null;
//		if (flightData.getIcao() == null || flightData.getIcao().length() == 0 || flightData.getLat() == null
//				|| flightData.getLong() == null) {
//			LOGGER.debug("Discarded:{}", flightData.getIcao());
//		} else {
//			result = new ProducerRecord<>(topic, flightData.getIcao(), flightData);
//		}
//
//		return result;
//	}

	protected void setClosed(boolean closed) {
		this.closed = closed;
	}

	public void close() {
		try {
			producer.close();
		} catch (Exception e) {
			LOGGER.error("Exception occurred while stopping the producer", e);
		}
	}

	protected long getLongValue(String value, long defaultValue) {
		long result = defaultValue;
		if (value != null) {
			try {
				result = Long.valueOf(value);
			} catch (NumberFormatException e) {
				LOGGER.warn("Assign default value {} -> {}", value, defaultValue);
			}
		}
		return result;
	}

	protected void logMetrics() {
		Map<MetricName, ? extends Metric> metrics = getProducer().metrics();
		LOGGER.info("*************************************");
		for (Entry<MetricName, ? extends Metric> metricEntry : metrics.entrySet()) {
			LOGGER.info("{}:{}->{}", metricEntry.getKey().name(), metricEntry.getValue().metricName().name(),
					metricEntry.getValue().metricValue());
		}
		LOGGER.info("*************************************");
	}

}
