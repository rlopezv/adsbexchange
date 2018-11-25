/**
 * 
 */
package net.upmt.moit.distributed.adsbexchange;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import net.upmt.moit.distributed.adsbexchange.model.FlightDataList;

/**
 * @author ramon
 *
 */
public class HttpProducer extends SimpleProducer {

	private OkHttpClient client = null;
	private String url;
	private long pollms;
	ObjectMapper mapper = new ObjectMapper();

	private final Logger LOGGER = LoggerFactory.getLogger(HttpProducer.class);

	public HttpProducer(String consumerId, Properties config) {
		super(consumerId, config);
		this.url = getConfig().getProperty("SOURCE_URL");
		this.pollms = Long.valueOf(getConfig().getProperty("POLL_TIME", "100"));
		this.client = new OkHttpClient();

	}

	protected OkHttpClient getClient() {
		return client;
	}

	protected String getUrl() {
		return url;
	}

	protected void setClient(OkHttpClient client) {
		this.client = client;
	}

	@Override
	public void run() {
		while (!isClosed()) {
			Request request = buildRequest();
			getClient().newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Request request, IOException e) {
					LOGGER.error("Error executing call {}", e.getMessage());
				}

				@Override
				public void onResponse(Response response) throws IOException {
					FlightDataList dataList = mapper.readValue(response.body().string(), FlightDataList.class);
					sendRecords(dataList);
					logMetrics();
				}
			});
			try {
				Thread.sleep(pollms);
			} catch (InterruptedException e) {
				LOGGER.error("Error waiting", e);
			}

		}
	}

	private Request buildRequest() {
		Request request = new Request.Builder().url(getUrl()).build();
		return request;
	}

}