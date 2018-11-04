/**
 * 
 */
package net.upmt.moit.distributed.adsbexchange.partitioner;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ramon
 *
 */
public class PartitionRebalanceListener implements ConsumerRebalanceListener {

	private KafkaConsumer<?, ?> kafkaConsumer = null;

	private final Logger LOGGER = LoggerFactory.getLogger(PartitionRebalanceListener.class);

	public PartitionRebalanceListener(KafkaConsumer<?, ?> kafkaConsumer) {
		this.kafkaConsumer = kafkaConsumer;

	}

	@Override
	public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
		LOGGER.info("{} topic-partitions are revoked from this consumer", Arrays.toString(partitions.toArray()));
	}

	@Override
	public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
		LOGGER.info("{} topic-partitions are assigned to this consumer", Arrays.toString(partitions.toArray()));
		Iterator<TopicPartition> topicPartitionIterator = partitions.iterator();
		while (topicPartitionIterator.hasNext()) {
			TopicPartition topicPartition = topicPartitionIterator.next();
			LOGGER.info("Current offset is {} committed offset is -> {}", kafkaConsumer.position(topicPartition),
					kafkaConsumer.committed(topicPartition));
//			if (startingOffset == 0) {
//				System.out.println("Setting offset to beginning");
//				kafkaConsumer.seekToBeginning(topicPartition);
//			} else if (startingOffset == -1) {
//				System.out.println("Setting it to the end ");
//				kafkaConsumer.seekToEnd(topicPartition);
//			} else {
//				System.out.println("Resetting offset to " + startingOffset);
//				kafkaConsumer.seek(topicPartition, startingOffset);
//			}
		}
	}
}
