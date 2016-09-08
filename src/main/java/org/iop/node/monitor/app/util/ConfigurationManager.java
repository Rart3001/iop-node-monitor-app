/*
 * @#ConfigurationManager  - 2016
 * Copyright bitDubai.com., All rights reserved.
 * You may not modify, use, reproduce or distribute this software.
 * BITDUBAI/CONFIDENTIAL
 */
package org.iop.node.monitor.app.util;


import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.ClassUtils;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * The Class <code>com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.util.ConfigurationManager</code> implements
 * <p/>
 * <p/>
 * Created by Roberto Requena - (rart3001@gmail.com) on 24/02/16.
 *
 * @version 1.0
 * @since Java JDK 1.7
 */
public class ConfigurationManager {

    /**
     * Represent the logger instance
     */
    private static final Logger LOG = Logger.getLogger(ClassUtils.getShortClassName(ConfigurationManager.class));

    /**
     * Represent the value of DIR_NAME
     */
    public static final String DIR_NAME = ProviderResourcesFilesPath.createNewFilesPath("configuration");

    /**
     * Represent the value of FILE_NAME
     */
    public static final String FILE_NAME = "IoP-node_conf.properties";

    /**
     * Represent the value of IDENTITY_PUBLIC_KEY
     */
    public static final String IDENTITY_PUBLIC_KEY = "ipk";

    /**
     * Represent the value of NODE_NAME
     */
    public static final String NODE_NAME = "node_name";

    /**
     * Represent the value of LAST_REGISTER_NODE_PROFILE
     * todo: ver bien esto
     */
//    public static final String LAST_REGISTER_NODE_PROFILE = "last_register_node_profile";

    /**
     * Represent the value of INTERNAL_IP
     */
    public static final String INTERNAL_IP = "internal_ip";

    /**
     * Represent the value of INTERNAL_IP
     */
    public static final String PUBLIC_IP = "public_ip";

    /**
     * Represent the value of LATITUDE
     */
    public static final String LATITUDE = "latitude";

    /**
     * Represent the value of LONGITUDE
     */
    public static final String LONGITUDE = "longitude";

    /**
     * Represent the value of PORT
     */
    public static final String PORT = "port";

    /**
     * Represent the value of USER
     */
    public static final String USER = "user";

    /**
     * Represent the value of PASSWORD
     */
    public static final String PASSWORD = "password";

    /**
     * Represent the value of REGISTERED_IN_CATALOG
     */
    public static final String REGISTERED_IN_CATALOG = "register_in_catalog";


    /**
     * Represent the value of configuration file
     */
    private static final PropertiesConfiguration configuration = new PropertiesConfiguration();

    /**
     * Validate if the file exist
     * @return boolean
     */
    public static boolean isExist(){

        File file = new File(DIR_NAME+File.separator+FILE_NAME);
        return (file.exists() && !file.isDirectory());

    }

    /**
     * Load the content of the configuration file
     * @throws ConfigurationException
     */
    public static void load() throws ConfigurationException {
        LOG.info("Loading configuration...");
        configuration.setFileName(DIR_NAME+File.separator+FILE_NAME);
        configuration.load();
    }

    /**
     * Get the value of a properties
     *
     * @param property
     * @return String
     */
    public static String getValue(String property){
        return configuration.getString(property);
    }

    /**
     * Update the value of a property
     * @param property
     * @param value
     */
    public static void updateValue(String property, String value) throws ConfigurationException {
        configuration.setProperty(property, value);
        configuration.save();
    }



}
