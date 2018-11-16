package net.upmt.moit.distributed.adsbexchange;
/**
 *
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.ResourceBundle;
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

import com.fasterxml.jackson.core.JsonProcessingException;

import net.upmt.moit.distributed.adsbexchange.model.FlightData;
import net.upmt.moit.distributed.adsbexchange.model.FlightDataList;
import net.upmt.moit.distributed.adsbexchange.util.Util;

public abstract class SimpleProducer implements Runnable {

	private final Logger LOGGER = LoggerFactory.getLogger(SimpleProducer.class);

	private KafkaProducer<String, FlightData> producer;
	private String clientId;
	private String topic;
	private Properties config;
	private Callback sentCallback = new LogCallback();
	private boolean sync = false;
	private boolean callback = false;

	/**
	 * Constructor
	 * 
	 * @param consumerId, name of the properties file used for configuring it
	 */
	public SimpleProducer(String consumerId) {
		LOGGER.info("Creating producer {}", consumerId);
		this.clientId = consumerId;
		config = Util.convertResourceBundleToProperties(ResourceBundle.getBundle(consumerId));
		topic = config.getProperty("topic");
		sync = !Boolean.valueOf(config.getProperty("commit.async", "true"));
		callback = Boolean.valueOf(config.getProperty("commit.callback", "false"));
		this.producer = new KafkaProducer<>(config);
	}

	protected Callback getSentCallback() {
		return sentCallback;
	}

	protected KafkaProducer<String, FlightData> getProducer() {
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

	protected Properties getConfig() {
		return config;
	}

	protected boolean isSync() {
		return sync;
	}

	protected boolean isCallback() {
		return callback;
	}

	protected boolean isClosed() {
		return false;
	}

	public abstract void run();

	private class LogCallback implements org.apache.kafka.clients.producer.Callback {
		@Override
		public void onCompletion(RecordMetadata data, Exception e) {
			if (e != null) {
				LOGGER.error("Error while producing message to topic : {}", data.topic(), e);
			} else {
				LOGGER.debug("Topic({}) - Partition({}) - Offset({}) - Message size({})", data.topic(),
						data.partition(), data.offset(), data.serializedValueSize());
			}
		}
	}

	protected void sendRecords(FlightDataList dataList) {
		LOGGER.info("Total number of records:{}", dataList.getAcList().size());
		long beginTime = System.currentTimeMillis();
		long endTime = System.currentTimeMillis();
		long totalSize = 0;
		List<Future<RecordMetadata>> results = new ArrayList<>();
		for (FlightData flightData : dataList.getAcList()) {

			try {
				ProducerRecord<String, FlightData> record = buildRecord(getTopic(), flightData);
				if (record != null) {
					Future<RecordMetadata> metadata = send(record);
					if (isSync()) {
						try {
							RecordMetadata data = metadata.get();
							totalSize += data.serializedValueSize();
							LOGGER.debug("Topic({}) - Partition({}) - Offset({}) - Message size({})", data.topic(),
									data.partition(), data.offset(), data.serializedValueSize());
						} catch (InterruptedException | ExecutionException e) {
							LOGGER.error("Error sending message", e);
						}
					} else {
						results.add(metadata);
					}
				}
			} catch (JsonProcessingException e) {
				LOGGER.error("Message discarded", e);
			}
		}
		long publishingTime = System.currentTimeMillis();
		endTime = publishingTime;
		if (results.size() > 0) {
			for (Future<RecordMetadata> result : results) {
				try {
					RecordMetadata data = result.get();
					totalSize += data.serializedValueSize();
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
		LOGGER.info("Records:{}", dataList.getAcList().size());
		LOGGER.info("Size:{}", totalSize);
		LOGGER.info("THroughput Mb/s:{}", ((float) totalSize / totalTime) / 1000);
		LOGGER.info("THroughput msg/s:{}", ((float) dataList.getAcList().size() / totalTime) * 1000);
//		LOGGER.info("{},{},{},{}", records, size, ((double) size / totalTime) * 1000,
//				((double) records / totalTime) * 1000);
		LOGGER.info("************************************************");

		LOGGER.info("Total number of records sent:{}", dataList.getAcList().size());
	}

	protected Future<RecordMetadata> send(ProducerRecord<String, FlightData> record) {
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

	protected ProducerRecord<String, FlightData> buildRecord(String topic, FlightData flightData)
			throws JsonProcessingException {
		ProducerRecord<String, FlightData> result = null;
		if (flightData.getIcao() == null || flightData.getIcao().length() == 0 || flightData.getLat() == null
				|| flightData.getLong() == null) {
			LOGGER.debug("Discarded:{}", flightData.getIcao());
		} else {
			result = new ProducerRecord<>(topic, flightData.getIcao(), flightData);
		}

		return result;
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
