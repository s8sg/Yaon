package org.opendaylight.controller.yaon.storage;

public enum ONPortKeyType {
	PORTNAME("PORTNAMEKEY"),
    PORTID("PORTIDKEY"),
    SLICEDPID("SLICEDPID"),
	VXLAN("VXLANPORT");

    private final String name;

    private ONPortKeyType(String s) {
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
