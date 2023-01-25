// License: GPL. For details, see Readme.txt file.
package org.openstreetmap.gui.jmapviewer.interfaces;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;

/**
 * Interface to be implemented by all one dimensional elements that can be displayed on the map.
 *
 * @author Jan Peter Stotz
 * @see JMapViewer#addMapMarker(MapMarker)
 * @see JMapViewer#getMapMarkerList()
 */
public interface MapImage extends MapObject, ICoordinate {

    /**
     * @return Latitude and Longitude of the map marker position
     */
    //ICoordinate getCoordinate();
    Coordinate getCoordinate();

    /**
     * @return Latitude of the map marker position
     */
    @Override
    double getLat();

    /**
     * @return Longitude of the map marker position
     */
    @Override
    double getLon();

    BufferedImage getImg();

    /**
     * Paints the map marker on the map. The <code>position</code> specifies the
     * coordinates within <code>g</code>
     *
     * @param g Graphics
     * @param position Point
     * @param img BufferedImage
     */
    void paint(Graphics g, Point position, BufferedImage img);
}
