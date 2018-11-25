
package net.rlopezv.miot.kafka.experiment;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "name", "partitions", "replicationFactor", "additionalProperties" })
public class TopicConfig implements Serializable {

	@JsonProperty("name")
	private String name;
	@JsonProperty("partitions")
	private Integer partitions = 1;
	@JsonProperty("replicationFactor")
	private Integer replicationFactor = 1;
	@JsonProperty("additionalProperties")
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();
	private final static long serialVersionUID = -8390844815285256554L;

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	public TopicConfig withName(String name) {
		this.name = name;
		return this;
	}

	@JsonProperty("partitions")
	public Integer getPartitions() {
		return partitions;
	}

	@JsonProperty("partitions")
	public void setPartitions(Integer partitions) {
		this.partitions = partitions;
	}

	public TopicConfig withPartitions(Integer partitions) {
		this.partitions = partitions;
		return this;
	}

	@JsonProperty("replicationFactor")
	public Integer getReplicationFactor() {
		return replicationFactor;
	}

	@JsonProperty("replicationFactor")
	public void setReplicationFactor(Integer replicationFactor) {
		this.replicationFactor = replicationFactor;
	}

	public TopicConfig withReplicationFactor(Integer replicationFactor) {
		this.replicationFactor = replicationFactor;
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

	public TopicConfig withAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
		return this;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("name", name).append("partitions", partitions)
				.append("replicationFactor", replicationFactor).append("additionalProperties", additionalProperties)
				.toString();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(partitions).append(additionalProperties).append(name)
				.append(replicationFactor).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof TopicConfig) == false) {
			return false;
		}
		TopicConfig rhs = ((TopicConfig) other);
		return new EqualsBuilder().append(partitions, rhs.partitions)
				.append(additionalProperties, rhs.additionalProperties).append(name, rhs.name)
				.append(replicationFactor, rhs.replicationFactor).isEquals();
	}

	public Properties getProperties() {
		Properties properties = new Properties();
		properties.putAll(getAdditionalProperties());
		return properties;
	}

}
