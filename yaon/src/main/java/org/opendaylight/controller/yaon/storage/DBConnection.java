package org.opendaylight.controller.yaon.storage;

public interface DBConnection {
	public Object getConnection() throws DBException;
}
