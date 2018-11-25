
package net.rlopezv.miot.kafka.experiment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "name", "environment", "topics", "producers", "consumers", "additionalProperties" })
public class ExperimentConfig implements Serializable {

	@JsonProperty("name")
	private String name;
	@JsonProperty("environment")
	private EnvironmentConfig environment;
	@JsonProperty("topics")
	private List<TopicConfig> topics = new ArrayList<TopicConfig>();
	@JsonProperty("producers")
	private List<ProducerConfig> producers = new ArrayList<ProducerConfig>();
	@JsonProperty("consumers")
	private List<ConsumerConfig> consumers = new ArrayList<ConsumerConfig>();
	@JsonProperty("additionalProperties")
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	public ExperimentConfig withName(String name) {
		this.name = name;
		return this;
	}

	@JsonProperty("environment")
	public EnvironmentConfig getEnvironment() {
		return environment;
	}

	@JsonProperty("environment")
	public void setEnvironment(EnvironmentConfig environment) {
		this.environment = environment;
	}

	public ExperimentConfig withEnvironment(EnvironmentConfig environment) {
		this.environment = environment;
		return this;
	}

	@JsonProperty("topics")
	public List<TopicConfig> getTopics() {
		return topics;
	}

	@JsonProperty("topics")
	public void setTopics(List<TopicConfig> topics) {
		this.topics = topics;
	}

	public ExperimentConfig withTopics(List<TopicConfig> topics) {
		this.topics = topics;
		return this;
	}

	@JsonProperty("producers")
	public List<ProducerConfig> getProducers() {
		return producers;
	}

	@JsonProperty("producers")
	public void setProducers(List<ProducerConfig> producers) {
		this.producers = producers;
	}

	public ExperimentConfig withProducers(List<ProducerConfig> producers) {
		this.producers = producers;
		return this;
	}

	@JsonProperty("consumers")
	public List<ConsumerConfig> getConsumers() {
		return consumers;
	}

	@JsonProperty("consumers")
	public void setConsumers(List<ConsumerConfig> consumers) {
		this.consumers = consumers;
	}

	public ExperimentConfig withConsumers(List<ConsumerConfig> consumers) {
		this.consumers = consumers;
		return this;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

	public ExperimentConfig withAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
		return this;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("name", name).append("environment", environment)
				.append("topics", topics).append("producers", producers).append("consumers", consumers)
				.append("additionalProperties", additionalProperties).toString();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(topics).append(environment).append(additionalProperties).append(name)
				.append(consumers).append(producers).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof ExperimentConfig) == false) {
			return false;
		}
		ExperimentConfig rhs = ((ExperimentConfig) other);
		return new EqualsBuilder().append(topics, rhs.topics).append(environment, rhs.environment)
				.append(additionalProperties, rhs.additionalProperties).append(name, rhs.name)
				.append(consumers, rhs.consumers).append(producers, rhs.producers).isEquals();
	}

}
