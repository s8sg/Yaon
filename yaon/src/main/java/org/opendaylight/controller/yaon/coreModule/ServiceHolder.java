package org.opendaylight.controller.yaon.coreModule;

import org.opendaylight.controller.forwardingrulesmanager.IForwardingRulesManager;
import org.opendaylight.controller.sal.flowprogrammer.IFlowProgrammerService;
import org.opendaylight.controller.sal.packet.IDataPacketService;
import org.opendaylight.controller.statisticsmanager.IStatisticsManager;
import org.opendaylight.controller.switchmanager.ISwitchManager;

public class ServiceHolder {
	private IFlowProgrammerService flowProgrammer = null;
	private IDataPacketService dataPacketService = null;
	private ISwitchManager switchManager = null;
	private IStatisticsManager statManager = null;
	private IForwardingRulesManager forwardingRulesManager = null;
	
	public ISwitchManager getSwitchManager() {
		return switchManager;
	}
	public void setSwitchManager(ISwitchManager switchManager) {
		this.switchManager = switchManager;
	}
	public IDataPacketService getDataPacketService() {
		return dataPacketService;
	}
	public void setDataPacketService(IDataPacketService dataPacketService) {
		this.dataPacketService = dataPacketService;
	}
	public IFlowProgrammerService getFlowProgrammer() {
		return flowProgrammer;
	}
	public void setFlowProgrammer(IFlowProgrammerService flowProgrammer) {
		this.flowProgrammer = flowProgrammer;
	}
	public IStatisticsManager getStatManager() {
		return statManager;
	}
	public void setStatManager(IStatisticsManager statManager) {
		this.statManager = statManager;
	}
	public IForwardingRulesManager getForwardingRulesManager() {
		return forwardingRulesManager;
	}
	public void setForwardingRulesManager(IForwardingRulesManager forwardingRulesManager) {
		this.forwardingRulesManager = forwardingRulesManager;
	}
}
