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

	/*Method that receive client rest api call for register agent  */
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



  	/*Method that receive client rest api call for register multicast  */
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



  	/*Method that receive client rest api call for add slice  */

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



    /*Method that receive client rest api call for add ports  */

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



    /*Method that receive client rest api call for add mac  */

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


    /*Method that receive client rest api call for Slice information */

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
        ArrayList<ArrayList<String>> allDetails=null;
        allDetails=yaonCFEApi.getSlicesInfo();
        try {
         	
    		
    		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
    		Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
    		Document doc = docBuilder.newDocument();		
    		Element rootElement = doc.createElement("SliceInfo");
    		doc.appendChild(rootElement);
    		if(allDetails!=null){
    			for(ArrayList<String> details:allDetails){
    			
    				Element slice = doc.createElement("slice");
    				rootElement.appendChild(slice);
    			
    				Element first = doc.createElement("sliceId");
    				first.appendChild(doc.createTextNode(details.get(0)));
    				slice.appendChild(first);
     
    				Element second = doc.createElement("description");
    				second.appendChild(doc.createTextNode(details.get(1)));
    				slice.appendChild(second);
    			}
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
    
    /*Method that receive client rest api call for Port information */

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
        ArrayList<ArrayList<String>> allDetails=null;
        allDetails=yaonCFEApi.getPortsInfo(sliceId);
        try {
         	
    		
    		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
    		Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
    		Document doc = docBuilder.newDocument();		
    		Element rootElement = doc.createElement("PortInfo");
    		doc.appendChild(rootElement);
    		if(allDetails!=null){
    			for(ArrayList<String> details:allDetails){
    			
    				Element port = doc.createElement("port");
    				rootElement.appendChild(port);
    			
    				Element first = doc.createElement("portId");
    				first.appendChild(doc.createTextNode(details.get(0)));
    				port.appendChild(first);
     
    				Element second = doc.createElement("dpId");
    				second.appendChild(doc.createTextNode(details.get(1)));
    				port.appendChild(second);
     			
    				Element third = doc.createElement("portname");
    				third.appendChild(doc.createTextNode(details.get(2)));
    				port.appendChild(third);
    				
    				Element fourth = doc.createElement("vlanId");
    				fourth.appendChild(doc.createTextNode(details.get(3)));
    				port.appendChild(fourth);
    				
    				Element fiveth = doc.createElement("description");
    				fiveth.appendChild(doc.createTextNode(details.get(4)));
    				port.appendChild(fiveth);
    				
    				Element sixth = doc.createElement("state");
    				sixth.appendChild(doc.createTextNode(details.get(5)));
    				port.appendChild(sixth);
    			}
     	
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
    
    /*Method that receive client rest api call for MAC information */

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
        ArrayList<ArrayList<String>> allDetails=null;
        allDetails=yaonCFEApi.getMacsInfo(sliceId, portId);
        try {
         	
    		
    		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
    		Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
    		Document doc = docBuilder.newDocument();		
    		Element rootElement = doc.createElement("MacInfo");
    		doc.appendChild(rootElement);
    		if(allDetails!=null){
    			for(ArrayList<String> details:allDetails){
    			
    				Element mac = doc.createElement("mac");
    				rootElement.appendChild(mac);
    			
    				Element first = doc.createElement("MACaddress");
    				first.appendChild(doc.createTextNode(details.get(0)));
    				mac.appendChild(first);
     
    				Element second = doc.createElement("state");
    				second.appendChild(doc.createTextNode(details.get(1)));
    				mac.appendChild(second);
    			}
     			
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
    
    
    /*Method that receive client rest api call for MAC information */

    @Path("/GetInfo/Dummy")
    @POST
    @StatusCodes({
        @ResponseCode(code = 200, condition = "Destination reachable"),
        @ResponseCode(code = 503, condition = "Internal error"),
        @ResponseCode(code = 503, condition = "Destination unreachable") })

    public Response getallDummy(InputStream incomingData) {

    	String output=null;
        
		
        try {
         	
    		
    		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
    		Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
    		Document doc = docBuilder.newDocument();		
    		Element rootElement = doc.createElement("Info");
    		doc.appendChild(rootElement);
    		
    		Element slice1 = doc.createElement("slice");
			rootElement.appendChild(slice1);
		
			Element first1 = doc.createElement("sliceId");
			first1.appendChild(doc.createTextNode("2526"));
			slice1.appendChild(first1);		
		
			Element second1 = doc.createElement("description");
			second1.appendChild(doc.createTextNode("slice1"));
			slice1.appendChild(second1);
			
			Element port11= doc.createElement("port");
			slice1.appendChild(port11);
			
			Element firstP11 = doc.createElement("portId");
			firstP11.appendChild(doc.createTextNode("1"));
			port11.appendChild(firstP11);
			
			Element secondP11 = doc.createElement("dpId");
			secondP11.appendChild(doc.createTextNode("00:00:00:00:00:00:00:01"));
			port11.appendChild(secondP11);
			
			Element thirdP11 = doc.createElement("portname");
			thirdP11.appendChild(doc.createTextNode("s1-eth2"));
			port11.appendChild(thirdP11);
			
			Element fourthP11 = doc.createElement("vlanId");
			fourthP11.appendChild(doc.createTextNode("8"));
			port11.appendChild(fourthP11);
			
			Element fivethP11 = doc.createElement("description");
			fivethP11.appendChild(doc.createTextNode("port1"));
			port11.appendChild(fivethP11);
			
			Element sixthP11 = doc.createElement("state");
			sixthP11.appendChild(doc.createTextNode("true"));
			port11.appendChild(sixthP11);

			Element mac111= doc.createElement("mac");
			port11.appendChild(mac111);			

			Element firstM111 = doc.createElement("MACaddress");
			firstM111.appendChild(doc.createTextNode("1223232323"));
			mac111.appendChild(firstM111);

			Element secondM111 = doc.createElement("state");
			secondM111.appendChild(doc.createTextNode("true"));
			mac111.appendChild(secondM111);

			Element mac112= doc.createElement("mac");
			port11.appendChild(mac112);			

			Element firstM112 = doc.createElement("MACaddress");
			firstM112.appendChild(doc.createTextNode("1223232324"));
			mac112.appendChild(firstM112);

			Element secondM112 = doc.createElement("state");
			secondM112.appendChild(doc.createTextNode("true"));
			mac112.appendChild(secondM112);
			
			Element port12= doc.createElement("port");
			slice1.appendChild(port12);
			
			Element firstP12 = doc.createElement("portId");
			firstP12.appendChild(doc.createTextNode("2"));
			port12.appendChild(firstP12);
			
			Element secondP12 = doc.createElement("dpId");
			secondP12.appendChild(doc.createTextNode("00:00:00:00:00:00:00:02"));
			port12.appendChild(secondP12);
			
			Element thirdP12 = doc.createElement("portname");
			thirdP12.appendChild(doc.createTextNode("s2-eth2"));
			port12.appendChild(thirdP12);
			
			Element fourthP12 = doc.createElement("vlanId");
			fourthP12.appendChild(doc.createTextNode("8"));
			port12.appendChild(fourthP12);
			
			Element fivethP12 = doc.createElement("description");
			fivethP12.appendChild(doc.createTextNode("port2"));
			port12.appendChild(fivethP12);
			
			Element sixthP12 = doc.createElement("state");
			sixthP12.appendChild(doc.createTextNode("true"));
			port12.appendChild(sixthP12);

			Element mac121= doc.createElement("mac");
			port12.appendChild(mac121);			

			Element firstM121 = doc.createElement("MACaddress");
			firstM121.appendChild(doc.createTextNode("1223232344"));
			mac121.appendChild(firstM121);

			Element secondM121 = doc.createElement("state");
			secondM121.appendChild(doc.createTextNode("true"));
			mac121.appendChild(secondM121);

			Element mac122= doc.createElement("mac");
			port12.appendChild(mac122);			

			Element firstM122 = doc.createElement("MACaddress");
			firstM122.appendChild(doc.createTextNode("ab23232323"));
			mac122.appendChild(firstM122);

			Element secondM122 = doc.createElement("state");
			secondM122.appendChild(doc.createTextNode("true"));
			mac122.appendChild(secondM122);

			Element slice2 = doc.createElement("slice");
			rootElement.appendChild(slice2);
			
			Element first2 = doc.createElement("sliceId");
			first2.appendChild(doc.createTextNode("2527"));
			slice2.appendChild(first2);		

			Element second2 = doc.createElement("description");
			second2.appendChild(doc.createTextNode("slice2"));
			slice2.appendChild(second2);
			
			Element port21= doc.createElement("port");
			slice2.appendChild(port21);
			
			Element firstP21 = doc.createElement("portId");
			firstP21.appendChild(doc.createTextNode("3"));
			port21.appendChild(firstP21);
			
			Element secondP21 = doc.createElement("dpId");
			secondP21.appendChild(doc.createTextNode("00:00:00:00:00:00:00:03"));
			port21.appendChild(secondP21);
			
			Element thirdP21 = doc.createElement("portname");
			thirdP21.appendChild(doc.createTextNode("s3-eth2"));
			port21.appendChild(thirdP21);
			
			Element fourthP21 = doc.createElement("vlanId");
			fourthP21.appendChild(doc.createTextNode("8"));
			port21.appendChild(fourthP21);
			
			Element fivethP21 = doc.createElement("description");
			fivethP21.appendChild(doc.createTextNode("port1"));
			port21.appendChild(fivethP21);
			
			Element sixthP21 = doc.createElement("state");
			sixthP21.appendChild(doc.createTextNode("true"));
			port21.appendChild(sixthP21);
		
			Element mac211= doc.createElement("mac");
			port21.appendChild(mac211);			

			Element firstM211 = doc.createElement("MACaddress");
			firstM211.appendChild(doc.createTextNode("1423232323"));
			mac211.appendChild(firstM211);

			Element secondM211 = doc.createElement("state");
			secondM211.appendChild(doc.createTextNode("true"));
			mac211.appendChild(secondM211);

			Element mac212= doc.createElement("mac");
			port21.appendChild(mac212);			

			Element firstM212 = doc.createElement("MACaddress");
			firstM212.appendChild(doc.createTextNode("1253232324"));
			mac212.appendChild(firstM212);

			Element secondM212 = doc.createElement("state");
			secondM212.appendChild(doc.createTextNode("true"));
			mac212.appendChild(secondM212);
			
			Element port22= doc.createElement("port");
			slice2.appendChild(port22);
			
			Element firstP22 = doc.createElement("portId");
			firstP22.appendChild(doc.createTextNode("4"));
			port22.appendChild(firstP22);
			
			Element secondP22 = doc.createElement("dpId");
			secondP22.appendChild(doc.createTextNode("00:00:00:00:00:00:00:04"));
			port22.appendChild(secondP22);
			
			Element thirdP22 = doc.createElement("portname");
			thirdP22.appendChild(doc.createTextNode("s4-eth2"));
			port22.appendChild(thirdP22);
			
			Element fourthP22 = doc.createElement("vlanId");
			fourthP22.appendChild(doc.createTextNode("8"));
			port22.appendChild(fourthP22);
			
			Element fivethP22 = doc.createElement("description");
			fivethP22.appendChild(doc.createTextNode("port2"));
			port22.appendChild(fivethP22);
			
			Element sixthP22 = doc.createElement("state");
			sixthP22.appendChild(doc.createTextNode("true"));
			port22.appendChild(sixthP22);

			Element mac221= doc.createElement("mac");
			port22.appendChild(mac221);			

			Element firstM221 = doc.createElement("MACaddress");
			firstM221.appendChild(doc.createTextNode("1223532344"));
			mac221.appendChild(firstM221);

			Element secondM221 = doc.createElement("state");
			secondM221.appendChild(doc.createTextNode("true"));
			mac221.appendChild(secondM221);

			Element mac222= doc.createElement("mac");
			port22.appendChild(mac222);			

			Element firstM222 = doc.createElement("MACaddress");
			firstM222.appendChild(doc.createTextNode("as23232323"));
			mac222.appendChild(firstM222);

			Element secondM222 = doc.createElement("state");
			secondM222.appendChild(doc.createTextNode("true"));
			mac222.appendChild(secondM222);
    	    
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

    /*Method that receive client rest api call for whole network information */

    @Path("/GetInfo")
    @POST
    @StatusCodes({
        @ResponseCode(code = 200, condition = "Destination reachable"),
        @ResponseCode(code = 503, condition = "Internal error"),
        @ResponseCode(code = 503, condition = "Destination unreachable") })

    public Response getAll(InputStream incomingData) {

        YaonCFEApi yaonCFEApi = (YaonCFEApi) ServiceHelper.getGlobalInstance(YaonCFEApi.class, this);
        String output=null;
        
		/* call YaonCFEApi */
        ArrayList<ArrayList<String>> allSlice=null;
        ArrayList<ArrayList<String>> allPorts=null;
        ArrayList<ArrayList<String>> allMac=null;
        allSlice=yaonCFEApi.getSlicesInfo();
        try {
         	
    		
    		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
    		Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
    		Document doc = docBuilder.newDocument();		
    		Element rootElement = doc.createElement("Info");
    		doc.appendChild(rootElement);
    		if(allSlice!=null){
    			for(ArrayList<String> sliceDetails:allSlice){
    			
    				Element slice = doc.createElement("slice");
    				rootElement.appendChild(slice);
    			
    				Element first = doc.createElement("sliceId");
    				first.appendChild(doc.createTextNode(sliceDetails.get(0)));
    				slice.appendChild(first);
     
    				Element second = doc.createElement("description");
    				second.appendChild(doc.createTextNode(sliceDetails.get(1)));
    				slice.appendChild(second);
    				
    				allPorts=yaonCFEApi.getPortsInfo(sliceDetails.get(0));
    				
    				if(allPorts!=null){
    					for(ArrayList<String> portDetails:allPorts){
    						
    						Element port = doc.createElement("port");
    						slice.appendChild(port);
    	    			
    	    				Element firstP = doc.createElement("portId");
    	    				firstP.appendChild(doc.createTextNode(portDetails.get(0)));
    	    				port.appendChild(firstP);
    	     
    	    				Element secondP = doc.createElement("dpId");
    	    				secondP.appendChild(doc.createTextNode(portDetails.get(1)));
    	    				port.appendChild(secondP);
    	     			
    	    				Element thirdP = doc.createElement("portname");
    	    				thirdP.appendChild(doc.createTextNode(portDetails.get(2)));
    	    				port.appendChild(thirdP);
    	    				
    	    				Element fourthP = doc.createElement("vlanId");
    	    				fourthP.appendChild(doc.createTextNode(portDetails.get(3)));
    	    				port.appendChild(fourthP);
    	    				
    	    				Element fivethP = doc.createElement("description");
    	    				fivethP.appendChild(doc.createTextNode(portDetails.get(4)));
    	    				port.appendChild(fivethP);
    	    				
    	    				Element sixthP = doc.createElement("state");
    	    				sixthP.appendChild(doc.createTextNode(portDetails.get(5)));
    	    				port.appendChild(sixthP);
    	    				
    	    				allMac=yaonCFEApi.getMacsInfo(sliceDetails.get(0), portDetails.get(0));
    	    				if(allMac!=null){
    	    	    			for(ArrayList<String> macDetails:allMac){
    	    	    			
    	    	    				Element mac = doc.createElement("mac");
    	    	    				port.appendChild(mac);
    	    	    			
    	    	    				Element firstM = doc.createElement("MACaddress");
    	    	    				firstM.appendChild(doc.createTextNode(macDetails.get(0)));
    	    	    				mac.appendChild(firstM);
    	    	     
    	    	    				Element secondM = doc.createElement("state");
    	    	    				secondM.appendChild(doc.createTextNode(macDetails.get(1)));
    	    	    				mac.appendChild(secondM);
    	    	    			}	
    	    	    		}	
    					}
    				}	
    			}
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
    
  	/*Method that receive client rest api call for delete slice  */

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



   	/*Method that receive client rest api call for delete ports  */

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


  
  	/*Method that receive client rest api call for delete mac  */
  
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

