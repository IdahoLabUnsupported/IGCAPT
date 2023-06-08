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
    
    public enum IgcaptProperty {
        ACK_SIZE,
        AGGREGATE_ICON,
        COLLAPSE_ICON,
        EXPAND_ICON,
        HEATMAP_END_COLOR,
        HEATMAP_GRID_SIZE,
        HEATMAP_KERNEL_RADIUS,
        HEATMAP_KERNEL_TYPE,
        HEATMAP_START_COLOR,
        LAST_ICON_PATH,
        LAST_PATH,
        SELECTION_ICON,
        SG_COMPONENTS_FILE,
        SG_USE_CASE_FILE,
        UNKNOWN_NODE_ICON,
        UTILIZATION_HIGH_LIMIT,
        UTILIZATION_MEDIUM_LIMIT
    }

    /**
     * Fields
     */
    private static IGCAPTproperties igcaptProperties = null;
    private Properties properties = new Properties();
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
    public void setPropertyKeyValue(IgcaptProperty key, String value) {
        properties.setProperty(convertKeyToString(key), value);
    }
    
    public String convertKeyToString(IgcaptProperty key) {
        String keyString=null;
        switch (key) {
            case ACK_SIZE:
                keyString = "ACKSize";
                break;
            case AGGREGATE_ICON:
                keyString = "aggregateIcon";
                break;
            case COLLAPSE_ICON:
                keyString = "collapseIcon";
                break;
            case EXPAND_ICON:
                keyString = "expandIcon";
                break;
            case HEATMAP_END_COLOR:
                keyString = "heatmapEndColor";
                break;
            case HEATMAP_GRID_SIZE:
                keyString = "heatmapGridSize";
                break;
            case HEATMAP_KERNEL_RADIUS:
                keyString = "heatmapKernelRadius";
                break;
            case HEATMAP_KERNEL_TYPE:
                keyString = "heatmapKernelType";
                break;
            case HEATMAP_START_COLOR:
                keyString = "heatmapStartColor";
                break;
            case LAST_ICON_PATH:
                keyString = "lastIconPath";
                break;
            case LAST_PATH:
                keyString = "LastPath";
                break;
            case SELECTION_ICON:
                keyString = "selectionIcon";
                break;
            case SG_COMPONENTS_FILE:
                keyString = "sgComponentsFile";
                break;
            case SG_USE_CASE_FILE:
                keyString = "sgUseCaseFile";
                break;
            case UNKNOWN_NODE_ICON:
                keyString = "unknownNodeIcon";
                break;
            case UTILIZATION_HIGH_LIMIT:
                keyString = "utilizationHighLimit";
                break;
            case UTILIZATION_MEDIUM_LIMIT:
                keyString = "utilizationMediumLimit";
                break;
        }
    
        return keyString;
    }

    /**
     * Get a Key-Value pair from the current Properties
     */
    public String getPropertyKeyValue(IgcaptProperty key) {
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
