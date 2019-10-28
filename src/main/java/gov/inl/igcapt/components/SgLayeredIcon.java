/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.igcapt.components;

import edu.uci.ics.jung.visualization.LayeredIcon;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import org.openstreetmap.gui.jmapviewer.IGCAPTgui;
import org.openstreetmap.gui.jmapviewer.SgNodeInterface;

/**
 *(c) 2018 BATTELLE ENERGY ALLIANCE, LLC
 *ALL RIGHTS RESERVED 
 *
 * @author FRAZJD
 */
public class SgLayeredIcon extends LayeredIcon {

    BufferedImage _image=null;
    
    public SgLayeredIcon(Image image) {
        super(image);
        _image = (BufferedImage)image;
    }

    // Return the composite image of all the icon images.
    public Image getCompositeImage() {
        
        // Paint the layered icon onto the image and return the single image.
        BufferedImage image = new BufferedImage(_image.getWidth(), _image.getHeight(), BufferedImage.TYPE_INT_ARGB);        
        Graphics2D graphics = image.createGraphics();
        paintIcon(null, graphics, 0, 0);
 
        return image;
    }
}