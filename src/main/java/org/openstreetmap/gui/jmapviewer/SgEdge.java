/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openstreetmap.gui.jmapviewer;

import org.openstreetmap.gui.jmapviewer.interfaces.MapLine;

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
    private MapLine _mapLine = null; // MapImage that corresponds to this node.

    public static double defaultUtilizationHigh = 0.8;
    public static double defaultUtilizationMedium = 0.5;
  
    public SgEdge(int id, String name, double weight, double calcTransRate, double edgeRate) {
        _name = name;
        _id = id;
        _weight = weight;
        _calcTransRate = calcTransRate;
        _edgeRate = edgeRate;
    }

    public SgEdge() {
// Constructor    
    }
    
    public void setMapLine(MapLine mapLine) {
        _mapLine = mapLine;
    }

    public MapLine getMapLine() {
        return _mapLine;
    }
    
    public double getUtilization() {
        return ((getEdgeRate() > 0.0)?(getCalcTransRate() / getEdgeRate()):0.0);
    }
  
    public boolean isOverHighUtilizationLimit()
    {
        double highLimitValue = getHighUtilizationLimit();
        
        return (getUtilization() > highLimitValue);
    }
    
    public static double getHighUtilizationLimit() {
        String highLimit = IGCAPTproperties.getInstance().getPropertyKeyValue("utilizationHighLimit");
        double highLimitValue;
        
        try {
            highLimitValue = Double.parseDouble(highLimit)/100.0;
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
            mediumLimitValue = Double.parseDouble(mediumLimit)/100.0;
        }
        catch (NullPointerException | NumberFormatException ex) {
            mediumLimitValue = defaultUtilizationMedium;
        }
        
        return mediumLimitValue;
    }
    
    public boolean isOverMidUtilizationLimit()
    {
        double mediumLimitValue = getMediumUtilizationLimit();
        
        return (getUtilization() > mediumLimitValue);
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

    public double setWeight(double weight) {
        _weight = weight;
        return _weight;
    }

    public double getEdgeRate() {
        return _edgeRate;
    }

    public double setEdgeRate(double edgeRate) {
        _edgeRate = edgeRate;
        return _edgeRate;
    }

    public double getCalcTransRate() {
        return _calcTransRate;
    }

    public double setCalcTransRate(double calcTransRate) {
        _calcTransRate = calcTransRate;
        return _calcTransRate;
    }
    
    public boolean isEnabled (){
        return _isEnabled;
    }
    
    public boolean setIsEnabled (boolean isEnabled){
        _isEnabled = isEnabled;
        return _isEnabled;
    }

    public String toString() {
        return getName();
    }

}
