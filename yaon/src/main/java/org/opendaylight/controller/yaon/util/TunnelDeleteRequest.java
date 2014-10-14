package org.opendaylight.controller.yaon.util;

import java.io.IOException;

import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TunnelDeleteRequest extends AgentRequest {
	
	private static final Logger logger = LoggerFactory
            .getLogger(TunnelDeleteRequest.class);
	
	private int vni;
	
	public TunnelDeleteRequest(String sliceId){
		vni = Integer.parseInt(sliceId);
	}

	@Override
	public boolean send(String agentUri) {
		// TODO Auto-generated method stub
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		try {
			String postAgentUri = "http://" + agentUri + "/overlay_networks/" + (vni & 0x00ffffff);
			HttpDelete request = new HttpDelete(postAgentUri);	
			CloseableHttpResponse httpResponse = httpClient.execute(request);
			StatusLine status = httpResponse.getStatusLine();
		    logger.info("Tunnel delete Response status code : {}", status.getStatusCode());
		    httpResponse.close();
		    httpClient.close();
		}
		catch(Exception ex) {
			logger.error("Exception while sending delete request to Agent !");
			return false;
		}
		return true;
	}
}
