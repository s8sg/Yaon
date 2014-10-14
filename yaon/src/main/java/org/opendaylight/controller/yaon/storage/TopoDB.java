package org.opendaylight.controller.yaon.storage;

import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopoDB {
	private static final Logger logger = LoggerFactory
            .getLogger(TopoDB.class);
	private static TopoDB topoDb = null;
	private DBConnection connection = null;
	private HashMap<String, Table> tables = null;
	
	/* Private globals for table name */
	private static String switchTableName = "SWITCHTABLE";
	private static String switchPortTableName = "SWITCHPORTTABLE";
	private static String portTableName = "PORTTABLE";

	/* Private constructor should be called only from createDb() */
	private TopoDB(DBConnection connection, HashMap<String, Table> tables){
		this.connection = connection;
		this.tables = tables;
	}

	/* Method to create a sqljetconnection object */
	private static DBConnection getConnection(){

		DBConnection conn = null;
		conn = HashDbConnection.init();
		return conn;
	}
	
	/* Static method to create Database
	 * @dpPath: Path of the database file
	 * @return: YAON FileDb object
	 * */
	public static TopoDB init(){

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
		/* Init Port Table */
		Table switchTable = SwitchTable.init(connection, switchTableName);
		tables.put(switchTableName, switchTable);

		/* 	Init SwitchPort Table */
		Table switchPortTable = SwitchPortTable.init(connection, switchPortTableName);
		tables.put(switchPortTableName, switchPortTable);
		
		/* 	Init Port Table */
		Table portTable = PortTable.init(connection, portTableName);
		tables.put(portTableName, portTable);

		/* DB is successfully created */
		TopoDB topoDb = new TopoDB(connection, tables);
		TopoDB.topoDb = topoDb;

		/* Return the file DB */
		return topoDb;
	}

	/* Method to get Switch table */
	private SwitchTable getSwitchTable(){
		return (SwitchTable)tables.get(switchTableName);
	}
	
	/* Method to get Switch-Port table */
	private SwitchPortTable getSwitchPortTable(){
		return (SwitchPortTable)tables.get(switchPortTableName);
	}
	
	/* Method to get Port table */
	private PortTable getPortTable(){
		return (PortTable)tables.get(portTableName);
	}

	/* Port Specific calls */
	
	
	public boolean addPort(String dpId, Object node, String portName, String portNo, Object nodeConnector){
		
		boolean ret = false; 
		
		logger.info("Addeding port to TopoDb for DpId: {}, PortName: {} and PortNo: {}", dpId, portName, portNo);
		
		/* Get port table */
		PortTable portTable = getPortTable();
		if(portTable == null) {
			logger.error("Port Table is not initialized !");
			return false;
		}
		
		/* Get Switch-Port table */
		SwitchPortTable switchPortTable = getSwitchPortTable();
		if(switchPortTable == null){
			logger.error("Switch port table is not initialized !");
			return false;
		}
		
		/* get switch table */
		SwitchTable switchTable = getSwitchTable();
		if(switchTable == null){
			logger.error("Switch table is not initialized !");
			return false;
		}
		
		/* Check if switch exist */
		ArrayList<Object> switchDetails = this.getSwitch(dpId);
		if(switchDetails == null){
			logger.error("Switch must be added before adding port !");
			return false;
		}
		
		
		/* Add the port to Port Table*/
		ArrayList<Object> primaryKeyValues = new ArrayList<Object>();
		primaryKeyValues.add(dpId);
		primaryKeyValues.add(portName);
		ArrayList<Object> fieldsValues = new ArrayList<Object>();
		fieldsValues.add(portNo);
		fieldsValues.add(nodeConnector);
		try {
			ret = portTable.add(primaryKeyValues, fieldsValues);
			if(ret == false){
				logger.error("Add to port table failed !");
				return false;
			}
		} catch (DBException e) {
			logger.error("Exception while getting data from Port Table !");
			return false;
		}
		
		/* Add the port to Switch-Port Table */
		primaryKeyValues = new ArrayList<Object>();
		primaryKeyValues.add(dpId);
		try {
			ArrayList<ArrayList<Object>> list = switchPortTable.find(primaryKeyValues, null);
			if(list == null){
				logger.error("Switch port table entry could not be found for switch: {} ", dpId);
				return false;
			}
			else {
				ArrayList<Object> portList = list.get(0);
				portList.add(portName);
			}
		}
		catch(DBException e){
			logger.error("Exception while getting data from Switch Port Table: {}", e);
			return false;
		}
		
		return ret; 
	}
	
	public boolean deletePort(String dpId, String portName){
		
		boolean ret = false;
		
		logger.info("Deleting port from Topo Db for dpId : {} and portName: {}", dpId, portName);
		
		/* Get port table */
		PortTable portTable = getPortTable();
		if(portTable == null) {
			logger.error("Port Table is not initialized !");
			return false;
		}
		
		/* Get Switch-Port table */
		SwitchPortTable switchPortTable = getSwitchPortTable();
		if(switchPortTable == null){
			logger.error("Switch port table is not initialized !");
			return false;
		}
		
		/* Get the port */
		ArrayList<Object> primaryKeyValues = new ArrayList<Object>();
		primaryKeyValues.add(dpId);
		primaryKeyValues.add(portName);
		ArrayList<ArrayList<Object>> ports = null;
		try {
			ports = portTable.find(primaryKeyValues, null);
			if(ports == null){
				logger.error("Data is not present in the Port Table !");
				return false;
			}
			
		
		} catch (DBException e) {
			logger.error("Exception while getting data from Port Table !");
			return false;
		}
		
		/* Delete the port */
		try {			
			ret = portTable.del(primaryKeyValues);	
		} catch (DBException e) {
			logger.error("Exception while getting data from Port Table !");
			return false;
		}
		
		/* Delete the port from switchPortTable */
		primaryKeyValues = new ArrayList<Object>();
		primaryKeyValues.add(dpId);
		try {
			ArrayList<ArrayList<Object>> list = switchPortTable.find(primaryKeyValues, null);
			if(list != null){
				ArrayList<Object> portList = list.get(0);
				for(Object port : portList){
					if(port.equals(portName)){
						portList.remove(port);
					}
				}
			}
		}
		catch(DBException e){
			logger.error("Exception while getting data from Switch Port Table !");
			return false;
		}
		
		return ret;
	}
	
	public ArrayList<Object> getPort(String dpId, String portName) {
		
		ArrayList<Object> ret = null;
		
		logger.info("Finding port from Topo Db for dpId : {} and portName: {}", dpId, portName);
		
		/* Get port table */
		PortTable portTable = getPortTable();
		if(portTable == null) {
			logger.error("Port Table is not initialized !");
			return null;
		}
		
		/* Get the port */
		ArrayList<Object> primaryKeyValues = new ArrayList<Object>();
		primaryKeyValues.add(dpId);
		primaryKeyValues.add(portName);
		ArrayList<ArrayList<Object>> ports = null;
		try {
			ports = portTable.find(primaryKeyValues, null);
			if(ports == null){
				logger.error("No port details found !");
				return null;
			}
			
			/* Initialize ret */
			ret = new ArrayList<Object>();
			for(Object obj : ports.get(0)){
				ret.add(obj);
			}
		
		} catch (DBException e) {
			logger.error("Exception while getting data from Port Table !");
			return null;
		}
		
		return ret;
	}
	
	public ArrayList<Object> getPortsByDp(String dpId){
		
		/* get switch-port table */
		SwitchPortTable switchPortTable = getSwitchPortTable();
		
		logger.info("Finding ports from Topo Db for dpId : {}", dpId);
		
		if(switchPortTable == null){
			logger.error("Switch port table is not initialized !");
			return null;
		}
		
		/* Get ports */
		ArrayList<Object> primaryKeyValues = new ArrayList<Object>();
		primaryKeyValues.add(dpId);
		ArrayList<ArrayList<Object>> portsList = null;
		try {
			portsList = switchPortTable.find(primaryKeyValues, null);
			if(portsList == null){
				logger.error("No Switch is found !");
				return null;
			}
		} catch (DBException e) {
			logger.error("Exception while getting data from Port Table !");
			return null;
		}
		
		return portsList.get(0); 
	}
	
	/* Switch Specific calls */
	
	
	public boolean addSwitch(String dpId, Object nodeObject, boolean switchState){

		boolean ret = false;
		
		logger.info("Adding switch to Topo DB for DpId : {}", dpId);
		
		/* get switch table */
		SwitchTable switchTable = getSwitchTable();
		if(switchTable == null){
			logger.error("Switch table is not initialized !");
			return false;
		}
		
		/* get switch port table */
		SwitchPortTable switchPortTable = getSwitchPortTable();
		if(switchPortTable == null){
			logger.error("Switch Port table is not initialized !");
			return false;
		}
		
		/* Check if switch is already added to switch table */
		ArrayList<Object> primaryKeyValues = new ArrayList<Object>();
		ArrayList<Object> fieldsValues = new ArrayList<Object>();
		primaryKeyValues.add(dpId);
		try {
			ArrayList<ArrayList<Object>> switchList = switchTable.find(primaryKeyValues, null);
			if(switchList == null){
				/* Add value to the switch table */
				fieldsValues.add(nodeObject);
				fieldsValues.add(new Boolean(switchState));
				ret = switchTable.add(primaryKeyValues, fieldsValues);
				if(ret == false){
					logger.error("Add to Switch table failed !");
					return false;
				}
			}
			else {
				/* Update the node object and switch state */
				ArrayList<Object> fieldsName = new ArrayList<Object>();
				fieldsName.add("NodeObject");
				fieldsName.add("NodeState");
				fieldsValues.add(nodeObject);
				fieldsValues.add(new Boolean(switchState));
				ret = switchTable.update(primaryKeyValues, fieldsName, fieldsValues);
				if(ret == false){
					logger.error("Update to Switch table failed !");
					return false;
				}
			}
		} catch (DBException e) {
			logger.error("Exception while getting data from Switch Table: {} !", e);
			return false;
		}
		
		/* Check if entry exist for switch */
		try {
			ArrayList<ArrayList<Object>> switchPortList = switchPortTable.find(primaryKeyValues, null);
			if(switchPortList == null){
				/* Add switch ports list */
				ArrayList<Object> ports = new ArrayList<Object>();
				fieldsValues = new ArrayList<Object>();
				fieldsValues.add(ports);
				if(!switchPortTable.add(primaryKeyValues, fieldsValues)){
					logger.error("Add to Switch Port table failed !");
					return false;
				}
			}
			else {
				/* Clear switch port list */ 
				switchPortList.get(0).clear();
			}
		} catch (DBException e) {
			logger.error("Exception while getting data from Switch Table: {} !", e);
			return false;
		}
		
		
		return ret;
	}
	
	public boolean deleteSwitch(String dpId){
		
		boolean ret = false;
		
		logger.info("Deleting switch from Topo Db for dpId : {}", dpId);
		
		/* get switch table */
		SwitchTable switchTable = getSwitchTable();
		if(switchTable == null){
			logger.error("Switch table is not initialized !");
			return false;
		}
		
		/* get switch port table */
		SwitchPortTable switchPortTable = getSwitchPortTable();
		if(switchPortTable == null){
			logger.error("Switch Port table is not initialized !");
			return false;
		}
		
		/* Check if switch is already added to switch table */
		ArrayList<Object> primaryKeyValues = new ArrayList<Object>();
		primaryKeyValues.add(dpId);
		try {
			ArrayList<ArrayList<Object>> switchList = switchTable.find(primaryKeyValues, null);
			if(switchList == null){
				logger.error("Switch doesn't exist !");
				return false;	
			}
			else {
				/* Update the node object */
				ret = switchTable.del(primaryKeyValues);
				if(ret == false){
					logger.error("Delete from Switch table failed !");
					return false;
				}
			}
		} catch (DBException e) {
			logger.error("Exception while getting data from Switch Table !");
			return false;
		}
		
		/* Check if entry exist for switch */
		try {
			ArrayList<ArrayList<Object>> switchPortList = switchPortTable.find(primaryKeyValues, null);
			if(switchPortList == null){
				logger.warn("Switch port table ref could not be found for dpId: {}", dpId);
			}
			else {
				/* Delete switch port list */
				if(!switchPortTable.del(primaryKeyValues)){
					logger.error("Error while deleting List from switch Port Table for dpid: {}", dpId);
					return false;
				}
			}
		} catch (DBException e) {
			logger.error("Exception while getting data from Switch Table: {} !", e);
			return false;
		}
		
		return ret;
	}
	
	public ArrayList<Object> getSwitch(String dpId){
		
		ArrayList<Object> ret = null;
		
		logger.info("Finding switch from Topo Db for dpId : {}", dpId);
		
		/* Get Switch table */
		SwitchTable switchTable = getSwitchTable();
		if(switchTable == null) {
			logger.error("Switch Table is not initialized !");
			return null;
		}
		
		/* Get the port */
		ArrayList<Object> primaryKeyValues = new ArrayList<Object>();
		primaryKeyValues.add(dpId);
		ArrayList<ArrayList<Object>> switchList = null; 
		try {
			switchList = switchTable.find(primaryKeyValues, null);
			if(switchList == null){
				logger.info("Switch not found in DB !");
				return null;
			}
			
			/* Initialize ret */
			ret = new ArrayList<Object>();
			for(Object obj : switchList.get(0)){
				ret.add(obj);
			}
		} catch (DBException e) {
			logger.error("Exception while getting data from Port Table !");
			return null;
		}
		
		return ret;
	}
}
