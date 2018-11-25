/**
 *
 */
package net.rlopezv.miot.kafka.producer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.rlopezv.miot.kafka.experiment.ProducerConfig;
import net.rlopezv.miot.kafka.experiment.TopicConfig;

/**
 * @author ramon
 *
 */

public class ProducerApp {

	final static Logger LOGGER = LoggerFactory.getLogger(ProducerApp.class);

	private ExecutorService executor = null;

	private List<AbstractProducer> producers = new ArrayList<>();

	public ProducerApp(int size) {
		if (size > 0) {
			executor = Executors.newFixedThreadPool(size);
		}
	}

	public void start() {
		for (AbstractProducer producer : producers) {
			execute(producer);
		}
	}

	/**
	 * 
	 * @param consumer
	 */
	private void execute(Runnable producer) {
		executor.execute(producer);
	}

	/**
	 * 
	 */
	public void shutdownAndAwaitTermination() {
		executor.shutdown(); // Disable new tasks from being submitted
		try {
			// Wait a while for existing tasks to terminate
			if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
				executor.shutdownNow(); // Cancel currently executing tasks
				// Wait a while for tasks to respond to being cancelled
				if (!executor.awaitTermination(60, TimeUnit.SECONDS))
					LOGGER.error("Pool did not terminate");
			}
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			executor.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
	}

	public AbstractProducer<?> addProducer(List<TopicConfig> topics, ProducerConfig producerConfig) {
		AbstractProducer<?> result = null;
		try {
			Class<?> producerClass = Class.forName(producerConfig.getImplementantionClass());
			Optional<TopicConfig> oTopicConfig = topics.stream()
					.filter(p -> p.getName().equals(producerConfig.getTopic())).findFirst();
			if (oTopicConfig.isPresent()) {
				Constructor<?> producerConstructor = producerClass.getConstructor(TopicConfig.class,
						ProducerConfig.class);
				result = (AbstractProducer<?>) producerConstructor.newInstance(oTopicConfig.get(), producerConfig);
				producers.add(result);
			} else {
				LOGGER.error("No topic configured: {}", producerConfig);
			}
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			LOGGER.error("Error creating producer", e);
		}
		return result;
	}

}
