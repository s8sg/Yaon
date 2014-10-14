package org.opendaylight.controller.yaon.test;

import java.util.ArrayList;

import junit.framework.Assert;

import org.opendaylight.controller.yaon.storage.DBException;
import org.opendaylight.controller.yaon.storage.PortTable;

public class PortTableTest {


	public static void addTest(PortTable obj, ArrayList<Object> Indexvalues, ArrayList<Object> Fieldvalues) throws DBException
	{

		Boolean a= obj.add(Indexvalues, Fieldvalues);
	    Assert.assertTrue(a);

	}

	public static void findTest(PortTable obj, ArrayList<Object> Indexvalues, ArrayList<Object> Fieldvalues) throws DBException
	{

  	    ArrayList<ArrayList<Object>> listOfRows= null;
  	    listOfRows =obj.find(Indexvalues, null);

  	    Assert.assertTrue(Fieldvalues.size() == listOfRows.get(0).size());

      for(int i=0;i<Fieldvalues.size();i++) {
      	Assert.assertTrue(((String)listOfRows.get(0).get(i)).equals(Fieldvalues.get(i)));
	    }


	}

	public static void updateTest(PortTable obj , ArrayList<Object> Indexvalues , ArrayList<Object> Fieldvalues) throws DBException
	{

		ArrayList upd1 = new ArrayList();
  	    ArrayList upd2 = new ArrayList();
  	    upd1.add("PORTNO");
  	    upd2.add("portno_Updated");
  	    Assert.assertTrue(obj.update(Indexvalues, upd1, upd2));
        Fieldvalues.set(0,"portno_Updated");

	}

	public static void deleteTest(PortTable obj, ArrayList<Object> Indexvalues) throws DBException
	{

		ArrayList dl = new ArrayList();
        Assert.assertTrue(obj.del(Indexvalues));

        /* Checking by trying to get deleted value */
  	    ArrayList<ArrayList<Object>> listOfRows= null;
  	    listOfRows = obj.find(Indexvalues, null);
  	    Assert.assertTrue(listOfRows.size()!=0);

	}


	public static void flushTest(PortTable obj, ArrayList<Object> Indexvalue) throws DBException {


        Assert.assertTrue(obj.flush());

        /* Checking by trying to get deleted value */
        ArrayList<ArrayList<Object>> listOfRows= null;
  	    listOfRows = obj.find(Indexvalue, null);
  	    Assert.assertTrue(listOfRows.size()!=0);

	}
}
