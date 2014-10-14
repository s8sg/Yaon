package org.opendaylight.controller.yaon.util;

import net.sf.json.JSONObject;

import org.apache.http.client.ResponseHandler;

public abstract class AgentRequest {
	
	/* send the JSON request in the specified agent URI */
	public abstract boolean send(String agentUri);

}
