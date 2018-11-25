
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

@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProducerConfig implements Serializable {

	@JsonProperty("name")
	private String name;
	@JsonProperty("implementantionClass")
	private String implementantionClass;
	@JsonProperty("additionalProperties")
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();
	private String clientId;
	private boolean commitAsync;
	private boolean commitCallback;
	private String topic;

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	public ProducerConfig withName(String name) {
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

	public ProducerConfig withImplementantionClass(String implementantionClass) {
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

	public ProducerConfig withAdditionalProperty(String name, Object value) {
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
		if ((other instanceof ProducerConfig) == false) {
			return false;
		}
		ProducerConfig rhs = ((ProducerConfig) other);
		return new EqualsBuilder().append(additionalProperties, rhs.additionalProperties).append(name, rhs.name)
				.append(implementantionClass, rhs.implementantionClass).isEquals();
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public ProducerConfig withClientId(String clientId) {
		this.clientId = clientId;
		return this;
	}

	public boolean getCommitAsync() {
		return commitAsync;
	}

	public boolean getCommitCallback() {
		return commitCallback;
	}

	public void setCommitAsync(boolean commitAsync) {
		this.commitAsync = commitAsync;
	}

	public void setCommitCallback(boolean commitCallback) {
		this.commitCallback = commitCallback;
	}

	public ProducerConfig withCommitAsync(boolean commitAsync) {
		this.commitAsync = commitAsync;
		return this;
	}

	public ProducerConfig withCommitCallback(boolean commitCallback) {
		this.commitCallback = commitCallback;
		return this;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public ProducerConfig withTopic(String topic) {
		this.topic = topic;
		return this;
	}

}
