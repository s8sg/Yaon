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

public class MulticastTable extends DBTable {
	private static final Logger logger = LoggerFactory
            .getLogger(MulticastTable.class);
	private static String tableName = null;
	private static String fieldDetails = "SLICEID TEXT NOT NULL, MULTICAST TEXT NOT NULL";
	private static int totalfieldsNos = 2;

	/* index details */							/* Index name , Fields */
	private static String[] sliceIdIndexDetails = {"multicastIndex","SLICEID"};
	private static int indexFeildsNos = 1;

	private DBConnection connection;
	private ISqlJetTable table;

	private MulticastTable(DBConnection connection, ISqlJetTable table){
		this.connection = connection;
		this.table = table;
	}

	public static MulticastTable init(DBConnection conn, String tableName) throws DBException{

		MulticastTable table = null;
		ISqlJetTable sqlTable = null;

		/* Assign Table Name */
		tableName = tableName.toUpperCase();
		MulticastTable.tableName = tableName;
		
		/* Create Table */
		sqlTable = DBTable.createTable((SqlJetDb) conn.getConnection(), tableName, fieldDetails);

		/* Create Index */
		DBTable.createIndex((SqlJetDb) conn.getConnection(), sliceIdIndexDetails[0], tableName, sliceIdIndexDetails[1]);

		table = new MulticastTable(conn, sqlTable);

		return table;
	}

	
	@Override
	public boolean add(ArrayList<Object> primaryKeyValues,
			ArrayList<Object> fieldsValues) throws DBException {
		
		/* Check if fieldsValues is null */
		if(fieldsValues == null) {
			logger.error("Null argument is passed !");
			return false;
		}

		/* Check if nos of fields is right */
		if(fieldsValues.size() != totalfieldsNos){
			logger.error("Invalid nos of arguments passed !");
			return false;
		}

		/* Insert into the table */
		try {
			/* Begin Transaction */
			((SqlJetDb) connection.getConnection()).beginTransaction(SqlJetTransactionMode.WRITE);
			/* Begin insertion */
			table.insert(fieldsValues.get(0), fieldsValues.get(1));
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

		/* Check if nos of fields is right */
		if(primaryKeyValues.size() != indexFeildsNos){
			logger.error("Invalid nos of arguments passed !");
			return false;
		}

		/* Delete from the table */
		try {
			/* Begin Transaction */
			((SqlJetDb) connection.getConnection()).beginTransaction(SqlJetTransactionMode.WRITE);
			/* Get cursor */
			ISqlJetCursor cursor = table.lookup(sliceIdIndexDetails[0], (String)primaryKeyValues.get(0));
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
			/* Close the Cursor */
			cursor.close();
			/* Begin commit */
			((SqlJetDb) connection.getConnection()).commit();
		}
		catch (SqlJetException e) {
			logger.error("Exception while deleting rows from Table");
			throw new DBException("Exception while deleting rows from Table" , e);
		}

		return true;
	}

	@Override
	public ArrayList<ArrayList<Object>> find(
			ArrayList<Object> primaryKeyValues, Object type) throws DBException {
		ArrayList<ArrayList<Object>> listOfRows= new ArrayList<ArrayList<Object>>();

		/* Check if fieldsValues is null */
		if(primaryKeyValues == null) {
			logger.error("Null argument is passed !");
			return null;
		}

		/* Check if nos of fields is right */
		if(primaryKeyValues.size() != indexFeildsNos){
			logger.error("Invalid nos of arguments passed !");
			return null;
		}

		/* Get from the table */
		try {
			/* Begin Transaction */
			((SqlJetDb) connection.getConnection()).beginTransaction(SqlJetTransactionMode.READ_ONLY);
			/* Get cursor */
			ISqlJetCursor cursor = table.lookup(sliceIdIndexDetails[0], primaryKeyValues.get(0));
			/* Create and add values to ListOfRows */
			if (!cursor.eof()) {
	             do {
	                 ArrayList<Object> feildsValues = new ArrayList<Object>();
	                 /* Get the values */
	                 feildsValues.add(cursor.getString("SLICEID"));
	                 feildsValues.add(cursor.getString("MULTICAST"));
	                 /* Add it to the ListOfRows */
	                 listOfRows.add(feildsValues);
	             } while(cursor.next());
	        }else {
				logger.info("No feilds found !");
				return null;
			}
			/* Log nos of row effected */
			logger.info("Find Rows Found: " + cursor.getRowCount());
			/* Close the Cursor */
			cursor.close();
			/* Begin commit */
			((SqlJetDb) connection.getConnection()).commit();
		}
		catch (SqlJetException e) {
			logger.error("Exception while finding data from Table");
			throw new DBException("Exception while finding data from Table" , e);
		}

		return listOfRows;
	}

	@Override
	public boolean update(ArrayList<Object> primaryKeyValues,
			ArrayList<Object> fieldsName, ArrayList<Object> fieldsValue)
			throws DBException {
		/* Check if fieldsValues is null */
		if(primaryKeyValues == null || fieldsName == null || fieldsValue == null) {
			logger.error("Null argument is passed !");
			return false;
		}

		/* Check if nos of fields is right */
		if(primaryKeyValues.size() != indexFeildsNos || fieldsName.size() > totalfieldsNos
				|| fieldsName.size() < 1 || fieldsValue.size() != fieldsName.size()){
			logger.error("Invalid nos of arguments passed !");
			return false;
		}

		/* Delete from the table */
		try {
			/* Begin Transaction */
			((SqlJetDb) connection.getConnection()).beginTransaction(SqlJetTransactionMode.WRITE);
			/* Get cursor */
			ISqlJetCursor cursor = table.lookup(sliceIdIndexDetails[0], (String)primaryKeyValues.get(0));
			/* Create and add values to ListOfRows */
			if (!cursor.eof()) {
	             do {
	                 HashMap<String, String> feildsValues = new HashMap<String, String>();
	                 /* Get the values */
	                 feildsValues.put("SLICEID", cursor.getString("SLICEID"));
	                 feildsValues.put("MULTICAST", cursor.getString("MULTICAST"));

	                 /* Change the details in feildsValues as per fieldsName and their fieldsValue */
	                 int i = 0;
	                 for(Object fieldName : fieldsName){
	                	 if(fieldName.equals("SLICEID") || fieldName.equals("MULTICAST")){
	                		 feildsValues.put((String)fieldName, (String) fieldsValue.get(i));
	                	 }
	                	 else {
	                		 logger.error("Invalid feild name is passed !");
	                		 return false;
	                	 }
	                	 i++;
	                 }
	                 logger.debug("Row Id: " + cursor.getRowId() + ", SLICEID: " + feildsValues.get("SLICEID") + ", MULTICAST: " + feildsValues.get("MULTICAST")); 
	                 cursor.updateWithRowId(cursor.getRowId(), feildsValues.get("SLICEID"), feildsValues.get("MULTICAST"));
	             } while(cursor.next());
	        }
			else {
				logger.error("No feilds matched !");
				return false;
			}
			/* Log nos of row effected */
			logger.info("Update Rows Effected: " + cursor.getRowCount());
			/* Close the Cursor */
			cursor.close();
			/* Begin commit */
			((SqlJetDb) connection.getConnection()).commit();
		}
		catch (SqlJetException e) {
			logger.error("Exception while updating data in Table");
			throw new DBException("Exception while updating data in Table" , e);
		}
		return true;
	}

	@Override
	public boolean flush() throws DBException {
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
			/* Close the Cursor */
			cursor.close();
			/* Begin commit */
			((SqlJetDb) connection.getConnection()).commit();
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
			ISqlJetCursor cursor = table.open();
			/* Get values using cursor */
			if (!cursor.eof()) {
	             do {
	                 ArrayList<Object> feildsValues = new ArrayList<Object>();
	                 /* Get the values */
	                 feildsValues.add(cursor.getString("SLICEID"));
	                 feildsValues.add(cursor.getString("MULTICAST"));
	                 /* Add it to the ListOfRows */
	                 listOfRows.add(feildsValues);
	             } while(cursor.next());
	        }else {
				logger.info("No feilds found!");
				return null;
			}
			/* Log nos of rows effected */
			logger.info("Get All Data Rows Found: " + cursor.getRowCount());		
			/* Close the Cursor */
			cursor.close();
			/* clear the table */
			table.clear();
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
