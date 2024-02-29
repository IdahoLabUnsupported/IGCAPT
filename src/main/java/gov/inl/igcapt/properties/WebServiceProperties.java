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
public final class WebServiceProperties implements Serializable {
    
    public enum WebServiceProperty {
        WEB_SERVICE_HOST,
        WEB_SERVICE_KEY
    }

    /**
     * Fields
     */
    private static WebServiceProperties m_webServiceProperties = null;
    private Properties properties = new Properties();
    private String _fileName = "webService.properties";

    /**
     * Creates new IGCAPTproperties
     */
    private WebServiceProperties() {
        loadProperties();
    }

    /**
     * Returns a Singleton instance of the class
     */
    public static WebServiceProperties getInstance() {
        if (m_webServiceProperties == null) {
            m_webServiceProperties = new WebServiceProperties();
        }
        return m_webServiceProperties;
    }

    private void loadProperties() {
        
        InputStreamReader in = null;

        try {
             in = new InputStreamReader(new FileInputStream(_fileName), "UTF-8");
             properties.load(in);
        } catch (FileNotFoundException ex) {
            System.err.println("ERROR: Web Service loadProperties failed");
            ex.printStackTrace();
        } catch (IOException ex) {
            System.err.println("ERROR: Web Service loadProperties failed");
            ex.printStackTrace();
        } finally {
            if (null != in) {
                try {
                     in.close();
                } catch (IOException ex) {
                    System.err.println("ERROR: Web Service Property file close failed");
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
            System.err.println("ERROR: Web Service Properties.save() failed");
            ioe.printStackTrace();
        }
    }

    /**
     * Add a new Key-Value pair in the current Properties
     */
    public void setPropertyKeyValue(WebServiceProperty key, String value) {
        properties.setProperty(convertKeyToString(key), value);
    }
    
    public String convertKeyToString(WebServiceProperty key) {
        String keyString=null;
        switch (key) {
            case WEB_SERVICE_HOST:
                keyString = "WebServiceHost";
                break;
            case WEB_SERVICE_KEY:
                keyString = "WebServiceKey";
                break;
        }
    
        return keyString;
    }
    
    public String buildUrlString(String path) {
        // NOTE: Host may be just a name or a name:port
        String host = getPropertyKeyValue(WebServiceProperty.WEB_SERVICE_HOST);
        String key = getPropertyKeyValue(WebServiceProperty.WEB_SERVICE_KEY);
        String urlString = "";
        
        if (key.equals("")) {
            urlString = "http://" + host + path;
        }
        else {
            urlString = "https://" + host + path + "?subscription-key=" + key;
        }
        
        return urlString;        
    }

    /**
     * Get a Key-Value pair from the current Properties
     */
    public String getPropertyKeyValue(WebServiceProperty key) {
        return properties.getProperty(convertKeyToString(key));
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
