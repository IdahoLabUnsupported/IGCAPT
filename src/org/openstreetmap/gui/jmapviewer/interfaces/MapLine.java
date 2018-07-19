/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openstreetmap.gui.jmapviewer.interfaces;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.util.List;


/**
 *(c) 2018 BATTELLE ENERGY ALLIANCE, LLC
 *ALL RIGHTS RESERVED 
 *
 * @author kur
 */

/**
 * Interface to be implemented by polygons that can be displayed on the map.
 *
 * @author Kurt Derr
 */public interface MapLine extends MapObject {
    

    /**
     * @return Latitude/Longitude of each point of polygon
     */
    List<? extends ICoordinate> getPoints();

    /**
     * Paints the map line on the map. The <code>points</code>
     * are specifying the coordinates within <code>g</code>
     *
     * @param g graphics
     * @param points list of points defining the polygon to draw
     */
    void paint(Graphics g, List<Point> points);

    /**
     * Paints the map line on the map. The <code>polygon</code>
     * is specifying the coordinates within <code>g</code>
     *
     * @param g graphics
     * @param polygon polygon to draw
     */
    void paint(Graphics g, Polygon polygon);
}
