package org.opendaylight.controller.yaon.storage;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HashDbConnection implements DBConnection {

	private static final Logger logger = LoggerFactory
            .getLogger(SqlJetDbConnection.class);

	private HashMap<String,HashTable> db = null;

	private HashDbConnection(HashMap<String,HashTable> db){
		this.db = db;
	}

	public static HashDbConnection init() {

		HashDbConnection connection = null;
		HashMap<String,HashTable> db = null;

		/* Create Map */
		db = new HashMap<String,HashTable>();

		/* Create new Connection */
		connection = new HashDbConnection(db);

		return connection;
	}



	@Override
	public Object getConnection() throws DBException {

		return this.db;
	}
}