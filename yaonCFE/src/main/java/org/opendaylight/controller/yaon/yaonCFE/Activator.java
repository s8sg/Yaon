package org.opendaylight.controller.yaon.yaonCFE;



import java.util.Dictionary;
import java.util.Hashtable;

import org.apache.felix.dm.Component;
import org.opendaylight.controller.sal.core.ComponentActivatorAbstractBase;
import org.opendaylight.controller.yaon.IYaonService;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Activator extends ComponentActivatorAbstractBase {
    protected static final Logger logger = LoggerFactory
            .getLogger(Activator.class);

    /**
     * Function called when the activator starts just after some
     * initializations are done by the
     * ComponentActivatorAbstractBase.
     *
     */


    YaonCFEApi service = new CFEImpl();

    @Override
	public void init() {

    }

    @Override
    public void start(BundleContext context) {
    	// TODO Auto-generated method stub
    	super.start(context);
    	logger.error("start method of yaon activator call");
    	context.registerService(YaonCFEApi.class.getName(), service, null);
    	logger.error("start method of yaon activator call and registered");

    }
    /**
     * Function called when the activator stops just before the
     * cleanup done by ComponentActivatorAbstractBase
     *
     */
    @Override
	public void destroy() {

    }

    /**
     * Function that is used to communicate to dependency manager the
     * list of known implementations for services inside a container
     *
     *
     * @return An array containing all the CLASS objects that will be
     * instantiated in order to get an fully working implementation
     * Object
     */
    @Override
	public Object[] getImplementations() {
    	logger.info("Bundle getting Configuration Front End implementation info!");
        Object[] res = {CFEImpl.class};
        return res;
    }

    /**
     * Function that is called when configuration of the dependencies
     * is required.
     *
     * @param c dependency manager Component object, used for
     * configuring the dependencies exported and imported
     * @param imp Implementation class that is being configured,
     * needed as long as the same routine can configure multiple
     * implementations
     * @param containerName The containerName being configured, this allow
     * also optional per-container different behavior if needed, usually
     * should not be the case though.
     */
    @Override
	public void configureInstance(Component c, Object imp, String containerName) {

    	logger.info("Exporting the YAON-CFE services");

    	Dictionary<String, String> props = new Hashtable<String, String>();
    	props.put("salListenerName", "YAONCFE");
    	c.setInterface(new String[] { YaonCFEApi.class.getName()}, props);

        logger.info("Registering dependent services");

    	if (imp.equals(CFEImpl.class)) {
    		c.add(createContainerServiceDependency(containerName).setService(
                    IYaonService.class).setCallbacks("setYaonService",
                    "unsetYaonService").setRequired(true));
    	}

    }
}
