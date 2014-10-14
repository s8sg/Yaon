package org.opendaylight.controller.yaon.storage;

import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

public class ONPortTable extends DBTable {
	private static final Logger logger = LoggerFactory
            .getLogger(ONPortTable.class);
	private static String tableName = null;
	private static String fieldDetails = " SLICEID TEXT NOT NULL, DPID TEXT NOT NULL, PORTID TEXT NOT NULL, PORTNAME TEXT NOT NULL, VLANID TEXT NOT NULL, VXLANPORT TEXT NOT NULL, DESC TEXT, STATE TEXT NOT NULL";
	private static int totalfieldsNos = 8;

	/* index details */							/* Index name , Fields */
	private static String[] portIdIndexDetails = {"portIdIndex","SLICEID, PORTID"};
	private static String[] portNameIndexDetails = {"portNameIndex","DPID, PORTNAME"};
	private static String[] vxlanIndexDetails = {"vxlanIndex", "DPID, VXLANPORT"};
	private static String[] sliceDpIndexDetails = {"sliceDpIndex", "SLICEID, DPID"};
	private static int indexFeildsNos = 2;

	private final DBConnection connection;
	private final ISqlJetTable table;

	private ONPortTable(DBConnection connection, ISqlJetTable table){
		this.connection = connection;
		this.table = table;
	}

	public static ONPortTable init(DBConnection conn, String tableName) throws DBException{

		ONPortTable table = null;
		ISqlJetTable sqlTable = null;

		/* Assign Table Name */
		tableName = tableName.toUpperCase();
		ONPortTable.tableName = tableName;

		/* Create Table */
		sqlTable = DBTable.createTable((SqlJetDb) conn.getConnection(), tableName, fieldDetails);

		/* Create Index */
		DBTable.createIndex((SqlJetDb) conn.getConnection(), portIdIndexDetails[0], tableName, portIdIndexDetails[1]);
		DBTable.createIndex((SqlJetDb) conn.getConnection(), portNameIndexDetails[0], tableName, portNameIndexDetails[1]);
		DBTable.createIndex((SqlJetDb) conn.getConnection(), vxlanIndexDetails[0], tableName, vxlanIndexDetails[1]);
		DBTable.createIndex((SqlJetDb) conn.getConnection(), sliceDpIndexDetails[0], tableName, sliceDpIndexDetails[1]);
		
		table = new ONPortTable(conn, sqlTable);

		return table;
	}

	@Override
	public boolean add(ArrayList<Object> primaryKeyValues, ArrayList<Object> fieldsValues) throws DBException {

		/* Check if fieldsValues is null */
		if(fieldsValues == null) {
			logger.error("Null argument is passed !");
			return false;
		}

        /* Type casting of Object to String for fieldsValues*/
		ArrayList<String> fieldsValues_string = new ArrayList<String>();
		for (Object object : fieldsValues) {
			fieldsValues_string.add((String)object);
		}


		/* Check if nos of fields is right */
		if(fieldsValues_string.size() != totalfieldsNos){
			logger.error("Invalid nos of arguments passed !");
			return false;
		}

		/* Insert into the table */
		try {
			/* Begin Transaction */
			((SqlJetDb) connection.getConnection()).beginTransaction(SqlJetTransactionMode.WRITE);
			/* Begin insertion */
			try {
				table.insert(fieldsValues_string.get(0), fieldsValues_string.get(1), fieldsValues_string.get(2),fieldsValues_string.get(3),fieldsValues_string.get(4),fieldsValues_string.get(5),fieldsValues_string.get(6),fieldsValues_string.get(7));

			}
			catch(SqlJetException e){
				logger.error("Row is already added in the Table");
				return false;
			}
			/* Begin commit */
			((SqlJetDb) connection.getConnection()).commit();
		}
		catch (SqlJetException e) {
			logger.error("Exception while inserting data in Table");
			throw new DBException("Exception while inserting data in Table" , e);
		}
		return true;
	}

