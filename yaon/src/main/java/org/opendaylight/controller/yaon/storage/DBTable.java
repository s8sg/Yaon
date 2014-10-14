package org.opendaylight.controller.yaon.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

public abstract class DBTable implements Table{

	private static final Logger logger = LoggerFactory
            .getLogger(DBTable.class);

	/* Method to create Table
	 * @tableName: name of the table to be created
	 * @fieldDetails: String of all fields details in below format:
	 *                 "<field_name> <type> <property> <property> .. ,
	 *                             <field_name> <type> <property> <property> .."
	 * @return: New table that is created
	 * */
	public static ISqlJetTable createTable(SqlJetDb db, String tableName, String fieldsDetails) throws DBException{

		ISqlJetTable table = null;

		/* Check if null arguments */
		if(tableName == null || fieldsDetails == null){
			logger.error("Null agrument is passed !");
			return null;
		}

		/* Create proper query string */
		String query = "CREATE TABLE " + tableName + " (" + fieldsDetails + ")";
		query = query.toUpperCase();

		logger.debug("Create Table query: " + query);
		/* begin transaction to write in db */
		try {
			db.beginTransaction(SqlJetTransactionMode.WRITE);
		} catch (SqlJetException e) {
			logger.error("Transaction initiation failed: " + query);
			throw new DBException("Transaction initiation failed:" + query , e);
		}
		try {
			db.createTable(query);
			table = db.getTable(tableName);
			if(table == null){
				logger.error("Table creation failed, could not be resolved after addition: " + query);
				throw new DBException("Transaction initiation failed:" + query, null);
			}
		} catch (SqlJetException e) {
			logger.error("Table craetion Exception for query: " + query);
			throw new DBException("Table creation failed, could not be resolved after addition: " + query , e);
		} finally {
			/* Commit transaction */
            try {
				db.commit();
			} catch (SqlJetException e) {
				logger.error("DB exception for Commit: " + query);
				throw new DBException("DB exception for Commit: " + query , e);
			}
        }

		return table;
	}


	/* Method to create Index
	 * @indexname: name of the Index
	 * @tableName: name of the table
	 * @fields: String of name of the fields That will be used for constructing index
	 *            "<field_name>, <field_name>"
	 * @return: true/false
	 * */
	public static boolean createIndex(SqlJetDb db, String indexName, String tableName, String fields) throws DBException{

		/* Check if arguments are null */
		if(indexName == null || tableName == null || fields == null){
			logger.error("Null argument is passed !");
			return false;
		}

		/* Check if table exist */
		if(!tableExist(db,tableName)){
			logger.error("Table doesn't exist: " + tableName);
			return false;
		}

		/* Create query for adding */
		String query = "CREATE INDEX " + indexName + " ON " + tableName + "(" + fields + ")";
		query = query.toUpperCase();
		
		logger.debug("Create Index query: " + query);

		/* Begin a transaction */
		try {
			db.beginTransaction(SqlJetTransactionMode.WRITE);
		} catch (SqlJetException e) {
			logger.error("Transaction initiation failed: " + query);
			throw new DBException("Transaction initiation failed:" + query , e);
		}
		try {
			db.createIndex(query);
		} catch (SqlJetException e) {
			logger.error("Table craetion Exception for query: " + query);
			throw new DBException("Table craetion Exception for query:" + query , e);
		} finally {
			/* Commit transaction */
            try {
				db.commit();
			} catch (SqlJetException e) {
				logger.error("DB exception for Commit: " + query);
				throw new DBException("DB exception for Commit: " + query , e);
			}
        }
		return true;
	}

	/* Method to check if DB exist
	 * @tableName: name of the table
	 * @return: true/false
	 * */
	public static boolean tableExist(SqlJetDb db, String tableName) throws DBException{

		ISqlJetTable table = null;

		/* Check if null arguments */
		if(tableName == null){
			logger.error("Null agrument is passed !");
			return false;
		}

		/* Begin a transaction */
		try {
			db.beginTransaction(SqlJetTransactionMode.READ_ONLY);
		} catch (SqlJetException e) {
			logger.error("Transaction could not be initiated!");
			throw new DBException("Transaction could not be initiated!" , e);
		}
		/* Check if table exist */
		try {
			table = db.getTable(tableName);
		} catch (SqlJetException e) {
			logger.error("TGet table Failed !");
			throw new DBException("Get table Exception !" , e);
		} finally {
			/* Commit transaction */
			try {
				db.commit();
			} catch (SqlJetException e) {
				logger.error("DB exception for Commit ");
				throw new DBException("DB exception for Commit "  , e);
			}
        }
		/* check if table is null */
		if(table != null){
			return true;
		}

		return false;
	}

	/* List of unimplemented Methods which should be implemented by the Subclass:
	 *
		boolean add(List<String> primaryKeyValues, List<String> fieldsValues);
		boolean del(List<String> primaryKeyValues, List<String> fieldsValues);
		List<List<String>> find(List<String> primaryKeyValues);
		boolean update(List<String> primaryKeyValues, List<String> fieldsName, List<String> fieldsValue);
		boolean flush();
		List<List<String>> getAllData();
	*/
}
