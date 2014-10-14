package org.opendaylight.controller.yaon.util;

import java.io.StringWriter;
import java.io.Writer;

import net.sf.json.JSONObject;

import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TunnelAddRequest extends AgentRequest {
	
	private static final Logger logger = LoggerFactory
            .getLogger(TunnelAddRequest.class);

	private String jsonString = null;
	
	public TunnelAddRequest(String sliceId, String address){
		
		logger.info("Debug: " + "Creating json string manually");
		jsonString = YaonUtil.jsonStringGenForTunnelAdd(address, sliceId);
		logger.info("Debug: " + "Json string: {}", jsonString);
	}

	@Override
	public boolean send(String agentUri) {
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		try {
			String postAgentUri = "http://" + agentUri + "/overlay_networks";
		    HttpPost request = new HttpPost(postAgentUri);
		    StringEntity params = new StringEntity(jsonString);
		    request.addHeader("content-type", "application/x-www-form-urlencoded");
		    request.setEntity(params);
		    logger.info("Sending tunnel add request to agent Uri: " + postAgentUri);
		    CloseableHttpResponse httpResponse = httpClient.execute(request);
		    StatusLine status = httpResponse.getStatusLine();
		    logger.info("Tunnel add Response status code : {}", status.getStatusCode());
		    httpResponse.close();
			httpClient.close();
		} 
		catch (Exception ex) {
		    logger.error("Exception while sending request to Agent : {}!", ex.toString());
			return false;
		}
		return true;
	}
}
