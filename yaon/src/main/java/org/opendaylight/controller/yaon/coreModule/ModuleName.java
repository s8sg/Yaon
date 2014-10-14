package org.opendaylight.controller.yaon.coreModule;

public enum ModuleName {
	    SliceManager("SliceManager"),
	    SwitchEventManager("SwitchEventManager"),
	    FlowManager("FlowManager"),
	    AgentManager("AgentManager"),
	    SliceDBManager("SliceDBManager"),
	    TopoDBManager("TopoDBManager");

	    private final String name;

	    private ModuleName(String s) {
	        name = s;
	    }

	    public boolean equalsName(String otherName){
	        return (otherName == null)? false:name.equals(otherName);
	    }

	    @Override
		public String toString(){
	       return name;
	    }
}
