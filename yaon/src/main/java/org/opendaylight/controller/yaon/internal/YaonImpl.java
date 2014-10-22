/*
 * Copyright (C) 2014 SDN Hub

 Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.
 You may not use this file except in compliance with this License.
 You may obtain a copy of the License at

    http://www.gnu.org/licenses/gpl-3.0.txt

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 implied.

 *
 */

package org.opendaylight.controller.yaon.internal;

import java.util.HashMap;
import java.util.Map;

import org.opendaylight.controller.forwardingrulesmanager.IForwardingRulesManager;
import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.core.NodeConnector;
import org.opendaylight.controller.sal.core.Property;
import org.opendaylight.controller.sal.core.UpdateType;
import org.opendaylight.controller.sal.flowprogrammer.IFlowProgrammerService;
import org.opendaylight.controller.sal.match.Match;
import org.opendaylight.controller.sal.packet.IDataPacketService;
import org.opendaylight.controller.sal.packet.IListenDataPacket;
import org.opendaylight.controller.sal.packet.Packet;
import org.opendaylight.controller.sal.packet.PacketResult;
import org.opendaylight.controller.sal.packet.RawPacket;
import org.opendaylight.controller.statisticsmanager.IStatisticsManager;
import org.opendaylight.controller.switchmanager.IInventoryListener;
import org.opendaylight.controller.switchmanager.ISwitchManager;
import org.opendaylight.controller.yaon.IYaonService;
import org.opendaylight.controller.yaon.coreModule.AgentManager;
import org.opendaylight.controller.yaon.coreModule.FlowManager;
import org.opendaylight.controller.yaon.coreModule.InternalModule;
import org.opendaylight.controller.yaon.coreModule.ModuleName;
import org.opendaylight.controller.yaon.coreModule.ServiceHolder;
import org.opendaylight.controller.yaon.coreModule.SliceDBManager;
import org.opendaylight.controller.yaon.coreModule.SliceManager;
import org.opendaylight.controller.yaon.coreModule.SwitchEventManager;
import org.opendaylight.controller.yaon.coreModule.TopoDBManager;
import org.opendaylight.controller.yaon.util.OdlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YaonImpl implements IYaonService, IInventoryListener, IListenDataPacket{

	private static final Logger logger = LoggerFactory
            .getLogger(YaonImpl.class);
	int thread_no = 0;
	
	/* External services */
	private ISwitchManager switchManager = null;
    private IFlowProgrammerService flowProgrammer = null;
    private IDataPacketService dataPacketService = null;
    private IForwardingRulesManager forwardingRulesManager = null;
	private IStatisticsManager statManager = null;

	/* Internal Project Globals */
	private ServiceHolder services = null;

	/* Internal Modules */
	private HashMap<String, InternalModule> modules = null;
	private SwitchEventManager switchEventManager = null;
	private SliceManager sliceManager = null;

	/* Default Constructor */
	public YaonImpl() {
		super();
		logger.info("Vnm getting instancetiated !");
	}

	/* Setter and UnSetter of External Services */
	
	void setDataPacketService(IDataPacketService s) {
    	logger.info("Datapacketservice set");
        this.dataPacketService = s;
    }

    void unsetDataPacketService(IDataPacketService s) {
    	logger.info("Datapacketservice reset");
        if (this.dataPacketService == s) {
            this.dataPacketService = null;
        }
    }

    public void setFlowProgrammerService(IFlowProgrammerService s) {
    	logger.info("FlowProgrammer is set!");
        this.flowProgrammer = s;
    }

    public void unsetFlowProgrammerService(IFlowProgrammerService s) {
    	logger.info("FlowProgrammer is removed!");
        if (this.flowProgrammer == s) {
            this.flowProgrammer = null;
        }
    }

    void setSwitchManager(ISwitchManager s) {
        logger.info("SwitchManager is set!");
        this.switchManager = s;
    }

    void unsetSwitchManager(ISwitchManager s) {
        if (this.switchManager == s) {
            logger.info("SwitchManager is removed!");
            this.switchManager = null;
        }
    }

    void setForwardingRulesManager(IForwardingRulesManager s) {
    	logger.info("ForwardingRulesManager is set!");
    	forwardingRulesManager = s;
    }

    void unsetForwardingRulesManager(IForwardingRulesManager s) {
    	if (this.forwardingRulesManager == s) {
            logger.info("Controller is removed!");
            this.forwardingRulesManager = null;
        }
    }

    void setStatisticsManager(IStatisticsManager s) {
    	logger.info("Statistics Manager is set!");
    	statManager = s;
    }

    void unsetStatisticsManager(IStatisticsManager s) {
    	if (this.statManager  == s) {
            logger.info("Statistics Manager is removed!");
            this.statManager = null;
        }
    }

    /* Internal function */
    
    private boolean initiateModuleDependency(HashMap<String, InternalModule> modules) {
    	
    	switchEventManager = (SwitchEventManager)modules.get(ModuleName.SwitchEventManager.toString());
    	sliceManager = (SliceManager)modules.get(ModuleName.SliceManager.toString());
    	
		if(switchEventManager == null || sliceManager == null){
			return false;
		}
		
		return true;
	}

    /* Function to be called by ODL */

    /**
     * Function called by the dependency manager when all the required
     * dependencies are satisfied
     *
     */
    public void init() {

    	/* Initialize YAON components */
    	
    	logger.info("YAON getting Initilizing by Dependency Manager!");
    	SwitchEventManager switchEventManager = null;
    	SliceManager sliceManager = null;
    	FlowManager flowManager = null;
    	AgentManager agentManager = null;
    	SliceDBManager sliceDbManager = null;
    	TopoDBManager topoDbManager = null;    	
    	
    	/* Initialize Service Holder */
    	
    	logger.info("Initializing Services Pojo!");
    	services = new ServiceHolder();
    	services.setDataPacketService(dataPacketService);
    	services.setFlowProgrammer(flowProgrammer);
    	services.setForwardingRulesManager(forwardingRulesManager);
    	services.setSwitchManager(switchManager);
    	services.setStatManager(statManager);
    	
    	/* Initialize Module MAP and add all Module */

    	logger.info("Initializing Module Map!");
    	modules = new HashMap<String, InternalModule>();

    	logger.info("Initializing SliceDBManager!");
    	sliceDbManager = SliceDBManager.init();    	
    	modules.put(ModuleName.SliceDBManager.toString(), sliceDbManager);
    	
    	logger.info("Initializing TopologyDBmanager!");
    	topoDbManager = TopoDBManager.init();
    	modules.put(ModuleName.TopoDBManager.toString(), topoDbManager);

    	logger.info("Initializing Switch Event Manager!");
    	switchEventManager = new SwitchEventManager();
    	modules.put(ModuleName.SwitchEventManager.toString(), switchEventManager);

    	logger.info("Initializing Slice Manager!");
    	sliceManager = new SliceManager();
    	modules.put(ModuleName.SliceManager.toString(), sliceManager);
    	
    	logger.info("Initializing Flow Manager!");
    	flowManager = new FlowManager();
    	modules.put(ModuleName.FlowManager.toString(), flowManager);
    	
    	logger.info("Initializing Overlay Network Manager!");
    	agentManager = new AgentManager();
    	modules.put(ModuleName.AgentManager.toString(), agentManager);
    	    	
    	/* Initiate Service Dependency Check for Each Module */
    	
    	if(!sliceDbManager.initiateServicesDepedency(services)){
    		logger.error("Slice Db Manager Service dependency not satisfied");
    		return;
    	}
    	if(!topoDbManager.initiateServicesDepedency(services)){
    		logger.error("TOPO Db Manager Service dependency not satisfied");
    		return;
    	}
    	if(!switchEventManager.initiateServicesDepedency(services)){
    		logger.error("Switch Event Manager Service dependency not satisfied");
    		return;
    	}
    	if(!flowManager.initiateServicesDepedency(services)){
    		logger.error("Flow Manager Service dependency not satisfied");
    		return;
    	}
    	if(!sliceManager.initiateServicesDepedency(services)){
    		logger.error("Slice Manager Service dependency not satisfied");
    		return;
    	}
    	
    	/* Initiate Other Module dependency Check for each Module */
    	
    	if(!sliceDbManager.initiateModuleDependency(modules)){
    		logger.error("Slice Db Manager Module dependency not satisfied");
    		return;
    	}
    	if(!topoDbManager.initiateModuleDependency(modules)){
    		logger.error("Topo Db Manager Module dependency not satisfied");
    		return;
    	}
    	if(!flowManager.initiateModuleDependency(modules)){
    		logger.error("Flow Manager Module dependency not satisfied");
    		return;
    	}
    	if(!agentManager.initiateModuleDependency(modules)){
    		logger.error("Overlay Network Manager Module dependency not satisfied");
    		return;
    	}
    	if (!switchEventManager.initiateModuleDependency(modules)){
    		logger.error("Switch Event Manager Module dependency not satisfied");
    		return;
    	}
    	if(!sliceManager.initiateModuleDependency(modules)){
    		logger.error("Slice Manager Module dependnecy not satisfied");
    		return;
    	}
    	if(!this.initiateModuleDependency(modules)){
    		logger.error("Yaon Module dependency not satisfied");
    		return;
    	}
    }

	/**
     * Function called by the dependency manager when at least one
     * dependency become unsatisfied or when the component is shutting
     * down because for example bundle is being stopped.
     *
     */
    void destroy() {
        logger.error("YAON has destroyed!");
    }

    /**
     * Function called by dependency manager after "init ()" is called
     * and after the services provided by the class are registered in
     * the service registry
     *
     */
    void start() {
    	logger.info("YAON has started!");
    }

    /**
     * Function called by the dependency manager before the services
     * exported by the component are unregistered, this will be
     * followed by a "destroy ()" calls
     *
     */
    void stop() {
        logger.info("Stopped");
    }



    /* InventoryListener service Interface - internal use only, exposed To ODL */

	@Override /* ODL NODE notification */
	public void notifyNode(Node node, UpdateType type, Map<String, Property> propMap) {

        if(node == null) {
            logger.warn("New Node Notification : Node is null ");
            return;
        }
        
        if(type == null) {
        	logger.warn("New Node Notification : Type is null ");
            return;
        }
        
        /* Property map currently not important for Node notification handle
         * For switch deletion property is blank
        if(propMap == null) {
        	logger.warn("New Node Notification : property Map is null ");
            return;
        }
        */
        
        /* check if OpenFlow switch (We only support OpenFlow switches for now) */
        if(OdlUtil.isOpenFlowSwitch(node)){
	        logger.info("OpenFlow node {} notification", node);	        
	        /* Send Openflow node notification to switchEventManager */
	        boolean ret = switchEventManager.switchChanged(node, type, propMap);
	        if(ret == false){
	        	logger.error("Node notification could not be processed successfully : {}", node);
	        }
        }
        else {
        	logger.info("Ignoring Non Openflow Node notification !");
        }
	}

	@Override /* ODL NODECONNECTOR notification */
	public void notifyNodeConnector(NodeConnector nodeConnector, UpdateType type, Map<String, Property> propMap) {

        if (nodeConnector == null) {
            logger.warn("New NodeConnector Notification : NodeConnector is null");
            return;
        }
        
        if(type == null){
        	logger.warn("New NodeConnector Notification : Type is null");
            return;
        }

        if(propMap == null){
        	logger.warn("New NodeConnector Notification : Property Map is null");
            return;
        }
        
        /* Check if node of Openflow node (We only support OpenFlow switches for now) */
        if(OdlUtil.isOpenFlowSwitchPort(nodeConnector)){
        	logger.info("OpenFlow nodeConnector {} notification", nodeConnector);
        	/* Check if special node (Special port notification is ignored ) */
        	if(switchManager.isSpecial(nodeConnector)){
        		logger.info("Ignoreing special node connector: {}", nodeConnector);
            }
        	else {
        		/* Send Notification to the switchEventManager */
        		boolean ret = switchEventManager.portChanged(nodeConnector, type, propMap);
        		if(ret == false) {
        			logger.error("Node Connector notification could not be processed successfully : {}", nodeConnector);
        		}
        	}
        }
        else {
        	logger.info("Ignoring Non Openflow NodeConnector notification !");
        }
	}

	/* IListenDataPacket services Interface - internal use only, not exposed */

	@Override /* ODL Packet In notification */
	public PacketResult receiveDataPacket(RawPacket pkt) {

		logger.warn("New Unexpected Data Packet Notification reached to YAON !");
		
		/* Check if pkt is null */
		if(pkt == null){
			logger.info("Packet is null !");
			return PacketResult.CONSUME;
		}
		
		/* Send packet in notification to switchEventManager */
		boolean ret = switchEventManager.packetInHandler(pkt);
		if(ret == false){
			logger.error("Packet in notification could not be processed successfully : {}", pkt);
		}
		
		return PacketResult.CONSUME;
	}

	
	
	
	/* YAON service Interface - exposed as a service */
	
	@Override
	public boolean addSlice(String sliceId, String desc) {
	
		boolean ret = false;
		
		/* Check if null argument is passed */
		if(sliceId == null || desc == null){
			logger.error("Null argument is passed !");
			return false;
		}
		
		if(sliceManager != null) {
			ret = sliceManager.addSlice(sliceId, desc);
		}
		else {
			logger.error("Slice manager is not initialized !");
		}
		
		return ret;
	}

	@Override
	public boolean deleteSlice(String sliceId) {

		boolean ret = false;
		
		/* Check if null argument is passed */
		if(sliceId == null){
			logger.error("Null argument is passed !");
			return false;
		}
		
		if(sliceManager != null) {
			ret = sliceManager.deleteSlice(sliceId);
		}
		else {
			logger.error("Slice manager is not initialized !");
		}
		
		return ret;
	}

	@Override
	public boolean addPort(String sliceId, String portId, String dataPathId, String portName, String vlan, String desc) {
	
		boolean ret = false;
		
		/* Check if null argument is passed */
		if(sliceId == null || portId == null || dataPathId == null || portName == null || vlan == null || desc == null){
			logger.error("Null argument is passed !");
			return false;
		}
		
		if(sliceManager != null) {
			ret = sliceManager.addPort(sliceId, portId, dataPathId, portName, vlan, desc);
		}
		else {
			logger.error("Slice manager is not initialized !");
		}
		
		return ret;
	}

	@Override
	public boolean deletePort(String sliceId, String portId) {
		
		boolean ret = false;
		
		/* Check if null argument is passed */
		if(sliceId == null || portId == null){
			logger.error("Null argument is passed !");
			return false;
		}
		
		if(sliceManager != null) {
			ret = sliceManager.deletePort(sliceId, portId);
		}
		else {
			logger.error("Slice manager is not initialized !");
		}
		
		return ret;
	}

	@Override
	public boolean addMac(String sliceId, String portId, String MAC) {
		
		boolean ret = false;
		
		/* Check if null argument is passed */
		if(sliceId == null || portId == null || MAC == null){
			logger.error("Null argument is passed !");
			return false;
		}
		
		if(sliceManager != null) {
			ret = sliceManager.addMac(sliceId, portId, MAC);
		}
		else {
			logger.error("Slice manager is not initialized !");
		}
		
		return ret;
	}

	@Override
	public boolean deleteMac(String sliceId, String portId, String MAC) {
		
		boolean ret = false;
		
		/* Check if null argument is passed */
		if(sliceId == null || portId == null || MAC == null){
			logger.error("Null argument is passed !");
			return false;
		}
		
		if(sliceManager != null) {
			ret = sliceManager.deleteMac(sliceId, portId, MAC);
		}
		else {
			logger.error("Slice manager is not initialized !");
		}
		
		return ret;
	}

	@Override
	public boolean registerAgent(String dataPathId, String agentUri) {
		
		boolean ret = false;
		
		/* Check if null argument is passed */
		if(dataPathId == null || agentUri == null){
			logger.error("Null argument is passed !");
			return false;
		}
		
		if(sliceManager != null) {
			ret = sliceManager.addAgent(dataPathId, agentUri);
		}
		else {
			logger.error("Slice manager is not initialized !");
		}
		
		return ret;
	}

	@Override
	public boolean registerMulticast(String sliceId, String multicast) {
		
		boolean ret = false;
		
		/* Check if null argument is passed */
		if(sliceId == null || multicast == null){
			logger.error("Null argument is passed !");
			return false;
		}
		
		if(sliceManager != null) {
			ret = sliceManager.addMulticast(sliceId, multicast);
		}
		else {
			logger.error("Slice manager is not initialized !");
		}
		
		return ret;
	}

}
