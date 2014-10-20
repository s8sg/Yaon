package org.opendaylight.controller.yaon.yaonCFENorthbound;

import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.codehaus.enunciate.jaxrs.ResponseCode;
import org.codehaus.enunciate.jaxrs.StatusCodes;
import org.opendaylight.controller.yaon.yaonCFE.YaonCFEApi;
import org.opendaylight.controller.sal.utils.ServiceHelper;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.opendaylight.controller.yaon.yaonCFENorthbound.JsonParsing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/")
public class YaonCFENorthbound {

	private static final Logger logger = LoggerFactory
       		.getLogger(YaonCFENorthbound.class);

	/*Method that recieve client restapi call for register agent  */

    	@Path("/{dpId}/Agent")
    	@POST
    	@StatusCodes({
        	@ResponseCode(code = 200, condition = "Destination reachable"),
        	@ResponseCode(code = 503, condition = "Internal error"),
        	@ResponseCode(code = 503, condition = "Destination unreachable") })

        public Response registerAgent(InputStream incomingData,@PathParam(value = "dpId") String dpId) {

        	YaonCFEApi yaonCFEApi = (YaonCFEApi) ServiceHelper.getGlobalInstance(YaonCFEApi.class, this);
        
		/*variable decleration*/
		String uri=null;
        
		StringBuilder str = new StringBuilder();
        
		try {
             		BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
                	String line = null;
                	while ((line = in.readLine()) != null) {
                		str.append(line);
                	}
        	}
        	catch(Exception e) {
        		logger.info("Exception occurred during reading data"+e);
        	}

		/*calling json parsing method*/
		JSONObject jsonObject = JsonParsing.Data(str);
        	uri=(String) jsonObject.get("agent_uri");

       		logger.info("Data Received From client: " +"dpId="+dpId+" AgentUri="+uri);

		/*checking for null object*/
       		if (yaonCFEApi == null) {
       			logger.info("YaonCFEApi object is null");
        	}

		/*call YaonCFEApi inteface*/
       		if(yaonCFEApi.registerAgent(dpId,uri)) {
          		return Response.ok(new String("Agent Register")).build();
        	} 
		else {
         		logger.info("Register Agent Failed!!!!!");
	    		//return Response.ok(new String("Agent Register Failed!!!!")).build();
        	}

		// return HTTP response 200 in case of success
        	//return Response.status(200).entity(str.toString()).build();
		return Response.ok(new String("Agent Register Failed!!!!")).build();
  	}



  	/*Method that recieve client restapi call for register multicast  */

 	@Path("/{sliceId}/Multicast")
    	@POST
    	@StatusCodes({
        	@ResponseCode(code = 200, condition = "Destination reachable"),
        	@ResponseCode(code = 503, condition = "Internal error"),
        	@ResponseCode(code = 503, condition = "Destination unreachable") })

        public Response registerMulticast(InputStream incomingData,@PathParam(value = "sliceId") String sliceId) {

        	YaonCFEApi yaonCFEApi = (YaonCFEApi) ServiceHelper.getGlobalInstance(YaonCFEApi.class, this);
        
		/*variable decleration*/
		String multicast=null;
        
		StringBuilder str = new StringBuilder();
        
		try{
                	BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
                	String line = null;
                	while ((line = in.readLine()) != null) {
                		str.append(line);
                	}
        	}
        	catch(Exception e){
        		logger.info("Exception occurred during reading data"+e);
        	}
	
		/*calling json parsing method*/
        	JSONObject jsonObject = JsonParsing.Data(str);
        	multicast=(String) jsonObject.get("multicast");

       		logger.info("Data Received From Client: " +"sliceId="+sliceId+" Multicast="+multicast);

		/*checking for null object*/
         	if (yaonCFEApi == null) {
           		logger.info("YaonCFEApi object is null");
        	}

		/*call YaonCFEApi inteface*/
       		if(yaonCFEApi.registerMulticast(sliceId,multicast)){
          		return Response.ok(new String("Multicast Added")).build();
        	}
		else {
            		logger.info("Register Multicast Failed!!!!");
	    		//return Response.ok(new String("Register Multicast Failed!!!!")).build();
		}

		// return HTTP response 200 in case of success
        	//return Response.status(200).entity(str.toString()).build();
		return Response.ok(new String("Register Multicast Failed!!!!")).build();
 	}



  	/*Method that recieve client restapi call for add slice  */

    	@Path("/Slice")
    	@POST
    	@StatusCodes({
        	@ResponseCode(code = 200, condition = "Destination reachable"),
        	@ResponseCode(code = 503, condition = "Internal error"),
        	@ResponseCode(code = 503, condition = "Destination unreachable") })

