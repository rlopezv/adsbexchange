/**
 * 
 */
package net.upmt.moit.distributed.adsbexchange.handler;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.upmt.moit.distributed.adsbexchange.ConsumerApp;
import net.upmt.moit.distributed.adsbexchange.SimpleConsumer;
import net.upmt.moit.distributed.adsbexchange.model.FlightData;

/**
 * Logs message info
 * 
 * @author ramon
 *
 */
public class LogMessageHandler extends ConsumerMessageHandler {

	final static Logger LOGGER = LoggerFactory.getLogger(ConsumerApp.class);

	public LogMessageHandler(SimpleConsumer consumer) {
		super(consumer);
	}

	@Override
	public void handleMessage(ConsumerRecord<String, FlightData> record) {
		// TODO Auto-generated method stub
		LOGGER.info("Consumed ({}) with offset {}:{}", record.key(), record.offset(), record.value());
	}

}
