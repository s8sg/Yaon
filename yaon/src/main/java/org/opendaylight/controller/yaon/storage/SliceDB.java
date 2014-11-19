package org.opendaylight.controller.yaon.storage;
import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SliceDB{
	private static final Logger logger = LoggerFactory
            .getLogger(SliceDB.class);
	private static SliceDB OnDb = null;
	private static String dbPath = "yaon";
	private DBConnection connection = null;
	private HashMap<String, Table> tables = null;
	private Boolean defaultstate = false;
	
	/* Private table name */
	private static String sliceTableName = "SLICETABLE";
	private static String onPortTableName = "ONPORTTABLE";
	private static String agentTableName = "AGENTTABLE";
	private static String macTableName = "MACTABLE";
	private static String multicastTableName = "MULTICASTTABLE";

	/* Private constructor should be called only from createDb() */
	private SliceDB(DBConnection connection, HashMap<String, Table> tables){
		this.connection = connection;
		this.tables = tables;
	}

	/* Method to create a sqljet connection object */
	private static DBConnection getConnection(){

		DBConnection conn = null;

		try {
			conn = SqlJetDbConnection.init(dbPath);
		} catch (DBException e) {
			logger.error("Connection could not be initiated: " + e.toString());
			return null;
		}
		return conn;
	}
	
	/* Static method to create Database
	 * @dpPath: Path of the database file
	 * @return: YAON FileDb object
	 * */
	public static SliceDB init(){

		DBConnection connection = null;
		HashMap<String, Table> tables = null;

		/* Get DB Connection */
		try {
			connection = getConnection();
			if(connection == null){
				logger.error("Connection could not be created");
				return null;
			}
			Object obj = connection.getConnection();
			if(obj == null){
				logger.error("Connection could not be initiated");
				return null;
			}
		}
		catch(DBException exp){
			logger.error("Connection exception: " + exp.toString());
			return null;
		}

		/* Init all Tables */
		tables = new HashMap<String, Table>();
		try {
			/* Init Slice Table */
			Table sliceTable = SliceTable.init(connection, sliceTableName);
			tables.put(sliceTableName, sliceTable);

			/* Init Port Table */
			Table portTable = ONPortTable.init(connection, onPortTableName);
			tables.put(onPortTableName, portTable);

			/* Init Agent Table */
			Table agentTable = AgentTable.init(connection, agentTableName);
			tables.put(agentTableName, agentTable);

			/* Init Mac Table */
			Table macTable = MacTable.init(connection, macTableName);
			tables.put(macTableName, macTable);
			
			/* Init Multicast table */
			Table multicastTable = MulticastTable.init(connection, multicastTableName);
			tables.put(multicastTableName, multicastTable);
			
		}
		catch(DBException exp){
			logger.error("Table initialization exception: " + exp.toString());
			return null;
		}

		/* DB is successfully created */
		SliceDB OnDb = new SliceDB(connection, tables);
		OnDb.OnDb = OnDb;

		/* Return the file DB */
		return OnDb;
	}

	/*** Agent table methods ***/

	/* To get agent table from tables */
	private AgentTable getAgentTable(){
		return (AgentTable)tables.get(agentTableName);
	}
	
	/* Add agent to Agent table */
	public boolean addAgent(String dpId, String agentUri){
		
		boolean ret = false;
		
		logger.info("Adding Agent to slice Db for dpId : {} and agent Uri: {}", dpId, agentUri);
		
		/* Get agent table */
		AgentTable agentTable = getAgentTable();
		if(agentTable == null){
			logger.error("Agent table is not initialized !");
			return false;
		}
		
		/* Add agent to agent table */
		ArrayList<Object> fieldsValues = new ArrayList<Object>();
		fieldsValues.add(dpId);
		fieldsValues.add(agentUri);
		try {
			ret = agentTable.add(null, fieldsValues);
		} catch (DBException e) {
			logger.error("Exception while adding into Agent table ! " + e);
			return false;
		}
		
		return ret;
	}
	
	/* Get Agent details */
	public ArrayList<ArrayList<Object>> getAgent(String dpId){
		
		logger.info("Finding Agent from slice Db for dpId : {}", dpId);
		
		/* Get agent table */
		AgentTable agentTable = getAgentTable();
		if(agentTable == null){
			logger.error("Agent table is not initialized !");
			return null;
		} 
		
		/* Get agent from agent table */
		ArrayList<Object> primaryKeyValues = new ArrayList<Object>();
		ArrayList<ArrayList<Object>> result = null;
		primaryKeyValues.add(dpId);
		try {
			result = agentTable.find(primaryKeyValues, null);
			if(result == null){
				logger.info("data could not be found in agent table!");
				return null;
			}
		} catch (DBException e) {
			logger.error("Exception while adding into Agent table ! " + e);
			return null; 
		}
		
		return result;
	}
	
	public boolean updateAgent(String dpId, String agentUri){
		
		logger.info("Updating Agent to slice Db for dpId : {} and agent Uri: {}", dpId, agentUri);
		
		/* Get agent table */
		AgentTable agentTable = getAgentTable();
		if(agentTable == null){
			logger.error("Agent table is not initialized !");
			return false;
		}
		
		/* Update current agentUri */
		ArrayList<Object> primaryKeyValues = new ArrayList<Object>();
		primaryKeyValues.add(dpId);
		ArrayList<Object> fieldsName = new ArrayList<Object>();
		fieldsName.add("URI");
		ArrayList<Object> fieldsValue = new ArrayList<Object>();
		fieldsValue.add(agentUri);
		try {
			agentTable.update(primaryKeyValues, fieldsName, fieldsValue);
		}
		catch(DBException e){
			logger.error("Exception while adding into Agent table ! " + e);
			return false; 
		}
		
		return true;
	}
	
	/*** Multicast table methods ***/
	
	private MulticastTable getMulticastTable(){
		return (MulticastTable)tables.get(multicastTableName);
	}
	
	/* Add multicast details to multicast table */
	public boolean addMulticast(String sliceId, String multicast){
		
		logger.info("Adding Multicast to slice Db for sliceId : {} and multicast: {}", sliceId, multicast);
		
		/* Get Multicast table */
		MulticastTable multicastTable = getMulticastTable();
		if(multicastTable == null){
			logger.error("Multicast table is not initialized !");
			return false;
		}
		
		/* Add agent to agent table */
		ArrayList<Object> fieldsValues = new ArrayList<Object>();
		fieldsValues.add(sliceId);
		fieldsValues.add(multicast);
		try {
			multicastTable.add(null, fieldsValues);
		} catch (DBException e) {
			logger.error("Exception while adding into Multicast table ! " + e);
			return false;
		}
		
		return true;
	}
	
	/* Get multicast details from multicast table */
	public ArrayList<ArrayList<Object>> getMulticast(String sliceId){
		
		logger.info("Finding Multicast from slice Db for sliceId : {}", sliceId);
		
		/* Get agent table */
		MulticastTable multicastTable = getMulticastTable();
		if(multicastTable == null){
			logger.error("Multicast table is not initialized !");
			return null;
		} 
		
		/* Get agent from agent table */
		ArrayList<Object> primaryKeyValues = new ArrayList<Object>();
		ArrayList<ArrayList<Object>> result;
		primaryKeyValues.add(sliceId);
		try {
			result = multicastTable.find(primaryKeyValues, null);
			if(result == null) {
				logger.info("data could not be found in multicst table !");
				return null;
			}
		} catch (DBException e) {
			logger.error("Exception while adding into Multicast table ! " + e);
			return null; 
		}
		
		return result;
	}
	
	/* Update multicast details into multicast table */
	public boolean updateMulticast(String sliceId, String multicast){
		
		logger.info("Updating Multicast to slice Db for sliceId : {} and multicast: {}", sliceId, multicast);
		
		/* Get agent table */
		MulticastTable multicastTable = getMulticastTable();
		if(multicastTable == null){
			logger.error("multicast table is not initialized !");
			return false;
		}
		
		/* Update current agentUri */
		ArrayList<Object> primaryKeyValues = new ArrayList<Object>();
		primaryKeyValues.add(sliceId);
		ArrayList<Object> fieldsName = new ArrayList<Object>();
		fieldsName.add("MULTICAST");
		ArrayList<Object> fieldsValue = new ArrayList<Object>();
		fieldsValue.add(multicast);
		try {
			multicastTable.update(primaryKeyValues, fieldsName, fieldsValue);
		}
		catch(DBException e){
			logger.error("Exception while adding into multicast table ! " + e);
			return false; 
		}
		
		return true;
	}
	
	
	/*** MAC table methods ***/
	
	private MacTable getMacTable(){
		return (MacTable)tables.get(macTableName);
	}

	/* Interface to getAllMacs details */ 
	public ArrayList<ArrayList<Object>> getMacs(String sliceId, String portId){
		
		logger.info("Finding Macs from slice Db for sliceId : {} and portId: {}", sliceId, portId);
		
		ArrayList<ArrayList<Object>> macs = null;
		
		/* Get mac table */
		MacTable macTable = getMacTable();
		if(macTable == null){
			logger.error("Mac table is not initialized !");
			return null;
		}
		
		/* Find macs from mac table */
		try{
			ArrayList<Object> primaryKeyValues = new ArrayList<Object>();
			primaryKeyValues.add(sliceId);
			primaryKeyValues.add(portId);
			macs = macTable.find(primaryKeyValues, null);
			if(macs == null){
				logger.info("data could no be found in macs table !");
				return null;
			}	
		}
		catch(Exception e){
			logger.error("Exception while finding macs !");
			return null;
		}
		
		return macs;
	}
	
	/* Add mac details to MacTable */
	public boolean addMac(String sliceId, String portId, String mac){
		
		logger.info("Adding Mac to slice Db for sliceId : {} portId: {} and Mac: {}", sliceId, portId, mac);
		
		/* Get mac table */
		MacTable macTable = getMacTable();
		if(macTable == null){
			logger.error("Mac table is not initialized !");
			return false;
		}
		
		/* Add agent to agent table */
		ArrayList<Object> fieldsValues = new ArrayList<Object>();
		fieldsValues.add(sliceId);
		fieldsValues.add(portId);
		fieldsValues.add(mac);
		fieldsValues.add(defaultstate.toString());
		try {
			macTable.add(null, fieldsValues);
		} catch (DBException e) {
			logger.error("Exception while adding into Multicast table ! " + e);
			return false;
		}
		
		return true;
	}
	
	/* Method to set mac state   */
	public boolean setMacStates(String sliceId, String portId, String mac, String state){

		logger.info("Setting Mac State to slice Db for sliceId : {} portId: {} Mac: {} ans state: {}", sliceId, portId, mac, state);
		
		/* Get mac table */
		MacTable macTable = getMacTable();
		if(macTable == null){
			logger.error("Mac table is not initialized !");
			return false;
		}

		/* Add agent to agent table */
		ArrayList<Object> fieldValue = new ArrayList<Object>();
		ArrayList<Object> fieldName = new ArrayList<Object>();
		ArrayList<Object> primaryKeyValues = new ArrayList<Object>();
		primaryKeyValues.add(sliceId);
		primaryKeyValues.add(portId);
		primaryKeyValues.add(mac);
		fieldValue.add(state);
		fieldName.add("STATE");
		try {
			if(!macTable.update(primaryKeyValues, fieldName, fieldValue)) {
				logger.error("Mac table error while updating");
			}
		} catch (DBException e) {
			logger.error("Exception while adding into Mac table ! " + e);
			return false;
		}
		return true;
	}

	/* Method to delete mac */
	public boolean deleteMac(String sliceId, String portId, String mac) {

		logger.info("Deleting Mac to slice Db for sliceId : {}, portId: {} and Mac: {}", sliceId, portId, mac);

		/* Get mac table */
		MacTable macTable = getMacTable();
		if(macTable == null){
			logger.error("Mac table is not initialized !");
			return false;
		}

		/* Add agent to agent table */
		ArrayList<Object> primaryKeyValues = new ArrayList<Object>();
		primaryKeyValues.add(sliceId);
		primaryKeyValues.add(portId);
		primaryKeyValues.add(mac);
		try {
			if(!macTable.del(primaryKeyValues)) {
				logger.error("Mac table error while deleting");
			}
		} catch (DBException e) {
			logger.error("Exception while deleting from Mac table ! " + e);
			return false;
		}

		return true;
	}


	/*** Methods for Slice ***/
	
	private SliceTable getsliceTable(){
		return (SliceTable)tables.get(sliceTableName);
	}

	
	/* Method to get all slices */
	public ArrayList<ArrayList<Object>> getAllSlices(){
		
		ArrayList<ArrayList<Object>> slices = null;
		
		/* Get slice table */
		SliceTable sliceTable = getsliceTable();
		if(sliceTable == null){
			logger.error("slice table is not initialized !");
			return null;
		}
		
		/* get all slices from slice table */
		try {
			slices = sliceTable.findAll();
			if(slices == null) {
				logger.info("Data could not be found in Slice table!");
			}
		}
		catch(Exception e){
			logger.error("Exception while getting details feom Slice table !" + e);
			return null;
		}
		
		return slices;
	}
	
	/* Method to get slice   */
	public ArrayList<ArrayList<Object>> getSlice(String sliceId) {
		
		ArrayList<ArrayList<Object>> slice = null;
		
		logger.info("Finding Slice from slice Db for sliceId : {}", sliceId);

		/* Get slice table */
		SliceTable sliceTable = getsliceTable();
		if(sliceTable == null){
			logger.error("slice table is not initialized !");
			return null;
		}

		/* Find slice from slice table */
		try {
			ArrayList<Object> primaryKeyValues = new ArrayList<Object>();
			primaryKeyValues.add(sliceId);
			slice = sliceTable.find(primaryKeyValues, null);
			if(slice == null) {
				logger.info("Data could not be found in Slice table!");
			}
		}
		catch(Exception e){
			logger.error("Exception while getting details feom Slice table !" + e);
			return null;
		}
		
		return slice;
	}

	/* Method to add slice   */
	public boolean addSlice(String sliceId, String description) {

		logger.info("Adding slice to slice Db for sliceId : {}", sliceId);
		
		/* Get slice table */
		SliceTable sliceTable = getsliceTable();
		if(sliceTable == null){
			logger.error("slice table is not initialized !");
			return false;
		}

		/* Add slice in Slice table */
		ArrayList<Object> fieldsValues = new ArrayList<Object>();
		fieldsValues.add(sliceId);
		fieldsValues.add(description);
		try {
			if(sliceTable.add(null, fieldsValues) != true) {
				logger.error("Error in Slice table while adding");
			}
		} catch (DBException e) {
			logger.error("Exception while adding into Slice table ! " + e);
			return false;
		}

		return true;
	}

	/* Method to delete slice */
	public boolean deleteSlice(String sliceId) {

		logger.info("Deleting slice from slice Db for sliceId : {}", sliceId);
		
		/* Get Slice table */
		SliceTable sliceTable = getsliceTable();
		if(sliceTable == null){
			logger.error("slice table is not initialized !");
			return false;
		}

		/* Delete slice from Slice table */
		ArrayList<Object> primaryKeyValues = new ArrayList<Object>();
		primaryKeyValues.add(sliceId);
		try {
			if(sliceTable.del(primaryKeyValues)!=true) {
				logger.error("Error in Slice table while deleting");
			}
		} catch (DBException e) {
			logger.error("Exception while deleting from Slice table ! " + e);
			return false;
		}

		return true;
	}


	/*** Methods for ONPort ***/
	
	private ONPortTable getOnPortTable(){
		return (ONPortTable)tables.get(onPortTableName);
	}

	/* Method to get port for slice   */
	public ArrayList<ArrayList<Object>> getPortsForSlice(String sliceId) {
		
		ArrayList<ArrayList<Object>> slice = null;

		logger.info("Finding All ports from slice Db for sliceId : {}", sliceId);
		
		/* Get slice table */
		ONPortTable portTable = getOnPortTable();
		if(portTable == null){
			logger.error("ON Port table is not initialized !");
			return null;
		}

		/* Get ports from slice table */
		try{
			ArrayList<Object> primaryKeyValues = new ArrayList<Object>();
			primaryKeyValues.add(sliceId);
			slice = portTable.find(primaryKeyValues, ONPortKeyType.PORTID);
			if(slice == null) {
				logger.info("Data could not be found in ON Port table!");
			}
		}
		catch(Exception e){
			logger.error("Exception while getting ports for ON Port table !" + e);
			return null;
		}
		return slice;
	}
	
	/* Method to get port details by Id  */
	public ArrayList<ArrayList<Object>> getPortById(String sliceId, String portId) {
		
		ArrayList<ArrayList<Object>> listofrows = null;

		logger.info("Finding All ports from slice Db for sliceId : {} and portId: {}", sliceId, portId);
		
		/* Get ONPort table */
		ONPortTable onportTable = getOnPortTable();
		if(onportTable == null){
			logger.error("ONPort table is not initialized !");
			return null;
		}

		/* Finding values from ONPort table using portID*/
		ArrayList<Object> primaryKeyValues = new ArrayList<Object>();
		primaryKeyValues.add(sliceId);
		primaryKeyValues.add(portId);

		try {
			listofrows=onportTable.find(primaryKeyValues, ONPortKeyType.PORTID);
			if(listofrows == null) {
				logger.info("Data could not be found in ON Port table!");
			}
		} catch (DBException e) {
			logger.error("Exception while getting port details by portId from ONPort table ! " + e);
			return null;
		}

		return listofrows;
	}

	/* Method to get port details by Name  */
	public ArrayList<ArrayList<Object>> getPortByName(String dpId, String portname) {

		ArrayList<ArrayList<Object>> listofrows = null;
		
		logger.info("Finding All ports from slice Db for dpId : {} and portName: {}", dpId, portname);

		/* Get ONPort table */
		ONPortTable onportTable = getOnPortTable();
		if(onportTable == null) {
			logger.error("ONPort table is not initialized !");
			return null;
		}

		/* Finding values from ONPort table using portName*/
		ArrayList<Object> primaryKeyValues = new ArrayList<Object>();
		primaryKeyValues.add(dpId);
		primaryKeyValues.add(portname);

		try {
			listofrows=onportTable.find(primaryKeyValues, ONPortKeyType.PORTNAME);
			if(listofrows == null) {
				logger.info("Data could not be found in ON Port table!");
			}
		} catch (DBException e) {
			logger.error("Exception while getting port details by portName from ONPort table " + e);
			return null;
		}

		return listofrows;
	}

	/* Method to get ports details by dpId  */
	public ArrayList<ArrayList<Object>> getPortsByDpid(String dpId) {
		
		ArrayList<ArrayList<Object>> listofrows = null;
		
		logger.info("Finding All ports from slice Db for dpId: {}", dpId);

		/* Get ONPort table */
		ONPortTable onportTable = getOnPortTable();
		if(onportTable == null){
			logger.error("ONPort table is not initialized !");
			return null;
		}

		/* Finding values from ONPort table using dpId*/
		ArrayList<Object> primaryKeyValues = new ArrayList<Object>();
		primaryKeyValues.add(dpId);

		try {
			listofrows = onportTable.find(primaryKeyValues, ONPortKeyType.PORTNAME);
			if(listofrows == null) {
				logger.info("Data could not be found in ON Port table!");
				return null;
			}
		} catch (DBException e) {
			logger.error("Exception while getting port details by dpid from ONPort table ! " + e);
			return null;
		}

		return listofrows;
	}

	/* Method to delete port  */
	public boolean deletePort(String sliceId, String portId) {

		logger.info("Deleting port from slice Db for sliceId : {} and portId: {}", sliceId, portId);
		
		/* Get ONPort table */
		ONPortTable onportTable = getOnPortTable();
		if(onportTable == null){
			logger.error("ONPort table is not initialized !");
			return false;
		}

		/* Delete values from ONPort table */
		ArrayList<Object> primaryKeyValues = new ArrayList<Object>();
		primaryKeyValues.add(sliceId);
		primaryKeyValues.add(portId);
		try {
			if(!onportTable.del(primaryKeyValues)) {
				logger.error("Error in ONPort table while deleting");
			}
		} catch (DBException e) {
			logger.error("Exception while deleting from ONPort table ! " + e);
			return false;
		}

		return true;
	}

	/* Method to add port  */
	public boolean addPort(String sliceId, String dpid, String portId, String portName, String vlanId, String vxlan, String desc) {

		logger.info("Adding port to slice Db for sliceId : {}, portId: {}, dpId: {} and portName: {}", sliceId, portId, dpid, portName);
		
		/* Get ONPort table */
		ONPortTable onportTable = getOnPortTable();
		if(onportTable == null){
			logger.error("ONPort table is not initialized !");
			return false;
		}

		/* Adding values into ONPort table */
		ArrayList<Object> fieldsValues = new ArrayList<Object>();
		fieldsValues.add(sliceId);
		fieldsValues.add(dpid);
		fieldsValues.add(portId);
		fieldsValues.add(portName);
		fieldsValues.add(vlanId);
		fieldsValues.add(vxlan);
		fieldsValues.add(desc);
		fieldsValues.add(defaultstate.toString());
		try {
			if(!onportTable.add(null, fieldsValues)) {
				logger.error("Error in ONPort table while adding");
			}
		} catch (DBException e) {
			logger.error("Exception while adding into ONPort table ! " + e);
			return false;
		}

		return true;
	}

	/* Method to set port state */
	public boolean setPortState(String sliceId, String portId, String state) {

		logger.info("Setting port state in slice Db for sliceId : {}, portId: {} and state: {}", sliceId, portId, state);
		
		/* Get ONPort table */
		ONPortTable onportTable = getOnPortTable();
		if(onportTable == null){
			logger.error("ONPort table is not initialized !");
			return false;
		}

		/* set port up state into ONPort table */
		ArrayList<Object> fieldValue = new ArrayList<Object>();
		ArrayList<Object> fieldName = new ArrayList<Object>();
		ArrayList<Object> primaryKeyValues = new ArrayList<Object>();
		primaryKeyValues.add(sliceId);
		primaryKeyValues.add(portId);
		fieldName.add("STATE");
		fieldValue.add(state);
		try {
			if(onportTable.update(primaryKeyValues, fieldName, fieldValue) != true){
				logger.error("Exception from ONPort table while updating");
			}
		} catch (DBException e) {
			logger.error("Exception while setting up state into OnPort table ! " + e);
			return false;
		}

		return true;
	}
	
	/* Get port by sliceId and dpId */
	public ArrayList<ArrayList<Object>> getAllPortInSLiceDp(String sliceId, String dpId){
		
		ArrayList<ArrayList<Object>> portsDetails = null;
		
		logger.info("Finding ports from slice Db for sliceId : {} and dpId: {}", sliceId, dpId);

		/* Get ONPort table */
		ONPortTable onportTable = getOnPortTable();
		if(onportTable == null){
			logger.error("ONPort table is not initialized !");
			return null;
		}

		/* set port up state into ONPort table */
		ArrayList<Object> primaryKeyValues = new ArrayList<Object>();
		primaryKeyValues.add(sliceId);
		primaryKeyValues.add(dpId);
		try {
			portsDetails = onportTable.find(primaryKeyValues, ONPortKeyType.SLICEDPID);
			if(portsDetails == null){
				logger.info("No port is found !");
				return null;
			}
		} catch (DBException e) {
			logger.error("Exception while setting up state into OnPort table ! " + e);
			return null;
		}

		return portsDetails;
	}
}
