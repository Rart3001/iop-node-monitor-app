package org.iop.node.monitor.app.rest;

import org.iop.node.monitor.app.rest.services.*;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * The Class <code>com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.rest.JaxRsActivator</code>
 * <p/>
 * Created by Roberto Requena - (rart3001@gmail.com) on 13/11/15.
 *
 * @version 1.0
 * @since Java JDK 1.7
 */
public class JaxRsActivator extends Application {

    private static final Set<Class<?>> services = new HashSet();

    @Override
    public Set<Class<?>> getClasses() {

        services.add(AvailableNodes.class);
        services.add(Nodes.class);
        services.add(Monitoring.class);
        services.add(UserAuth.class);
        services.add(ConfigurationService.class);
        services.add(DataBases.class);
        services.add(Actors.class);
        services.add(NetworkData.class);

        return services;
    }

}