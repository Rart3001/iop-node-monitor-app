package org.iop.node.monitor.app.rest.services;

import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.util.GsonProvider;
import com.google.gson.Gson;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang.ClassUtils;
import org.apache.log4j.Logger;
import org.iop.node.monitor.app.util.ConfigurationManager;
import org.jboss.resteasy.annotations.GZIP;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * The class <code>com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.rest.ConfigurationService</code>
 * </p>
 * <p/>
 * Created by Roberto Requena - (rart3001@gmail.com) on 22/06/16.
 *
 * @version 1.0
 * @since Java JDK 1.7
 */
@Path("/rest/api/v1/admin/configuration")
public class ConfigurationService {

    /**
     * Represent the logger instance
     */
    private final Logger LOG = Logger.getLogger(ClassUtils.getShortClassName(ConfigurationService.class));

    /**
     * Represent the gson
     */
    private final Gson gson;

    /**
     * Constructor
     */
    public ConfigurationService() {
        super();
        this.gson = GsonProvider.getGson();
    }

    @GET
    @GZIP
    public String isActive() {
        return "The Configuration WebService is running ...";
    }


    @GET
    @Path("/get")
    @Produces(MediaType.APPLICATION_JSON)
    @GZIP
    public Response getConfiguration() {

        LOG.info("Executing getConfiguration()");

        Configuration configuration = new Configuration();
        configuration.setIpk(ConfigurationManager.getValue(ConfigurationManager.IDENTITY_PUBLIC_KEY));
        configuration.setNodeName(ConfigurationManager.getValue(ConfigurationManager.NODE_NAME));
        configuration.setInternalIp(ConfigurationManager.getValue(ConfigurationManager.INTERNAL_IP));
        configuration.setPublicIp(ConfigurationManager.getValue(ConfigurationManager.PUBLIC_IP));
        configuration.setPort(Integer.valueOf(ConfigurationManager.getValue(ConfigurationManager.PORT)));
        configuration.setLatitude(Double.valueOf(ConfigurationManager.getValue(ConfigurationManager.LATITUDE)));
        configuration.setLongitude(Double.valueOf(ConfigurationManager.getValue(ConfigurationManager.LONGITUDE)));
        configuration.setUser(ConfigurationManager.getValue(ConfigurationManager.USER));
        configuration.setRegisterInCatalog(Boolean.valueOf(ConfigurationManager.getValue(ConfigurationManager.REGISTERED_IN_CATALOG)));

        return Response.status(200).entity(gson.toJson(configuration)).build();

    }

    @POST
    @Path("/save")
    @Produces(MediaType.TEXT_PLAIN)
    @GZIP
    public Response saveConfiguration(Configuration configuration) {

        LOG.info("Executing saveConfiguration()");

        try {

            if(!configuration.getNodeName().equals(ConfigurationManager.getValue(ConfigurationManager.NODE_NAME))){
                ConfigurationManager.updateValue(ConfigurationManager.INTERNAL_IP, configuration.getNodeName());
            }

            if(!configuration.getInternalIp().equals(ConfigurationManager.getValue(ConfigurationManager.INTERNAL_IP))){
                ConfigurationManager.updateValue(ConfigurationManager.INTERNAL_IP, configuration.getInternalIp());
            }

            if(!configuration.getPublicIp().equals(ConfigurationManager.getValue(ConfigurationManager.PUBLIC_IP))){
                ConfigurationManager.updateValue(ConfigurationManager.PUBLIC_IP, configuration.getPublicIp());
            }

            if(!configuration.getPort().toString().equals(ConfigurationManager.getValue(ConfigurationManager.PORT))){
                ConfigurationManager.updateValue(ConfigurationManager.PORT, configuration.getPort().toString());
            }

            if(!configuration.getLatitude().toString().equals(ConfigurationManager.getValue(ConfigurationManager.LATITUDE))){
                ConfigurationManager.updateValue(ConfigurationManager.LATITUDE, configuration.getLatitude().toString());
            }

            if(!configuration.getLongitude().toString().equals(ConfigurationManager.getValue(ConfigurationManager.LONGITUDE))){
                ConfigurationManager.updateValue(ConfigurationManager.LONGITUDE, configuration.getLongitude().toString());
            }

            if(!configuration.getUser().equals(ConfigurationManager.getValue(ConfigurationManager.USER))){
                ConfigurationManager.updateValue(ConfigurationManager.USER, configuration.getUser());
            }

            if(!configuration.getPassword().equals(ConfigurationManager.getValue(ConfigurationManager.PASSWORD))){
                ConfigurationManager.updateValue(ConfigurationManager.PASSWORD, configuration.getPassword());
            }

        } catch (ConfigurationException e) {
            e.printStackTrace();
            return Response.status(500).entity(e.getMessage()).build();
        }

        return Response.status(200).entity("Configuration save success").build();

    }

}
