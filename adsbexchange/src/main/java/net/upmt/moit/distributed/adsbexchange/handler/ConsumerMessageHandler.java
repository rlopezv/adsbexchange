/**
 * 
 */
package net.upmt.moit.distributed.adsbexchange.handler;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import net.upmt.moit.distributed.adsbexchange.SimpleConsumer;
import net.upmt.moit.distributed.adsbexchange.model.FlightData;

/**
 * Class to implement in order to handle messages
 * 
 * @author ramon
 *
 */
public abstract class ConsumerMessageHandler {

	private SimpleConsumer consumer;

	public ConsumerMessageHandler(SimpleConsumer consumer) {
		this.consumer = consumer;
	}

	protected SimpleConsumer getConsumer() {
		return consumer;
	}

	public void handleMessages(ConsumerRecords<String, FlightData> consumerRecords) {
		for (ConsumerRecord<String, FlightData> consumerRecord : consumerRecords) {
			handleMessage(consumerRecord);
		}
	}

	public abstract void handleMessage(ConsumerRecord<String, FlightData> record);

}
