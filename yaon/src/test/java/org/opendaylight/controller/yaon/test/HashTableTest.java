package org.opendaylight.controller.yaon.test;

import java.util.ArrayList;

import org.opendaylight.controller.yaon.storage.HashDbConnection;
import org.opendaylight.controller.yaon.storage.PortTable;
import org.opendaylight.controller.yaon.storage.SwitchPortTable;
import org.opendaylight.controller.yaon.storage.SwitchTable;


public class HashTableTest {


	public static void initTest()
	{
		try
		{
			HashDbConnection con =  HashDbConnection.init();

			/* SwitchTable Testing */
			SwitchTable st=SwitchTable.init(con, "SwitchTable");
			ArrayList<Object> arr1 = new ArrayList<Object>();
			ArrayList<Object> arr2 = new ArrayList<Object>();
			arr1.add("dpid_string");
			arr2.add("NodeObjectData");
			SwitchTableTest.addTest(st,arr1,arr2);
			SwitchTableTest.findTest(st,arr1,arr2);
			SwitchTableTest.updateTest(st,arr1,arr2);
			SwitchTableTest.deleteTest(st,arr1);
			SwitchTableTest.flushTest(st,arr1);


			/* SwitchPortTable Testing */
			SwitchPortTable spt=SwitchPortTable.init(con, "switch_port");
			ArrayList<Object> ar1 = new ArrayList<Object>();
			ArrayList<Object> ar2 = new ArrayList<Object>();
		    ar1.add("dpid_string");
		    ar2.add("ports_String");
			SwitchPortTableTest.addTest(spt,ar1,ar2);
			SwitchPortTableTest.findTest(spt, ar1,ar2);
			SwitchPortTableTest.updateTest(spt,ar1,ar2);
			SwitchPortTableTest.deleteTest(spt,ar1);
			SwitchPortTableTest.flushTest(spt, ar1);



			/* ortTable Testing */
			PortTable pt=PortTable.init(con, "port");
			ArrayList<Object> a1 = new ArrayList<Object>();
			ArrayList<Object> a2 = new ArrayList<Object>();
		    a1.add("dpid_string");
		    a1.add("portname_string");
		    a2.add("portno_string");
		    a2.add("nodeConnectorObjectData");
			PortTableTest.addTest(pt,a1,a2);
			PortTableTest.findTest(pt, a1,a2);
			PortTableTest.updateTest(pt,a1,a2);      
			PortTableTest.deleteTest(pt,a1);
			PortTableTest.flushTest(pt,a1);

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
