package org.opendaylight.controller.yaon.storage;

import java.util.ArrayList;


public interface Table {
	boolean add(ArrayList<Object> primaryKeyValues, ArrayList<Object> fieldsValues) throws DBException;
	boolean del(ArrayList<Object> primaryKeyValues) throws DBException;
	ArrayList<ArrayList<Object>> find(ArrayList<Object> primaryKeyValues, Object type) throws DBException;
	boolean update(ArrayList<Object> primaryKeyValues, ArrayList<Object> fieldsName, ArrayList<Object> fieldsValue) throws DBException;
	boolean flush() throws DBException;
	ArrayList<ArrayList<Object>> getAllData() throws DBException;
}
