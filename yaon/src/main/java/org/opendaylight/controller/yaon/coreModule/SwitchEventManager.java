package org.opendaylight.controller.yaon.coreModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.core.NodeConnector;
import org.opendaylight.controller.sal.core.Property;
import org.opendaylight.controller.sal.core.UpdateType;
import org.opendaylight.controller.sal.packet.RawPacket;
import org.opendaylight.controller.switchmanager.ISwitchManager;
import org.opendaylight.controller.yaon.storage.StorageLock;
import org.opendaylight.controller.yaon.util.OdlUtil;
import org.opendaylight.controller.yaon.util.YaonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwitchEventManager implements InternalModule{

	/* Internal Globals */
	private static final Logger logger = LoggerFactory
            .getLogger(SwitchEventManager.class);
	private TopoDBManager topoDbManager = null;
	private SliceDBManager sliceDbManager = null;
	private FlowManager flowManager = null;
	
	private ISwitchManager switchManager = null;
 
	/* Initializing Function */
	@Override
	public boolean initiateServicesDepedency(ServiceHolder services) {
		/* NO ODL Service is required by SwitchEventManager */
		ISwitchManager switchManager = services.getSwitchManager();
		if(switchManager == null){
			logger.error("Switch-Manager is not initialized");
			return false;
		}
		this.switchManager = switchManager;
		return true;
 	}
	
	@Override
	public boolean initiateModuleDependency(HashMap<String, InternalModule> modules){
		
		/* Get required module */
		sliceDbManager = (SliceDBManager) modules.get(ModuleName.SliceDBManager.toString());
		topoDbManager = (TopoDBManager) modules.get(ModuleName.TopoDBManager.toString());
		flowManager = (FlowManager) modules.get(ModuleName.FlowManager.toString());
		
		/* Check if all dependency resolved */ 
		if(topoDbManager == null || flowManager == null || sliceDbManager == null) {
			return false;
		}
		
		return true;
	}

	/* Feature Functions */
	public boolean switchChanged(Node node, UpdateType type, Map<String, Property> propMap) {

		/* Extract switch information */
		/* Extract dpID */
		String dpId = OdlUtil.getDpIdFromNode(node);
		if(dpId == null){
			logger.error("Switch dpId could not be extracted !");
			return false;
		}
		
		/* Check type of switch notification */
		switch (type) {
        	case ADDED:
        		if(!this.switchAdded(node, dpId)){
        			logger.error("Switch addition failed !");
        			return false;
        		}
        		break;

        	case CHANGED:
	            if(!this.switchModified(node, dpId)){
	            	logger.error("Switch modification failed !");
	            	return false;
	            }
	            break;

        	case REMOVED:
	        	if(!this.switchDeleted(node, dpId)){
	        		logger.error("Switch removeal failed !");
	        		return false;
	        	}
	        	break;

        	default:
        		logger.error("Unknown Type OpenFlow Switch Notification has reached to VNM!");
		}
		
		return true;
	}

	public boolean portChanged(NodeConnector nodeConnector, UpdateType type, Map<String, Property> propMap){

		/* Extract port information */
		/* Extract port Name */
		String portName = OdlUtil.getPortName(propMap);
		if(portName == null){
			logger.error("Port Name could not be extracted !");
			return false;
		}
		
		/* Extract port No */
		String portNo = OdlUtil.getPortNo(nodeConnector);
		if(portNo == null){
			logger.error("Port No could not be extracted !");
			return false;
		}
		
		/* Extract Node from NodeConnector */
		Node node = OdlUtil.getNodeFromPort(nodeConnector);
		if(node == null) {
			logger.error("Node could not be extracted from nodeConnector !");
			return false;
		}
		
		/* Extract dpId from Node */
		String dpId = OdlUtil.getDpIdFromNode(node);
		if(dpId == null){
			logger.error("Switch dpId could not be extracted !");
			return false;
		}
		
		/* check type of port notification */
        switch (type) {
	        case ADDED:
	        	if(!this.portAdded(nodeConnector, portName, portNo, dpId, node)){
	        		logger.error("NodeConnector addition failed !");
	        		return false;
	        	}
	        	break;

	        case CHANGED:
	        	if(!this.portModified(nodeConnector, portName, portNo, dpId, node)){
	        		logger.error("NodeConnector modification failed !");
	        		return false;
	        	}
	        	break;

	        case REMOVED:
	        	if(!this.portDeleted(nodeConnector, portName, portNo, dpId, node)){
	        		logger.error("NodeConnector removeal failed");
	        		return false;
	        	}
	        	break;

	        default:
	            logger.error("Unknown NodeConnector notification received");
	            return false;
        }
        
        return true;
	}


	/* Internal Function */
	
	/* Switch changed function */

	private boolean switchAdded(Node node, String dpId) {

		logger.info("Debug : " +"New OpenFlow Switch Added notification is being processed for dpId: {}", dpId);
		
		boolean ret = false;
		
		/* Lock storage lock */
		StorageLock.acquireLock();
		
		ret = _switchAdded(node, dpId);
		
		/* Release storage lock */
		StorageLock.releaseLock();
		
		return ret;
	}
	
	private boolean _switchAdded(Node node, String dpId) {
		
		/* Check topo DB to get switch */
		logger.info("Debug : " +"Checking if node is alredy added to topo DB !");
		Node topoNode = topoDbManager.getNode(dpId);
        
		/* Check if not null */
		if(topoNode != null){
			logger.info("Switch Node is already added in topo Db - Updating switch state in topo DB!");
		}
		
		logger.info("Debug : " +"Adding switch to Topo Db !");
		
		/* Add switch to the topo db */
		if(!topoDbManager.addSwitch(dpId, node, true)){
			logger.error("Switch addition to topo db failed!");
			return false;
		}
		
		logger.info("Debug : " +"Setting default flow to Topo Db !");
		
		/* Set default flow for switch */
		if(!flowManager.setAndVerifySwitchDefaultFlow(dpId, node)){
			logger.error("Switch default flow addition failed!");
			return false;
		}
		
		/* All port and MAC specific flow must be added to ports up events */ 
		
		logger.info("Debug : " +"Getting all node Connector from node !");
		
		/* Get all ports for switch to add in topoDB*/
		Set<NodeConnector> allNodeConnectors = switchManager.getNodeConnectors(node);
		
		/* check if no node is attached */
		if(allNodeConnectors == null){
			logger.info("No node connector is attached to node: {}", node);
		}
		else {
			logger.info("Debug : " +"Adding all node Connector to topo Db !");
			for(NodeConnector nodeConnector : allNodeConnectors){
				
				if(switchManager.isSpecial(nodeConnector)){
	        		logger.info("Ignoreing special node connector: {}", nodeConnector);
	        		continue;
	            }
				
				logger.info("Debug : " +"Getting port no from node Connector !");
				String portNo = OdlUtil.getPortNo(nodeConnector);
				if(portNo == null){
					logger.error("Port no could not be resolved for dpId: {} and nodeconnector: {}", dpId, nodeConnector);
					continue;
				}
				
				logger.info("Debug : " +"Gettinh property Map from node Connector !");
				Map<String, Property> portPropMap = switchManager.getNodeConnectorProps(nodeConnector);
				if(portPropMap == null){
					logger.error("Node Connector property map could not be extracted for dpId: {} and nodeConnector: {}", dpId, nodeConnector);
					continue;
				}
				
				logger.info("Debug : " +"Getting port Name from node Connector property Map !");
				String portName = OdlUtil.getPortName(portPropMap);
				if(portName == null){
					logger.error("Port name could not be resolved for dpId: {} and nodeconnector: {}", dpId, nodeConnector);
					continue;
				}
				
				logger.info("Debug : " +"Adding port to topo DB which is not already added !");
				if(topoDbManager.getPort(dpId, portName) == null) {
					/* Perform port add to topo Db*/
					logger.info("New port is found in Switch : {}", nodeConnector);
					if(!this._portAdded(nodeConnector, portName, portNo, dpId, node)){
						logger.info("Port is not added from switch event for dpId: {}, portName: {}", dpId, portName);
					}
				}
				else {
					logger.info("Port is already added while the switch was inactive: {}", nodeConnector);
					/* Add port specific default flows */
					 
					logger.info("Debug : " +"Adding port Default Flow as the switch was inactive in time of port appeared!");
					if(!flowManager.setAndVerifyPortDefaultFlow(dpId, node, portNo, null, nodeConnector)){
						logger.error("Port default could not be successfully added !");
						return false;
					}
				}
				
			}
		}
		
		return true;
	}

	private boolean switchModified(Node node, String dpId){

		logger.info("Debug : " +"New OpenFlow Switch Modification notification is being processed!");

		boolean ret = false;
		
		/* Lock storage lock */
		StorageLock.acquireLock();
		
		ret = _switchModified(node, dpId);
		
		/* Release storage lock */
		StorageLock.releaseLock();

		return ret;
	}
	
	private boolean _switchModified(Node node, String dpId){
		
		logger.info("Debug : " +"Getting switch from topoDB !");
		/* Check topo DB to get switch */
		Node topoNode = topoDbManager.getNode(dpId);
        
		logger.info("Debug : " +"Checking if Switch is already attched in TopoDB !");
		/* Check if not null */
		if(topoNode != null){
			logger.info("Debug : " +"Switch is attached - updating node object!");
			/* Updating the node object by adding for same dpId again */
			if(!topoDbManager.addSwitch(dpId, node, true)){
				logger.error("Update of switch failed !");
				return false;
			}
		}
		else{
			logger.info("Debug : " +"Switch is not attached - Performing new Switch Addition!");
			this.switchAdded(node, dpId);
		}
		
		return true;
	}

	private boolean switchDeleted(Node node, String dpId) {

		logger.info("Debug : " +"New OpenFlow Switch Deleted Info has reached to VNM!");

		boolean ret = false;
		
		/* Lock storage lock */
		StorageLock.acquireLock();
		
		ret = _switchDeleted(node, dpId);
		
		/* Release storage lock */
		StorageLock.releaseLock();
		
		return ret;
	}

	private boolean _switchDeleted(Node node, String dpId) {
		
		/* Check topo DB to get switch */
		logger.info("Debug : " +"Checking if Switch of dpId: " + dpId + " is added to Topo DB!");
		Node topoNode = topoDbManager.getNode(dpId);
        
		/* Check if not null */
		if(topoNode == null){
			logger.warn("Node is not present in Topo Db - Duplicate node delete notification for switch: {}", dpId);
			return false;
		}
		
		/* Get ports for the switch */
		logger.info("Debug : " +"Getting all ports for the switch from topoDB!");
		ArrayList<String> ports = topoDbManager.getPortsNameByDp(dpId);
		if(ports == null){
			logger.info("No ports in topo DB for the Switch: {}", dpId);
			return true;
		}
		
		/* For each ports */
		logger.info("Debug : " +"Deleting each port from Topo DB and checking if port is attached To Slice DB!");
		for(String portName : ports){
			
			/* Delete port from topo Db */
			logger.info("Debug : " +"Deleting port from Topo DB");
			if(!topoDbManager.deletePort(dpId, portName)){
				logger.warn("Port deletion from topo db failed for dpId: {} portName: {}", dpId, portName);
				//continue;
			}
			
			logger.info("Debug : " +"Checking if port exist in Slice DB");
			/* Get port from slice Db */
			ArrayList<Object> portDetails = sliceDbManager.getPortByName(dpId, portName);
			
			/* Check if portDetails is null */
			if(portDetails == null){
				logger.info("No slice reference is found for dpId: {} portName: {}", dpId, portName);
				continue;
			}
			
			/* Extract sliceId and portId from port Details */
			logger.info("Debug : " +"Extracting sliceId and portID from portDetails from SliceDB");
			String sliceId = sliceDbManager.extractSliceId(portDetails);
			String portId = sliceDbManager.extractPortId(portDetails);
				
			/* Check if null */
			logger.info("Debug : " +"Checking if port id and slice Id is NULL");
			if(sliceId == null || portId == null){
				logger.warn("Corrupted information found in SliceDb for dpId: {} portName: {}", dpId, portName);
			}
			else {
				/* Get all MACs for the port */
				logger.info("Debug : " +"Getting all macs in slice DB for the port in slice DB");
				ArrayList<String> macs = sliceDbManager.getMacsByPort(sliceId, portId);
	
				/* Check if no MACs */
				if(macs == null){
					logger.info("No Mac reference is found for dpId: {} portName: {}", dpId, portName);
				}
				else{
					/* For each MAC */
					logger.info("Debug : " +"For each mac in port setting mac state down");
					for(String MAC : macs){
				
						/* set MAC state down */
						logger.info("Debug : " +"Setting mac state down in slice DB");
						if(!sliceDbManager.setMacStateDown(sliceId, portId, MAC)){
							logger.error("Mac state could not be Set as down for dpId: {} portName: {} MAC: {}", dpId, portName, MAC);
						}
					}
				}
			}
			/* Set port state down */
			logger.info("Debug : " +"Setting port state down in slice DB");
			if(!sliceDbManager.setPortStateDown(sliceId, portId)){
				logger.error("Port state could not be Set as down for dpId: {} portName: {}", dpId, portName);
			}
		}
		
		logger.info("Debug : " +"Deleting switch from Topo DB!");
		/* delete switch from topo DB */
		if(!topoDbManager.deleteSwitch(dpId)){
			logger.error("Switch deletion from topo db failed for switch: {}", dpId);
			return false;
		}
		
		return true;
	}

	/* Port changed function */
	
	private boolean portAdded(NodeConnector nodeConnector, String portName, String portNo, String dpId, Node node) {

		logger.info("Debug : " +"New OpenFlow Port Added Info has reached to VNM!");
		
		boolean ret = false;
		
		/* Lock storage lock */
		StorageLock.acquireLock();

		ret = this._portAdded(nodeConnector, portName, portNo, dpId, node);
		
		/* Release storage lock */
		StorageLock.releaseLock();
		
		return ret;
	}
	
	private boolean _portAdded(NodeConnector nodeConnector, String portName, String portNo, String dpId, Node node) {

		logger.info("Debug : " +"New OpenFlow Port Added Info has reached to VNM!");
		
		/* Check if port is already added to topo Db */
		logger.info("Debug : " +"Checking if port is already attach !");
		ArrayList<Object> port = topoDbManager.getPort(dpId, portName);
		
		if(port != null){
			logger.error("Duplicate port add notification for dpId: {} portName: {}", dpId, portName);
			return false;
		}
		
		/* Check if switch exist */
		logger.info("Debug : " +"Checking if switch is already added for dpId: {}!", dpId);
		if(topoDbManager.getNode(dpId) == null){
			/* Add switch with inactive state in topoDb */
			logger.info("Debug : " +"Adding switch in topoDB with state down for dpId: {} !", dpId);
			if(!topoDbManager.addSwitch(dpId, node, false)){
				logger.error("Switch addition with state sown failed for dpID: {}", dpId);
				return false;
			}
			
			/* No default flow would be added in inactive switch */
		}
		else {
			logger.info("Debug : " +"Switch is already added in topo DB for dpId: {}!", dpId);
		}
		
		logger.info("Debug : " +"Adding port to TOPO DB !");
		/* Added port to Topo DB */
		if(!topoDbManager.addPort(dpId, node, portName, portNo, nodeConnector)){
			logger.error("Port Addition to topo DB failed for dpId: {} portName: {}", dpId, portName);
			return false;
		}
		
		/* Add port specific default flows */
		/* TODO: Due to some bug in ODL this flow is not getting added in port discovery (if node notification still not came ) 
		 * A additional checking and port state is introduced */
		logger.info("Debug : " +"Adding port Default Flow !");
		if(topoDbManager.getNodeState(dpId) == true){
			if(!flowManager.setAndVerifyPortDefaultFlow(dpId, node, portNo, null, nodeConnector)){
				logger.error("Port default could not be successfully added !");
				return false;
			}
		}
		else {
			logger.info("Node is sill not discovered - port flow could not be added !");
		}
		
		
		/* Check if normal port or Vxlan port */
		logger.info("Debug : " +"Checking if port is normal port or vxlan port!");
		if(!YaonUtil.isVxlanPort(portName)){
			
			logger.info("Debug : " +"Precessing non-vxlan port!");
			
			/* Check for port reference in slice DB */
			logger.info("Debug : " +"Checking if port is added in slice DB!");
			ArrayList<Object> portDetails = sliceDbManager.getPortByName(dpId, portName);
			
			/* Check if null */
			if(portDetails == null){
				logger.info("No port ref found in slice db for dpId: {} portName: {}", dpId, portName);
				return true;
			}
			
			logger.info("Debug : " +"Extracting sliceID, portID, vlan snd vxlanport from topo DB!");
			/* Extract slice Id and port Id from port details */
			String sliceId = sliceDbManager.extractSliceId(portDetails);
			String portId = sliceDbManager.extractPortId(portDetails);
			String vlan = sliceDbManager.extractVlan(portDetails);
			String vxlanPortName = sliceDbManager.extractVxlan(portDetails);
			
			/* Check if null */
			if(sliceId == null || portId == null || vxlanPortName == null || vlan == null){
				logger.info("Corrupted information found from slice Db for dpId: {} portName: {}", dpId, portName);
			}
			else {
				
				logger.info("Debug : " +"Getting vxlan port from topoDB!");
				ArrayList<Object> vxlanPortDetails  = topoDbManager.getPort(dpId, vxlanPortName);
				if(vxlanPortDetails == null){
					logger.error("Vxlan port is down for sliceId: {}", sliceId);
					return false;
				}
				
				/* Extract vxlanNodeConn */
				logger.info("Debug : " +"Extracting vxlan port nodeConnector from topoDB!");
				NodeConnector vxlanNodeConn = topoDbManager.extractNodeConnector(vxlanPortDetails);
				/* Extract vxlanPortNo */
				logger.info("Debug : " +"Extracting vxlan port no from topoDB!");
				String vxlanPortNo = topoDbManager.extractPortNo(vxlanPortDetails);

				/* Get all port in the slice and in same DP */
				logger.info("Debug : " +"Get all port that belongs to same slice and DP!");
				ArrayList<ArrayList<Object>> allPorts = sliceDbManager.getAllPortInSliceDp(sliceId, dpId);
				ArrayList<NodeConnector> nodeConnActivePorts = null;
				
				if(allPorts != null){
					/* Extract node connector */
					/* generate NodeConnector Array */
					logger.info("Debug : " +"Generate node connector array for all active ports in sliceDb!");
					nodeConnActivePorts = getNodeConnectorArrayFromSlicePortDetails(dpId, allPorts);
					
					/* Modify MAC flow for all ports */
					for(ArrayList<Object> portAllDetails : allPorts){
						
						logger.info("Debug : " +"Extract port ID from port Detail in sliceDB!");
						String otherPortId = sliceDbManager.extractPortId(portAllDetails);
						logger.info("Debug : " +"Extract port Name from port Detail in sliceDB!");
						String otherPortName = sliceDbManager.extractPortName(portAllDetails);
						logger.info("Debug : " +"Get port from Topo DB!");
						ArrayList<Object> otherTopoPortDetails = topoDbManager.getPort(dpId, otherPortName);
						if(otherTopoPortDetails != null){
							logger.info("Debug : " +"Extract port no from port Details!");
							String otherPortNo = topoDbManager.extractPortNo(otherTopoPortDetails);
							logger.info("Debug : " +"Extract NodeConnector from port Details!");
							NodeConnector otherPortNodeConn = topoDbManager.extractNodeConnector(otherTopoPortDetails);
							/* Ignore current port */
							if(!otherPortId.equals(portId)){		
								/* Get all MACs to modify forward flow */
								logger.info("Debug : " +"Get all macs details from port!");
								ArrayList<String> allMacs = sliceDbManager.getMacsByPort(sliceId, otherPortId);
								if(allMacs != null){
									for(String MAC : allMacs){
										logger.info("Debug : " +"Addiing forwarding flow for MAC!");
										if(!flowManager.setAndVerifyForwardinngFlow(dpId, node, otherPortNodeConn, otherPortNo, vxlanPortNo, vxlanNodeConn, MAC, nodeConnActivePorts)) {
											logger.warn("Flow could not be configured for: sliceID: {} portId: {} and Mac: {}", sliceId, portId, MAC);
										}
										logger.info("Debug : " +"Setting MAC state up !");
										if(!sliceDbManager.setMacStateUp(sliceId, portId, MAC)){
											logger.warn("Error while Setting MAC state Up for  sliceID: {} portId: {} and Mac: {}", sliceId, portId, MAC);
										}
									}
								}
							}
							else {
								logger.info("Debug : " +"Ignoring current port!");
							}
						}
					}
				}
				
				logger.info("Debug : " + "Getting all macs for port!");
				/* Get all MACs for the port */
				ArrayList<String> macs = sliceDbManager.getMacsByPort(sliceId, portId);
				if(macs != null) {
					logger.info("Debug : " + "For each mac setting and veryfing forwarding flow!");
					for(String MAC : macs){
						if(!flowManager.setAndVerifyForwardinngFlow(dpId, node, nodeConnector, portNo, vxlanPortNo, vxlanNodeConn, MAC, nodeConnActivePorts)) {
							logger.warn("Flow could not be configured for: sliceID: {} portId: {} and Mac: {}", sliceId, portId, MAC);
						}
						if(!sliceDbManager.setMacStateUp(sliceId, portId, MAC)){
							logger.warn("Error while Setting MAC state Up for  sliceID: {} portId: {} and Mac: {}", sliceId, portId, MAC);
						}
					}
				}
				else {
					logger.info("No macs are configured for Port of sliceID: {} portId: {}", sliceId, portId);
				}
				
				/* Set port state up */
				if(!sliceDbManager.setPortStateUp(sliceId, portId)){
					logger.error("Port state could not be set as up for dpId: {} portName: {}", dpId, portName);
					return false;
				}
			}
		}
		else {
			
			logger.info("Debug : " + "Processing vxlan port");
			
			/* Get slice Id from vxlan port */
			String sliceId = YaonUtil.getSliceIDFromVxlan(portName);
			
			logger.info("Debug: " + "Slice Id generated from vxlan port name is: {}", sliceId);
			
			/* Get all port in the slice and in same DP */
			logger.info("Debug : " + "Getting all port in same slice and DP");
			ArrayList<ArrayList<Object>> allPorts = sliceDbManager.getAllPortInSliceDp(sliceId, dpId);
			
			/* Check if invalid VXLAN port */
			if(allPorts == null){
				/* TODO: it should be treated as normal port */ 
				logger.error("Ignoring Invalid vxlan port notification for sliceID: {} ", sliceId);
				return false;
			}
			
			/* Extract node connector */
			/* generate NodeConnector Array */
			logger.info("Debug : " + "Generate node connector array for active ports");
			ArrayList<NodeConnector> nodeConnActivePorts = getNodeConnectorArrayFromSlicePortDetails(dpId, allPorts);
			
			/* Add vxlan port Flow */
			logger.info("Debug : " + "Adding vxlan port flow");
			if(!flowManager.setAndVerifyVxlanDefaultFlow(dpId, node, portNo, nodeConnector, nodeConnActivePorts)){
				logger.error("Vxlan port flow could not be modified for vxlan: {} ", portName);
				return false;
			}
		}
		
		return true;
	}

	private boolean portDeleted(NodeConnector nodeConnector, String portName, String portNo, String dpId, Node node) {

		logger.info("New OpenFlow Port deleted Info has reached to VNM!");
		
		boolean ret = false;
		
		/* Lock storage lock */
		StorageLock.acquireLock();

		ret = this._portDeleted(nodeConnector, portName, portNo, dpId, node);
		
		/* Release storage lock */
		StorageLock.releaseLock();
		
		return ret;
	}
	
	private boolean _portDeleted(NodeConnector nodeConnector, String portName, String portNo, String dpId, Node node) {
		
		/* Check if port is already added to topo Db */
		logger.info("Debug : " + "Getting port from topo DB");
		ArrayList<Object> port = topoDbManager.getPort(dpId, portName);
		
		/* Check if port is null */
		if(port == null){
			logger.error("Port is not added to topo Db for dpId: {} portName: {}", dpId, portName);
			return true;
		}
		
		/* Delete port from topo DB */
		logger.info("Debug : " + "Deleting port from Topo DB");
		if(!topoDbManager.deletePort(dpId, portName)){
			logger.error("Port deletion from topoDb failed for port dpId: {} portName: {}", dpId, portName);
			return false;
		}
		
		/* Remove port default flow */
		logger.info("Debug : " + "Removing port default flow");
		if(!flowManager.removePortDefaultFlow(dpId, node, portNo, null, nodeConnector)){
			logger.error("Flow programmer failed to delete port default flow for dpid: {} portNo: {}", dpId, portNo);
			return false;
		}
		
		/* Get port from slice Db */
		logger.info("Debug : " + "Checking if port exist in sliceDB");
		ArrayList<Object> portDetails = sliceDbManager.getPortByName(dpId, portName);
		
		/* Check if portDetails is null */
		if(portDetails == null){
			logger.info("No slice reference is found for pId: {} portName: {}", dpId, portName);
			return true;
		}
		
		/* Extract sliceId and portId from port Details */
		logger.info("Debug : " + "Extracting sliceID and PortID from sliceDB");
		String sliceId = sliceDbManager.extractSliceId(portDetails);
		String portId = sliceDbManager.extractPortId(portDetails);
			
		/* Check if null */
		if(sliceId == null || portId == null){
			logger.warn("Corrupted information found in SliceDb for dpId: {} portName: {}", dpId, portName);
		}
		else {
			logger.info("Debug : " + "Getting all MACs from portID and sliceID");
			/* Get all MACs for the port */
			ArrayList<String> macs = sliceDbManager.getMacsByPort(sliceId, portId);

			/* Check if no MACs */
			if(macs == null){
				logger.info("No Mac reference is found for dpId: {} portName: {}", dpId, portName);
			}
			else{
				/* For each MAC */
				logger.info("Debug : " + "For each mac configured setting MAC state DOWNs");
				for(String MAC : macs){
			
					/* set MAC state down */
					if(!sliceDbManager.setMacStateDown(sliceId, portId, MAC)){
						logger.error("Mac state could not be Set as down for dpId: {} portName: {} MAC: {}", dpId, portName, MAC);
					}
				}
			}
		}
		
		/* Set port state down */
		logger.info("Debug : " + "Setting port state down");
		if(!sliceDbManager.setPortStateDown(sliceId, portId)){
			logger.error("Port state could not be Set as down for dpId: {} portName: {}", dpId, portName);
		}
		
		return true;
	}

	private boolean portModified(NodeConnector nodeConnector, String portName, String portNo, String dpId, Node node) {

		logger.info("New OpenFlow Port modified Info has reached to VNM!");
		
		boolean ret = false;
		
		/* Lock storage lock */
		StorageLock.acquireLock();
		
		ret = this._portModified(nodeConnector, portName, portNo, dpId, node);
		
		/* Release storage lock */
		StorageLock.releaseLock();
		
		return ret;
	}
	
	private boolean _portModified(NodeConnector nodeConnector, String portName, String portNo, String dpId, Node node){
		
		/* Check if port is already added to topo Db */
		logger.info("Debug : " + "Getting port from topo DB");
		ArrayList<Object> port = topoDbManager.getPort(dpId, portName);
		
		/* Check if port is null */
		if(port == null){
			/* TODO: might need to send the port property map */
			logger.info("Debug : " + "Port not present : Add the port to Topo DB");
			this.portAdded(nodeConnector, portName, portNo, dpId, node);
		}
		else {
			/* Update port details in topo db */
			logger.info("Debug : " + "Updating port nodeConnector object in topoDB");
			if(!topoDbManager.addPort(dpId, node, portName, portNo, nodeConnector)){
				logger.error("Update of Port failed for dpId: {} portName: {}", dpId, portName);
				return false;
			}
		}
		
		return true;
	}
	
	public boolean packetInHandler(RawPacket pkt) {

		logger.info("Packet in notification is being processed for pkt: {}", pkt);
		
		/* Get Incoming NodeConnector for pkt */
		logger.info("Debug : " + "Getting Incoming Node connector for Packet");
		NodeConnector inComingNode = pkt.getIncomingNodeConnector();
		if(inComingNode == null){
			logger.error("Incoming nodeConnector information could not be extraacted from pkt: {}", pkt);
			return false;
		}
		
		/* Get Node for the nodeConnector */
		logger.info("Debug : " + "Getting Node object from port");
		Node node = OdlUtil.getNodeFromPort(inComingNode);
		if(node == null){
			logger.error("Incoming Node information could not be extracted from pkt: {}", pkt);
			return false;
		}
		
		/* Get DpId of the Node */
		logger.info("Debug : " + "Extracting DPID from node");
		String dpId = OdlUtil.getDpIdFromNode(node);
		if(dpId == null){
			logger.error("DpId information could not be extracted from Node: {} for Pkt: ", node, pkt);
			return false;
		}
		
		/* Add switch default flow to extracted node */
		logger.info("Debug : " + "Setting switch defauult flow for unexpected packet in");
		flowManager.setAndVerifySwitchDefaultFlow(dpId, node);
		
		return true;
	}
	
	/* Internal functions */
	
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
