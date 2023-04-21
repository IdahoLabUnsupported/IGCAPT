/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.igcapt.graph;

import gov.inl.igcapt.properties.IGCAPTproperties;
import java.util.HashMap;
import java.util.Map;
import org.openstreetmap.gui.jmapviewer.Coordinate;

/**
 *(c) 2018 BATTELLE ENERGY ALLIANCE, LLC
 *ALL RIGHTS RESERVED 
 *
 * @author ymj
 */
public class SgEdge {
    
    private String _name;
    private int _id;
    private double _weight;
    private double _calcTransRate;
    private double _edgeRate;
    private boolean _isEnabled = true;
//    private MapLine _mapLine = null; // MapImage that corresponds to this node.
    private Coordinate _midPoint = null;
    private Map<String, String> _attributes = new HashMap<>();
    
    //                                                    ============================================
    //                                                    |              Total Overhead              |
    //                                                    ============================================
    // Net payload is computed as net payload = payload + _fixedOverhead + _multiplierOverhead*payload
    private int _fixedOverhead = 0;
    private double _multiplierOverhead = 0.0;

    public static double defaultUtilizationHigh = 0.8;
    public static double defaultUtilizationMedium = 0.5;
  
    public SgEdge(int id, String name, double weight, double calcTransRate, double edgeRate) {
        _name = name;
        _id = id;
        _weight = weight;
        _calcTransRate = calcTransRate;
        _edgeRate = edgeRate;
    }

    public SgEdge(int id, String name, double weight, double calcTransRate, double edgeRate, int fixedOverhead, double multiplierOverhead) {
        _name = name;
        _id = id;
        _weight = weight;
        _calcTransRate = calcTransRate;
        _edgeRate = edgeRate;
        _fixedOverhead = fixedOverhead;
        _multiplierOverhead = multiplierOverhead;
    }

    public SgEdge() {
// Constructor    
    }
    
    public void setMidPoint(Coordinate point) {
        _midPoint = point;
    }
    
    public Coordinate getMidPoint() {
        return _midPoint;
    }
    
// These are never called and perhaps _mapLine should be removed from the class
//    public void setMapLine(MapLine mapLine) {
//        _mapLine = mapLine;
//    }

//    public MapLine getMapLine() {
//        return _mapLine;
//    }
    
    public double getUtilization() {
        return ((getEdgeRate() > 0.0)?(getCalcTransRate() / getEdgeRate()):0.0);
    }
  
    public boolean isOverHighUtilizationLimit()
    {
        double highLimitValue = getHighUtilizationLimit()/100.0;
        
        return (getUtilization() > highLimitValue);
    }
    
    public static double getHighUtilizationLimit() {
        String highLimit = IGCAPTproperties.getInstance().getPropertyKeyValue("utilizationHighLimit");
        double highLimitValue;
        
        try {
            highLimitValue = Double.parseDouble(highLimit);
        }
        catch (NullPointerException | NumberFormatException ex) {
            highLimitValue = defaultUtilizationHigh;
        }
        
        return highLimitValue;
    }
    
    public static double getMediumUtilizationLimit() {
        String mediumLimit = IGCAPTproperties.getInstance().getPropertyKeyValue("utilizationMediumLimit");
        double mediumLimitValue;
        
        try {
            mediumLimitValue = Double.parseDouble(mediumLimit);
        }
        catch (NullPointerException | NumberFormatException ex) {
            mediumLimitValue = defaultUtilizationMedium;
        }
        
        return mediumLimitValue;
    }
    
    public boolean isOverMidUtilizationLimit()
    {
        double mediumLimitValue = getMediumUtilizationLimit();
        
        return (getUtilization() > mediumLimitValue/100.0);
    }
    
    // Assume this means that no analysis has been done.
    public boolean isZeroUtilizationLimit()
    {
        return (Math.abs(getUtilization()) <= 0.00000001);
    }

    public String getName() {
        return _name;
    }
    
    public void setName(String name) {
        _name = name;
    }
    
    public int getId() {
        return _id;
    }

    public int setId(int id) {
        _id = id;
        return _id;
    }

    public double getWeight() {
        return _weight;
    }

    public void setWeight(double weight) {
        _weight = weight;
    }

    public double getEdgeRate() {
        return _edgeRate;
    }

    public void setEdgeRate(double edgeRate) {
        _edgeRate = edgeRate;
    }

    public double getCalcTransRate() {
        return _calcTransRate;
    }

    public void setCalcTransRate(double calcTransRate) {
        _calcTransRate = calcTransRate;
    }
    
    public boolean isEnabled (){
        return _isEnabled;
    }
    
    public void setIsEnabled (boolean isEnabled){
        _isEnabled = isEnabled;
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * @return the _fixedOverhead
     */
    public int getFixedOverhead() {
        return _fixedOverhead;
    }

    /**
     * @param fixedOverhead the _fixedOverhead to set
     */
    public void setFixedOverhead(int fixedOverhead) {
        _fixedOverhead = fixedOverhead;
    }

    /**
     * @return the _multiplierOverhead
     */
    public double getMultiplierOverhead() {
        return _multiplierOverhead;
    }

    /**
     * @param multiplierOverhead the _multiplierOverhead to set
     */
    public void setMultiplierOverhead(double multiplierOverhead) {
        _multiplierOverhead = multiplierOverhead;
    }
}
