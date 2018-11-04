package net.upmt.moit.distributed.adsbexchange.partitioner;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.upmt.moit.distributed.adsbexchange.model.FlightData;

public class CustomPartitioner implements Partitioner {

	private final Logger LOGGER = LoggerFactory.getLogger(CustomPartitioner.class);

	private static Map<String, Integer> partitionMap;
	private static Map<String, BoundingBox> boundingBoxMap = new HashMap<>();

	public CustomPartitioner() {
		LOGGER.info("Using Partitiioner:{}", this.getClass());
	}

	public void configure(Map<String, ?> configs) {
		boundingBoxMap.put("SouthAmerica", new BoundingBox(-109.4749, -59.4505, -26.3325, 13.3903));
		boundingBoxMap.put("Europe", new BoundingBox(-31.2660, 27.6363, 39.8693, 81.0088));
		boundingBoxMap.put("Asia", new BoundingBox(-25.3587, -46.9005, 63.5254, 37.5671));
		boundingBoxMap.put("Africa", new BoundingBox(-25.3587, -46.9005, 63.5254, 37.5671));
		boundingBoxMap.put("NorthAmerica", new BoundingBox(-167.2764, 5.4995, -52.2330, 83.1621));
		boundingBoxMap.put("Australia", new BoundingBox(105.3770, -53.0587, -175.2925, -6.0694));

		partitionMap = new HashMap<String, Integer>();
		for (Map.Entry<String, ?> entry : configs.entrySet()) {
			if (entry.getKey().startsWith("partitions.")) {
				String keyName = entry.getKey();
				String value = (String) entry.getValue();
				int paritionId = Integer.parseInt(keyName.substring(11));
				partitionMap.put(value, paritionId);
			}
		}
		LOGGER.info("Partitions map:{}", partitionMap);
	}

	public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
		String partitionName = getPartitionName((FlightData) value);
		int result = -1;
		if (partitionName != null) {
			if (partitionMap.containsKey(partitionName)) {
				// If the country is mapped to particular partition return it
				result = partitionMap.get(partitionName);
			}
		}
		if (result < 0) {
			result = partitionMap.get("default");
		}
		return result;
	}

	private String getPartitionName(final FlightData data) {
		String result = null;
		Optional<Entry<String, BoundingBox>> oCandidate = boundingBoxMap.entrySet().stream()
				.filter(p -> p.getValue().isIncluded(data.getLat(), data.getLong())).findFirst();
		if (oCandidate.isPresent()) {
			result = oCandidate.get().getKey();
		}
		return result;
	}

	public void close() {
	}

	/**
	 * Bounding box class
	 * 
	 * @author ramon
	 *
	 */
	class BoundingBox {

		private Double minLat;
		private Double minLng;
		private Double maxLat;
		private Double maxLng;

		public BoundingBox(Double minLng, Double maxLng, Double minLat, Double maxLat) {
			super();
			this.minLat = minLat;
			this.minLng = minLng;
			this.maxLat = maxLat;
			this.maxLng = maxLng;
		}

		public Double getMinLat() {
			return minLat;
		}

		public void setMinLat(Double minLat) {
			this.minLat = minLat;
		}

		public Double getMinLng() {
			return minLng;
		}

		public void setMinLng(Double minLng) {
			this.minLng = minLng;
		}

		public Double getMaxLat() {
			return maxLat;
		}

		public void setMaxLat(Double maxLat) {
			this.maxLat = maxLat;
		}

		public Double getMaxLng() {
			return maxLng;
		}

		public void setMaxLng(Double maxLng) {
			this.maxLng = maxLng;
		}

		/**
		 * Checks if bounding box includes point
		 * 
		 * @param latitude
		 * @param longitude
		 * @return
		 */
		private boolean isIncluded(Double latitude, Double longitude) {
			boolean result = false;
			if (latitude > minLat && latitude < maxLat && longitude > minLng && longitude < maxLng) {
				result = true;
			}
			return result;
		}
	}
}