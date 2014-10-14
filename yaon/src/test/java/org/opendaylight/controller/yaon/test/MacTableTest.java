package org.opendaylight.controller.yaon.test;

import java.util.ArrayList;

import junit.framework.Assert;

import org.opendaylight.controller.yaon.storage.DBException;
import org.opendaylight.controller.yaon.storage.MacTable;

public class MacTableTest {



	public static void addTest(MacTable obj, ArrayList<Object> previous) throws DBException {

  	    Assert.assertTrue(obj.add(null, previous));

	}

	public static void reAddTest(MacTable obj, ArrayList<Object> previous) throws DBException {

		Assert.assertTrue(obj.add(null, previous) == true);
	}


	public static void findTest(MacTable obj, ArrayList<Object> previous) throws DBException {

		ArrayList ar1 = new ArrayList();
  	    ar1.add(previous.get(0));
  	    ar1.add(previous.get(1));

  	    ArrayList<ArrayList<Object>> listOfRows= null;
  	    listOfRows = obj.find(ar1, null);

  	    if(listOfRows != null) {
  	    	Assert.assertTrue(previous.size() == listOfRows.get(0).size());

  	    	for(int i=0;i<previous.size();i++) {
  	    		Assert.assertTrue(((String)listOfRows.get(0).get(i)).equals(previous.get(i)));
  	    	}
  	    }
  	    else {
  	    	System.out.println("No rows found!");
  	    }

	}



	public static void updateTest(MacTable obj, ArrayList<Object> previous) throws DBException {

		ArrayList upd1 = new ArrayList();
  	    ArrayList upd2 = new ArrayList();
  	    ArrayList upd3 = new ArrayList();
  	    upd1.add(previous.get(0));
  	    upd1.add(previous.get(1));
  	    upd1.add(previous.get(2));
  	    upd2.add("STATE");
  	    upd3.add("false");
        Assert.assertTrue(obj.update(upd1, upd2, upd3));
        previous.set(3, "false");
	}



	public static void deleteTest(MacTable obj, ArrayList<Object> previous) throws DBException {

		ArrayList dl = new ArrayList();
        dl.add(previous.get(0));
        dl.add(previous.get(1));
        Assert.assertTrue(obj.del(dl));
	}

	public static void flushTest(MacTable obj, ArrayList<Object> previous) throws DBException {
        Assert.assertTrue(obj.flush());
	}



}
