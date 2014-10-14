package org.opendaylight.controller.yaon.test;

import java.util.ArrayList;

import junit.framework.Assert;

import org.opendaylight.controller.yaon.storage.AgentTable;
import org.opendaylight.controller.yaon.storage.DBException;

public class AgentTableTest {

	public static void addTest(AgentTable obj, ArrayList ar) throws DBException {

		Assert.assertTrue(obj.add(null, ar));
	}

	public static void reAddTest(AgentTable obj, ArrayList ar) throws DBException {

		Assert.assertTrue(obj.add(null, ar) == true);
	}

	public static void findTest(AgentTable obj, ArrayList<Object> previous) throws DBException {

		ArrayList ar1 = new ArrayList();
  	    ar1.add(previous.get(0));

  	    ArrayList<ArrayList<Object>> listOfRows= null;
  	    listOfRows = obj.find(ar1, null);
  	    
  	    if(listOfRows != null){

  	    	Assert.assertTrue(previous.size() == listOfRows.get(0).size());

  	    	for(int i=0;i<previous.size();i++) {
  	    		Assert.assertTrue(((String)listOfRows.get(0).get(i)).equals(previous.get(i)));
  	    	}
  	    }
  	    else {
  	    	System.out.println("FindTest : No rows found !");
  	    }
	}



	public static void updateTest(AgentTable obj, ArrayList<Object> previous) throws DBException {

		ArrayList upd1 = new ArrayList();
  	    ArrayList upd2 = new ArrayList();
  	    ArrayList upd3 = new ArrayList();
  	    upd1.add(previous.get(0));
  	    upd2.add("URI");
  	    upd3.add("URI_String_updated");
        Assert.assertTrue(obj.update(upd1, upd2, upd3));
        previous.set(1, "URI_String_updated");
        findTest(obj, previous);
	}

	public static void deleteTest(AgentTable obj, ArrayList<Object> previous) throws DBException {

		ArrayList dl = new ArrayList();
        dl.add(previous.get(0));
        Assert.assertTrue(obj.del(dl));
	}

	public static void flushTest(AgentTable obj, ArrayList<Object> previous) throws DBException {

        Assert.assertTrue(obj.flush());


	}
}