     	public Response addSlice(InputStream incomingData) {

        	YaonCFEApi yaonCFEApi = (YaonCFEApi) ServiceHelper.getGlobalInstance(YaonCFEApi.class, this);
	
		/*variable decleration*/
		String sliceId=null;
        	String des=null;
        
		StringBuilder str = new StringBuilder();
        
		try{
                	BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
                	String line = null;
                	while ((line = in.readLine()) != null) {
                		str.append(line);
                	}
        	}
        	catch(Exception e){
        		logger.info("Exception occurred during reading data"+e);
        	}
	
		/*calling json parsing method*/
        	JSONObject jsonObject = JsonParsing.Data(str);
        	sliceId=String.valueOf((Long) jsonObject.get("id"));
        	des=(String) jsonObject.get("description");

       		logger.info("Data Received From Client: " +"sliceId="+sliceId+" Description="+des);

		/*checking for null object*/
		if (yaonCFEApi == null) {
           		logger.info("YaonCFEApi object is null");
        	}

		/*call YaonCFEApi inteface*/
       		if(yaonCFEApi.addSlice(sliceId,des)){
          		return Response.ok(new String("Slice Added")).build();
        	}
		else{
            		logger.info("Add Slice Failed!!!!! ");
	    		//return Response.ok(new String("Add Slice Failed!!!!")).build();
        	}

		// return HTTP response 200 in case of success
        	//return Response.status(200).entity(str.toString()).build();
		return Response.ok(new String("Add Slice Failed!!!!")).build();
	}



    	/*Method that recieve client restapi call for add ports  */

    	@Path("/{sliceId}/Ports")
    	@POST
    	@StatusCodes({
               	@ResponseCode(code = 200, condition = "Destination reachable"),
               	@ResponseCode(code = 503, condition = "Internal error"),
               	@ResponseCode(code = 503, condition = "Destination unreachable") })

     	public Response addPort(InputStream incomingData,@PathParam(value = "sliceId") String sliceId) {

        	YaonCFEApi yaonCFEApi = (YaonCFEApi) ServiceHelper.getGlobalInstance(YaonCFEApi.class, this);
        
		/*variable decleration*/
		String portId=null;
        	String desc=null;
        	String dpId=null;
        	String portName=null;
        	String vlan=null;
        
		StringBuilder str = new StringBuilder();
        
		try{
            		BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
            		String line = null;
            		while ((line = in.readLine()) != null){
                		str.append(line);
            		}
        	}
        	catch (Exception e) {
            		logger.info("Exception occurred during reading data"+e);
        	}

		/*calling json parsing method*/
       		JSONObject jsonObject = JsonParsing.Data(str);
        	portId=String.valueOf((Long) jsonObject.get("id"));
        	dpId=(String) jsonObject.get("datapath_id");
        	portName=(String) jsonObject.get("name");
        	vlan=String.valueOf((Long) jsonObject.get("vid"));
        	desc=(String) jsonObject.get("description");


      		logger.info("Data Received From Client: " +"sliceId="+sliceId+"|| Port ID="+portId+"|| DataPath Id="+dpId+"|| Port Name= "+portName+"|| vlan="+vlan+"|| Description="+desc);

		/*checking for null object*/
		if (yaonCFEApi == null){
           		logger.info("YaonCFEApi object is null");
        	}

		/*call YaonCFEApi inteface*/
       		if(yaonCFEApi.addPort(sliceId,portId,dpId,portName,vlan,desc)){
          		return Response.ok(new String("Port Added")).build();
        	} 
		else{
            		logger.info("Add Port Failed!!!! ");
	    		//return Response.ok(new String("Add Port Failed!!!!")).build();
        	}

		// return HTTP response 200 in case of success
        	//return Response.status(200).entity(str.toString()).build();
		return Response.ok(new String("Add Port Failed!!!!")).build();
	}



    	/*Method that recieve client restapi call for add mac  */

    	@Path("/{sliceId}/Ports/{portId}/MAC")
    	@POST
    	@StatusCodes({
        	@ResponseCode(code = 200, condition = "Destination reachable"),
        	@ResponseCode(code = 503, condition = "Internal error"),
        	@ResponseCode(code = 503, condition = "Destination unreachable") })

