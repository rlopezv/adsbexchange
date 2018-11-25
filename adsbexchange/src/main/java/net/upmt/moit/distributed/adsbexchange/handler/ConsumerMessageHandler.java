/**
 * 
 */
package net.upmt.moit.distributed.adsbexchange.handler;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import net.upmt.moit.distributed.adsbexchange.SimpleConsumer;

/**
 * Class to implement in order to handle messages
 * 
 * @author ramon
 *
 */
public abstract class ConsumerMessageHandler<T> {

	private SimpleConsumer consumer;

	public ConsumerMessageHandler(SimpleConsumer consumer) {
		this.consumer = consumer;
	}

	protected SimpleConsumer getConsumer() {
		return consumer;
	}

	public void handleMessages(ConsumerRecords<String, T> consumerRecords) {
		for (ConsumerRecord<String, T> consumerRecord : consumerRecords) {
			handleMessage(consumerRecord);
		}
	}

	public abstract void handleMessage(ConsumerRecord<String, T> record);

}
