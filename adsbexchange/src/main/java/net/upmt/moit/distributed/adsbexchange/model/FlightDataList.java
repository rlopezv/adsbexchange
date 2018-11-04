package net.upmt.moit.distributed.adsbexchange.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FlightDataList implements Serializable {

	@JsonProperty("acList")
	private List<FlightData> acList = null;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();
	private final static long serialVersionUID = -7525918838531253269L;

	@JsonProperty("acList")
	public List<FlightData> getAcList() {
		return acList;
	}

	@JsonProperty("acList")
	public void setAcList(List<FlightData> acList) {
		this.acList = acList;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("acList", acList).append("additionalProperties", additionalProperties)
				.toString();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(acList).append(additionalProperties).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof FlightDataList) == false) {
			return false;
		}
		FlightDataList rhs = ((FlightDataList) other);
		return new EqualsBuilder().append(acList, rhs.acList).append(additionalProperties, rhs.additionalProperties)
				.isEquals();
	}

}
