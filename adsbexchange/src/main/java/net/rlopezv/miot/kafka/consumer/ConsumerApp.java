/**
 *
 */
package net.rlopezv.miot.kafka.consumer;

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

import net.rlopezv.miot.kafka.experiment.ConsumerConfig;
import net.rlopezv.miot.kafka.experiment.TopicConfig;

/**
 * @author ramon
 *
 */

public class ConsumerApp {

	final static Logger LOGGER = LoggerFactory.getLogger(ConsumerApp.class);

	private ExecutorService executor = null;

	private List<AbstractConsumer<?>> consumers = new ArrayList<>();

	public ConsumerApp(int size) {
		if (size > 0) {
			executor = Executors.newFixedThreadPool(size);
		}
	}

	public void start() {
		for (AbstractConsumer<?> consumer : consumers) {
			execute(consumer);
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

	public AbstractConsumer<?> addConsumer(List<TopicConfig> topics, ConsumerConfig consumerConfig) {
		AbstractConsumer<?> result = null;
		try {
			Class<?> consumerClass = Class.forName(consumerConfig.getImplementantionClass());
			Optional<TopicConfig> oTopicConfig = topics.stream()
					.filter(p -> p.getName().equals(consumerConfig.getTopic())).findFirst();
			if (oTopicConfig.isPresent()) {
				Constructor<?> consumerConstructor = consumerClass.getConstructor(TopicConfig.class,
						ConsumerConfig.class);
				result = (AbstractConsumer<?>) consumerConstructor.newInstance(oTopicConfig.get(), consumerConfig);
				consumers.add(result);
			} else {
				LOGGER.error("No topic configured: {}", consumerConfig);
			}
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			LOGGER.error("Error creating consumer", e);
		}
		return result;
	}

}
