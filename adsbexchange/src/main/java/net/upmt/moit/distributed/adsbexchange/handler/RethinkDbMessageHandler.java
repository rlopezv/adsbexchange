/**
 * 
 */
package net.upmt.moit.distributed.adsbexchange.handler;

import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;

import net.upmt.moit.distributed.adsbexchange.ConsumerApp;
import net.upmt.moit.distributed.adsbexchange.SimpleConsumer;
import net.upmt.moit.distributed.adsbexchange.model.FlightData;

/**
 * Logs message info
 * 
 * @author ramon
 *
 */
public class RethinkDbMessageHandler extends ConsumerMessageHandler {

	final static Logger LOGGER = LoggerFactory.getLogger(ConsumerApp.class);

	private final static String DB_HOST = "db.host";
	private final static String DB_DB_NAME = "db.dbName";
	private final static String DB_TABLE_NAME = "db.tableName";

	private final static ObjectMapper mapper = new ObjectMapper();

	private Connection connection;
	private String tableName;

	public RethinkDbMessageHandler(SimpleConsumer consumer) {
		super(consumer);
		connection = buildConnection(consumer.getConfig());
	}

	protected Connection buildConnection(Properties config) {
		String hostName = config.getProperty(DB_HOST, "localhost");
		String dbName = config.getProperty(DB_DB_NAME, "dbName");
		tableName = config.getProperty(DB_TABLE_NAME, "tableName");
		LOGGER.info("Connecting to {}: ({}.{})", hostName, dbName, tableName);
		return RethinkDB.r.connection().hostname(hostName).db(dbName).connect();
	}

	protected Connection getConnection() {
		return connection;
	}

	protected void setConnection(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void handleMessage(ConsumerRecord<String, FlightData> record) {
		try {
			RethinkDB.r.table(tableName).insert(RethinkDB.r.json(mapper.writeValueAsString(record.value())))
					.run(getConnection());
		} catch (JsonProcessingException e) {
			LOGGER.error("Error transforming data:{}", record.value(), e);
		}
	}

}
