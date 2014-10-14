package org.opendaylight.controller.yaon.storage;

import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwitchPortTable extends HashTable{


	private static final Logger logger = LoggerFactory
            .getLogger(SwitchPortTable.class);
	private static String tableName = null;
	private static ArrayList<String> fieldNames = new ArrayList<String>() {{
	    add("PORTS");
	}};
	private static int feildsNos = 1;
	private static ArrayList<String> indexFieldsName = new ArrayList<String>(){{
		add("DPID");
	}};
	private static int indexFeildsNos = 1;
	private DBConnection connection;
	private HashMap<String, ArrayList<Object>> table;

	private SwitchPortTable(DBConnection conn, HashMap<String, ArrayList<Object>> table){

		this.table = table;
		this.connection = conn;
	}

	public static SwitchPortTable init(DBConnection connection, String tableName){

		/* Check if null object is passed */
		if(connection == null || tableName == null){
			logger.error("Null object is passed !");
			return null;
		}

		/* Set table name */
		tableName = tableName.toUpperCase();
		SwitchPortTable.tableName = tableName;
		
		/* create table */
		HashMap<String, ArrayList<Object>> hashTable = HashTable.createTable(SwitchPortTable.tableName, fieldNames);
		if(hashTable == null){
			logger.error("Hash table creation failed !");
			return null;
		}

		/* Create SwitchTable object */
		SwitchPortTable table = new SwitchPortTable(connection, hashTable);

		return table;
	}

	@Override
	public boolean add(ArrayList<Object> primaryKeyValues,
			ArrayList<Object> fieldsValues) throws DBException {

		/* Check if null object is passed */
		if(primaryKeyValues == null || fieldsValues == null){
			logger.error("Null object is passed !");
			return false;
		}

		/* check if invalid nos of arguments passed */
		if(primaryKeyValues.size() != indexFeildsNos){
			logger.error("Invalid nos of primary key is passed !");
			return false;
		}

		/* Check if invalid nos of arguments passed */
		if(fieldsValues.size() != feildsNos){
			logger.error("Invalid nos of feilds value is passed !");
			return false;
		}

		/* generate primary key */
		String pk = (String) primaryKeyValues.get(0);

		/* Put data in Table */
		this.table.put(pk, fieldsValues);

		return true;
	}

	@Override
	public boolean del(ArrayList<Object> primaryKeyValues) throws DBException {

		/* Check if null object is passed */
		if(primaryKeyValues == null){
			logger.error("Null object is passed !");
			return false;
		}

		/* check if invalid nos of arguments passed */
		if(primaryKeyValues.size() != indexFeildsNos){
			logger.error("Invalid nos of primary key is passed !");
			return false;
		}

		/* generate primary key */
		String pk = (String) primaryKeyValues.get(0);

		/* Check if table contains the value */
		if(table.containsKey(pk)) {
			table.remove(primaryKeyValues.get(0));
			return true;
		}
		return false;
	}

	@Override
	public ArrayList<ArrayList<Object>> find(ArrayList<Object> primaryKeyValues, Object type)
			throws DBException {

		/* Check if null object is passed */
		if(primaryKeyValues == null){
			logger.error("Null object is passed !");
			return null;
		}

		/* check if invalid nos of arguments passed */
		if(primaryKeyValues.size() != indexFeildsNos){
			logger.error("Invalid nos of primary key is passed !");
			return null;
		}

		/* generate primary key */
		String pk = (String) primaryKeyValues.get(0);

		ArrayList<Object> rows = table.get(pk);
		if(rows == null){
			logger.info("No feilds found!");
			return null;	
		}
		ArrayList<ArrayList<Object>> results = new ArrayList<ArrayList<Object>>();
		results.add(rows);

		return results;
	}

	@Override
	public boolean update(ArrayList<Object> primaryKeyValues,
			ArrayList<Object> fieldsName, ArrayList<Object> fieldsValue)
			throws DBException {

		/* Check if null object is passed */
		if(primaryKeyValues == null || fieldsName == null || fieldsValue == null){
			logger.error("Null object is passed !");
			return false;
		}

		/* check if invalid nos of arguments passed */
		if(primaryKeyValues.size() != indexFeildsNos){
			logger.error("Invalid nos of primary key is passed !");
			return false;
		}

		/* Check if fieldsName and fieldsValue is same */
		if(fieldsName.size() != fieldsValue.size()){
			logger.error("Invalid nos of arguments is passed !");
			return false;
		}

		/* generate primary key */
		String pk = (String) primaryKeyValues.get(0);

		/* Get List of fields */
		ArrayList<Object> currentValues = this.table.get(pk);
		if(currentValues == null){
			logger.error("Data not found !");
			return false;
		}

		/* Get field name and update data */
		int j = 0;
		for(Object fieldname : fieldsName){
			ArrayList<Object> storedFeildsName = this.table.get(SwitchPortTable.tableName);
			/* Check field location and change the data of that location */
			for(int i=0 ; i < storedFeildsName.size() ; i++ ){
				if(((String)fieldname).equals(storedFeildsName.get(i))){
					currentValues.add(i, fieldsValue.get(j));
					j++;
				}
			}
		}

		return true;
	}

	@Override
	public boolean flush() throws DBException {

		/* Clear the table */
		table.clear();

		return true;
	}

	@Override
	public ArrayList<ArrayList<Object>> getAllData() throws DBException {
		// TODO Auto-generated method stub
		return null;
	}

}
