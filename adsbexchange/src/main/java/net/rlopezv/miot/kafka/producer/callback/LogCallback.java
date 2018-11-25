package net.rlopezv.miot.kafka.producer.callback;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;

public class LogCallback implements org.apache.kafka.clients.producer.Callback {

	private Logger logger = null;

	public LogCallback(Logger logger) {
		this.logger = logger;
	}

	@Override
	public void onCompletion(RecordMetadata data, Exception e) {
		if (e != null) {
			logger.error("Error while producing message to topic : {}", data.topic(), e);
		} else {
			logger.debug("Topic({}) - Partition({}) - Offset({}) - Message size({})", data.topic(), data.partition(),
					data.offset(), data.serializedValueSize());
		}
	}
}
