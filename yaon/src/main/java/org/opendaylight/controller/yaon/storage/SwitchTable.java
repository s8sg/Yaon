package org.opendaylight.controller.yaon.storage;

import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwitchTable extends HashTable{

	private static final Logger logger = LoggerFactory
            .getLogger(SwitchTable.class);
	private static String tableName = null;
	private static ArrayList<String> fieldsNames = new ArrayList<String>() {{
	    add("NodeObject");
	    add("NodeState");
	}};
	private static int feildsNos = 2;
	private static ArrayList<String> indexFieldsName = new ArrayList<String>(){{
		add("DPID");
	}};
	private static int indexFeildsNos = 1;
	private DBConnection connection;
	private HashMap<String, ArrayList<Object>> table;

	private SwitchTable(DBConnection conn, HashMap<String, ArrayList<Object>> table){

		this.table = table;
		this.connection = conn;
	}

	public static SwitchTable init(DBConnection connection, String tableName){

		/* Check if null object is passed */
		if(connection == null || tableName == null){
			logger.error("Null object is passed !");
			return null;
		}

		/* Set table name */
		tableName = tableName.toUpperCase();
		SwitchTable.tableName = tableName;
		
		/* create table */
		HashMap<String, ArrayList<Object>> hashTable = HashTable.createTable(SwitchTable.tableName, fieldsNames);

		if(hashTable == null){
			logger.error("Hash table creation failed !");
			return null;
		}

		/* Create SwitchTable object */
		SwitchTable table = new SwitchTable(connection, hashTable);

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

		this.table.put((String) primaryKeyValues.get(0), fieldsValues);



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

		/* Check if table contains the value */
		if(table.containsKey(primaryKeyValues.get(0))) {
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

		/* Get List of fields */
		ArrayList<Object> currentValues = this.table.get(primaryKeyValues.get(0));
		if(currentValues == null){
			logger.error("Data not found !");
			return false;
		}

		/* Get field name and update data */
		int j = 0;
		for(Object fieldname : fieldsName){
			ArrayList<Object> storedFeildsName = this.table.get(SwitchTable.tableName);
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
