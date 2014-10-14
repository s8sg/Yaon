package org.opendaylight.controller.yaon.yaonCFENorthbound;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

public class YaonCFENorthboundRSApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(YaonCFENorthbound.class);
        return classes;
    }
}
