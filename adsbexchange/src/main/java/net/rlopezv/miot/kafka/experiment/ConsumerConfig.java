
package net.rlopezv.miot.kafka.experiment;

import java.io.Serializable;
import java.util.HashMap;
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
@JsonPropertyOrder({ "name", "implementantionClass", "additionalProperties" })
public class ConsumerConfig implements Serializable {

	@JsonProperty("name")
	private String name;
	@JsonProperty("implementantionClass")
	private String implementantionClass;
	@JsonProperty("additionalProperties")
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();
	private String topic;

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	public ConsumerConfig withName(String name) {
		this.name = name;
		return this;
	}

	@JsonProperty("implementantionClass")
	public String getImplementantionClass() {
		return implementantionClass;
	}

	@JsonProperty("implementantionClass")
	public void setImplementantionClass(String implementantionClass) {
		this.implementantionClass = implementantionClass;
	}

	public ConsumerConfig withImplementantionClass(String implementantionClass) {
		this.implementantionClass = implementantionClass;
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

	public ConsumerConfig withAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
		return this;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("name", name).append("implementantionClass", implementantionClass)
				.append("additionalProperties", additionalProperties).toString();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(additionalProperties).append(name).append(implementantionClass)
				.toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof ConsumerConfig) == false) {
			return false;
		}
		ConsumerConfig rhs = ((ConsumerConfig) other);
		return new EqualsBuilder().append(additionalProperties, rhs.additionalProperties).append(name, rhs.name)
				.append(implementantionClass, rhs.implementantionClass).isEquals();
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public ConsumerConfig withTopic(String topic) {
		this.topic = topic;
		return this;
	}

	@SuppressWarnings("unchecked")
	public <T> T getProperty(String key, Class<T> targetClass) {
		Object value = getAdditionalProperties().get(key);
		Object result = null;
		if (value != null) {
			if (String.class == targetClass) {
				result = value;
			} else if (Integer.class == targetClass) {
				result = Integer.valueOf(String.valueOf(value));
			} else if (Long.class == targetClass) {
				result = Long.valueOf(String.valueOf(value));
			}
		}
		return (T) result;
	}

	public <T> T getProperty(String key, Class<T> targetClass, T defaultValue) {
		T result = getProperty(key, targetClass);
		if (result == null) {
			result = defaultValue;
		}
		return (T) result;
	}

}
