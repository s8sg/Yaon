package org.opendaylight.controller.yaon.util;

import java.math.BigInteger;

public class YaonUtil {
	
	private static String vxlanPortName = "vxlan";

	public static String generateVxlanPortName(String sliceId){
		
		String vxLanString = vxlanPortName + sliceId;
		
		return vxLanString;
	}

	public static boolean isVxlanPort(String portName){
		if(portName.contains(vxlanPortName)){
			return true;
		}
		return false;
	}

	public static String getSliceIDFromVxlan(String portName) {
		String sliceId = portName.substring(vxlanPortName.length(), portName.length());
		return sliceId;
	}
	
	public static byte[] generateBytesForMac(String mac){
		BigInteger bi = new BigInteger(mac, 16);
		byte[] bytes = bi.toByteArray();
		return bytes;
	}
	
	public static String jsonStringGenForTunnelAdd(String broadcastAddress, String sliceId){
		
		/*
		try {
			logger.info("Debug: " + "Creating new json object");
			JSONObject jsonObj = new JSONObject();
	    	/* Virtual network identifier */
	    	/*logger.info("Debug: " + "Adding vni in the json object");
			jsonObj.put("vni", sliceId);
			/* Add broadcast address */
			/*logger.info("Debug: " + "Adding broadcast in json object");
			jsonObj.put("broadcast", address);
			logger.info("Debug: " + "Creating json string");
			jsonString = jsonObj.toString();
			logger.info("Debug: " + "Tunnel add request successfully created");
		} catch (Exception e) {
			logger.error("Exception while Creating tunnel add JSON request !");
		}*/
		String jsonString = "{" + "\"broadcast\":\"" + broadcastAddress + "\",\"vni\":" + sliceId + "}";
		return jsonString;
	}
	
}
