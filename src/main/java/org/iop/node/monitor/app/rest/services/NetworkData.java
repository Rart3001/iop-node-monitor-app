/*
* @#NetworkData.java - 2016
* Copyright bitDubai.com., All rights reserved.
 * You may not modify, use, reproduce or distribute this software.
* BITDUBAI/CONFIDENTIAL
*/
package org.iop.node.monitor.app.rest.services;

import com.bitdubai.fermat_api.layer.all_definition.network_service.enums.NetworkServiceType;
import com.bitdubai.fermat_api.layer.osa_android.location_system.Location;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.database.exceptions.CantReadRecordDataBaseException;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.profiles.ActorProfile;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.util.GsonProvider;
import com.google.gson.JsonObject;
import org.apache.commons.lang.ClassUtils;
import org.apache.log4j.Logger;
import org.iop.node.monitor.app.context.NodeContext;
import org.iop.node.monitor.app.context.NodeContextItem;
import org.iop.node.monitor.app.database.jpa.daos.JPADaoFactory;
import org.iop.version_1.structure.database.jpa.entities.ActorCatalog;
import org.iop.version_1.structure.database.jpa.entities.Client;
import org.iop.version_1.structure.database.jpa.entities.NodeCatalog;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;
import org.apache.commons.codec.binary.Base64;

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

    @GET
    @Path("/data")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getServerData(){

        JsonObject jsonObject = new JsonObject();
        Location location = (Location) NodeContext.get(NodeContextItem.LOCATION);

        jsonObject.addProperty("hash", UUID.randomUUID().toString());
        jsonObject.addProperty("location", GsonProvider.getGson().toJson(location));
        jsonObject.addProperty("os","");
        jsonObject.addProperty("networkServices", GsonProvider.getGson().toJson(getNetworkServicesCount()));

        return Response.status(200).entity(GsonProvider.getGson().toJson(jsonObject)).build();

    }

    @GET
    @Path("/clients")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClients(){

        List<String> listOfClients = new ArrayList<>();

        try {

            List<Client> clients = JPADaoFactory.getClientDao().list();

            for (Client checkedInProfile : clients) {

                JsonObject jsonObjectClient = new JsonObject();

                jsonObjectClient.addProperty("hash", checkedInProfile.getId());
                jsonObjectClient.addProperty("location", GsonProvider.getGson().toJson(checkedInProfile.getLocation()));

                jsonObjectClient.addProperty("networkServices",
                        GsonProvider.getGson().toJson(JPADaoFactory.getNetworkServiceDao().getListOfNetworkServiceOfClientSpecific(checkedInProfile.getId())));

                listOfClients.add(GsonProvider.getGson().toJson(jsonObjectClient));

            }

            return Response.status(200).entity(GsonProvider.getGson().toJson(listOfClients)).build();

        }catch (Exception e){

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("code",e.hashCode());
            jsonObject.addProperty("message",e.getMessage());
            jsonObject.addProperty("details",GsonProvider.getGson().toJson(e.getCause()));


            JsonObject jsonObjectError = new JsonObject();
            jsonObjectError.addProperty("error",GsonProvider.getGson().toJson(jsonObject));

            return Response.status(200).entity(GsonProvider.getGson().toJson(jsonObjectError)).build();
        }


    }

    @GET
    @Path("/actors")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getActors(){

        List<String> actors = new ArrayList<>();
        try {

            List<ActorCatalog> actorCatalogList = JPADaoFactory.getActorCatalogDao().list();

            for(ActorCatalog actor : actorCatalogList){

                JsonObject jsonObjectActor = new JsonObject();
                jsonObjectActor.addProperty("hash",actor.getId());
                jsonObjectActor.addProperty("type",actor.getActorType());
                jsonObjectActor.addProperty("links", GsonProvider.getGson().toJson(new ArrayList<>()));
                jsonObjectActor.addProperty("location", GsonProvider.getGson().toJson(actor.getLocation()));

                JsonObject jsonObjectActorProfile = new JsonObject();
                jsonObjectActorProfile.addProperty("phrase", "There is not Phrase");
                jsonObjectActorProfile.addProperty("picture", Base64.encodeBase64String(actor.getPhoto()));
                jsonObjectActorProfile.addProperty("name", actor.getName());

                jsonObjectActor.addProperty("profile", GsonProvider.getGson().toJson(jsonObjectActorProfile));


                actors.add(GsonProvider.getGson().toJson(jsonObjectActor));

            }

            return Response.status(200).entity(GsonProvider.getGson().toJson(actors)).build();

        }catch (Exception e){

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("code",e.hashCode());
            jsonObject.addProperty("message",e.getMessage());
            jsonObject.addProperty("details",GsonProvider.getGson().toJson(e.getCause()));


            JsonObject jsonObjectError = new JsonObject();
            jsonObjectError.addProperty("error",GsonProvider.getGson().toJson(jsonObject));

            return Response.status(200).entity(GsonProvider.getGson().toJson(jsonObjectError)).build();
        }

    }


    private Map<NetworkServiceType,Long> getNetworkServicesCount(){

        Map<NetworkServiceType,Long> listNetworkServicesCount = new HashMap<>();

        try {

            List<Object[]> list = JPADaoFactory.getNetworkServiceDao().countOnLineByType();

            for(Object[] values : list)
                listNetworkServicesCount.put(((NetworkServiceType) values[0]), ((Long) values[1]));

        } catch (Exception e) {
//            e.printStackTrace();
        }

        return listNetworkServicesCount;
    }


}
