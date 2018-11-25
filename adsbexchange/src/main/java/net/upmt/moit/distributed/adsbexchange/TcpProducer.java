package net.upmt.moit.distributed.adsbexchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.upmt.moit.distributed.adsbexchange.model.FlightDataList;

public class TcpProducer extends SimpleProducer {

	private final Logger LOGGER = LoggerFactory.getLogger(TcpProducer.class);

	private Socket socket;
	private String address;
	private int port;
	ObjectMapper mapper = new ObjectMapper();

	public TcpProducer(String consumerId, Properties config) {
		super(consumerId, config);
		this.address = getConfig().getProperty("TCP_SOURCE_ADDRESS");
		this.port = Integer.valueOf(getConfig().getProperty("TCP_SOURCE_PORT", "32005"));

	}

	@Override
	public void run() {
		try {
			openSocket();
			LOGGER.info("Conectado a {} {}", socket.getInetAddress(), socket.getPort());
			while (!socket.isClosed()) {
				BufferedReader buff = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String message = buff.readLine();
				sendRecords(mapper.readValue(message, FlightDataList.class));
			}
		} catch (IOException e) {
			LOGGER.error("Error TCP", e);
		}

	}

	protected void openSocket() throws UnknownHostException, IOException {
		socket = new Socket(InetAddress.getByName(address), port);
	}

	protected void closeSocket() throws IOException {
		if (!socket.isClosed()) {
			socket.close();
		}
	}

}
