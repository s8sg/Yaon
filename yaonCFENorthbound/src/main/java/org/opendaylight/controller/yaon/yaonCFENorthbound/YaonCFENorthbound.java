package org.opendaylight.controller.yaon.yaonCFENorthbound;

import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.codehaus.enunciate.jaxrs.ResponseCode;
import org.codehaus.enunciate.jaxrs.StatusCodes;
import org.opendaylight.controller.yaon.yaonCFE.YaonCFEApi;
import org.opendaylight.controller.sal.utils.ServiceHelper;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.opendaylight.controller.yaon.yaonCFENorthbound.JsonParsing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.OutputKeys;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Path("/")
public class YaonCFENorthbound {

	private static final Logger logger = LoggerFactory
       		.getLogger(YaonCFENorthbound.class);

	/*Method that receive client rest-api call for register agent  */
    @Path("/agents/{dpId}")
    @POST
    @StatusCodes({
    	@ResponseCode(code = 200, condition = "Destination reachable"),
        @ResponseCode(code = 503, condition = "Internal error"),
        @ResponseCode(code = 503, condition = "Destination unreachable") })

    public Response registerAgent(InputStream incomingData, @PathParam(value = "dpId") String dpId) {

        YaonCFEApi yaonCFEApi = (YaonCFEApi) ServiceHelper.getGlobalInstance(YaonCFEApi.class, this);
        String uri = null;
        String tunnelEndpoint = null;
        	
        StringBuilder str = new StringBuilder();
        
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
            String line = null;
            while ((line = in.readLine()) != null) {
            	str.append(line);
            }
        }
        catch(Exception e) {
        	logger.error("Exception occurred during reading data: " + e);
        }

        /* Get json data */
        JSONObject jsonObject = JsonParsing.Data(str);
        uri = (String) jsonObject.get("control_uri");
        tunnelEndpoint = (String) jsonObject.get("tunnel_endpoint");
        
        /* trimming ip and port */
        uri = uri.substring("http://".length(), uri.length() - 1);
        
        	
       	logger.info("Data Received From client: " + "dpId = "+ dpId + ", AgentUri = " + uri + "and tunnel_endpoint = " + tunnelEndpoint);

       	/* checking for null object */
       	if (yaonCFEApi == null) {
       		logger.error("YaonCFEApi object is null !");
        }

