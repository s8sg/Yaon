package org.opendaylight.controller.yaon.storage;

import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class HashTable implements Table{


	private static final Logger logger = LoggerFactory
            .getLogger(HashTable.class);

	/* Method to create Table
	 * @tableName: name of the table to be created
	 * @fieldsName[]: ArrayList of fields name
	 * @return: New table that is created
	 * */
	public static HashMap<String, ArrayList<Object>> createTable(String tableName, ArrayList<String> fieldNames){

		HashMap<String, ArrayList<Object>> map = null;


		/* Check if null argument passed */
		if(tableName == null || fieldNames == null){
			logger.error("NULL argument is passed!");
			return null;
		}

		/* Check if any feildName is null */
		for(String field : fieldNames){
			if(field == null){
				logger.error("NULL value is passed in the feilds name!");
				return null;
			}
		}
		map = new HashMap<String, ArrayList<Object>>();
		
		ArrayList<Object> temp_fieldNames = new ArrayList<Object>();
		
		for(String str : fieldNames){
			temp_fieldNames.add(str);
		}
		
		/* Dummy row to put tableName and feildsName for Location */
		map.put(tableName, temp_fieldNames);

		/* return map */
		return map;
	}

	/*	Unimplemented Table
	 *
	 	boolean add(ArrayList<String> primaryKeyValues, ArrayList<String> fieldsValues) throws DBException;
		boolean del(ArrayList<String> primaryKeyValues, ArrayList<String> fieldsvalues) throws DBException;
		ArrayList<ArrayList<String>> find(ArrayList<String> primaryKeyValues) throws DBException;
		boolean update(ArrayList<String> primaryKeyValues, ArrayList<String> fieldsName, ArrayList<String> fieldsValue) throws DBException;
		boolean flush() throws DBException;
		ArrayList<ArrayList<String>> getAllData() throws DBException;
	 */
}
