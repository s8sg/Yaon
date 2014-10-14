package org.opendaylight.controller.yaon.storage;

@SuppressWarnings("serial")
public class DBException extends Exception {

	Exception e;
	
	public DBException(String exceptionString, Exception e){
		super(exceptionString);
		this.e = e;
	}

	@Override
	public String toString(){
		return "DbException: " + super.getMessage() + " " + e;
	}
}
