/*
* @#NetworkData.java - 2016
* Copyright bitDubai.com., All rights reserved.
 * You may not modify, use, reproduce or distribute this software.
* BITDUBAI/CONFIDENTIAL
*/
package org.iop.node.monitor.app.rest.services;

import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.database.exceptions.CantReadRecordDataBaseException;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.util.GsonProvider;
import com.google.gson.JsonObject;
import org.apache.commons.lang.ClassUtils;
import org.apache.log4j.Logger;
import org.iop.node.monitor.app.database.jpa.daos.JPADaoFactory;
import org.iop.version_1.structure.database.jpa.entities.NodeCatalog;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class <code>com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.rest.AvailableNodes</code>
 * <p/>
 * Created by Hendry Rodriguez - (elnegroevaristo@gmail.com) on 26/04/16.
 *
 * @version 1.0
 * @since Java JDK 1.7
 */
@Path("/rest/api/v1/network")
public class NetworkData {

    /**
     * Represent the LOG
     */
    private final Logger LOG = Logger.getLogger(ClassUtils.getShortClassName(NetworkData.class));

    /**
     * Constructor
     */
    public NetworkData(){

    }

    @GET
    @Path("/catalog")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNodes(){

         /*
          * Get the node catalog list
          */
        try {

            List<NodeCatalog> nodesCatalogs = JPADaoFactory.getNodeCatalogDao().findAll(null,null, 1000, 0);
            List<String> nodes = new ArrayList<>();

            if(nodesCatalogs != null){
                for(NodeCatalog node : nodesCatalogs){
                    nodes.add(node.getIp());
                }
            }

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("nodes", GsonProvider.getGson().toJson(nodes));

            return Response.status(200).entity(GsonProvider.getGson().toJson(jsonObject)).build();


        } catch (CantReadRecordDataBaseException e) {
           // e.printStackTrace();

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("code",e.hashCode());
            jsonObject.addProperty("message",e.getMessage());
            jsonObject.addProperty("details",GsonProvider.getGson().toJson(e.getCause()));


            JsonObject jsonObjectError = new JsonObject();
            jsonObjectError.addProperty("error",GsonProvider.getGson().toJson(jsonObject));

            return Response.status(200).entity(GsonProvider.getGson().toJson(jsonObjectError)).build();

        }


    }

}
