package org.opendaylight.controller.yaon.coreModule;

import java.util.ArrayList;
import java.util.HashMap;

import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.core.NodeConnector;
import org.opendaylight.controller.yaon.storage.SliceDB;
import org.opendaylight.controller.yaon.storage.StorageLock;
import org.opendaylight.controller.yaon.storage.TopoDB;
import org.opendaylight.controller.yaon.util.OdlUtil;
import org.opendaylight.controller.yaon.util.YaonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SliceManager implements InternalModule{

	/* Internal Globals */
	private static final Logger logger = LoggerFactory
            .getLogger(SliceManager.class);
	private TopoDBManager topoDbManager = null;
	private SliceDBManager sliceDbManager = null;
	private FlowManager flowManager = null;
	private AgentManager agentManager = null;

	/* Initializing function */
	@Override
	public boolean initiateServicesDepedency(ServiceHolder services) {
		/* No ODL service is required by slice Manager */
		return true;
	}

	@Override
	public boolean initiateModuleDependency(HashMap<String, InternalModule> modules){
		
		/* check if Overlay network manager is available */
		agentManager = (AgentManager)modules.get(ModuleName.AgentManager.toString());
		sliceDbManager = (SliceDBManager) modules.get(ModuleName.SliceDBManager.toString());
		topoDbManager = (TopoDBManager) modules.get(ModuleName.TopoDBManager.toString());
		flowManager = (FlowManager) modules.get(ModuleName.FlowManager.toString());
		
		/* Check if all dependency resolved */ 
		if(topoDbManager == null || flowManager == null || sliceDbManager == null || agentManager == null) {
			return false;
		}
		
		return true;
	}

	
	/* Slice Operations */

	/* Add Slice */
	public boolean addSlice(String sliceId, String desc){
		
		logger.info("Processing Slice Addition Request for SliceId: {}", sliceId);
		
		boolean ret = false;
		
		/* Lock storage lock */
		StorageLock.acquireLock();
		
		ret = _addSlice(sliceId, desc);
		
		/* release Storage lock */
		StorageLock.releaseLock();
		
		return ret;
	}
	
	private boolean _addSlice(String sliceId, String desc){
		
		logger.info("Debug : " + "Checking if slice is already added in sliceDB");
		/* Check if slice is already added in slice Db */
		ArrayList<Object> sliceDetails = sliceDbManager.getSlice(sliceId);
		if(sliceDetails != null){
			logger.error("Slice if Id : {} is already added !", sliceId);
			return false;
		}
		
		logger.info("Debug : " + "Adding slice ti slice DB");
		/* Add slice to sliceDb */
		if(!sliceDbManager.addSlice(sliceId, desc)){
			logger.error("Slice could not be added to slice Db for sliceId : {}", sliceId);
			return false;
		}
		
		return true;
	}

	/* Delete Slice */
	public boolean deleteSlice(String sliceId) {
		
		logger.info("Processing Slice Deletion Request for SliceId: {}", sliceId);
	
		boolean ret = false;
		
		/* Lock storage lock */
		StorageLock.acquireLock();
		
		ret = _deleteSlice(sliceId);
		
		/* release Storage lock */
		StorageLock.releaseLock();
			
		return ret;
	}
	
	
	private boolean _deleteSlice(String sliceId) {
		
		logger.info("Debug : " + "checking if slcie is alreay added in slice DB");
		/* Check if slice is already added to slice Db */
		ArrayList<Object> sliceDetails = sliceDbManager.getSlice(sliceId);
		if(sliceDetails == null){
			logger.error("Slice if Id : {} is not added !", sliceId);
			return false;
		}
		
		logger.info("Debug : " + "Getting ports for slice DB");
		/* Get ports for a slice */
		ArrayList<ArrayList<Object>> portsList = sliceDbManager.getPortsForSlice(sliceId);
		if(portsList == null){
			logger.info("No port is configured for slice Id: {}", sliceId);
		}
		else {
			
			logger.info("Debug : " + "For each port configured for the slice deleting mac and port");
			/* For each port */
			for(ArrayList<Object> portDetails : portsList){
				
				/* Extract port Id */
				String portId = sliceDbManager.extractPortId(portDetails);
				
				/* delete port from slice Db */
				if(!this._deletePort(sliceId, portId)){
					logger.error("Port deletion failed for SliceId: {} and portId: {}", sliceId, portId);
				}
			}
		}
		
		logger.info("Debug : " + "Deleting slice from slcie DB");
		/* Delete slice from sliceDb */
		if(!sliceDbManager.deleteSlice(sliceId)){
			logger.error("Slice could not be deleted from slice DB for slice Id: {}", sliceId);
			return false;
		}
		
		return true;
	}
	
	
	/* Add port to a specific switch for a slice */
	public boolean addPort(String SliceId, String portId, String dataPathId, String portName, String vlan, String desc){
		
		logger.info("Processing Port Addition Request for SliceId: {}, PortId: {} ", SliceId, portId);;
		
		boolean ret = false;
		
		/* Lock storage lock */
		StorageLock.acquireLock();
		
		ret = _addPort(SliceId, portId, dataPathId, portName, vlan, desc);
		
		/* release Storage lock */
		StorageLock.releaseLock();
		
		return ret;
	}
	
	
	private boolean _addPort(String sliceId, String portId, String dataPathId, String portName, String vlan, String desc){
		
		logger.info("Debug : " + "Checking id slice Exist");
		/* Check if slice exist */
		ArrayList<Object> sliceDetails = sliceDbManager.getSlice(sliceId);
		if(sliceDetails == null){
			logger.error("No Slice is created sliceId: {}", sliceId);
			return false;
		}
		
		logger.info("Debug : " + "Checking if port is already addded for same sliceId and portId");
		/* Check if port with same Id is already exist */
		ArrayList<Object> portDetails = sliceDbManager.getPortById(sliceId, portId);
		if(portDetails != null){
			logger.error("A port with in same slice with same sliceId exist, for sliceId: {} and portId: {}", sliceId, portId);
			return false;
		}
		
		logger.info("Debug : " + "Checking if port is already added for same dpId and portName");
		/* Check if port with same name already exist */
		portDetails = sliceDbManager.getPortByName(dataPathId, portName);
		if(portDetails != null){
			logger.error("A port with in same name with same portname exist, for dpId: {} and portName: {}", dataPathId, portName);
			return false;
		}
		
		logger.info("Debug : " + "Checking if port exist in topo DB");
		/* Check if port exist in the topo db 
		 * TODO: currently port should be discovered before configuring the same port */
		ArrayList<Object> topoPortDetails = topoDbManager.getPort(dataPathId, portName);
		if(topoPortDetails == null){
			logger.error("No port is discovered for portName: {} and DpId: {}", portName, dataPathId);
			return false;
		}
		
		logger.info("Debug : " + "Checking if multicast is configured for the slice");
		/* Get multicast address for slice */
		String multicast = sliceDbManager.getMulticastAddress(sliceId);
		if(multicast == null){
			logger.error("Slice configuration is incomplete, Multicast is not set for slice: {}", sliceId);
			return false;
		}
		
		logger.info("Debug : " + "Checking if agent is registered for the Dpid ");
		String agentUri = sliceDbManager.getAgentUri(dataPathId);
		if(agentUri == null){
			logger.error("Configuration is incomplete, No agent is registered for doId: {}", dataPathId);
			return false;
		}
		
		/* Generate vxlan port name */
		String vxlanPortName = YaonUtil.generateVxlanPortName(sliceId);
		logger.info("Debug : " + "Generated vxlan port name: " + vxlanPortName);
		
		logger.info("Debug : " + "Adding port to slice DB ");
		/* Add port to slice DB */
		if(!sliceDbManager.addPort(sliceId, dataPathId, portId, portName, vlan, vxlanPortName, desc)){
			logger.error("port could not be added to sliceDb for portId: {}  sliceId: {}", portId, sliceId);
		}
		
		/* Extract node object from portDetails from topoDb */
		NodeConnector topoNodeConnector = topoDbManager.extractNodeConnector(topoPortDetails);
		String topoPortNo = topoDbManager.extractPortNo(topoPortDetails);
		
		/* Get switch object from nodeConnector */
		Node topoNode = topoDbManager.getNode(dataPathId);
		if(topoNode == null){
			logger.error("Switch could not be found in TopoDb for dpId: {}", dataPathId);
			return false;
		}
		
		logger.info("Debug : " + "Extracted node connector, port no: {} and node Obejct", topoPortNo);
		
		/* Check if vxlan port exist in topo DB */
		logger.info("Debug : " + "Checking if vxlan port exist");
		ArrayList<Object> vxlanPortDetails  = topoDbManager.getPort(dataPathId, vxlanPortName);
		if(vxlanPortDetails == null){
			logger.info("Debug : " + "Vxlan port is not added - going to call agent");
			
			logger.info("Debug : " + "Getting agent uri for the dpId");
			/* Get agent uri */
			agentUri = sliceDbManager.getAgentUri(dataPathId);
			if(agentUri == null){
				logger.error("Agent uri is not set for dataPath: {}", dataPathId);
				return false;
			}
			
			logger.info("Debug : " + "Calling agent to add the vxlan port");
			/* Call the agent to add vxlan port */
			if(!agentManager.addTunnelToNetwork(sliceId, multicast, agentUri)){
				logger.error("Agent call to add tunnel failed for dpId: {} sliceId: {}", dataPathId, sliceId);
				return false;
			}
			
			/* Wait for vxlan port to appear in topo DB */
			/* TODO: hard coded retry count and sleep time */
			int retryCount = 0;
			
			logger.info("Debug : " + "Waiting for vxlan port to appear in topo Db");
			
			/* Release lock for switch event manager */
			StorageLock.releaseLock();
			
			while(true){
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					logger.error("Exception while trying to sleep : {}", e.toString());
				}
				
				/* Acquire lock */
				StorageLock.acquireLock();
				
				/* Try to get the Topo port */
				if((vxlanPortDetails = topoDbManager.getPort(dataPathId, vxlanPortName)) != null || retryCount > 100){
					break;
				}
				
				/* Release the lock */
				StorageLock.releaseLock();
			}
			 
			if(vxlanPortDetails == null){
				logger.error("Vxlan port could not be found after time out !");
				return false;
			}
		}
		
		/* Extract vxlanNodeConn */
		NodeConnector vxlanNodeConn = topoDbManager.extractNodeConnector(vxlanPortDetails);
		/* Extract vxlanPortNo */
		String vxlanPortNo = topoDbManager.extractPortNo(vxlanPortDetails);

		logger.info("Debug : " + "Extracted vxlan port no: {} and nodeConnector", vxlanPortNo);
		
		logger.info("Debug : " + "Getting all port in same slice and same DP");
		/* Get all port in the slice and in same DP */
		ArrayList<ArrayList<Object>> allPorts = sliceDbManager.getAllPortInSliceDp(sliceId, dataPathId);
		
		/* Extract node connector */
		/* generate NodeConnector Array */
		logger.info("Debug : " + "Generating node connector array from all ports details");
		ArrayList<NodeConnector> nodeConnActivePorts = getNodeConnectorArrayFromSlicePortDetails(dataPathId, allPorts);
		
		/* Add vxlan port Flow */
		logger.info("Debug : " + " Setting vxlan flow ");
		if(!flowManager.setAndVerifyVxlanDefaultFlow(dataPathId, topoNode, vxlanPortNo, vxlanNodeConn, nodeConnActivePorts)){
			logger.error("Vxlan port flow could not be modified for vxlan: {} ", vxlanPortName);
			return false;
		}
		
		logger.info("Debug : " + "For each ports in same slice and DP modifying forwarding flow (add current port to all other ports forward flow)");
		if(allPorts != null){
			
			/* Modify MAC flow for all ports */
			for(ArrayList<Object> portAllDetails : allPorts){
				
				String otherPortId = sliceDbManager.extractPortId(portAllDetails);
				String otherPortName = sliceDbManager.extractPortName(portAllDetails);
				logger.info("Debug : " + "Extracted portID: {} and portName: {} from portAllDetails", otherPortId, otherPortName);
				/* Get port from topo Db */
				logger.info("Debug : " + "Getting port details from topoDB");
				ArrayList<Object> otherTopoPortDetails = topoDbManager.getPort(dataPathId, otherPortName);
				/* Check if port is added in topo DB */
				if(otherTopoPortDetails != null){
					logger.info("Debug : " + "Port is added in topo DB");
					String otherPortNo = topoDbManager.extractPortNo(otherTopoPortDetails);
					NodeConnector otherPortNodeConn = topoDbManager.extractNodeConnector(otherTopoPortDetails);
					logger.info("Debug : " + "Logger extracted portNo: {} and nodeConnector from topo port Details", otherPortNo);
					/* Ignore current port */
					logger.info("Debug : " + "otherPortId: " + otherPortId + "Current portId: " + portId);
					if(!otherPortId.equals(portId)){		
						logger.info("Debug : " + "Getting all macs configured under the other port");
						/* Get all MACs to modify forward flow */
						ArrayList<String> allMacs = sliceDbManager.getMacsByPort(sliceId, otherPortId);
						if(allMacs != null){
							logger.info("Debug : " + "For each mac modifying the forwarding flow");
							for(String MAC : allMacs){
								if(!flowManager.setAndVerifyForwardinngFlow(dataPathId, topoNode, otherPortNodeConn, otherPortName, vxlanPortNo, vxlanNodeConn, MAC, nodeConnActivePorts)) {
									logger.warn("Flow could not be configured for: sliceID: {} portId: {} and Mac: {}", sliceId, portId, MAC);
								}
							}
						}
						else{
							logger.info("Debug : " + "No mac is yet configured");
						}
					}
					else {
						logger.info("Debug : " + "Ignoring current port");
					}
				}
				else {
					logger.info("Debug : " + "Port is not added in topo DB");
				}
			}
		}
		else {
			logger.info("Debug : " + "No other port is configured for same slice in same DP");
		}
		
		logger.info("Debug : " + "Setting port state up in sliceDb");
		/* Set port state up in slice DB */
		if(!sliceDbManager.setPortStateUp(sliceId, portId)){
			logger.error("Failue while setting port state up in slice Db for sliceId: {} and portId: {}", sliceId, portId);
			return false;
		}
		
		return true;
	}
	
	/* Delete port form a specific switch for a slice */
	public boolean deletePort(String sliceId, String portId){

		logger.info("Processing Port Deletion Request for SliceId: {}, PortId: {} ", sliceId, portId);;
		
		boolean ret = false;
		
		/* Lock storage lock */
		StorageLock.acquireLock();
		
		ret = _deletePort(sliceId, portId);
		
		/* release Storage lock */
		StorageLock.releaseLock();
		
		return ret;
	}
	
	private boolean _deletePort(String sliceId, String portId){
		
		logger.info("Debug : " + "Checking if port is already addded for same sliceId and portId");
		/* Check if port with same Id is already exist */
		ArrayList<Object> portDetails = sliceDbManager.getPortById(sliceId, portId);
		if(portDetails == null){
			logger.error("No port with exist, for sliceId: {} and portId: {}", sliceId, portId);
			return false;
		}
		
		logger.info("Debug : " + "Extracting portName and dpId");
		String portName = sliceDbManager.extractPortName(portDetails);
		String dataPathId = sliceDbManager.extractDpId(portDetails);
		
		/* Get switch node from topo Db */ 
		Node switchNode = topoDbManager.getNode(dataPathId);
		
		
		/* Get all macs configured for the port */
		ArrayList<String> macs = sliceDbManager.getMacsByPort(sliceId, portId);
		if(macs == null){
			logger.info("No macs is configured !");
		}
		else {
			/* For each mac delete the mac from the port */
			for(String mac : macs){
				if(!this._deleteMac(sliceId, portId, mac)){
					logger.warn("Automated Mac deletion failed for SliceID: {}, portId: {} and MAC : {}", sliceId, portId, mac);
				}
			}
		}
		
		/* Delete port from slice DB */
		logger.info("Debug : " + "Deleting port from slice DB");
		if(!sliceDbManager.deletePort(sliceId, portId)){
			logger.error("Port deletion failed from Slice Db for SliceId: {} and portId: {}", sliceId, portId);
			return false;
		}
		
		/* Generate vxlan port name */
		String vxlanPortName = YaonUtil.generateVxlanPortName(sliceId);
		logger.info("Debug : " + "Generated vxlan port name: " + vxlanPortName);
		
		/* Get VXlan port from topo DB */
		ArrayList<Object> vxlanTopoPortDetails = topoDbManager.getPort(dataPathId, vxlanPortName);
		
		NodeConnector vxlanNodeConn = null;
		String vxlanPortNo = null;
	
		
		/* Check if vxlan port is null */
		if(vxlanTopoPortDetails != null){
			
			/* Get vxlan NodeConnector and port no */
			vxlanNodeConn = topoDbManager.extractNodeConnector(vxlanTopoPortDetails);
			vxlanPortNo = topoDbManager.extractPortNo(vxlanTopoPortDetails);
		}
		else {
			logger.warn("Vxlan port could not be found in TopoDb - flow could not be removed");
		}
		
		/* Remove vxlan port and flow if necessary */
		ArrayList<ArrayList<Object>> allPortsDetails = sliceDbManager.getAllPortInSliceDp(sliceId, dataPathId);
		
		/* Check if current port is the last port configured */
		if(allPortsDetails == null || allPortsDetails.size() == 0){		
			
			/* Check if vxlan port is null */
			if(vxlanTopoPortDetails != null){
				/* Remove vxlan port flow */
				if(!flowManager.removeVxlanDefaultFlow(dataPathId, switchNode, vxlanPortNo, vxlanNodeConn)){
					logger.warn("Vxlan Default flow could not be removed");
				}
			}
			else {
				logger.warn("Vxlan port could not be found in TopoDb - flow could not be removed");
			}
			
			/* Call agent to delete the tunnel from switch */  
			/* Get agent URI for the switch */
			String agentUri = sliceDbManager.getAgentUri(dataPathId);
			
			/* Call agent to delete tunnel from switch */
			if(!agentManager.deleteTunnelFromNetwork(sliceId, agentUri)){
				logger.warn("Tunnel could not be deleted from switch");
			}
		}
		else{
			/* For all other port and all other mac change the forwarding flow */
			
			/* generate NodeConnector Array */
			logger.info("Debug : " + "Generating node connector array from all ports details");
			ArrayList<NodeConnector> nodeConnActivePorts = getNodeConnectorArrayFromSlicePortDetails(dataPathId, allPortsDetails);
			
			/* Modify MAC flow for all ports */
			for(ArrayList<Object> portAllDetails : allPortsDetails){
				
				String otherPortId = sliceDbManager.extractPortId(portAllDetails);
				String otherPortName = sliceDbManager.extractPortName(portAllDetails);
				logger.info("Debug : " + "Extracted portID: {} and portName: {} from portAllDetails", otherPortId, otherPortName);
				/* Get port from topo Db */
				logger.info("Debug : " + "Getting port details from topoDB");
				ArrayList<Object> otherTopoPortDetails = topoDbManager.getPort(dataPathId, otherPortName);
				/* Check if port is added in topo DB */
				if(otherTopoPortDetails != null){
					logger.info("Debug : " + "Port is added in topo DB");
					String otherPortNo = topoDbManager.extractPortNo(otherTopoPortDetails);
					NodeConnector otherPortNodeConn = topoDbManager.extractNodeConnector(otherTopoPortDetails);
					logger.info("Debug : " + "Logger extracted portNo: {} and nodeConnector from topo port Details", otherPortNo);
					/* Ignore current port */
					if(!otherPortId.equals(portId)){		
						logger.info("Debug : " + "Getting all macs configured under the other port");
						/* Get all MACs to modify forward flow */
						ArrayList<String> allMacs = sliceDbManager.getMacsByPort(sliceId, otherPortId);
						if(allMacs != null){
							logger.info("Debug : " + "For each mac modifying the forwarding flow");
							for(String MAC : allMacs){
								if(!flowManager.setAndVerifyForwardinngFlow(dataPathId, switchNode, otherPortNodeConn, otherPortNo, vxlanPortNo, vxlanNodeConn, MAC, nodeConnActivePorts)) {
									logger.warn("Flow could not be configured for: sliceID: {} portId: {} and Mac: {}", sliceId, portId, MAC);
								}
							}
						}
						else{
							logger.info("Debug : " + "No mac is yet configured");
						}
					}
					else {
						logger.info("Debug : " + "Ignoring current port");
					}
				}
				else {
					logger.info("Debug : " + "Port is not added in topo DB");
				}
			}
		}
		
		
		
		return true;
	}

	/* Add MAC for a port in slice */
	public boolean addMac(String sliceId, String portId, String MAC){

		logger.info("Processing Mac addition Request for SliceId: {}, PortId: {} and MAC: {}", sliceId, portId, MAC);;

		boolean ret = false;
		
		/* Lock storage lock */
		StorageLock.acquireLock();
				
		ret = this._addMac(sliceId, portId, MAC);

		/* release Storage lock */
		StorageLock.releaseLock();
		
		return ret;
	}
	
	private boolean _addMac(String sliceId, String portId, String MAC){
				
		logger.info("Debug : " + "Checking if mac already exist");
		/* Check if MAC exist */
		if(sliceDbManager.isMacExist(sliceId, portId, MAC)){
			logger.error("MAC: {} already exist to Slice DB !", MAC);
			return false;
		}
		
		logger.info("Debug : " + "Adding mac to slice DB");
		/* Add MAC to slice DB */
		if(!sliceDbManager.addMac(sliceId, portId, MAC)){
			logger.error("MAC: {} could not be aadded to slcie DB !", MAC);
			return false;
		}
		
		logger.info("Debug : " + "getting port information from slice DB");
		/* Get port information from Slice DB */
		ArrayList<Object> slicePortDetails = sliceDbManager.getPortById(sliceId, portId);
		/* Extract dpId */
		String dpId = sliceDbManager.extractDpId(slicePortDetails);
		/* Extract portName */
		String portName = sliceDbManager.extractPortName(slicePortDetails);
		logger.info("Debug : " + "Extracted portname: {} and dpId: {}", portName, dpId);
		
		logger.info("Debug : " + "Getting port details from topo DB");
		/* Get TopoNodeConn from siceDbManager */
		ArrayList<Object> topoNodeConn =  topoDbManager.getPort(dpId, portName);
		if(topoNodeConn == null){
			logger.error("Port: {} in Slice: {} is not active !", portId, sliceId);
			return false;
		}
		/* Extract nodeConnector */
		NodeConnector nodeConn = topoDbManager.extractNodeConnector(topoNodeConn);

		logger.info("Debug : " + "Extracted node connector");
		
		logger.info("Debug : " + "Getting node object for nodeConnector");
		/* Get Node object for dpId */
		Node topoNode = topoDbManager.getNode(dpId);
		if(topoNode == null){
			logger.error("Switch could not be found in TopoDb for dpId: {}", dpId);
			return false;
		}
		
		/* Get VXLAN port */
		String vxlanPortname = sliceDbManager.extractVxlan(slicePortDetails);
		logger.info("Debug : " + "generated VXLAN port name: {}", vxlanPortname);
		logger.info("Debug : " + "Getting vxlan port from topo DB");
		/* get VXLAN topo Node */
		ArrayList<Object> vxlanPortDetails  = topoDbManager.getPort(dpId, vxlanPortname);
		if(vxlanPortDetails == null){
			logger.error("VXLAN port is not active for sliceId: {} ", sliceId, portId);
			return false;
		}
		/* Get vxlan NodeConnector */
		NodeConnector vxlanTopoNodeConn = topoDbManager.extractNodeConnector(vxlanPortDetails);
		/* Get vxlan port No */
		String vxlanPortNo = topoDbManager.extractPortNo(vxlanPortDetails);
		
		logger.info("Debug : " + "Extracted vxlan port nodeConnector and portNo: {}", vxlanPortNo);
		
		logger.info("Debug : " + "Getting all ports in same slice and same DP");
		/* Get list of ports in the same slice and same dpId */
		ArrayList<ArrayList<Object>> allPorts = sliceDbManager.getAllPortInSliceDp(sliceId, dpId);

		logger.info("Debug : " + "Generatinf nodeconnector array from slice Port details");
		/* generate NodeConnector Array */
		ArrayList<NodeConnector> nodeConnActivePorts = getNodeConnectorArrayFromSlicePortDetails(dpId, allPorts);
		
		/* Install flow */
		logger.info("Debug : " + "Installing forwarding flows");
		if(!flowManager.setAndVerifyForwardinngFlow(dpId, topoNode, nodeConn, portName, vxlanPortNo, vxlanTopoNodeConn, MAC, nodeConnActivePorts)) {
			logger.warn("Flow could not be configured for: sliceID: {} portId: {} and Mac: {}", sliceId, portId, MAC);
			return false;
		}
		
		logger.info("Debug : " + "Setting mac state up");
		/* Set mac state up */
		if(!sliceDbManager.setMacStateUp(sliceId, portId, MAC)){
			logger.error("Failue while setting MAC state up in slice Db for sliceId: {} and portId: {} and MAC: {}", sliceId, portId, MAC);
			return false;
		}
		
		return true;
	}
	
	/* Delete MAC from a port in slice */
	public boolean deleteMac(String sliceId, String portId, String MAC){
		
		logger.info("Processing Mac addition Request for SliceId: {}, PortId: {} and MAC: {}", sliceId, portId, MAC);;

		boolean ret = false;
		
		/* Lock storage lock */
		StorageLock.acquireLock();
				
		ret = this._deleteMac(sliceId, portId, MAC);

		/* release Storage lock */
		StorageLock.releaseLock();
		
		return ret;
	}
	
	private boolean _deleteMac(String sliceId, String portId, String MAC){
		
		logger.info("Debug : " + "Checking if mac already exist");
		/* Check if MAC exist */
		if(!sliceDbManager.isMacExist(sliceId, portId, MAC)){
			logger.error("MAC: {} doesn't exist in Slice DB !", MAC);
			return false;
		}
		
		logger.info("Debug : " + "getting port information from slice DB");
		/* Get port information from Slice DB */
		ArrayList<Object> slicePortDetails = sliceDbManager.getPortById(sliceId, portId);
		/* Extract dpId */
		String dpId = sliceDbManager.extractDpId(slicePortDetails);
		/* Extract portName */
		String portName = sliceDbManager.extractPortName(slicePortDetails);
		logger.info("Debug : " + "Extracted portname: {} and dpId: {}", portName, dpId);
		
		logger.info("Debug : " + "Getting port details from topo DB");
		/* Get TopoNodeConn from siceDbManager */
		ArrayList<Object> topoNodeConn =  topoDbManager.getPort(dpId, portName);
		if(topoNodeConn == null){
			logger.error("Port: {} in Slice: {} is not active !", portId, sliceId);
			return false;
		}
		/* Extract nodeConnector */
		NodeConnector nodeConn = topoDbManager.extractNodeConnector(topoNodeConn);
		logger.info("Debug : " + "Extracted node connector");
		
		logger.info("Debug : " + "Getting node object for nodeConnector");
		/* Get Node object for dpId */
		Node topoNode = topoDbManager.getNode(dpId);
		if(topoNode == null){
			logger.error("Switch could not be found in TopoDb for dpId: {}", dpId);
			return false;
		}
				
		/* uninstall flow */
		logger.info("Debug : " + "Uninstalling forwarding flows");
		if(!flowManager.removeForwardinngFlow(dpId, topoNode, nodeConn, portName, MAC)) {
			logger.warn("Flow could not be deleted for: sliceID: {} portId: {} and Mac: {}", sliceId, portId, MAC);
			return false;
		}
		
		/* Delete MAC to slice DB */
		logger.info("Debug : " + "Deleting mac from slice DB");
		if(!sliceDbManager.deleteMac(sliceId, portId, MAC)){
			logger.error("MAC: {} could not be aadded to slcie DB !", MAC);
			return false;
		}
		
		return true;
	}
	
	/* Add Agent to a specific switch */
	public boolean addAgent(String dataPathId, String agentUri){

		logger.info("New Agent is being registered !");
		
		boolean ret = false;
		
		/* Lock storage lock */
		StorageLock.acquireLock();
	
		ret = this._addAgent(dataPathId, agentUri); 
				
		/* release Storage lock */
		StorageLock.releaseLock();

		return ret;
	}
	
	private boolean _addAgent(String dataPathId, String agentUri){
		
		/* Add agent to slice DB */
		logger.info("Debug : " + "Adding agent to slice DB");
		if(!sliceDbManager.addAgent(dataPathId, agentUri)){
			logger.error("Agent addition failed in Slice DB for DP: {} Agrnt Uri: {}", dataPathId, agentUri);
			return false;
		}
		
		return true;
	}	

	/* Add Multicast for a slice */
	public boolean addMulticast(String sliceId, String multicast){
		
		logger.info("New Multicast is being registered !");
		
		boolean ret = false;
		
		/* Lock storage lock */
		StorageLock.acquireLock();
		
		ret = _addMulticast(sliceId, multicast);
		
		/* release Storage lock */
		StorageLock.releaseLock();

		return ret;
	}
	
	private boolean _addMulticast(String sliceId, String multicast){
		
		/* Add multicast to slice DB */
		logger.info("Debug : " + "Adding multicast to slice DB");
		if(!sliceDbManager.addMulticast(sliceId, multicast)){
			logger.error("Multicast addition failed in Slice DB for Sliec: {} Multicast: {}", sliceId, multicast);
			return false;
		}
		
		return true;
	}

	/* Internal function for slice manager */
	
	private ArrayList<NodeConnector> getNodeConnectorArrayFromSlicePortDetails(String dpId, ArrayList<ArrayList<Object>> allPortsDetails){
		
		/* generate NodeConnector Array */
		ArrayList<NodeConnector> nodeConnActivePorts = new ArrayList<NodeConnector>();
		for(ArrayList<Object> portDetails : allPortsDetails){		
			/* Extract Port name */
			String portName = sliceDbManager.extractPortName(portDetails);
			/* Get portDetails from Topo DB */
			ArrayList<Object> topoPortDetails = topoDbManager.getPort(dpId, portName);
			if(topoPortDetails != null){
				NodeConnector portNodeConnector = topoDbManager.extractNodeConnector(topoPortDetails);
				nodeConnActivePorts.add(portNodeConnector);
			}	
		}
		
		return nodeConnActivePorts;
	}

}