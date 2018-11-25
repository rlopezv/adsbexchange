/**
 *
 */
package net.upmt.moit.distributed.adsbexchange;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.rlopezv.miot.kafka.experiment.ConsumerConfig;
import net.upmt.moit.distributed.adsbexchange.util.Util;

/**
 * @author ramon
 *
 */

public class ConsumerApp {

	final static Logger LOGGER = LoggerFactory.getLogger(ConsumerApp.class);

	private final static int DEFAULT_CONSUMERS_NUMBER = 1;

	private final static String DEFAULT_CONSUMERS_PREFIX = "consumer_";

	final static ResourceBundle resourceBundle = ResourceBundle.getBundle("kafka");

	private ExecutorService executor = null;

	private Properties config;

	private SimpleConsumer buildConsumer(String consumer) {
		SimpleConsumer result = null;
		try {
			Class<?> producerClass = Class.forName(this.getConfig().getProperty(consumer + ".class"));
			Constructor<?> consumerConstructor = producerClass.getConstructor(String.class);
			result = (SimpleConsumer) consumerConstructor.newInstance(consumer);
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			LOGGER.error("Error creating producer", e);
		}
		return result;
	}

	public static void main(String[] args) {

		int consumersNum = DEFAULT_CONSUMERS_NUMBER;
		// Builds infrastucture for execution
		// Executor service
		ConsumerApp app = new ConsumerApp();
		List<String> consumerNames = Arrays.asList(app.getConfig().getProperty("consumers").split(","));

		for (String producerName : consumerNames) {
			app.execute(app.buildConsumer(producerName));
		}

		// Shutdown hook to ensure ordered close of consumers
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				app.shutdownAndAwaitTermination();
			}
		});

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
	 * @param consumer
	 */
	private void start(Runnable consumer) {
		executor.execute(consumer);
	}

	ConsumerApp() {
		this.config = Util.convertResourceBundleToProperties(ResourceBundle.getBundle("kafka"));
	}

	public ConsumerApp(int size) {
		if (size > 0) {
			executor = Executors.newFixedThreadPool(size);
		}
	}

	protected Properties getConfig() {
		return config;
	}

	protected void setConfig(Properties config) {
		this.config = config;
	}

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

	public SimpleConsumer addConsumer(ConsumerConfig consumerConfig) {
		SimpleConsumer result = null;
		try {
			Class<?> producerClass = Class
					.forName(this.getConfig().getProperty(consumerConfig.getImplementantionClass() + ".class"));
			Constructor<?> consumerConstructor = producerClass.getConstructor(String.class);
			result = (SimpleConsumer) consumerConstructor.newInstance(consumerConfig.getName());
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			LOGGER.error("Error creating producer", e);
		}
		return result;

	}

}
