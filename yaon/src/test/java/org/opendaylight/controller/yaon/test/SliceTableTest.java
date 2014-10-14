package org.opendaylight.controller.yaon.test;

import java.util.ArrayList;

import junit.framework.Assert;

import org.opendaylight.controller.yaon.storage.DBException;
import org.opendaylight.controller.yaon.storage.SliceTable;

public class SliceTableTest {


	public static void addTest(SliceTable obj, ArrayList<Object> previous) throws DBException {

  	    Assert.assertTrue(obj.add(null, previous));

	}

	public static void reAddTest(SliceTable obj, ArrayList ar) throws DBException {

		Assert.assertTrue(obj.add(null, ar) == true);
	}


	public static void findTest(SliceTable obj, ArrayList<Object> previous) throws DBException {

		ArrayList ar1 = new ArrayList();
  	    ar1.add(previous.get(0));

  	    ArrayList<ArrayList<Object>> listOfRows= null;
  	    listOfRows = obj.find(ar1, null);
  	    
  	    if(listOfRows != null) {
	        Assert.assertTrue(previous.size() == listOfRows.get(0).size());
	
	        for(int i=0;i<previous.size();i++) {
	        	Assert.assertTrue(((String)listOfRows.get(0).get(i)).equals(previous.get(i)));
	        }
  	    }
  	    else {
  	    	System.out.println("No Rows are found !");
  	    }
	}

	public static void updateTest(SliceTable obj, ArrayList<Object> previous) throws DBException {

		ArrayList upd1 = new ArrayList();
  	    ArrayList upd2 = new ArrayList();
  	    ArrayList upd3 = new ArrayList();
  	    upd1.add(previous.get(0));
  	    upd2.add("DESC");
  	    upd3.add("desc_string updated");
        Assert.assertTrue(obj.update(upd1, upd2, upd3));
        previous.set(1, "desc_string updated");
	}

	public static void deleteTest(SliceTable obj,ArrayList<Object> previous) throws DBException {

		ArrayList dl = new ArrayList();
        dl.add(previous.get(0));
        Assert.assertTrue(obj.del(dl));
	}

	public static void flushTest(SliceTable obj, ArrayList<Object> previous) throws DBException {

        Assert.assertTrue(obj.flush());
	}
}
