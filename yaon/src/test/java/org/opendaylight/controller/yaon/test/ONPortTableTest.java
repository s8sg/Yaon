package org.opendaylight.controller.yaon.test;

import java.util.ArrayList;

import junit.framework.Assert;

import org.opendaylight.controller.yaon.storage.DBException;
import org.opendaylight.controller.yaon.storage.ONPortKeyType;
import org.opendaylight.controller.yaon.storage.ONPortTable;

public class ONPortTableTest {


	public static void addTest(ONPortTable obj, ArrayList<Object> Fieldvalues) throws DBException {

  	    Assert.assertTrue(obj.add(null, Fieldvalues));
	}

	public static void reAddTest(ONPortTable obj, ArrayList ar) throws DBException {

		Assert.assertTrue(obj.add(null, ar) == true);
	}

	public static void findTest(ONPortTable obj, ArrayList<Object> previous) throws DBException {

		ArrayList ar1 = new ArrayList();
  	    ar1.add(previous.get(0));
  	    ar1.add(previous.get(2));

  	    ArrayList<ArrayList<Object>> listOfRows= null;
  	    listOfRows = obj.find(ar1, ONPortKeyType.PORTID);
  	    
  	    if(listOfRows != null) {
  	    	Assert.assertTrue(previous.size() == listOfRows.get(0).size());

  	    	for(int i=0;i<previous.size();i++) {
  	    		Assert.assertTrue(((String)listOfRows.get(0).get(i)).equals(previous.get(i)));
  	    	}
  	    }
  	    else {
  	    	System.out.println("No rows found !");
  	    }
  	    
  	    ar1 = new ArrayList();
  	    ar1.add(previous.get(1));
	    ar1.add(previous.get(3));
	    
  	    listOfRows = obj.find(ar1, ONPortKeyType.PORTNAME);
	    
	    if(listOfRows != null) {
	    	Assert.assertTrue(previous.size() == listOfRows.get(0).size());

	    	for(int i=0;i<previous.size();i++) {
	    		Assert.assertTrue(((String)listOfRows.get(0).get(i)).equals(previous.get(i)));
	    	}
	    }
	    else {
	    	System.out.println("No rows found !");
	    }
	}



	public static void updateTest(ONPortTable obj, ArrayList<Object> previous) throws DBException {

		ArrayList upd1 = new ArrayList();
  	    ArrayList upd2 = new ArrayList();
  	    ArrayList upd3 = new ArrayList();
  	    upd1.add(previous.get(0));
  	    upd1.add(previous.get(2));
  	    upd2.add("DESC");
  	    upd3.add("Description Update");
        Assert.assertTrue(obj.update(upd1, upd2, upd3));
        previous.set(6, "Description Update");
	}


	public static void deleteTest(ONPortTable obj,ArrayList<Object> previous) throws DBException {

		ArrayList dl = new ArrayList();
        dl.add(previous.get(0));
        Assert.assertTrue(obj.del(dl));
	}

	public static void flushTest(ONPortTable obj, ArrayList<Object> previous) throws DBException {

        Assert.assertTrue(obj.flush());
	}

}
