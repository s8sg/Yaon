package org.opendaylight.controller.yaon.coreModule;

import java.util.ArrayList;
import java.util.HashMap;

import org.opendaylight.controller.yaon.storage.SliceDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SliceDBManager implements InternalModule{
	
	private static final Logger logger = LoggerFactory
            .getLogger(SliceDBManager.class);
	private static SliceDBManager sliceManager = null;
	private SliceDB sliceDb;
	private Object sliceDbLock;
	private HashMap<String, InternalModule> modules;
	
	private SliceDBManager(SliceDB sliceDb){
		this.sliceDb = sliceDb;
		this.sliceDbLock = new Object();
	}
	
	public static SliceDBManager init(){
		
		/* check if already initialized */
		if(SliceDBManager.sliceManager != null){
			logger.warn("SliceDBManager is already initialized !");
			return SliceDBManager.sliceManager;
		}
		
		/* Initialize slice DB */
		SliceDB sliceDb = SliceDB.init();
		if(sliceDb == null){
			logger.error("Slice DB initialization failed !");
			return null;
		}
		
		/* Create SliceDBManager object */
		SliceDBManager sliceManager = new SliceDBManager(sliceDb);
		
		SliceDBManager.sliceManager = sliceManager;
		
		return SliceDBManager.sliceManager;
	}

	@Override
	public boolean initiateServicesDepedency(ServiceHolder services) {
		/* No ODL service is needed for SliceDbManager */
		return true;
	}
	
	@Override
	public boolean initiateModuleDependency(HashMap<String, InternalModule> modules) {
		
		/* No further module dependency so return true */
		return true;
	}
	
	/* Method to add agent in SliceDb */
	public boolean addAgent(String dpId, String agentUri){
		
		boolean ret = false;
		
		if(sliceDb.getAgent(dpId) != null){
			ret = sliceDb.updateAgent(dpId, agentUri);
		}
		else {
			ret = sliceDb.addAgent(dpId, agentUri);
		}
		
		return ret;
	}
	
	/* Method to get agent Uri */
	public String getAgentUri(String dpId){
		
		String ret = null;
		
		ArrayList<ArrayList<Object>> listOfRows = sliceDb.getAgent(dpId);
		if(listOfRows != null){
			ArrayList<Object> list = listOfRows.get(0);
			if(list != null){
				ret = (String)list.get(1);
			}
		}
		
		return ret;
	}
	
	/* Method to add multicast in SliceDb */
	public boolean addMulticast(String sliceId, String multicast){
		
		boolean ret = false;
		
		if(sliceDb.getMulticast(sliceId) != null){
			ret = sliceDb.updateMulticast(sliceId, multicast);
		}
		else {
			ret = sliceDb.addMulticast(sliceId, multicast);
		}
		
		return ret;
	}
	
	/* Method to get multicast address */
	public String getMulticastAddress(String sliceId){
		
		String ret = null;
		
		ArrayList<ArrayList<Object>> listOfRows = sliceDb.getMulticast(sliceId);
		if(listOfRows != null){
			ArrayList<Object> list = listOfRows.get(0);
			if(list != null){
				ret = (String)list.get(1);
			}
		}
		
		return ret;
	}
	
	
	/* Methods for MAC */


	/* Method to add MAC in SliceDb */
	public boolean addMac(String sliceid, String portid, String mac) {
		
		boolean ret = false;

		/* Check if null argument is passed */
		if(sliceid == null|| portid == null|| mac == null){
			logger.error("Null Argument is passed !");
			return false;
		}

		ret = sliceDb.addMac(sliceid, portid, mac);
		
		return ret;
	}

	/* Method to check if MAC exist Slicedb */
	public boolean isMacExist(String sliceid, String portid, String mac) {
		boolean ret = false;
		ArrayList<ArrayList<Object>> alldetails = null;

		/* Check if null argument is passed */
		if(sliceid == null|| portid == null|| mac == null){
			logger.error("Null Argument is passed !");
			return false;
		}

		alldetails = sliceDb.getMacs(sliceid, portid);
		if(alldetails != null) {
			for(ArrayList<Object> obj : alldetails) {
				if(obj.get(2).equals(mac)) {
					ret = true;
					break;
				}
			}
		}
		
		return ret;
	}

	/* Method to set mac UpState in SliceDb */
	public boolean setMacStateUp(String sliceid, String portid, String mac) {
		
		boolean ret = false;

		/* Check if null argument is passed */
		if(sliceid == null|| portid == null|| mac == null){
			logger.error("Null Argument is passed !");
			return false;
		}

		ret = sliceDb.setMacStates(sliceid, portid, mac, new Boolean(true).toString());
		
		return ret;
	}

	/* Method to set mac DownState in SliceDb */
	public boolean setMacStateDown(String sliceid, String portid, String mac) {
		
		boolean ret=false;

		/* Check if null argument is passed */
		if(sliceid == null|| portid == null|| mac == null){
			logger.error("Null Argument is passed !");
			return false;
		}
		
		ret = sliceDb.setMacStates(sliceid, portid, mac, new Boolean(false).toString());
		
		return ret;
	}

	/* Method to get Macs by port from SliceDb */
	public ArrayList<String> getMacsByPort(String sliceid, String portid) {
		
		ArrayList<String> allMacs = null;

		/* Check if null argument is passed */
		if(sliceid == null|| portid == null){
			logger.error("Null Argument is passed !");
			return null;
		}

		ArrayList<ArrayList<Object>> alldetails = sliceDb.getMacs(sliceid, portid);
		for(ArrayList<Object> macDetail : alldetails){
			if(allMacs == null){
				allMacs = new ArrayList<String>();
			}
			String mac = this.extractMac(macDetail);
			allMacs.add(mac);
		}
		
		return allMacs;
	}

	/* Method to delete mac from SliceDb */
	public boolean deleteMac(String sliceid, String  portid, String mac) {
		
		boolean ret=false;

		/* Check if null argument is passed */
		if(sliceid == null|| portid == null|| mac == null){
			logger.error("Null Argument is passed !");
			return false;
		}

		ret = sliceDb.deleteMac(sliceid, portid, mac);
		
		return ret;
	}

	/* Method to extract mac address */
	public String extractMac(ArrayList<Object> details) {
		
		String ret = null;

		/* Check if null argument is passed */
		if(details == null){
			logger.error("Null Argument is passed !");
			return null;
		}
		ret = (String)details.get(2);
		
	    return ret;
	}


	/*** Methods foe slice ***/
	
	/* Method to get slice from SliceDb */
	public ArrayList<Object> getSlice(String sliceid) {
		
		ArrayList<ArrayList<Object>> alldetails=null;
		ArrayList<Object> details=null;

		/* Check if null argument is passed */
		if(sliceid == null){
			logger.error("Null Argument is passed !");
			return null;
		}

		alldetails=sliceDb.getSlice(sliceid);
		if(alldetails != null){
			details = alldetails.get(0);
		}
		else{
			return null;
		}
		
		return details;
	}

	/* Method to add slice in SliceDb */
	public boolean addSlice(String sliceid,String description) {
		
		boolean ret = false;

		/* Check if null argument is passed */
		if(sliceid == null|| description == null){
			logger.error("Null Argument is passed !");
			return false;
		}

		ret = sliceDb.addSlice(sliceid, description);
		
		return ret;
	}

	/* Method to delete slice from SliceDb */
	public boolean deleteSlice(String sliceid) {
		
		boolean ret=false;

		/* Check if null argument is passed */
		if(sliceid == null){
			logger.error("Null Argument is passed !");
			return false;
		}

		ret = sliceDb.deleteSlice(sliceid);
		
		return ret;
	}



	/*** Methods for ONPort ***/

	/* Method to get port for slice from SliceDb */
	public ArrayList<ArrayList<Object>> getPortsForSlice(String sliceid) {
		
		ArrayList<ArrayList<Object>> alldetails=null;

		/* Check if null argument is passed */
		if(sliceid == null){
			logger.error("Null Argument is passed !");
			return null;
		}

		alldetails = sliceDb.getPortsForSlice(sliceid);
		
		return alldetails;
	}
	
	/* Method to get port details by Id  */
	public ArrayList<Object> getPortById(String sliceid, String portid) {
		
		ArrayList<ArrayList<Object>> alldetails=null;
		ArrayList<Object> details=null;

		/* Check if null argument is passed */
		if(sliceid == null|| portid == null){
			logger.error("Null Argument is passed !");
			return null;
		}

		alldetails = sliceDb.getPortById(sliceid, portid);
		if(alldetails!=null){
			details=alldetails.get(0);
		}
		
		return details;
	}

	/* Method to get port details by Name  */
	public ArrayList<Object> getPortByName(String dpid,String portname) {
		
		ArrayList<ArrayList<Object>> alldetails = null;
		ArrayList<Object> details = null;

		/* Check if null argument is passed */
		if(dpid == null|| portname == null){
			logger.error("Null Argument is passed !");
			return null;
		}

		alldetails = sliceDb.getPortByName(dpid, portname);
		if(alldetails != null){
			details = alldetails.get(0);
		}
		
		return details;
	}

	/* Method to get port details by dpId  */
	public ArrayList<ArrayList<Object>> getPortsByDpid(String sliceid, String dpid) {
		
		ArrayList<ArrayList<Object>> allDetails = null;
		ArrayList<ArrayList<Object>> refinedDetails = null;
		
		/* Check if null argument is passed */
		if(sliceid == null|| dpid == null){
			logger.error("Null Argument is passed !");
			return null;
		}

		allDetails = sliceDb.getPortsByDpid(dpid);
		if(allDetails != null){
			for(ArrayList<Object> portRow : allDetails){
				if(portRow.get(0).equals(dpid)){
					if(refinedDetails == null){
						refinedDetails = new ArrayList<ArrayList<Object>>();
					}
					refinedDetails.add(portRow);
				}
			}
		}
		
		return refinedDetails;
	}

	/* Method to delete  port  */
	public boolean deletePort(String sliceid, String portid) {
		
		boolean ret = false;

		/* Check if null argument is passed */
		if(sliceid == null|| portid == null){
			logger.error("Null Argument is passed !");
			return false;
		}

		ret = sliceDb.deletePort(sliceid, portid);
		
		return ret;
	}

	/* Method to add  port  */
	public boolean addPort(String sliceId, String dpid, String portId, String portName, String vlanId, String vxlan, String desc) {
		
		boolean ret = false;

		/* Check if null argument is passed */
		if(sliceId == null|| dpid == null|| portId == null|| portName == null|| vlanId == null|| vxlan == null|| desc == null){
			logger.error("Null Argument is passed !");
			return false;
		}

		ret = sliceDb.addPort(sliceId, dpid, portId, portName, vlanId, vxlan, desc);
		
		return ret;
	}
	
	/* Method to set up port state */
	public boolean setPortStateUp(String sliceId, String portId) {
		
		boolean ret = false;

		/* Check if null argument is passed */
		if(sliceId == null|| portId == null){
			logger.error("Null Argument is passed !");
			return false;
		}

		ret = sliceDb.setPortState(sliceId, portId, new Boolean("true").toString());
		
		return ret;

	}

	/* Method to set down port state */
	public boolean setPortStateDown(String sliceId, String portId) {
		
		boolean ret=false;

		/* Check if null argument is passed */
		if(sliceId == null|| portId == null){
			logger.error("Null Argument is passed !");
			return false;
		}

		ret = sliceDb.setPortState(sliceId, portId, new Boolean("false").toString());
		
		return ret;

	}
	
	/* Method to get all port for a slice and dpid */
	public ArrayList<ArrayList<Object>> getAllPortInSliceDp(String sliceId, String dpId){
		
		ArrayList<ArrayList<Object>> ret = null;
		
		/* Check if null argument is passed */
		if(sliceId == null || dpId == null) {
			logger.error("Null argument passed !");
			return null;
		}
		
		ret =  sliceDb.getAllPortInSLiceDp(sliceId, dpId);
		
		return ret;
	}

	/* Method for checking port existence */
	public boolean isPortExist(String sliceId , String dpId, String portId, String portName) {
		
		boolean ret=false;

		ArrayList<ArrayList<Object>> detailsByID=null;
		ArrayList<ArrayList<Object>> detailsByName=null;

		/* Check if null argument is passed */
		if(sliceId == null|| portId == null|| dpId == null){
			logger.error("Null Argument is passed !");
			return false;
		}

		detailsByID = sliceDb.getPortById(sliceId, portId);
		detailsByName = sliceDb.getPortByName(dpId, portName);

		if(detailsByID != null && detailsByName != null){
			ret = true;
		}

		return ret;
	}

	/* Method to extract vlanName from Port details */
	public String extractVlan(ArrayList<Object> details) {
		
		String ret = null;

		/* Check if null argument is passed */
		if(details == null){
			logger.error("Null Argument is passed !");
			return null;
		}
		ret = (String)details.get(4);
		
	    return ret;
	}

	/* Method to extract PortName from Port details */
	public String extractPortName(ArrayList<Object> details) {
		
		String ret = null;

		/* Check if null argument is passed */
		if(details == null){
			logger.error("Null Argument is passed !");
			return null;
		}
		ret = (String)details.get(3);
		
		return ret;
	}

	/* Method to extract DpId from Port details */
	public String extractDpId(ArrayList<Object> details) {
		
		String ret = null;

		/* Check if null argument is passed */
		if(details == null){
			logger.error("Null Argument is passed !");
			return null;
		}
		ret = (String)details.get(1);
		
	    return ret;
	}

	/* Method to extract SliceId from Port details */
	public String extractSliceId(ArrayList<Object> details) {
		
		String ret = null;

		/* Check if null argument is passed */
		if(details == null){
			logger.error("Null Argument is passed !");
			return null;
		}

		ret = (String)details.get(0);
		
	    return ret;
	}

	/* Method to extract PortId from Port details */
	public String extractPortId(ArrayList<Object> details) {
		
		String ret = null;

		/* Check if null argument is passed */
		if(details == null){
			logger.error("Null Argument is passed !");
			return null;
		}
		ret = (String) details.get(2);
		
	    return ret;
	}
	
	/* Method to extract vxlan port name */
	public String extractVxlan(ArrayList<Object> details) {
		
		String ret = null;

		/* Check if null argument is passed */
		if(details == null){
			logger.error("Null Argument is passed !");
			return null;
		}
		ret = (String)details.get(5);
		
	    return ret;
	}
	
	
}
