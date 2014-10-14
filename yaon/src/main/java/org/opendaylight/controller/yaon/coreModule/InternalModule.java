package org.opendaylight.controller.yaon.coreModule;

import java.util.HashMap;

public interface InternalModule {
	// Methods must to be implemented for all modules inside YAON
	public boolean initiateModuleDependency(HashMap<String, InternalModule> modules);
	public boolean initiateServicesDepedency(ServiceHolder services);
}
