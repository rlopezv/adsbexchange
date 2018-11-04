package net.upmt.moit.distributed.adsbexchange.serialize;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.upmt.moit.distributed.adsbexchange.model.FlightData;

public class FlightDataDeserializer implements Deserializer<FlightData> {

	private static final Logger LOGGER = LoggerFactory.getLogger(FlightDataDeserializer.class);

	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public FlightData deserialize(String arg0, byte[] arg1) {
		FlightData result = null;
		try {
			result = objectMapper.readValue(arg1, FlightData.class);
		} catch (Exception e) {
			LOGGER.error("Error deserializing:{}", e.getMessage());
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
