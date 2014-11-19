package org.opendaylight.controller.yaon.yaonCFE;

import java.util.ArrayList;

import org.opendaylight.controller.yaon.IYaonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CFEImpl implements YaonCFEApi{

	private static final Logger logger = LoggerFactory
            .getLogger(CFEImpl.class);

	/* External services */
	static private IYaonService yaonService = null;


	/* Default Constructor */
	public CFEImpl() {
		super();
		logger.info("Configuraton FroentEnd getting instancetiated !");
	}

	/* Setter and UnSetter of External Services */

	void setYaonService(IYaonService s) {
		logger.info("YaonServices is set!");
        yaonService = s;
	}

	void UnsetYaonService(IYaonService s) {
		if (yaonService == s) {
            logger.info("YaonServices is removed!");
            yaonService = null;
        }
	}


	void init() {
		logger.info("Configuration Front End has Initialized !");
	}

	void destroy() {
		logger.info("Configuration Front End has destroyed!");
	}

	 void start() {
	    logger.info("Configuration Front End has started !");


	 }

	 void stop() {
		logger.info("CFE Stopped!!");
	 }

	@Override
	public boolean registerAgent(String dpId, String uri, String tunnelEndpoint) {
		Boolean ret=false;
		logger.info("CFE register Agent call");
		ret = yaonService.registerAgent(dpId, uri, tunnelEndpoint);
		return ret;
	}


	@Override
	public boolean addSlice(String sliceId, String desc) {
		Boolean ret=false;
		logger.info("CFE add slice call");
		ret = yaonService.addSlice(sliceId,desc);
		return ret;
	}

	@Override
	public boolean deleteSlice(String sliceId) {
		Boolean ret=false;
		logger.info("CFE delete slice call");
		ret = yaonService.deleteSlice(sliceId);
		return ret;
	}

	@Override
	public boolean addPort(String sliceId, String portId, String dataPathId,String portName, String vlan, String desc) {
		Boolean ret=false;
		logger.info("CFE add port call");
		ret = yaonService.addPort(sliceId,portId,dataPathId,portName,vlan,desc);
		return ret;
	}

	@Override
	public boolean deletePort(String sliceId, String portId) {
		Boolean ret=false;
		logger.info("CFE delete port call");
		ret = yaonService.deletePort(sliceId,portId);
		return ret;
	}

	@Override
	public boolean addMac(String sliceId, String portId, String MAC) {
		Boolean ret=false;
		logger.info("CFE add MAC call");
		ret = yaonService.addMac(sliceId,portId,MAC);
		return ret;
	}

	@Override
	public boolean deleteMac(String sliceId, String portId, String MAC) {
		Boolean ret=false;
		logger.info("CFE delete MAC call");
		ret = yaonService.deleteMac(sliceId,portId,MAC);
		return ret;
	}

	@Override
	public boolean registerMulticast(String sliceId, String multicast) {
		Boolean ret=false;
		logger.info("CFE register mulicast call");
		ret = yaonService.registerMulticast(sliceId,multicast);
		return ret;
	}

	@Override
	public ArrayList<ArrayList<String>> getSlicesInfo() {
		ArrayList<ArrayList<String>> ret;
		ret=yaonService.getSlicesInfo();
		return ret;
	}

	@Override
	public ArrayList<ArrayList<String>> getPortsInfo(String sliceId) {
		ArrayList<ArrayList<String>> ret;
		ret=yaonService.getPortsInfo(sliceId);
		return ret;
	}

	@Override
	public ArrayList<ArrayList<String>> getMacsInfo(String sliceId,
			String portId) {
		ArrayList<ArrayList<String>> ret;
		ret=yaonService.getMacsInfo(sliceId, portId);
		return ret;
	}
}

