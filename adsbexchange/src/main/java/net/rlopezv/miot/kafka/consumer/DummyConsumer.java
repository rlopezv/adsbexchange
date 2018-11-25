/**
 * 
 */
package net.rlopezv.miot.kafka.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.rlopezv.miot.kafka.experiment.ConsumerConfig;
import net.rlopezv.miot.kafka.experiment.TopicConfig;

/**
 * @author ramon
 *
 */
public class DummyConsumer extends AbstractConsumer<String> {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass().getName());

	private int numExecutions = 0;
	private static final String EXECUTIONS = "executions";

	public DummyConsumer(TopicConfig topicConfig, ConsumerConfig config) {
		super(topicConfig, config);
		numExecutions = Integer.parseInt(String.valueOf(config.getAdditionalProperties().get(EXECUTIONS)));

	}

}