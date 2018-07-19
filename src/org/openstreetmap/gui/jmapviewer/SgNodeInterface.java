/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openstreetmap.gui.jmapviewer;

import java.util.ArrayList;
import javax.swing.Icon;
import org.openstreetmap.gui.jmapviewer.interfaces.MapImage;

/**
 *(c) 2018 BATTELLE ENERGY ALLIANCE, LLC
 *ALL RIGHTS RESERVED 
 *
 * @author FRAZJD
 */
public interface SgNodeInterface {
    
    public void setMapImage(MapImage mapImage);
    public MapImage getMapImage();
    public String getName();
    public void setName(String name);
    public int getId();
    public void setId(int id);
    public String getType();
    public void setType(String uuidStr);
    public double getComputedRate();
    public Icon getIcon();
    public double getLat();
    public double getLongit();
    public void setLat(double lat);
    public void setLongit(double longit);
    public SgNode getRefNode();
    public void setRefNode(SgNode refNode);
    public boolean canCollapse();
    public boolean canExpand();
    public ArrayList<SgNodeInterface> getConnectedNodes(boolean recursive, ArrayList<SgNodeInterface>existingNodeList);
}
