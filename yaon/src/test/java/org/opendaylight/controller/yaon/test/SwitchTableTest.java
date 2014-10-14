package org.opendaylight.controller.yaon.test;

import java.util.ArrayList;

import junit.framework.Assert;

import org.opendaylight.controller.yaon.storage.DBException;
import org.opendaylight.controller.yaon.storage.SwitchTable;

public class SwitchTableTest {

	public static void addTest(SwitchTable obj, ArrayList<Object> Indexvalue, ArrayList<Object> Fieldvalue) throws DBException
	{

		 Boolean a = obj.add(Indexvalue, Fieldvalue);
		 Assert.assertTrue(a);

	}



	public static void findTest(SwitchTable obj, ArrayList<Object> Indexvalue, ArrayList<Object> Fieldvalue) throws DBException
	{


		ArrayList ar1 = new ArrayList();
  	    ar1.add(Indexvalue.get(0));
  	    ArrayList<ArrayList<Object>> listOfRows= new ArrayList<ArrayList<Object>>();
  	    listOfRows =obj.find(ar1, null);

  	    String p1 = (String)listOfRows.get(0).get(0);

  	    Assert.assertTrue(Fieldvalue.size() == listOfRows.get(0).size());


  	    for(int i=0;i<Fieldvalue.size();i++) {
  	    	Assert.assertTrue(((String)listOfRows.get(0).get(i)).equals(Fieldvalue.get(i)));
	    }
;

	}

	public static void updateTest(SwitchTable obj , ArrayList<Object> Indexvalue , ArrayList<Object> Fieldvalue) throws DBException
	{

		ArrayList upd1 = new ArrayList();
  	    ArrayList upd2 = new ArrayList();
  	    ArrayList upd3 = new ArrayList();
  	    upd1.add(Indexvalue.get(0));
  	    upd2.add("NodeObject");
  	    upd3.add("NodeObjectData_Updated");
        Assert.assertTrue(obj.update(upd1, upd2, upd3));
        Fieldvalue.set(0, "NodeObject_Updated");
	}

	public static void deleteTest(SwitchTable obj, ArrayList<Object> Indexvalue) throws DBException
	{

		ArrayList dl = new ArrayList();
        dl.add(Indexvalue.get(0));
        Assert.assertTrue(obj.del(dl));

  	    ArrayList<ArrayList<Object>> listOfRows= null;
  	    listOfRows = obj.find(Indexvalue, null);
  	    Assert.assertTrue(listOfRows.size()!=0);


	}

	public static void flushTest(SwitchTable obj, ArrayList<Object> Indexvalue) throws DBException {

		Assert.assertTrue(obj.flush());

        /* Checking by trying to get deleted value */
        ArrayList<ArrayList<Object>> listOfRows= null;
  	    listOfRows = obj.find(Indexvalue, null);
  	    Assert.assertTrue(listOfRows.size()!=0);

	}

}
