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

import net.upmt.moit.distributed.adsbexchange.util.Util;

/**
 * @author ramon
 *
 */

public class ProducerApp {

	final static Logger LOGGER = LoggerFactory.getLogger(ProducerApp.class);

	private Properties config = null;

	private final static int DEFAULT_PRODUCER_NUMBER = 1;

	private final static String DEFAULT_PRODUCER_PREFIX = "producer_";

	private ExecutorService executor = Executors.newFixedThreadPool(10);

	public static void main(String[] args) {

		int consumersNum = DEFAULT_PRODUCER_NUMBER;
		// Builds infrastucture for execution
		// Executor service
		ProducerApp app = new ProducerApp();

		List<String> producerNames = Arrays.asList(app.getConfig().getProperty("producers").split(","));
		for (String producerName : producerNames) {
			app.execute(app.buildProducer(producerName));
		}

		// Shutdown hook to ensure ordered close of consumers
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				app.shutdownAndAwaitTermination();
			}
		});

	}

	ProducerApp() {
		this.config = Util.convertResourceBundleToProperties(ResourceBundle.getBundle("kafka"));
	}

	protected Properties getConfig() {
		return config;
	}

	private SimpleProducer buildProducer(String producerName) {
		SimpleProducer result = null;
		try {
			Class<?> producerClass = Class.forName(this.getConfig().getProperty(producerName + ".class"));
			Constructor<?> producerConstructor = producerClass.getConstructor(String.class);
			result = (SimpleProducer) producerConstructor.newInstance(producerName);
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			LOGGER.error("Error creating producer", e);
		}
		return result;
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

}