	@Override
	public boolean del(ArrayList<Object> primaryKeyValues) throws DBException {


		/* Check if fieldsValues is null */
		if(primaryKeyValues == null) {
			logger.error("Null argument is passed !");
			return false;
		}

		/* Type casting of Object to String for primaryKeyValues*/
		ArrayList<String> primaryKeyValues_string = new ArrayList<String>();
		for (Object object : primaryKeyValues) {
			primaryKeyValues_string.add((String)object);
		}


		/* Check if nos of fields is right */
		if(primaryKeyValues_string.size()>indexFeildsNos || primaryKeyValues_string.size()<1 ){
			logger.error("Invalid nos of arguments passed !");
			return false;
		}

		/* Delete from the table */
		try {
			/* Begin Transaction */
			((SqlJetDb) connection.getConnection()).beginTransaction(SqlJetTransactionMode.WRITE);
			/* Get cursor */
			ISqlJetCursor cursor = null;
			if(primaryKeyValues_string.size() == 1) {
				cursor = table.lookup(portIdIndexDetails[0], primaryKeyValues_string.get(0));
			}
			else if(primaryKeyValues_string.size() == 2) {
				cursor = table.lookup(portIdIndexDetails[0], primaryKeyValues_string.get(0),primaryKeyValues_string.get(1));
			}
			/* Get row Count */
			long rowCount = cursor.getRowCount();
			boolean foundFlag = false;
			/* Delete using cursor */
			while(!cursor.eof()) {
				if(foundFlag == false){
					foundFlag = true;
				}
				cursor.delete();
	        }
			if(!foundFlag) {
				logger.error("No feilds matched !");
				return false;
			}
			/* Log nos of row effected */
			logger.info("Delete Rows Effected: " + rowCount);
			/* Begin commit */
			((SqlJetDb) connection.getConnection()).commit();
			/* Close the Cursor */
			cursor.close();

		}
		catch (SqlJetException e) {
			logger.error("Exception while deleting rows from Table");
			throw new DBException("Exception while deleting rows from Table" , e);
		}

		return true;
	}

	@Override
	public ArrayList<ArrayList<Object>> find(ArrayList<Object> primaryKeyValues, Object type) throws DBException {
		ArrayList<ArrayList<Object>> listOfRows= new ArrayList<ArrayList<Object>>();

		/* Check if fieldsValues is null */
		if(primaryKeyValues == null || type == null) {
			logger.error("Null argument is passed !");
			return null;
		}

		/* Type casting of Object to String for primaryKeyValues */
		ArrayList<String> primaryKeyValues_string = new ArrayList<String>();
		for (Object object : primaryKeyValues) {
			primaryKeyValues_string.add((String)object);
		}

		/* Check if nos of fields is right */
		if(primaryKeyValues_string.size()>indexFeildsNos || primaryKeyValues_string.size()<1 ){
			logger.error("Invalid nos of arguments passed !");
			return null;
		}

		/* Delete from the table */
		try {
			/* Begin Transaction */
			((SqlJetDb) connection.getConnection()).beginTransaction(SqlJetTransactionMode.WRITE);
			/* Get cursor */
			ISqlJetCursor cursor = null;
			if(((ONPortKeyType)type) == ONPortKeyType.PORTNAME) { 
				if(primaryKeyValues_string.size() == 1) {
					cursor = table.lookup(portNameIndexDetails[0], primaryKeyValues_string.get(0));
				}
				else if(primaryKeyValues_string.size() >= 2) {
					cursor = table.lookup(portNameIndexDetails[0], primaryKeyValues_string.get(0),primaryKeyValues_string.get(1));
				}
			}
			else if(((ONPortKeyType)type) == ONPortKeyType.VXLAN){
				if(primaryKeyValues_string.size() == 1) {
					cursor = table.lookup(vxlanIndexDetails[0], primaryKeyValues_string.get(0));
				}
				else if(primaryKeyValues_string.size() >= 2) {
					cursor = table.lookup(vxlanIndexDetails[0], primaryKeyValues_string.get(0),primaryKeyValues_string.get(1));
				}
			}
			else if(((ONPortKeyType)type) == ONPortKeyType.SLICEDPID){
				if(primaryKeyValues_string.size() == 1) {
					cursor = table.lookup(sliceDpIndexDetails[0], primaryKeyValues_string.get(0));
				}
				else if(primaryKeyValues_string.size() >= 2) {
					cursor = table.lookup(sliceDpIndexDetails[0], primaryKeyValues_string.get(0),primaryKeyValues_string.get(1));
				}
			}
			else{
				if(primaryKeyValues_string.size() == 1) {
					cursor = table.lookup(portIdIndexDetails[0], primaryKeyValues_string.get(0));
				}
				else if(primaryKeyValues_string.size() >= 2) {
					cursor = table.lookup(portIdIndexDetails[0], primaryKeyValues_string.get(0),primaryKeyValues_string.get(1));
				}
			}
			/* Create and add values to ListOfRows */
			if (!cursor.eof()) {
	             do {
	                 ArrayList<Object> feildsValues = new ArrayList<Object>();
	                 /* Get the values */
	                 feildsValues.add(cursor.getString("SLICEID"));
	                 feildsValues.add(cursor.getString("DPID"));
	                 feildsValues.add(cursor.getString("PORTID"));
	                 feildsValues.add(cursor.getString("PORTNAME"));
	                 feildsValues.add(cursor.getString("VLANID"));
	                 feildsValues.add(cursor.getString("VXLANPORT"));
	                 feildsValues.add(cursor.getString("DESC"));
	                 feildsValues.add(cursor.getString("STATE"));
	                 /* Add it to the ListOfRows */
	                 listOfRows.add(feildsValues);
	             } while(cursor.next());
	        }else {
				logger.info("No feilds found !");
				return null;
			}
			/* Log nos of row effected */
			logger.info("Find Rows Found: " + cursor.getRowCount());
			/* Begin commit */
			((SqlJetDb) connection.getConnection()).commit();
			/* Close the Cursor */
			cursor.close();
		}
		catch (SqlJetException e) {
			logger.error("Exception while finding data from Table");
			throw new DBException("Exception while finding data from Table" , e);
		}

		return listOfRows;
	}

