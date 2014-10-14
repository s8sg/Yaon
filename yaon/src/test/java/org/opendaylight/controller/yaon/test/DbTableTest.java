package org.opendaylight.controller.yaon.test;

import java.util.ArrayList;

import org.opendaylight.controller.yaon.storage.AgentTable;
import org.opendaylight.controller.yaon.storage.MacTable;
import org.opendaylight.controller.yaon.storage.ONPortTable;
import org.opendaylight.controller.yaon.storage.SliceTable;
import org.opendaylight.controller.yaon.storage.SqlJetDbConnection;

public class DbTableTest {


	public static void initTest(String path)
	{
		try
		{
			/* Initialize sqljetDbConnection connection */
		    SqlJetDbConnection con = SqlJetDbConnection.init(path);
		    
		    /* Agent Table Testing */
      	    AgentTable agt= AgentTable.init(con, "AGENTTABLE");
		    ArrayList<Object> ar = new ArrayList<Object>();
	  	    ar.add("dpid_string");
	  	    ar.add("uri_string");
		    AgentTableTest.addTest(agt, ar);
		    AgentTableTest.reAddTest(agt, ar);
		    AgentTableTest.findTest(agt, ar);
		    AgentTableTest.updateTest(agt, ar);     
		    AgentTableTest.deleteTest(agt, ar);
		    AgentTableTest.findTest(agt, ar);
		    AgentTableTest.addTest(agt, ar);
		    AgentTableTest.flushTest(agt, ar);
		    AgentTableTest.findTest(agt, ar);
      	   
		    
		    /* Slice Table Testing */
            SliceTable st = SliceTable.init(con, "SLICETABLE");
            ArrayList<Object> arr2 = new ArrayList<Object>();
      	    arr2.add("Sliceid_string");
      	    arr2.add("desc_string");
            SliceTableTest.addTest(st,arr2);
            SliceTableTest.reAddTest(st, arr2);
		    SliceTableTest.findTest(st, arr2);
		    SliceTableTest.updateTest(st, arr2);
		    SliceTableTest.findTest(st, arr2);
		    SliceTableTest.deleteTest(st,arr2);
		    SliceTableTest.findTest(st, arr2);
    	    SliceTableTest.addTest(st,arr2);
            SliceTableTest.flushTest(st, arr2);
            SliceTableTest.findTest(st, arr2);
           

		    /* Mac table Testing */
		    MacTable  mt = MacTable.init(con, "MACTABLE");
		    ArrayList<Object> arr3 = new ArrayList<Object>();
	  	   	arr3.add("sliceid_String");
	  	   	arr3.add("portid_String");
	  	   	arr3.add("mac_string");
	  	   	arr3.add("true");
	  	   	MacTableTest.addTest(mt, arr3);
	  	   	MacTableTest.reAddTest(mt, arr3);     
	  	   	MacTableTest.findTest(mt, arr3);
	  	   	MacTableTest.updateTest(mt, arr3);		
	  	   	MacTableTest.deleteTest(mt, arr3);
	  	   	MacTableTest.findTest(mt, arr3);
	  	   	MacTableTest.addTest(mt, arr3);
	  	   	MacTableTest.flushTest(mt, arr3);
		  	MacTableTest.findTest(mt, arr3);


		  	/* ONPort table Testing */
		  	ONPortTable opt = ONPortTable.init(con, "ONPORTTABLE");
		  	ArrayList<Object> arr4 = new ArrayList<Object>();
		  	arr4.add("Sliceid_String");
		  	arr4.add("dpid_String");
		  	arr4.add("portid_String");
		  	arr4.add("portname_String");
		  	arr4.add("vlanid_String");
		  	arr4.add("type_String");
		  	arr4.add("description_string");
		  	arr4.add("Delhi");
		  	ONPortTableTest.addTest(opt,arr4);
		  	ONPortTableTest.reAddTest(opt, arr4);           
		  	ONPortTableTest.findTest(opt, arr4);					
		  	ONPortTableTest.updateTest(opt,arr4);				
		   	ONPortTableTest.deleteTest(opt,arr4);
		   	ONPortTableTest.findTest(opt, arr4);	
		   	ONPortTableTest.addTest(opt,arr4);				
		   	ONPortTableTest.flushTest(opt, arr4);
		   	ONPortTableTest.findTest(opt, arr4);	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}


	}

}
