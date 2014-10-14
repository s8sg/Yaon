package org.opendaylight.controller.yaon.test;

import java.util.ArrayList;

import junit.framework.Assert;

import org.opendaylight.controller.yaon.storage.DBException;
import org.opendaylight.controller.yaon.storage.SwitchPortTable;

public class SwitchPortTableTest {


	public static void addTest(SwitchPortTable obj, ArrayList<Object> Indexvalue, ArrayList<Object> Fieldvalue) throws DBException
	{

	    Boolean a= obj.add(Indexvalue, Fieldvalue);
	    Assert.assertTrue(a);

	}


	public static void findTest(SwitchPortTable obj, ArrayList<Object> Indexvalue, ArrayList<Object> Fieldvalue) throws DBException
	{


  	    ArrayList<ArrayList<Object>> listOfRows= new ArrayList<ArrayList<Object>>();
  	    listOfRows =obj.find(Indexvalue, null);

  	    Assert.assertTrue(Fieldvalue.size() == listOfRows.get(0).size());


      for(int i=0;i<Fieldvalue.size();i++) {
      	Assert.assertTrue(((String)listOfRows.get(0).get(i)).equals(Fieldvalue.get(i)));
	    }



	}

	public static void updateTest(SwitchPortTable obj , ArrayList<Object> Indexvalue , ArrayList<Object> Fieldvalue) throws DBException
	{

		ArrayList upd1 = new ArrayList();
  	    ArrayList upd2 = new ArrayList();
  	    ArrayList upd3 = new ArrayList();
  	    upd1.add(Indexvalue.get(0));
  	    upd2.add("PORTS");
  	    upd3.add("port_Updated");
        Assert.assertTrue(obj.update(upd1, upd2, upd2));
        Fieldvalue.set(0,"port_Updated");

	}

	public static void deleteTest(SwitchPortTable obj, ArrayList<Object> Indexvalue) throws DBException
	{


        Assert.assertTrue(obj.del(Indexvalue));

  	    ArrayList<ArrayList<Object>> listOfRows= null;
  	    listOfRows = obj.find(Indexvalue, null);
  	    Assert.assertTrue(listOfRows.size()!=0);

	}


	public static void flushTest(SwitchPortTable obj, ArrayList<Object> Indexvalue) throws DBException {


        Assert.assertTrue(obj.flush());

        /* Checking by trying to get deleted value */
        ArrayList<ArrayList<Object>> listOfRows= null;
  	    listOfRows = obj.find(Indexvalue, null);
  	    Assert.assertTrue(listOfRows.size()!=0);

	}


}
