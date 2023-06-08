package org.openstreetmap.gui.jmapviewer;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Font;
import java.awt.image.BufferedImage;
import org.openstreetmap.gui.jmapviewer.interfaces.MapImage;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;

/**
 * A simple implementation of the {@link MapMarker} interface. Each map marker
 * is painted as a circle with a black border line and filled with a specified
 * color.
 *
 * @author Jan Peter Stotz
 *
 */
public class MapImageImpl extends MapObjectImpl implements MapImage {

    private Coordinate coord;
    BufferedImage img;
    String id;
    
    //private MapMarker.STYLE markerStyle;

    enum STYLE {
        FIXED,
        VARIABLE
    }

    // constructor for images
    public MapImageImpl(double lat, double lon, BufferedImage image, double radius) {
        this(null, null, new Coordinate(lat, lon), radius);
        img = image;
    }

    /**
     * Constructs a new {@code MapImageImpl}.
     * @param layer Layer of the map marker
     * @param name Name of the map marker
     * @param coord Coordinates of the map marker
     * @param radius Radius of the map marker position
     */
    public MapImageImpl(Layer layer, String name, Coordinate coord, double radius) {
        this(layer, name, coord, radius, STYLE.VARIABLE, getDefaultStyle());
    } 

    /**
     * Constructs a new {@code MapImageImpl}.
     * @param layer Layer of the map marker
     * @param name Name of the map marker
     * @param coord Coordinates of the map marker
     * @param radius Radius of the map marker position
     * @param markerStyle Marker style (fixed or variable)
     * @param style Graphical style
     */
    public MapImageImpl(Layer layer, String name, Coordinate coord, double radius, STYLE markerStyle, Style style) {
        super(layer, name, style);
        this.coord = coord;
    }

    @Override
    public Coordinate getCoordinate() {
        return coord;
    }

    @Override
    public double getLat() {
        return coord.getLat();
    }

    @Override
    public double getLon() {
        return coord.getLon();
    }

    public BufferedImage getImg() {
        return img;
    }

    public void setImg(BufferedImage img) {
        this.img = img;
    }

    public String getId() {
        return id;
    }

    public void setId(String someId) {
        id = someId;
    }


    @Override
    public void paint(Graphics g, Point position, BufferedImage img) {

        if (g instanceof Graphics2D && getBackColor() != null) {
            Graphics2D g2 = (Graphics2D) g;
            Composite oldComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
            g2.setPaint(getBackColor());
            //g.fillOval(position.x - sizeH, position.y - sizeH, size, size);
            g2.setComposite(oldComposite);
                    
            // place the image so the connecting lines go to the image center
            int imageWidth = img.getWidth();
            int imageHeight = img.getHeight();
            int newImageWidth = (int) (imageWidth/2);
            int newImageHeight = (int) (imageHeight/2);
            g2.drawImage(img, null, position.x - newImageWidth, position.y - newImageHeight);
            
            // draw the text label for the image
            var imageId = getId();
            if (imageId != null) {
                g2.setColor(Color.black);
                g2.setFont(new Font(g2.getFont().getFontName(), Font.BOLD, g2.getFont().getSize()));                
                g2.drawString(imageId, position.x + newImageWidth, position.y + newImageHeight);
            }
        }
    }

    public static Style getDefaultStyle() {
        return new Style(Color.ORANGE, new Color(200, 200, 200, 200), null, getDefaultFont());
    }

    @Override
    public String toString() {
        return "MapMarker at " + getLat() + ' ' + getLon();
    }
// these two methods will not work!
    @Override
    public void setLat(double lat) {
        if (coord == null) coord = new Coordinate(lat, 0);
        else coord.setLat(lat);
    }

    @Override
    public void setLon(double lon) {
        if (coord == null) coord = new Coordinate(0, lon);
        else coord.setLon(lon);
    }
   public void setLatLon(double lat, double lon) {
           if (coord == null) coord = new Coordinate(lat, lon);
           else {
               coord.setLat(lat);
               coord.setLon(lon);
           } 
} 
    
}
