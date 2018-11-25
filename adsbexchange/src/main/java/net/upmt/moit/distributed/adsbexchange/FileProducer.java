/**
 * 
 */
package net.upmt.moit.distributed.adsbexchange;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.upmt.moit.distributed.adsbexchange.model.FlightDataList;

/**
 * @author ramon
 *
 */
public class FileProducer extends SimpleProducer {

	private WatchService watcher = null;
	private String path;

	private final Logger LOGGER = LoggerFactory.getLogger(FileProducer.class);

	public FileProducer(String consumerId, Properties config) {
		super(consumerId, config);
		this.path = getConfig().getProperty("FILE_PATH");

	}

	protected WatchService getWatchService() throws IOException {
		if (watcher == null) {
			Path dirPath = Paths.get("/", path);
			watcher = dirPath.getFileSystem().newWatchService();
			dirPath.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
		}
		return watcher;

	}

	protected String getPath() {
		return path;
	}

	@Override
	public void run() {
		ObjectMapper mapper = new ObjectMapper();
		while (!isClosed()) {

			WatchKey key = null;

			try {
				key = getWatchService().take();
			} catch (InterruptedException | IOException e) {
				LOGGER.error("Error adquiring lock", e);
			}
			if (key != null) {
				for (WatchEvent<?> event : key.pollEvents()) {
					WatchEvent.Kind<?> kind = event.kind();

					if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
						LOGGER.info("New file");
						WatchEvent<Path> ev = (WatchEvent<Path>) event;
						Path file = ev.context();
						LOGGER.info("Found file:{}", file.getFileName());
						try {
							Path filePath = Paths.get(path, file.getFileName().toString());
							FlightDataList dataList = mapper.readValue(filePath.toFile(), FlightDataList.class);
							sendRecords(dataList);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					boolean valid = key.reset();
					if (!valid) {
						break;
					}

				}
			}
		}
	}
}