       	/* call YaonCFEApi to register agent */
       	if(yaonCFEApi.registerAgent(dpId, uri, tunnelEndpoint)) {
          	return Response.ok(new String("Agent is registered Successfully !")).build();
        } 
       	else {
         	logger.error("Agent Registration Failed !");		
        }

		
		return Response.ok(new String("Agent Registration Failed !")).build();
  	}



  	/*Method that receive client rest-api call for register multicast  */
 	@Path("/{sliceId}/Multicast")
    @POST
    @StatusCodes({
        @ResponseCode(code = 200, condition = "Destination reachable"),
        @ResponseCode(code = 503, condition = "Internal error"),
        @ResponseCode(code = 503, condition = "Destination unreachable") })

    public Response registerMulticast(InputStream incomingData,@PathParam(value = "sliceId") String sliceId) {

        YaonCFEApi yaonCFEApi = (YaonCFEApi) ServiceHelper.getGlobalInstance(YaonCFEApi.class, this);
        
		String multicast = null;
        
		StringBuilder str = new StringBuilder();
        
		try{
            BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
            String line = null;
            while ((line = in.readLine()) != null) {
                str.append(line);
            }
        }
        catch(Exception e){
        	logger.error("Exception occurred during reading data: " + e);
        }
	
		/* Get json data */
        JSONObject jsonObject = JsonParsing.Data(str);
        multicast = (String) jsonObject.get("multicast");

       	logger.info("Data Received From Client: " +"sliceId="+sliceId+" Multicast="+multicast);

		/*checking for null object*/
        if (yaonCFEApi == null) {
           	logger.error("YaonCFEApi object is null !");
        }

		/* call YaonCFEApi */
       	if(yaonCFEApi.registerMulticast(sliceId,multicast)){
          	return Response.ok(new String("Multicast is added successfully !")).build();
        }
		else {
            logger.error("Multicast Registration Failed !!");
		}

		return Response.ok(new String("Multicast addition Failed !")).build();
 	}



  	/*Method that receive client rest-api call for add slice  */

    @Path("/Slice")
    @POST
    @StatusCodes({
        @ResponseCode(code = 200, condition = "Destination reachable"),
        @ResponseCode(code = 503, condition = "Internal error"),
        @ResponseCode(code = 503, condition = "Destination unreachable") })

    public Response addSlice(InputStream incomingData) {

        YaonCFEApi yaonCFEApi = (YaonCFEApi) ServiceHelper.getGlobalInstance(YaonCFEApi.class, this);
	
		String sliceId=null;
        String des = null;
        
		StringBuilder str = new StringBuilder();
        
		try{
			BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
            String line = null;
            while ((line = in.readLine()) != null) {
            	str.append(line);
            }
        }
        catch(Exception e){
        	logger.info("Exception occurred during reading data: " + e);
        }
	
		/* calling json parsing method */
        JSONObject jsonObject = JsonParsing.Data(str);
        sliceId = String.valueOf((Long) jsonObject.get("id"));
        des = (String) jsonObject.get("description");

       	logger.error("Data Received From Client: " +"sliceId="+sliceId+" Description="+des);

		/* checking for null object */
		if (yaonCFEApi == null) {
           	logger.error("YaonCFEApi object is null !");
        }

		/* call YaonCFEApi */
       	if(yaonCFEApi.addSlice(sliceId,des)){
          	return Response.ok(new String("Slice is added successfuly !")).build();
        }
		else{
        	logger.error("Slice addition Failed !");
        }

		
		return Response.ok(new String("Slice addition Failed !")).build();
	}



    /*Method that receive client rest-api call for add ports  */

    @Path("/{sliceId}/Ports")
    @POST
    @StatusCodes({
        @ResponseCode(code = 200, condition = "Destination reachable"),
        @ResponseCode(code = 503, condition = "Internal error"),
        @ResponseCode(code = 503, condition = "Destination unreachable") })

    public Response addPort(InputStream incomingData,@PathParam(value = "sliceId") String sliceId) {

        YaonCFEApi yaonCFEApi = (YaonCFEApi) ServiceHelper.getGlobalInstance(YaonCFEApi.class, this);
        
		String portId = null;
        String desc = null;
        String dpId = null;
        String portName = null;
        String vlan = null;
        
		StringBuilder str = new StringBuilder();
        
		try{
            BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
            String line = null;
            while ((line = in.readLine()) != null){
            	str.append(line);
            }
        }
        catch (Exception e) {
            logger.error("Exception occurred during reading data: " + e);
        }

		/*calling json parsing method*/
       	JSONObject jsonObject = JsonParsing.Data(str);
        portId = String.valueOf((Long) jsonObject.get("id"));
        dpId = (String) jsonObject.get("datapath_id");
        portName = (String) jsonObject.get("name");
        vlan = String.valueOf((Long) jsonObject.get("vid"));
        desc = (String) jsonObject.get("description");


      	logger.info("Data Received From Client: " + "sliceId= " + sliceId + ", Port ID=" + portId +", DataPath Id=" + dpId + ", Port Name= " + portName + ", vlan=" + vlan + ", Description=" + desc);

		/*checking for null object*/
		if (yaonCFEApi == null){
           	logger.error("YaonCFEApi object is null !");
        }

		/*call YaonCFEApi inteface*/
       	if(yaonCFEApi.addPort(sliceId,portId,dpId,portName,vlan,desc)){
          	return Response.ok(new String("Port is added successfully !")).build();
        } 
		else{
            logger.error("Port addition Failed !");
	    }
       	
		return Response.ok(new String("Port addition Failed !")).build();
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
        	logger.error("Exceptoin occurred during reading data : " + e);
        }

		/*calling json parsing method*/
        JSONObject jsonObject = JsonParsing.Data(str);
        mac=(String) jsonObject.get("address");

        logger.info("Data Received From Client: " +"sliceId="+sliceId+"|| portId="+portId+"|| address="+mac);

		/*checking for null object*/
        if (yaonCFEApi == null)	{
        	logger.error("YaonCFEApi object is null !");
        }

		/*call YaonCFEApi inteface*/
        if(yaonCFEApi.addMac(sliceId,portId,mac)) {
        	return Response.ok(new String("MAC is added successfully !")).build();
        } 
		else {
        	logger.error("MAC addition failed !");
	    }

		return Response.ok(new String("MAC addition failed !")).build();
  	}


    /*Method that receive client rest-api call for Slice information */

    @Path("/GetInfo/Slice")
    @POST
    @StatusCodes({
        @ResponseCode(code = 200, condition = "Destination reachable"),
        @ResponseCode(code = 503, condition = "Internal error"),
        @ResponseCode(code = 503, condition = "Destination unreachable") })

    public Response getSlice(InputStream incomingData) {

        YaonCFEApi yaonCFEApi = (YaonCFEApi) ServiceHelper.getGlobalInstance(YaonCFEApi.class, this);
        String output=null;
        
		/* call YaonCFEApi */
        ArrayList<ArrayList<String>> ret;
        ret=yaonCFEApi.getSlicesInfo();
        try {
         	
    		
    		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
    		Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
    		Document doc = docBuilder.newDocument();		
    		Element rootElement = doc.createElement("SliceInfo");
    		doc.appendChild(rootElement);
    		for(int i=0;i<=ret.size();i++){
    			
    			Element slice = doc.createElement("slice");
    			rootElement.appendChild(slice);
    			
     			Element first = doc.createElement("slice-id");
     			first.appendChild(doc.createTextNode(ret.get(i).get(0)));
     			slice.appendChild(first);
     
     			Element second = doc.createElement("description");
     			second.appendChild(doc.createTextNode(ret.get(i).get(1)));
     			slice.appendChild(second);
     	
    		}
    	    
     		StringWriter stw = new StringWriter(); 
     		transformer.transform(new DOMSource(doc), new StreamResult(stw)); 
    		output = stw.getBuffer().toString();
    	 
    	  } catch (ParserConfigurationException pce) {
    		pce.printStackTrace();
    	  } catch (TransformerException tfe) {
    		tfe.printStackTrace();
    	  }
        
        if(output==null){
        	
        	return Response.ok(new String("Failed To Generate XML !")).build();
        }
		
		return Response.ok(output).build();
	}
    
    /*Method that receive client rest-api call for Port information */

    @Path("/GetInfo/{sliceId}/Port")
    @POST
    @StatusCodes({
        @ResponseCode(code = 200, condition = "Destination reachable"),
        @ResponseCode(code = 503, condition = "Internal error"),
        @ResponseCode(code = 503, condition = "Destination unreachable") })

    public Response getPort(InputStream incomingData,@PathParam(value = "sliceId") String sliceId) {

        YaonCFEApi yaonCFEApi = (YaonCFEApi) ServiceHelper.getGlobalInstance(YaonCFEApi.class, this);
        String output=null;
        
		/* call YaonCFEApi */
        ArrayList<ArrayList<String>> ret;
        ret=yaonCFEApi.getPortsInfo(sliceId);
        try {
         	
    		
    		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
    		Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
    		Document doc = docBuilder.newDocument();		
    		Element rootElement = doc.createElement("PortInfo");
    		doc.appendChild(rootElement);
    		for(int i=0;i<=ret.size();i++){
    			
    			Element port = doc.createElement("port");
    			rootElement.appendChild(port);
    			
     			Element first = doc.createElement("port-id");
     			first.appendChild(doc.createTextNode(ret.get(i).get(0)));
     			port.appendChild(first);
     
     			Element second = doc.createElement("dpId");
     			second.appendChild(doc.createTextNode(ret.get(i).get(1)));
     			port.appendChild(second);
     			
     			Element third = doc.createElement("portname");
     			third.appendChild(doc.createTextNode(ret.get(i).get(2)));
     			port.appendChild(third);
     	
    		}
    	    
     		StringWriter stw = new StringWriter(); 
     		transformer.transform(new DOMSource(doc), new StreamResult(stw)); 
    		output = stw.getBuffer().toString();
    	 
    	  } catch (ParserConfigurationException pce) {
    		pce.printStackTrace();
    	  } catch (TransformerException tfe) {
    		tfe.printStackTrace();
    	  }
        
        if(output==null){
        	
        	return Response.ok(new String("Failed To Generate XML !")).build();
        }
		
		return Response.ok(output).build();
	}
    
    /*Method that receive client rest-api call for MAC information */

    @Path("/GetInfo/{sliceId}/{portId}/MAC")
    @POST
    @StatusCodes({
        @ResponseCode(code = 200, condition = "Destination reachable"),
        @ResponseCode(code = 503, condition = "Internal error"),
        @ResponseCode(code = 503, condition = "Destination unreachable") })

    public Response getMAC(InputStream incomingData,@PathParam(value = "sliceId") String sliceId,@PathParam(value = "portId") String portId) {

        YaonCFEApi yaonCFEApi = (YaonCFEApi) ServiceHelper.getGlobalInstance(YaonCFEApi.class, this);
        String output=null;
        
		/* call YaonCFEApi */
        ArrayList<ArrayList<String>> ret;
        ret=yaonCFEApi.getMacsInfo(sliceId, portId);
        try {
         	
    		
    		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
    		Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
    		Document doc = docBuilder.newDocument();		
    		Element rootElement = doc.createElement("MacInfo");
    		doc.appendChild(rootElement);
    		for(int i=0;i<=ret.size();i++){
    			
    			Element port = doc.createElement("mac");
    			rootElement.appendChild(port);
    			
     			Element first = doc.createElement("address");
     			first.appendChild(doc.createTextNode(ret.get(i).get(0)));
     			port.appendChild(first);
     
     			Element second = doc.createElement("state");
     			second.appendChild(doc.createTextNode(ret.get(i).get(1)));
     			port.appendChild(second);
     			
    		}
    	    
     		StringWriter stw = new StringWriter(); 
     		transformer.transform(new DOMSource(doc), new StreamResult(stw)); 
    		output = stw.getBuffer().toString();
    	 
    	  } catch (ParserConfigurationException pce) {
    		pce.printStackTrace();
    	  } catch (TransformerException tfe) {
    		tfe.printStackTrace();
    	  }
        
        if(output==null){
        	
        	return Response.ok(new String("Failed To Generate XML !")).build();
        }
		
		return Response.ok(output).build();
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
        	logger.info("YaonCFEApi object is null !");
        }

		/*call YaonCFEApi inteface*/
       	if(yaonCFEApi.deleteSlice(sliceId)) {
          	return Response.ok(new String("slice Deleted")).build();
        } 
		else{
            logger.info("Slice Deletion Failed !");
        }

		return Response.ok(new String("Slice Deletion Failed !")).build();
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
           	logger.error("YaonCFEApi object is null !");
        }

		/*call YaonCFEApi inteface*/
       	if(yaonCFEApi.deletePort(sliceId,portId)){
          	return Response.ok(new String("Port is deleted successfully !")).build();
 		} 
		else{
            logger.error("Port Deletion Failed !");
        }

		return Response.ok(new String("Port Deletion Failed !")).build();
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
           	logger.error("YaonCFEApi object is null !");
        }

		/*call YaonCFEApi inteface*/
       	if(yaonCFEApi.deleteMac(sliceId,portId,MAC)){
        	return Response.ok(new String("MAC is deleted successfully !")).build();
        } 
		else{
            logger.error("MAC Deletion Failed !");
        }

		return Response.ok(new String("MAC Deletion Failed !")).build();
    }


}

