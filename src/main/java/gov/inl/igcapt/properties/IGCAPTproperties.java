/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.igcapt.properties;

import java.util.Enumeration;
import java.util.Properties;
import java.io.*;

/**
 *(c) 2018 BATTELLE ENERGY ALLIANCE, LLC
 *ALL RIGHTS RESERVED 
 *
 * @author kur
 */
/**
 *
 *
 * A singleton class to store all property key and value information for a
 * project. Revision History: Software Developer	Change
 */

public final class IGCAPTproperties implements Serializable {

    /**
     * Fields
     */
    private static IGCAPTproperties igcaptProperties = null;
    private Properties properties = new Properties();
    private transient FileReader fis = null;
    private String _fileName = "igcapt.properties";

    /**
     * Creates new IGCAPTproperties
     */
    private IGCAPTproperties() {
        loadProperties();
    }

    /**
     * Returns a Singleton instance of the class
     */
    public static IGCAPTproperties getInstance() {
        if (igcaptProperties == null) {
            igcaptProperties = new IGCAPTproperties();
        }
        return igcaptProperties;
    }

    private void loadProperties() {
        
        InputStreamReader in = null;

        try {
             in = new InputStreamReader(new FileInputStream(_fileName), "UTF-8");
             properties.load(in);
        } catch (FileNotFoundException ex) {
            System.err.println("ERROR: loadProperties failed");
            ex.printStackTrace();
        } catch (IOException ex) {
            System.err.println("ERROR: loadProperties failed");
            ex.printStackTrace();
        } finally {
            if (null != in) {
                try {
                     in.close();
                } catch (IOException ex) {
                    System.err.println("ERROR: Property file close failed");
                    ex.printStackTrace();
                }
             }
        }
    }

    /**
     * Saves property Key-Value pairs to file
     */
    public void storeProperties() {
        try {
            properties.store(new FileOutputStream(_fileName), "New Sample.properties");
        } catch (IOException ioe) {
            System.err.println("ERROR: Properties.save() failed");
            ioe.printStackTrace();
        }
    }

    /**
     * Add a new Key-Value pair in the current Properties
     */
    public void setPropertyKeyValue(String key, String value) {
        properties.setProperty(key, value);
    }

    /**
     * Get a Key-Value pair from the current Properties
     */
    public String getPropertyKeyValue(String key) {
        return properties.getProperty(key);
    }

    /**
     * Displays all Key-Value pairs in the current Properties
     */
    public void showProperties() {
        Enumeration e = properties.propertyNames();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            String value = properties.getProperty(key);
            System.out.println(key + "=" + value);
        }
    }
}
