/**
 * 
 */
package net.rlopezv.miot.kafka.producer;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.rlopezv.miot.kafka.experiment.ProducerConfig;
import net.rlopezv.miot.kafka.experiment.TopicConfig;

/**
 * @author ramon
 *
 */
public class DummyProducer extends AbstractProducer<String> {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass().getName());

	private int numExecutions = 0;
	private static final String EXECUTIONS = "executions";

	public DummyProducer(TopicConfig topicConfig, ProducerConfig config) {
		super(topicConfig, config);
		numExecutions = Integer.parseInt(String.valueOf(config.getAdditionalProperties().get(EXECUTIONS)));

	}

	@Override
	public void run() {
		int i = 0;
		while (!isClosed()) {
			sendRecords(buildData());
			i++;
			if (i >= numExecutions) {
				setClosed(true);
				close();
			}
		}
	}

	private List<String> buildData() {
		List<String> result = new ArrayList<String>();
		int numRecords = new Random().nextInt((1000 - 1) + 1) + 1;
		Random r = new Random();
		for (int i = 0; i < numRecords; i++) {
			byte[] array = new byte[1024]; // length is bounded by 7
			r.nextBytes(array);
			result.add(new String(array, Charset.forName("UTF-8")));
		}
		return result;
	}

	@Override
	protected ProducerRecord<String, String> buildRecord(String topic, String data) {
		return new ProducerRecord<>(topic, System.nanoTime() + "", data);
	}
}