	@Override
	public boolean update(ArrayList<Object> primaryKeyValues,
			ArrayList<Object> fieldsName, ArrayList<Object> fieldsValue) throws DBException {

		/* Type casting of Object to String for primaryKeyValues*/
		ArrayList<String> primaryKeyValues_string = new ArrayList<String>();
		for (Object object : primaryKeyValues) {
			primaryKeyValues_string.add((String)object);
		}

        /* Type casting of Object to String for fieldsValue*/
		ArrayList<String> fieldsValues_string = new ArrayList<String>();
		for (Object object : fieldsValue) {
			fieldsValues_string.add((String)object);
		}


		/* Type casting of Object to String for fieldsName*/
		ArrayList<String> fieldsName_string = new ArrayList<String>();
		for (Object object : fieldsName) {
			fieldsName_string.add((String)object);
		}

		/* Check if fieldsValues is null */
		if(primaryKeyValues_string == null || fieldsName_string == null || fieldsValues_string == null) {
			logger.error("Null argument is passed !");
			return false;
		}

		/* Check if nos of fields is right */
		if(primaryKeyValues_string.size() > indexFeildsNos || primaryKeyValues_string.size()<1 || fieldsName_string.size() > totalfieldsNos
				|| fieldsName_string.size() < 1 || fieldsValues_string.size() != fieldsName.size()){
			logger.error("Invalid nos of arguments passed !");
			return false;
		}

		/* Delete from the table */
		try {
			/* Begin Transaction */
			((SqlJetDb) connection.getConnection()).beginTransaction(SqlJetTransactionMode.WRITE);
			/* Get cursor */

			ISqlJetCursor cursor = null;

			if(primaryKeyValues_string.size() == 1) {
				cursor = table.lookup(portIdIndexDetails[0], primaryKeyValues_string.get(0));
			}
			else if(primaryKeyValues_string.size()==2) {
				cursor = table.lookup(portIdIndexDetails[0], primaryKeyValues_string.get(0),primaryKeyValues_string.get(1));
			}
			
			/* Create and add values to ListOfRows */
			if (!cursor.eof()) {
	             do {
	                 HashMap<String, String> feildsValues = new HashMap<String, String>();
	                 /* Get the values */
	                 feildsValues.put("SLICEID", cursor.getString("SLICEID"));
	                 feildsValues.put("DPID", cursor.getString("DPID"));
	                 feildsValues.put("PORTID", cursor.getString("PORTID"));
	                 feildsValues.put("PORTNAME", cursor.getString("PORTNAME"));
	                 feildsValues.put("VLANID", cursor.getString("VLANID"));
	                 feildsValues.put("VXLANPORT", cursor.getString("VXLANPORT"));
	                 feildsValues.put("DESC", cursor.getString("DESC"));
	                 feildsValues.put("STATE", cursor.getString("STATE"));

	                 /* Change the details in feildsValues as per fieldsName and their fieldsValue */
	                 int i = 0;
	                 for(String fieldName : fieldsName_string){
                      if(fieldName.equals("SLICEID") || fieldName.equals("DPID") || fieldName.equals("PORTID")
	                             || fieldName.equals("PORTNAME") || fieldName.equals("VLANID") || fieldName.equals("VXLANPORT")
	                             || fieldName.equals("DESC") || fieldName.equals("STATE")){
	                         feildsValues.put(fieldName, fieldsValues_string.get(i));
                            }
                             else {
                            	 logger.error("Invalid feild name is passed !");
                                 return false;
                             }
                           i++;
                           }
                           cursor.updateWithRowId(cursor.getRowId(), feildsValues.get("SLICEID"), feildsValues.get("DPID"), feildsValues.get("PORTID"), feildsValues.get("PORTNAME"), feildsValues.get("VLANID"), feildsValues.get("VXLANPORT"), feildsValues.get("DESC"), feildsValues.get("STATE"));

	             } while(cursor.next());
	        }
			else {
				logger.error("No feilds matched !");
				return false;
			}
			/* Log nos of row effected */
			logger.info("Update Rows Effected: " + cursor.getRowCount());
			/* Begin commit */
			((SqlJetDb) connection.getConnection()).commit();
			/* Close the Cursor */
			cursor.close();
		}
		catch (SqlJetException e) {
			logger.error("Exception while updating data in Table");
			throw new DBException("Exception while updating data in Table" , e);
		}
		return true;

	}