     	public Response addMac(InputStream incomingData,@PathParam(value = "sliceId") String sliceId,@PathParam(value = "portId") String portId) {

        	YaonCFEApi yaonCFEApi = (YaonCFEApi) ServiceHelper.getGlobalInstance(YaonCFEApi.class, this);
        
		/*variable decleration*/
		String mac=null;

        	StringBuilder str = new StringBuilder();
        	try{
            		BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
            		String line = null;
            		while ((line = in.readLine()) != null){
                		str.append(line);
            		}
        	}
        	catch(Exception e){
        		logger.info("Exceptoin occurred during reading data"+e);
        	}

		/*calling json parsing method*/
        	JSONObject jsonObject = JsonParsing.Data(str);
        	mac=(String) jsonObject.get("address");

        	logger.info("Data Received From Client: " +"sliceId="+sliceId+"|| portId="+portId+"|| address="+mac);

		/*checking for null object*/
        	if (yaonCFEApi == null)	{
           		logger.info("YaonCFEApi object is null");
        	}

		/*call YaonCFEApi inteface*/
        	if(yaonCFEApi.addMac(sliceId,portId,mac)) {
          		return Response.ok(new String("MAC Added")).build();
        	} 
		else {
            		logger.info("Add MAC Failed!!!! ");
	    		//return Response.ok(new String("MAC Add Failed!!!!")).build();
 		}

		// return HTTP response 200 in case of success
        	//return Response.status(200).entity(str.toString()).build();
		return Response.ok(new String("MAC Add Failed!!!!")).build();
  	}



  	/*Method that recieve client restapi call for delete slice  */

    	@Path("/{sliceId}")
    	@DELETE
    	@StatusCodes({
        	@ResponseCode(code = 200, condition = "Destination reachable"),
        	@ResponseCode(code = 503, condition = "Internal error"),
        	@ResponseCode(code = 503, condition = "Destination unreachable") })

      	public Response deleteSlice(@PathParam(value = "sliceId") String sliceId) {

        	YaonCFEApi yaonCFEApi = (YaonCFEApi) ServiceHelper.getGlobalInstance(YaonCFEApi.class, this);

		/*checking for null object*/
        	if (yaonCFEApi == null){
        		logger.info("YaonCFEApi object is null");
        	}

        	// return HTTP response 200 in case of success

		/*call YaonCFEApi inteface*/
       		if(yaonCFEApi.deleteSlice(sliceId)) {
          		return Response.ok(new String("slice Deleted")).build();
        	} 
		else{
            		logger.info("Slice Deletion Failed!!!!");
	    		//return Response.ok(new String("Slice Deletion Failed!!!!")).build();
        	}

        	//return Response.status(200).entity("reachable").build();
		return Response.ok(new String("Slice Deletion Failed!!!!")).build();
   	}



   	/*Method that recieve client restapi call for delete ports  */

    	@Path("/{sliceId}/Ports/{portId}")
    	@DELETE
    	@StatusCodes({
        	@ResponseCode(code = 200, condition = "Destination reachable"),
        	@ResponseCode(code = 503, condition = "Internal error"),
        	@ResponseCode(code = 503, condition = "Destination unreachable") })

      	public Response deletePort(@PathParam(value = "sliceId") String sliceId,@PathParam(value = "portId") String portId) {

        	YaonCFEApi yaonCFEApi = (YaonCFEApi) ServiceHelper.getGlobalInstance(YaonCFEApi.class, this);

		/*checking for null object*/
        	if (yaonCFEApi == null){
           		logger.info("YaonCFEApi object is null");
        	}

		/*call YaonCFEApi inteface*/
       		if(yaonCFEApi.deletePort(sliceId,portId)){
          		return Response.ok(new String("Port Deleted")).build();
 		} 
		else{
            		logger.info("Port Deletion Failed!!!! ");
	    		//return Response.ok(new String("Port Deletion Failed!!!!")).build();
        	}

		// return HTTP response 200 in case of success
        	//return Response.status(200).entity("reachable").build();
		return Response.ok(new String("Port Deletion Failed!!!!")).build();
    	}


  
  	/*Method that recieve client restapi call for delete mac  */
  
    	@Path("/{sliceId}/Ports/{portId}/MAC/{MAC}")
    	@DELETE
    	@StatusCodes({
        	@ResponseCode(code = 200, condition = "Destination reachable"),
        	@ResponseCode(code = 503, condition = "Internal error"),
        	@ResponseCode(code = 503, condition = "Destination unreachable") })

      	public Response deleteMac(@PathParam(value = "sliceId") String sliceId,@PathParam(value = "portId") String portId,@PathParam(value = "MAC") String MAC) {

        	YaonCFEApi yaonCFEApi = (YaonCFEApi) ServiceHelper.getGlobalInstance(YaonCFEApi.class, this);

		/*checking for null object*/
        	if (yaonCFEApi == null){
           		logger.info("YaonCFEApi object is null");
        	}

		/*call YaonCFEApi inteface*/
       		if(yaonCFEApi.deleteMac(sliceId,portId,MAC)){
        		return Response.ok(new String("MAC Deleted")).build();
        	} 
		else{
            		logger.info("MAC Deletion Failed!!!!! ");
	    		//return Response.ok(new String("MAC Deletion Failed!!!!")).build();
        	}

		// return HTTP response 200 in case of success
        	//return Response.status(200).entity("reachable").build();
		return Response.ok(new String("MAC Deletion Failed!!!!")).build();
    	}


}

