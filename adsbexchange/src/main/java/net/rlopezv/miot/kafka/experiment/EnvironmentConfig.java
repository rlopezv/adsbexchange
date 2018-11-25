
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

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "zookeeperHost", "sessionTimeoutMs", "connectionTimeoutMs", "maxInFlightRequests", "metricGroup",
		"metricType", "consumerTreadNumber", "producerTreadNumber", "additionalProperties" })
public class EnvironmentConfig implements Serializable {

	@JsonProperty("zookeeperHost")
	private String zookeeperHost;
	@JsonProperty("sessionTimeoutMs")
	private Integer sessionTimeoutMs;
	@JsonProperty("connectionTimeoutMs")
	private Integer connectionTimeoutMs;
	@JsonProperty("maxInFlightRequests")
	private Integer maxInFlightRequests;
	@JsonProperty("metricGroup")
	private String metricGroup;
	@JsonProperty("metricType")
	private String metricType;
	@JsonProperty("consumerTreadNumber")
	private Integer consumerTreadNumber;
	@JsonProperty("producerTreadNumber")
	private Integer producerTreadNumber;
	@JsonProperty("additionalProperties")
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();
	private final static long serialVersionUID = 7508666494615500056L;

	@JsonProperty("zookeeperHost")
	public String getZookeeperHost() {
		return zookeeperHost;
	}

	@JsonProperty("zookeeperHost")
	public void setZookeeperHost(String zookeeperHost) {
		this.zookeeperHost = zookeeperHost;
	}

	public EnvironmentConfig withZookeeperHost(String zookeeperHost) {
		this.zookeeperHost = zookeeperHost;
		return this;
	}

	@JsonProperty("sessionTimeoutMs")
	public Integer getSessionTimeoutMs() {
		return sessionTimeoutMs;
	}

	@JsonProperty("sessionTimeoutMs")
	public void setSessionTimeoutMs(Integer sessionTimeoutMs) {
		this.sessionTimeoutMs = sessionTimeoutMs;
	}

	public EnvironmentConfig withSessionTimeoutMs(Integer sessionTimeoutMs) {
		this.sessionTimeoutMs = sessionTimeoutMs;
		return this;
	}

	@JsonProperty("connectionTimeoutMs")
	public Integer getConnectionTimeoutMs() {
		return connectionTimeoutMs;
	}

	@JsonProperty("connectionTimeoutMs")
	public void setConnectionTimeoutMs(Integer connectionTimeoutMs) {
		this.connectionTimeoutMs = connectionTimeoutMs;
	}

	public EnvironmentConfig withConnectionTimeoutMs(Integer connectionTimeoutMs) {
		this.connectionTimeoutMs = connectionTimeoutMs;
		return this;
	}

	@JsonProperty("maxInFlightRequests")
	public Integer getMaxInFlightRequests() {
		return maxInFlightRequests;
	}

	@JsonProperty("maxInFlightRequests")
	public void setMaxInFlightRequests(Integer maxInFlightRequests) {
		this.maxInFlightRequests = maxInFlightRequests;
	}

	public EnvironmentConfig withMaxInFlightRequests(Integer maxInFlightRequests) {
		this.maxInFlightRequests = maxInFlightRequests;
		return this;
	}

	@JsonProperty("metricGroup")
	public String getMetricGroup() {
		return metricGroup;
	}

	@JsonProperty("metricGroup")
	public void setMetricGroup(String metricGroup) {
		this.metricGroup = metricGroup;
	}

	public EnvironmentConfig withMetricGroup(String metricGroup) {
		this.metricGroup = metricGroup;
		return this;
	}

	@JsonProperty("metricType")
	public String getMetricType() {
		return metricType;
	}

	@JsonProperty("metricType")
	public void setMetricType(String metricType) {
		this.metricType = metricType;
	}

	public EnvironmentConfig withMetricType(String metricType) {
		this.metricType = metricType;
		return this;
	}

	@JsonProperty("consumerTreadNumber")
	public Integer getConsumerTreadNumber() {
		return consumerTreadNumber;
	}

	@JsonProperty("consumerTreadNumber")
	public void setConsumerTreadNumber(Integer consumerTreadNumber) {
		this.consumerTreadNumber = consumerTreadNumber;
	}

	public EnvironmentConfig withConsumerTreadNumber(Integer consumerTreadNumber) {
		this.consumerTreadNumber = consumerTreadNumber;
		return this;
	}

	@JsonProperty("producerTreadNumber")
	public Integer getProducerTreadNumber() {
		return producerTreadNumber;
	}

	@JsonProperty("producerTreadNumber")
	public void setProducerTreadNumber(Integer producerTreadNumber) {
		this.producerTreadNumber = producerTreadNumber;
	}

	public EnvironmentConfig withProducerTreadNumber(Integer producerTreadNumber) {
		this.producerTreadNumber = producerTreadNumber;
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

	public EnvironmentConfig withAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
		return this;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("zookeeperHost", zookeeperHost)
				.append("sessionTimeoutMs", sessionTimeoutMs).append("connectionTimeoutMs", connectionTimeoutMs)
				.append("maxInFlightRequests", maxInFlightRequests).append("metricGroup", metricGroup)
				.append("metricType", metricType).append("consumerTreadNumber", consumerTreadNumber)
				.append("producerTreadNumber", producerTreadNumber).append("additionalProperties", additionalProperties)
				.toString();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(metricGroup).append(producerTreadNumber).append(consumerTreadNumber)
				.append(metricType).append(maxInFlightRequests).append(additionalProperties).append(sessionTimeoutMs)
				.append(zookeeperHost).append(connectionTimeoutMs).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof EnvironmentConfig) == false) {
			return false;
		}
		EnvironmentConfig rhs = ((EnvironmentConfig) other);
		return new EqualsBuilder().append(metricGroup, rhs.metricGroup)
				.append(producerTreadNumber, rhs.producerTreadNumber)
				.append(consumerTreadNumber, rhs.consumerTreadNumber).append(metricType, rhs.metricType)
				.append(maxInFlightRequests, rhs.maxInFlightRequests)
				.append(additionalProperties, rhs.additionalProperties).append(sessionTimeoutMs, rhs.sessionTimeoutMs)
				.append(zookeeperHost, rhs.zookeeperHost).append(connectionTimeoutMs, rhs.connectionTimeoutMs)
				.isEquals();
	}

}
