package org.opendaylight.controller.yaon.storage;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.table.ISqlJetTransaction;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

public class SqlJetDbConnection implements DBConnection{

	private static final Logger logger = LoggerFactory
            .getLogger(SqlJetDbConnection.class);

	private String dbPath = null;
	private SqlJetDb db = null;

	private SqlJetDbConnection(String dbPath, SqlJetDb db){
		this.dbPath = dbPath;
		this.db = db;
	}

	public static SqlJetDbConnection init(String dbPath) throws DBException{

		File dbFile = null; 	/* File for Database */
		SqlJetDb db = null;		/* DB file that will be created */

		dbFile = new File(dbPath);
		logger.info("DB file absolute path: " + dbFile.getAbsolutePath());

		/* Delete old file */
		dbFile.delete();
		dbFile = new File(dbPath);
		
		/* Open the Dbfile */
		try {
			db = SqlJetDb.open(dbFile, true);
		}catch(SqlJetException exp){
			logger.error("SQLDB open failed for: " + dbPath);
			throw new DBException("SQLDB open failed for:" + dbPath , exp);
		}

		logger.info("Initializing new DB !");

		/* Set DB option that have to be set before running any transactions */
		try {
			db.getOptions().setAutovacuum(true);
		}catch(SqlJetException exp){
			logger.error("SQLDB initialization failed for: " + dbPath , exp);
			throw new DBException("SQLDB initialization failed for:" + dbPath , exp);
		}

	    /* set DB option that have to be set in a transaction */
		/* Set Db run transaction */
		try {
			db.runTransaction(new ISqlJetTransaction() {
		    	@Override
		    	public Object run(SqlJetDb db) throws SqlJetException {
		    		db.getOptions().setUserVersion(1);
		    		return true;
		    	}

		    }, SqlJetTransactionMode.WRITE);
		}catch(SqlJetException exp){
			logger.error("SQLDB initialization failed for: " + dbPath);
			throw new DBException("SQLDB initialization failed for:" + dbPath , exp);
		}

		/* Create new Connection Object */
		SqlJetDbConnection conn  = new SqlJetDbConnection(dbPath, db);

		/* Return the file DB */
		return conn;
	}


	/* getCoonection() : Method to get DBConnection object
	 * 					 Create connection at the first call
	 * @return:			 Sql DB object
	 */
	@Override
	public Object getConnection() {
		return this.db;
	}

}