	@Override
	public boolean flush() throws DBException{

		/* Delete from the table */
		try {
			/* Begin Transaction */
			((SqlJetDb) connection.getConnection()).beginTransaction(SqlJetTransactionMode.WRITE);
			/* Get cursor */
			ISqlJetCursor cursor = table.open();
			/* Get row Count */
			long rowCount = cursor.getRowCount();
			boolean foundFlag = false;
			/* Delete using cursor */
			while(!cursor.eof()) {
				if(foundFlag == false){
					foundFlag = true;
				}
				cursor.delete();
	        }
			if(!foundFlag) {
				logger.error("No feilds matched !");
				return false;
			}
			/* Log nos of row effected */
			logger.info("Flush Rows Effected: " + rowCount);
			/* Begin commit */
			((SqlJetDb) connection.getConnection()).commit();
			/* Close the Cursor */
			cursor.close();
		}
		catch (SqlJetException e) {
			logger.error("Exception while flushing the table");
			throw new DBException("Exception while flushing the table" , e);
		}

		return true;
	}

	@Override
	public ArrayList<ArrayList<Object>> getAllData() throws DBException {
		ArrayList<ArrayList<Object>> listOfRows= new ArrayList<ArrayList<Object>>();
		try {
			/* Begin Transaction */
			((SqlJetDb) connection.getConnection()).beginTransaction(SqlJetTransactionMode.READ_ONLY);
			/* Get cursor */
			ISqlJetCursor cursor  = table.open();
			/* Get values using cursor */
			if (!cursor.eof()) {
	             do {
	                 ArrayList<Object> feildsValues = new ArrayList<Object>();
	                 /* Get the values */
	                 feildsValues.add(cursor.getString("SLICEID"));
	                 feildsValues.add(cursor.getString("DPID"));
	                 feildsValues.add(cursor.getString("PORTID"));
	                 feildsValues.add(cursor.getString("PORTNAME"));
	                 feildsValues.add(cursor.getString("VLANID"));
	                 feildsValues.add(cursor.getString("VXLANPORT"));
	                 feildsValues.add(cursor.getString("DESC"));
	                 feildsValues.add(cursor.getString("STATE"));
	                 /* Add it to the ListOfRows */
	                 listOfRows.add(feildsValues);
	             } while(cursor.next());
	        }else {
				logger.info("No feilds found !");
				return null;
			}
			/* Log nos of row effected */
			logger.info("Get All Data Rows Found: " + cursor.getRowCount());
			/* Close the Cursor */
			cursor.close();
			/* Begin commit */
			((SqlJetDb) connection.getConnection()).commit();

		}
		catch (SqlJetException e) {
			logger.error("Exception while getting all data");
			throw new DBException("Exception while getting all data" , e);
		}
		return listOfRows;
	}
}
