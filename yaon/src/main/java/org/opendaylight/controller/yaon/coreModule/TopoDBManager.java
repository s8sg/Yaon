
package org.opendaylight.controller.yaon.coreModule;

import java.util.ArrayList;
import java.util.HashMap;

import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.core.NodeConnector;
import org.opendaylight.controller.yaon.storage.TopoDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopoDBManager implements InternalModule {

	private static final Logger logger = LoggerFactory
            .getLogger(TopoDBManager.class);
	private static TopoDBManager topoManager = null;
	private TopoDB topoDb;
	private Object topoDbLock;
	private HashMap<String, InternalModule> modules;
	
	private TopoDBManager(TopoDB topoDb){
		this.topoDb = topoDb;
		this.topoDbLock = new Object();
	}
	
	public static TopoDBManager init(){
		
		/* check if already initialized */
		if(TopoDBManager.topoManager != null){
			logger.warn("SliceDBManager is already initialized !");
			return TopoDBManager.topoManager;
		}
		
		/* Initialize slice DB */
		TopoDB topoDB = TopoDB.init();
		if(topoDB == null){
			logger.error("Slice DB initialization failed !");
			return null;
		}
		
		/* Create SliceDBManager object */
		TopoDBManager topoManager = new TopoDBManager(topoDB);
		
		TopoDBManager.topoManager = topoManager;
		
		return TopoDBManager.topoManager;
	}

	@Override
	public boolean initiateServicesDepedency(ServiceHolder services) {
		/* No service is needed for TopoDbManager */
		return true;
	}
	
	@Override
	public boolean initiateModuleDependency(HashMap<String, InternalModule> modules) {
		
		/* No further module dependency so return true */
		return true;
	}
	
	/* Port Specific Call */
	
	public boolean addPort(String dpId, Object node, String portName, String portNo, Object nodeConnector){
		
		boolean ret = false; 
		
		/* Check if null arguments passed */
		if(dpId == null || portName == null || portNo == null || nodeConnector == null || node == null){
			logger.error("Null argument is passed !");
			return false;
		}
		
		synchronized (topoDbLock) {
			ret = topoDb.addPort(dpId, node, portName, portNo, nodeConnector);
		}
		
		return ret;
	}
	
	public boolean deletePort(String dpID, String portName){
		boolean ret;
		
		if(dpID == null || portName == null){
			logger.error("Null argument is passed !");
			return false;
		}
		
		synchronized (topoDbLock) {
			ret = topoDb.deletePort(dpID, portName);
		}
		
		return ret;
	}
	
	public ArrayList<Object> getPort(String dpId, String portName) {
		
		ArrayList<Object> ret = null;
		
		/* Check if null argument is passed */
		if(dpId == null || portName == null){
			logger.error("Null argument is passed !");
			return null;
		}
		
		synchronized (topoDbLock) {
			ArrayList<Object> portDetails = topoDb.getPort(dpId, portName);
			ret = portDetails;
		}
		
		return ret;
	}
	
	public ArrayList<String> getPortsNameByDp(String dpId){
		
		ArrayList<String> ret = null;
		
		/* Check if null argument is passed */
		if(dpId == null){
			logger.error("Null argument is passsed !");
			return null;
		}
		
		ArrayList<Object> ports;
		synchronized (topoDbLock) {
			ports = topoDb.getPortsByDp(dpId);
			if(ports == null) {
				logger.error("No ports are found !");
				return null;
			}
		}
		
		ret = new ArrayList<String>();
		for(Object obj : ports){
			ret.add((String)obj);
		}
		
		return ret;
	}
	
	public String extractPortNo(ArrayList<Object> portDetails){
		
		String ret;
		
		/* Check if null Argument is passed */
		if(portDetails == null){
			logger.error("Null argument is passed !");
			return null;
		}
		
		ret = (String)portDetails.get(0);
		
		return ret;
	}

	public NodeConnector extractNodeConnector(ArrayList<Object> portDetails){
		
		Object ret;
		
		/* Check if null Argument is passed */
		if(portDetails == null){
			logger.error("Null argument is passed !");
			return null;
		}
		
		ret = (Object) portDetails.get(1);
		
		return (NodeConnector)ret;
	}
	
	public boolean addSwitch(String dpId, Object nodeObject, boolean state){
		boolean ret = false;
		
		if(dpId == null || nodeObject == null){
			logger.error("Null argument is passed !");
			return false;
		}
		
		synchronized(topoDbLock) {
			ret = topoDb.addSwitch(dpId, nodeObject, state);
		}
		
		return ret;
	}
	
	public boolean deleteSwitch(String dpId){
		boolean ret = false;
		
		if(dpId == null){
			logger.error("Null argument is passed !");
			return false;
		}
		
		synchronized(topoDbLock) {
			ret = topoDb.deleteSwitch(dpId);
		}
		
		return ret;
	}
	
	public Node getNode(String dpId){
		
		Node ret = null;
		
		if(dpId == null){
			logger.error("Null argument is passed !");
			return null;
		}
		
		synchronized(topoDbLock) {
			ArrayList<Object> dataList = topoDb.getSwitch(dpId);
			if(dataList == null){
				return null;
			}
			ret = (Node)dataList.get(0);
		}
		
		return ret;
	}

	public boolean setNodeStateUp(String dpId) {
		
		if(dpId == null){
			logger.error("Null argument is passed !");
			return false;
		}
		
		synchronized(topoDbLock) {
			Node nodeObject = this.getNode(dpId);
		    topoDb.addSwitch(dpId, nodeObject, true);
		}
		
		return true;
	}
	
	public boolean getNodeState(String dpId){
		
		boolean ret;
		
		if(dpId == null){
			logger.error("Null argument is passed !");
			return false;
		}
		
		synchronized(topoDbLock) {
			ArrayList<Object> dataList = topoDb.getSwitch(dpId);
			if(dataList == null){
				return false;
			}
			ret = (Boolean)dataList.get(1);
		}
		
		return ret;
	}
}
