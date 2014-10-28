package org.opendaylight.controller.yaon.coreModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.opendaylight.controller.forwardingrulesmanager.FlowEntry;
import org.opendaylight.controller.forwardingrulesmanager.IForwardingRulesManager;
import org.opendaylight.controller.sal.action.Action;
import org.opendaylight.controller.sal.action.Drop;
import org.opendaylight.controller.sal.action.Output;
import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.core.NodeConnector;
import org.opendaylight.controller.sal.flowprogrammer.Flow;
import org.opendaylight.controller.sal.match.Match;
import org.opendaylight.controller.sal.match.MatchField;
import org.opendaylight.controller.sal.match.MatchType;
import org.opendaylight.controller.sal.utils.Status;
import org.opendaylight.controller.yaon.util.YaonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlowManager implements InternalModule{

	/* Internal Globals */
	private static final Logger logger = LoggerFactory
            .getLogger(FlowManager.class);

	/* Internal Project Globals */
	private IForwardingRulesManager forwardingRulesManager = null;
	
	/* Internal global values */
	private String invalidMac = "000000000000";
	private String anyMac = "FFFFFFFFFFFF";
	private String defaultSwitchDropFlows = "DEFAULT_SWITCH_DROP_FLOWS";
	private String defaultPortDropFlows = "PORT_DROP_FLOWS";
	private String vxlanPortForwardFlows = "VXLAN_PORT_FORWARD_FLOWS";
	private String macForwardFlows = "MAC_FORWARD_FLOWS";
	private short switch_drop_priority = 512;
	private short port_drop_priority = 2000;
	private short forward_priority = 4096;
	
	/* Initializing Functions */
	@Override
	public boolean initiateServicesDepedency(ServiceHolder services) {	
		/* Get Forwarding Rules manager */
		forwardingRulesManager = services.getForwardingRulesManager();
		if(forwardingRulesManager == null){
			logger.error("Forwarding rule Manager could not be set !");
			return false;
		}
		return true;
	}
	
	@Override
	public boolean initiateModuleDependency(HashMap<String, InternalModule> modules){
		return true;
	}
	
	
	public boolean setAndVerifySwitchDefaultFlow(String dpId, Node node) {
		
		logger.info("Default flow is being set to switch for DpId: {}", dpId);
		
		/* Default_switch_drop_flows */
		Flow drop_switch_src_1 = null;
		Flow drop_switch_src_2 = null;
		Flow drop_switch_dst = null;
		Flow drop_switch_any = null;
		Match match = null;
		List<Action> actions = null; 
		String flowName = null;
		Status status = null;
		boolean ret = true;
		
		/* Create drop_switch_src_1 Flow */
		drop_switch_src_1 = new Flow();
		/* Generate flow name */
		flowName = "drop_switch_src_1_" + dpId;
		/* Create Match */
		match = new Match();
		match.setField(new MatchField(MatchType.DL_SRC, YaonUtil.generateBytesForMac(invalidMac)));
		/* Create actions */
		actions = new ArrayList<Action>();
		actions.add(new Drop());
		drop_switch_src_1.setMatch(match);
		drop_switch_src_1.setActions(actions);
		drop_switch_src_1.setPriority((short)switch_drop_priority);
		/* Add Flow to switch */
		/*** TODO: mac is not being added in the flow */
		/*
		status = forwardingRulesManager.modifyOrAddFlowEntry(new FlowEntry(defaultSwitchDropFlows, flowName, drop_switch_src_1, node));
		if (!status.isSuccess()) {
            logger.error("Flow programmer failed to program the flow: {}. The failure is: {}",
            		drop_switch_src_1, status.getDescription());
            ret = false;
        }
		*/
		
		/* Create drop_switch_src_2 Flow */
		drop_switch_src_2 = new Flow();
		/* Generate flow name */
		flowName = "drop_switch_src_2_" + dpId;
		/* Create Match */
		match = new Match();
		match.setField(new MatchField(MatchType.DL_SRC, YaonUtil.generateBytesForMac(anyMac)));
		/* Create actions */
		actions = new ArrayList<Action>();
		actions.add(new Drop());
		drop_switch_src_2.setMatch(match);
		drop_switch_src_2.setActions(actions);
		drop_switch_src_2.setPriority((short)switch_drop_priority);
		/* Add Flow to switch */
		/*** TODO: mac is not being added in the flow */
		/*
		status = forwardingRulesManager.modifyOrAddFlowEntry(new FlowEntry(defaultSwitchDropFlows, flowName, drop_switch_src_2, node));
		if (!status.isSuccess()) {
            logger.error("Flow programmer failed to program the flow: {}. The failure is: {}",
            		drop_switch_src_2, status.getDescription());
            ret = false;
        }
        */
		
		/* Create drop_switch_dst Flow */
		drop_switch_dst = new Flow();
		/* Generate flow name */
		flowName = "drop_switch_dst_" + dpId;
		/* Create Match */
		match = new Match();
		match.setField(new MatchField(MatchType.DL_DST, YaonUtil.generateBytesForMac(invalidMac)));
		/* Create actions */
		actions = new ArrayList<Action>();
		actions.add(new Drop());
		drop_switch_dst.setMatch(match);
		drop_switch_dst.setActions(actions);
		drop_switch_dst.setPriority((short)switch_drop_priority);
		/* Add Flow to switch */
		/*** TODO: mac is not being added in the flow */
		/*
		status = forwardingRulesManager.modifyOrAddFlowEntry(new FlowEntry(defaultSwitchDropFlows, flowName, drop_switch_dst, node));
		if (!status.isSuccess()) {
            logger.error("Flow programmer failed to program the flow: {}. The failure is: {}",
            		drop_switch_dst, status.getDescription());
            ret = false;
        }
        */
		
		/*** Added as other drops flow is not being added ***/
		/* Create drop_switch_any Flow */
		drop_switch_any = new Flow();
		/* Generate flow name */
		flowName = "drop_switch_any_" + dpId;
		/* Create Match */
		match = new Match();
		/* Create actions */
		actions = new ArrayList<Action>();
		actions.add(new Drop());
		drop_switch_any.setMatch(match);
		drop_switch_any.setActions(actions);
		drop_switch_any.setPriority((short)switch_drop_priority);
		/* Add Flow to switch */
		status = forwardingRulesManager.modifyOrAddFlowEntry(new FlowEntry(defaultSwitchDropFlows, flowName, drop_switch_any, node));
		if (!status.isSuccess()) {
            logger.error("Flow programmer failed to program the flow: {}. The failure is: {}",
            		drop_switch_any, status.getDescription());
            ret = false;
        }
		else {
			logger.info("Flow is successfully added : {}", drop_switch_any);
		}
		
		return ret;
	}
	
	public boolean setAndVerifyPortDefaultFlow(String dpId, Node node, String portNo, String vlan, NodeConnector port){
		
		logger.info("Default flow is being set to port for DpId: {}, portNo: {}", dpId, portNo);
		
		/* Default_port_drop_flows */
		Flow drop_port = null;
		Match match = null;
		List<Action> actions = null; 
		String flowName = null;
		Status status = null;
		boolean ret = true;
		
		/* Create drop_port Flow */
		drop_port = new Flow();
		/* Generate flow name */
		flowName = "drop_port_" + dpId;
		/* Create Match */
		match = new Match();
		match.setField(new MatchField(MatchType.IN_PORT, port));
		/* Create actions */
		actions = new ArrayList<Action>();
		actions.add(new Drop());
		/* Configure flow */
		drop_port.setMatch(match);
		drop_port.setActions(actions);
		drop_port.setPriority((short)port_drop_priority);
		/* Add Flow to switch */
		status = forwardingRulesManager.modifyOrAddFlowEntry(new FlowEntry(defaultPortDropFlows, flowName, drop_port, node));
		if (!status.isSuccess()) {
            logger.error("Flow programmer failed to program the flow: {}. The failure is: {}",
            		drop_port, status.getDescription());
            ret = false;
        }
		else{
			logger.info("Flow is successfully added : {}", drop_port);
		}
		return ret;
	}
	
	public boolean removePortDefaultFlow(String dpId, Node node, String portNo, String vlan, NodeConnector port){
		
		logger.info("Default flow is being removed to port for DpId: {}, portNo: {}", dpId, portNo);
		
		/* Default_port_drop_flows */
		Flow drop_port = null;
		Match match = null;
		List<Action> actions = null; 
		String flowName = null;
		Status status = null;
		boolean ret = true;
		
		/* Create drop_port Flow */
		drop_port = new Flow();
		/* Generate flow name */
		flowName = "drop_port_" + dpId;
		/* Create Match */
		match = new Match();
		match.setField(new MatchField(MatchType.IN_PORT, port));
		/* Create actions */
		actions = new ArrayList<Action>();
		actions.add(new Drop());
		/* Configure flow */
		drop_port.setMatch(match);
		drop_port.setActions(actions);
		drop_port.setPriority((short)port_drop_priority);
		/* Add Flow to switch */
		status = forwardingRulesManager.uninstallFlowEntry(new FlowEntry(defaultPortDropFlows, flowName, drop_port, node));
		if (!status.isSuccess()) {
            logger.error("Flow programmer failed to remove the flow: {}. The failure is: {}",
            		drop_port, status.getDescription());
            ret = false;
        }
		else{
			logger.info("Flow is successfully added : {}", drop_port);
		}
		return ret;
	}
	
	public boolean setAndVerifyVxlanDefaultFlow(String dpId, Node node, String vxlanPortNo, NodeConnector vxlanPort, List<NodeConnector> otherPorts){

		logger.info("Default flow is being set/modify to vxlan port for DpId: {} vxlan PortNo: {} and otherPorts: {}", dpId, vxlanPortNo, otherPorts);
		
		/* vxlanPort_forward_flow */
		Flow forward_vxlan = new Flow();
		Match match = null;
		List<Action> actions = null; 
		String flowName = null;
		Status status = null;
		boolean ret = true;
		
		/* Create forward_vxlan Flow */
		forward_vxlan = new Flow();
		/* Generate flow name */
		flowName = "forward_vxlan_" + dpId + "_" + vxlanPortNo;
				
		/* Create Match */
		match = new Match();
		match.setField(new MatchField(MatchType.IN_PORT, vxlanPort));
		/* Create actions */
		actions = new ArrayList<Action>();
		/* Add all port to output */
		for(NodeConnector nodeConnector : otherPorts){
			actions.add(new Output(nodeConnector));
		}
		
		forward_vxlan.setMatch(match);
		forward_vxlan.setActions(actions);
		forward_vxlan.setPriority((short)forward_priority);
		
		/* Add/Delete Flow */
		if(actions.size() > 0){
			status = forwardingRulesManager.modifyOrAddFlowEntry(new FlowEntry(vxlanPortForwardFlows, flowName, forward_vxlan, node));
			if (!status.isSuccess()) {
	            logger.error("Flow programmer failed to program the flow: {}. The failure is: {}",
	            		forward_vxlan, status.getDescription());
	            ret = false;
	        }
		}
		else{
			logger.info("Flow is successfully added : {}", forward_vxlan);
		}
		
		return ret;
	}
	
	public boolean removeVxlanDefaultFlow(String dpId, Node node, String vxlanPortNo, NodeConnector vxlanPort){
		
		logger.info("Default flow is being removed from vxlan port for DpId: {} vxlan PortNo: {}", dpId, vxlanPortNo);
		
		/* vxlanPort_forward_flow */
		Flow forward_vxlan = new Flow();
		Match match = null;
		List<Action> actions = null; 
		String flowName = null;
		Status status = null;
		boolean ret = true;
		
		/* Create forward_vxlan Flow */
		forward_vxlan = new Flow();
		/* Generate flow name */
		flowName = "forward_vxlan_" + dpId + "_" + vxlanPortNo;
				
		/* Create Match */
		match = new Match();
		match.setField(new MatchField(MatchType.IN_PORT, vxlanPort));
		/* Create actions */
		actions = new ArrayList<Action>();
	
		forward_vxlan.setMatch(match);
		forward_vxlan.setActions(actions);
		forward_vxlan.setPriority((short)forward_priority);
		
		/* Add/Delete Flow */
		status = forwardingRulesManager.uninstallFlowEntry(new FlowEntry(vxlanPortForwardFlows, flowName, forward_vxlan, node));
		if (!status.isSuccess()) {
	       logger.error("Flow programmer failed to remove the flow: {}. The failure is: {}",
	       forward_vxlan, status.getDescription());
	       ret = false;
	    }
		else{
			logger.info("Flow is successfully Deleted : {}", forward_vxlan);
		}
		
		return ret;
	}
	
	public boolean setAndVerifyForwardinngFlow(String dpId, Node node, NodeConnector port, String portName, String vxlanPortNo, NodeConnector vxlanPort, String MAC, List<NodeConnector> otherPorts) {
		
		logger.info("Forwarding flow is being set/modify to port for DpId: {}, vxlan PortNo: {} and MAC: {}", dpId, portName, MAC);
		
		/* Max_forward_flow */
		Flow forward_mac = new Flow();
		Match match = null;
		List<Action> actions = null; 
		String flowName = null;
		Status status = null;
		boolean ret = true;
		
		/* Create forward_mac Flow */
		forward_mac = new Flow();
		/* Generate flow name */
		flowName = "forward_mac_" + dpId + "_" + portName + "_" + MAC;
		/* Create Match */
		match = new Match();
		match.setField(new MatchField(MatchType.IN_PORT, port));
		/*** TODO: mac is not being added in the flow */
		//match.setField(new MatchField(MatchType.DL_SRC, OdlUtil.generateBytesForMac(MAC)));
		/* Create actions */
		actions = new ArrayList<Action>();
		/* check if no other port exist */
		if(otherPorts != null){
			/* Add all port to output except current port */
			for(NodeConnector nodeConnector : otherPorts){
				if(nodeConnector != port){
					actions.add(new Output(nodeConnector));
					logger.info("Ignoring current port from all port list !");
				}
			}
		}
		/* Add vxlan port to the action */
		actions.add(new Output(vxlanPort));
		forward_mac.setMatch(match);
		forward_mac.setActions(actions);
		forward_mac.setPriority((short)forward_priority);
		/* Add Flow to switch */
		status = forwardingRulesManager.modifyOrAddFlowEntry(new FlowEntry(macForwardFlows, flowName, forward_mac, node));
		if (!status.isSuccess()) {
            logger.error("Flow programmer failed to program the flow: {}. The failure is: {}",
            		forward_mac, status.getDescription());
            ret = false;
        }
		else{
			logger.info("Flow is successfully added : {}", forward_mac);
		}
		
		return ret;
	}
	
	public boolean removeForwardinngFlow(String dpId, Node node, NodeConnector port, String portName, String MAC) {
		
		logger.info("Forwarding flow is being set/modify to port for DpId: {}, vxlan PortNo: {} and MAC: {}", dpId, portName, MAC);
		
		/* Max_forward_flow */
		Flow forward_mac = new Flow();
		Match match = null;
		List<Action> actions = null; 
		String flowName = null;
		Status status = null;
		boolean ret = true;
		
		/* Create forward_mac Flow */
		forward_mac = new Flow();
		/* Generate flow name */
		flowName = "forward_mac_" + dpId + "_" + portName + "_" + MAC;
		
		/* Create Match */
		match = new Match();
		match.setField(new MatchField(MatchType.IN_PORT, port));
		/*** TODO: mac is not being added in the flow */
		//match.setField(new MatchField(MatchType.DL_SRC, OdlUtil.generateBytesForMac(MAC)));
		/* Create actions */
		actions = new ArrayList<Action>();
		
		/* Add vxlan port to the action */
		forward_mac.setMatch(match);
		forward_mac.setActions(actions);
		forward_mac.setPriority((short)forward_priority);
		
		/* Delete Flow from switch */
		status = forwardingRulesManager.uninstallFlowEntry(new FlowEntry(macForwardFlows, flowName, forward_mac, node));
		if (!status.isSuccess()) {
            logger.error("Flow programmer failed to remove the flow: {}. The failure is: {}",
            		forward_mac, status.getDescription());
            ret = false;
        }
		else{
			logger.info("Flow is successfully Deleted : {}", forward_mac);
		}
		
		return ret;
	}
}
