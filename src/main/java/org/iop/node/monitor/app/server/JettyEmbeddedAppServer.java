package org.iop.node.monitor.app.server;

import com.bitdubai.fermat_api.layer.all_definition.common.system.annotations.NeededAddonReference;
import com.bitdubai.fermat_api.layer.all_definition.enums.Addons;
import com.bitdubai.fermat_api.layer.all_definition.enums.Layers;
import com.bitdubai.fermat_api.layer.all_definition.enums.Platforms;
import com.bitdubai.fermat_api.layer.all_definition.location_system.NetworkNodeCommunicationDeviceLocation;
import com.bitdubai.fermat_api.layer.osa_android.location_system.Location;
import com.bitdubai.fermat_api.layer.osa_android.location_system.LocationManager;
import com.bitdubai.fermat_api.layer.osa_android.location_system.LocationSource;
import org.apache.commons.lang.ClassUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.annotations.ServletContainerInitializersStarter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Slf4jLog;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.websocket.jsr356.server.ServerContainer;
import org.iop.node.monitor.app.context.NodeContext;
import org.iop.node.monitor.app.context.NodeContextItem;
import org.iop.node.monitor.app.rest.JaxRsActivator;
import org.iop.node.monitor.app.rest.security.AdminRestApiSecurityFilter;
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;

import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.EnumSet;

/**
 * The class <code>com.bitdubai.fermat_p2p_plugin.layer.ws.communications.cloud.server.developer.bitdubai.version_1.structure.jetty.JettyEmbeddedAppServer</code>
 * is the application webapp server to deploy the webapp socket server</p>
 * <p/>
 * Created by Roberto Requena - (rart3001@gmail.com) on 08/01/16.
 *
 * @version 1.0
 * @since Java JDK 1.7
 */

public class JettyEmbeddedAppServer {

    /**
     * Represent the logger instance
     */
    private static Logger LOG = Logger.getLogger(ClassUtils.getShortClassName(JettyEmbeddedAppServer.class));

    @NeededAddonReference(platform = Platforms.OPERATIVE_SYSTEM_API, layer = Layers.SYSTEM, addon = Addons.DEVICE_LOCATION)
    private LocationManager locationManager;

    /**
     * Represent the DEFAULT_CONTEXT_PATH value (/iop-node)
     */
    public static final String DEFAULT_CONTEXT_PATH = "/iop-node";

    /**
     * Represent the DEFAULT_PORT number
     */
    public static final int DEFAULT_PORT = 8080;

    /**
     * Represent the DEFAULT_IP number
     */
    public static final String DEFAULT_IP = "0.0.0.0";

    /**
     * Represent the JettyEmbeddedAppServer instance
     */
    private static JettyEmbeddedAppServer instance;

    /**
     * Represent the server instance
     */
    private Server server;

    /**
     * Represent the webapp socket server container instance
     */
    private ServerContainer wsServerContainer;

    /**
     * Represent the ServletContextHandler instance
     */
    private ServletContextHandler servletContextHandler;

    /**
     * Represent the ServerConnector instance
     */
    private ServerConnector serverConnector;

    /**
     * Constructor
     */
    private JettyEmbeddedAppServer() {
        super();
    }

    /**
     * Initialize and configure the server instance
     *
     * @throws IOException
     * @throws ServletException
     */
    private void initialize() throws IOException, ServletException, URISyntaxException {

        LOG.info("Initializing the internal Server");
        Log.setLog(new Slf4jLog(Server.class.getName()));

        /*
         * Create and configure the server
         */
        this.server = new Server();
        this.serverConnector = new ServerConnector(server);
        this.serverConnector.setReuseAddress(Boolean.TRUE);

        LOG.info("Server configure ip = " + DEFAULT_IP);
        LOG.info("Server configure port = " + DEFAULT_PORT);

        this.serverConnector.setHost(DEFAULT_IP);
        this.serverConnector.setPort(DEFAULT_PORT);
        this.server.addConnector(serverConnector);

        /*
         * Setup the basic application "context" for this application at "/iop-node"
         */
        this.servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        this.servletContextHandler.setContextPath(JettyEmbeddedAppServer.DEFAULT_CONTEXT_PATH);
        this.server.setHandler(servletContextHandler);

        /*
         * Initialize webapp layer
         */
        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setContextPath(JettyEmbeddedAppServer.DEFAULT_CONTEXT_PATH);

        URL webPath = JettyEmbeddedAppServer.class.getClassLoader().getResource("web");
        LOG.info("webPath = " + webPath.getPath());

        webAppContext.setResourceBase(webPath.toString());
        webAppContext.setContextPath(JettyEmbeddedAppServer.DEFAULT_CONTEXT_PATH+"/web");
        webAppContext.addBean(new ServletContainerInitializersStarter(webAppContext), true);
        webAppContext.setWelcomeFiles(new String[]{"index.html"});
        webAppContext.addFilter(AdminRestApiSecurityFilter.class, "/rest/api/v1/admin/*", EnumSet.of(DispatcherType.REQUEST));
        webAppContext.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
        servletContextHandler.setHandler(webAppContext);
        server.setHandler(webAppContext);

        /*
         * Initialize restful service layer
         */
        ServletHolder restfulServiceServletHolder = new ServletHolder(new HttpServlet30Dispatcher());
        restfulServiceServletHolder.setInitParameter("javax.ws.rs.Application", JaxRsActivator.class.getName());
        restfulServiceServletHolder.setInitParameter("resteasy.use.builtin.providers", "true");
        restfulServiceServletHolder.setAsyncSupported(Boolean.TRUE);
        webAppContext.addServlet(restfulServiceServletHolder, "/rest/api/v1/*");

        this.server.dump(System.err);

    }

    /**
     * Start the server instance
     *
     * @throws Exception
     */
    public void start() throws Exception {

        Location location = (locationManager != null && locationManager.getLocation() != null) ? locationManager.getLocation() : null;

        if(location == null)
            location = new NetworkNodeCommunicationDeviceLocation(
                    0.0 ,
                    0.0,
                    0.0     ,
                    0        ,
                    0.0     ,
                    System.currentTimeMillis(),
                    LocationSource.UNKNOWN
            );

        NodeContext.add(NodeContextItem.LOCATION, location);

        this.initialize();
        LOG.info("Starting the internal server");
        this.server.start();
        LOG.info("Server URI = " + this.server.getURI());
        this.server.join();



    }


    /**
     * Get the instance value
     *
     * @return instance current value
     */
    public synchronized static JettyEmbeddedAppServer getInstance() {
        if (instance == null) {
            instance = new JettyEmbeddedAppServer();
        }
        return instance;
    }

}