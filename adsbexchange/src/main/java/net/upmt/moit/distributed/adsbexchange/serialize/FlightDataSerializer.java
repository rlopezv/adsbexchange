package net.upmt.moit.distributed.adsbexchange.serialize;

import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.upmt.moit.distributed.adsbexchange.model.FlightData;

public class FlightDataSerializer implements Serializer<FlightData> {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	private static final Logger LOGGER = LoggerFactory.getLogger(FlightDataSerializer.class);

	@Override
	public byte[] serialize(String arg0, FlightData arg1) {
		byte[] result = null;

		try {
			result = objectMapper.writeValueAsString(arg1).getBytes();
		} catch (Exception e) {
			LOGGER.error("Error serializing:{}", e.getMessage());
		}
		return result;
	}

	@Override
	public void close() {

	}

	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {
		// TODO Auto-generated method stub

	}
}
