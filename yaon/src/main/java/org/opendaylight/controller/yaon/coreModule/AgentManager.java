package org.opendaylight.controller.yaon.coreModule;

import java.util.HashMap;

import org.opendaylight.controller.yaon.util.TunnelAddRequest;
import org.opendaylight.controller.yaon.util.TunnelDeleteRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AgentManager implements InternalModule{

	/* Internal Globals */
	private static final Logger logger = LoggerFactory
            .getLogger(AgentManager.class);

	/* Initializing Functions */
	@Override
	public boolean initiateModuleDependency(HashMap<String, InternalModule> modules){
		return true;
	}
	@Override
	public boolean initiateServicesDepedency(ServiceHolder services) {
		/* No ODL service is required */
		return true;
	}

	public boolean addTunnelToNetwork(String sliceId, String broadcast_address, String agentUri) {
		/* Create TunnelAdd request */
		logger.info("Debug: " + "Creating new tunnel add request for sliceId: {} and Broadcast_address: {}", sliceId, broadcast_address);
		TunnelAddRequest tunnelAddRequest = new TunnelAddRequest(sliceId, broadcast_address);
		logger.info("Debug: " + "Sending tunnel add request to Ageent URi: {}", agentUri);
		if(!tunnelAddRequest.send(agentUri)){
			logger.error("Tunnel addition request failed to be sent !");
			return false;
		}
		return true;
	}
	
	public boolean deleteTunnelFromNetwork(String sliceId, String agentUri) {
		/* Create TunnelAdd request */
		logger.info("Debug: " + "Creating new tunnel delete request for sliceId: {}", sliceId);
		TunnelDeleteRequest tunneDeleteRequest = new TunnelDeleteRequest(sliceId);
		logger.info("Debug: " + "Sending tunnel delete request to Ageent URi: {}", agentUri);
		if(!tunneDeleteRequest.send(agentUri)){
			logger.error("Tunnel Deletion request failed to be sent !");
			return false;
		}
		return true;
	}
}
