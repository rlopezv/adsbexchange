/**
 * 
 */
package net.rlopezv.miot.kafka;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.kafka.common.utils.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kafka.admin.RackAwareMode;
import kafka.zk.AdminZkClient;
import kafka.zk.KafkaZkClient;
import net.rlopezv.miot.kafka.experiment.ExperimentConfig;
import net.rlopezv.miot.kafka.experiment.ProducerConfig;
import net.rlopezv.miot.kafka.experiment.TopicConfig;
import net.rlopezv.miot.kafka.producer.ProducerApp;
import scala.collection.Map;

/**
 * 
 * Class in charge of creating the environment for an scenario It wiil create
 * topics if necesarry. And it will start the consumers/producers
 * 
 * @author ramon
 *
 */
public class App {

	private Logger LOGGER = LoggerFactory.getLogger(App.class);
	private final ObjectMapper mapper = new ObjectMapper();

	private ExperimentConfig config;

	protected ExperimentConfig getConfig() {
		return config;
	}

	protected void setConfig(ExperimentConfig config) {
		this.config = config;
	}

	protected ObjectMapper getMapper() {
		return mapper;
	}

	final KafkaZkClient getZkClient() {
		String zookeeperHost = getConfig().getEnvironment().getZookeeperHost();
		boolean isSucre = false;
		int sessionTimeoutMs = getConfig().getEnvironment().getSessionTimeoutMs();
		int connectionTimeoutMs = getConfig().getEnvironment().getConnectionTimeoutMs();
		int maxInFlightRequests = getConfig().getEnvironment().getMaxInFlightRequests();
		Time time = Time.SYSTEM;
		String metricGroup = getConfig().getEnvironment().getMetricGroup();
		String metricType = getConfig().getEnvironment().getMetricType();
		return KafkaZkClient.apply(zookeeperHost, isSucre, sessionTimeoutMs, connectionTimeoutMs, maxInFlightRequests,
				time, metricGroup, metricType);
	}

	final AdminZkClient getKafkaAdminClient() {
		return new AdminZkClient(getZkClient());
	}

	/**
	 * Check if a topic exists and creates or updates it with topic configuration
	 */
	private void createTopicIfNotExists(TopicConfig topicConfig) {

		String topic = topicConfig.getName();
		int partitions = topicConfig.getPartitions();
		int replicationFactor = topicConfig.getReplicationFactor();
		Properties commonConfig = topicConfig.getProperties();
		RackAwareMode rackAwareMode = RackAwareMode.Disabled$.MODULE$;
		// disabled, enforced, safe;
		if (listTopics().stream().filter(p -> p.equalsIgnoreCase(topic)).count() == 0) {
			getKafkaAdminClient().createTopic(topic, partitions, replicationFactor, commonConfig, rackAwareMode);
			LOGGER.info("Added topic:{}", topic);
		} else {
			LOGGER.info("Modifing topic:{}", topic);
		}

	}

	/**
	 * Obtains the list of available topics
	 * 
	 * @return
	 */
	private List<Properties> listTopicInfos() {
		List<Properties> topics = new ArrayList<Properties>();
		Map<String, Properties> scalaTopicConfigs = getKafkaAdminClient().getAllTopicConfigs();

		java.util.Map<String, Properties> topicConfigs = scala.collection.JavaConverters
				.mapAsJavaMapConverter(scalaTopicConfigs).asJava();

		for (Entry<String, Properties> topicConfigEntry : topicConfigs.entrySet()) {
			topics.add(topicConfigEntry.getValue());
		}

		return topics;
	}

	/**
	 * Obtains the list of available topics
	 * 
	 * @return
	 */
	private List<String> listTopics() {
		List<String> topics = new ArrayList<String>();
		Map<String, Properties> scalaTopicConfigs = getKafkaAdminClient().getAllTopicConfigs();

		java.util.Map<String, Properties> topicConfigs = scala.collection.JavaConverters
				.mapAsJavaMapConverter(scalaTopicConfigs).asJava();

		for (Entry<String, Properties> topicConfigEntry : topicConfigs.entrySet()) {
			topics.add(topicConfigEntry.getKey());
		}

		return topics;
	}

	final void deleteTopic(String topic) {
		getKafkaAdminClient().deleteTopic(topic);
		LOGGER.info("Deleted topic:{}", topic);
	}

	final ExperimentConfig parseConfig(String path) throws JsonParseException, JsonMappingException, IOException {
		return getMapper().readValue(new File(path), ExperimentConfig.class);

	}

	public static void main(final String[] args) {

		String configFile = null;
//		if (args == null || args.length == 0) {
//			System.out.println("Usage: java -jar app.jar filename");
//		} else {
		App app = new App();
		try {
			configFile = "/home/ramon/git/absexchange/adsbexchange/etc/experiment.json";
			app.configure(configFile);
			app.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
//		}

		// Shutdown hook to ensure ordered close of consumers
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				app.finish();
			}
		});
	}

	private void finish() {
		for (TopicConfig topic : getConfig().getTopics()) {
			deleteTopic(topic.getName());
		}

	}

	private void start() {
		LOGGER.info("Topics available: {}", listTopics());
		for (TopicConfig topic : getConfig().getTopics()) {
			createTopicIfNotExists(topic);
		}
		// ConsumerApp consumerApp = new ConsumerApp(getConfig().getConsumers().size());
		ProducerApp producerApp = new ProducerApp(getConfig().getProducers().size());
		LOGGER.info("Topics info: {}", listTopicInfos());
		for (ProducerConfig producerConfig : getConfig().getProducers()) {
			producerApp.addProducer(config.getTopics(), producerConfig);
		}
//		for (ConsumerConfig consumerConfig : getConfig().getConsumers()) {
//			consumerApp.addConsumer(config.getTopics(), consumerConfig);
//		}

		producerApp.start();

	}

	private void configure(String configPath) throws JsonParseException, JsonMappingException, IOException {
		setConfig(parseConfig(configPath));

	}

}
