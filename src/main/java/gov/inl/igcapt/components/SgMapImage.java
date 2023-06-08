package gov.inl.igcapt.components;


import java.awt.image.BufferedImage;
import org.openstreetmap.gui.jmapviewer.MapImageImpl;
import gov.inl.igcapt.graph.SgNodeInterface;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *(c) 2018 BATTELLE ENERGY ALLIANCE, LLC
 *ALL RIGHTS RESERVED 
 *
 * @author FRAZJD
 */
public class SgMapImage extends MapImageImpl {
    
    private SgNodeInterface _sgNode = null;  // SgNode that corresponds to this image, if there is one.

    public void setSgNode(SgNodeInterface sgNode) {
        _sgNode = sgNode;
    }

    public SgNodeInterface getSgNode() {
        return _sgNode;
    }
    
    public SgMapImage(double lat, double lon, BufferedImage image, double radius, SgNodeInterface sgNode) {
        super(lat, lon, image, radius);
        _sgNode = sgNode;
    }
    
}
