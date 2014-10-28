package org.opendaylight.controller.yaon;



public interface IYaonService {

	/* Interface To Create a New Slice */
	public boolean addSlice(String sliceId, String desc);
	
	/* Interface to delete Slice */
	public boolean deleteSlice(String sliceId);

	/* Interface To Add a port to a Specific Slice */
	public boolean addPort(String sliceId, String portId, String dataPathId, String portName, String vlan, String desc);
	
	/* Interface To Delete a port from a Specific Slice */
	public boolean deletePort(String sliceId, String portId);

	/* Interface To Add a MAC to a specific port */
	public boolean addMac(String sliceId, String portId, String MAC);
	
	/* Interface to delete a MAC from a specific port */
	public boolean deleteMac(String sliceId, String portId, String MAC);
	
	/* Interface To Register an Agent URI for a Specific Switch */
	public boolean registerAgent(String dataPathId, String agentUri, String tunnelEndpoint);
	
	/* Interface to Register an Multicast for a Specific Slice */
	public boolean registerMulticast(String sliceId, String multicast);
	
	/* Interface to init YaonService global instance */
	public void init();
}
