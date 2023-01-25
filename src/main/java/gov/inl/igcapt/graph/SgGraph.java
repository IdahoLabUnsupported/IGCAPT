/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.igcapt.graph;

import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.Pair;
import gov.inl.igcapt.components.SgLayeredIcon;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;

import org.openstreetmap.gui.jmapviewer.IGCAPTgui;
import org.openstreetmap.gui.jmapviewer.interfaces.MapImage;

/**
 *(c) 2018 BATTELLE ENERGY ALLIANCE, LLC
 *ALL RIGHTS RESERVED 
 *
 * @author FRAZJD
 */
public class SgGraph extends UndirectedSparseMultigraph<SgNodeInterface, SgEdge> implements SgNodeInterface {
    public SgGraph() {
        super();
        
       _name = "";
        _id = -1;
        _type = "921eee6b-d5d7-474a-ad45-2b8f79834ca2";
    }
    
    private String _name;
    private int _id;
    private String _type;
    double _lat;
    double _longit;
    double _xCoord = 0;
    double _yCoord = 0;
    SgLayeredIcon _icon = null;
    private SgNode _refNode = null;
    MapImage _mapImage = null; // MapImage that corresponds to this node.
    
    @Override
    public void setMapImage(MapImage mapImage) {
        _mapImage = mapImage;
    }

    @Override
    public MapImage getMapImage() {
        return _mapImage;
    }
    
    @Override
    public String getName() {
        String returnval = _name;
        
        if (_refNode != null) {
            returnval = _refNode.getName();
        }
        
        return returnval;
    }
    
    @Override
    public void setName(String name) {
        _name = name;
    }
    
    @Override
    public int getId() {
        return _id;
    }
    
    @Override
    public void setId(int id) {
        _id = id;
    }
    
    @Override
    public String getType() {
        return _type;
    }
    
    @Override
    public void setType(String type) {
        _type = type;
    }
    
    @Override
    public double getComputedRate() {
        return 0.0;
    }
    
    @Override
    public Icon getIcon() {
        
        updateLayeredIcon();
        return _icon;
    }
   
    @Override
    public double getLat() {
        return _refNode.getLat();
    }

    @Override
    public void setLat(double lat) {
        _refNode.setLat(lat);
    }

    @Override
    public double getLongit() {
        return _refNode.getLongit();
    }

    @Override
    public void setLongit(double longit) {
        _refNode.setLongit(longit);
    }
    
    public double getYCoord() {
        return _yCoord;
    }

    public double getXCoord() {
        return _xCoord;
    }

    @Override
    public SgNode getRefNode() {
        return _refNode;
    }

    @Override
    public void setRefNode(SgNode refNode) {
        _refNode = refNode;
    }

    @Override
    public ArrayList<SgNodeInterface> getConnectedNodes(boolean recursive, ArrayList<SgNodeInterface> existingNodeList) {
        
        ArrayList<SgNodeInterface> returnval = new ArrayList<>(existingNodeList);      
        List<SgEdge> connectedEdges = new ArrayList<>(IGCAPTgui.getInstance().getGraph().getIncidentEdges(this));

        for (SgEdge edge : connectedEdges) {
            Pair<SgNodeInterface> endPointPair = IGCAPTgui.getInstance().getGraph().getEndpoints(edge);

            SgNodeInterface first = endPointPair.getFirst();
            SgNodeInterface second = endPointPair.getSecond();

            SgNodeInterface sgNodeInterfaceToAdd = null;
            
            if (first != this) {
                sgNodeInterfaceToAdd = first;
            }
            else if (second != this) {
                sgNodeInterfaceToAdd = second;
            }
            
            if (sgNodeInterfaceToAdd != null && !returnval.contains(sgNodeInterfaceToAdd)) {
                returnval.add(sgNodeInterfaceToAdd);
                if (recursive) {
                    returnval = sgNodeInterfaceToAdd.getConnectedNodes(recursive, returnval);
                }
            }
        }
                            
        return returnval;
    }

    @Override
    public boolean canCollapse() {
        return false;
    }
    
    // Override these two methods so we can keep the graphs (original and collapsed) synchronized.
    @Override
    public boolean addVertex(SgNodeInterface vertex) {
        boolean returnval = false;
        SgGraph originalGraph = IGCAPTgui.getInstance().getOriginalGraph();
        
        // Make sure to only add if not there and be sure to only add SgNodes to the original graph.
        if (this != originalGraph && !originalGraph.containsVertex(vertex) && vertex instanceof SgNode) {
            originalGraph.addVertex(vertex);
        }
        returnval = super.addVertex(vertex);
        
        return returnval;
    }
    
    @Override
    public boolean addEdge(SgEdge edge, SgNodeInterface v1, SgNodeInterface v2) {
        boolean returnval = false;
        
        SgGraph originalGraph = IGCAPTgui.getInstance().getOriginalGraph();
        if (this != originalGraph) {
            
            SgNodeInterface v1Node = v1.getRefNode();
            SgNodeInterface v2Node = v2.getRefNode();
            
            originalGraph.addEdge(edge, v1Node, v2Node);
        }
        returnval = super.addEdge(edge, v1, v2);
                
        return returnval;
    }
    
    private void updateLayeredIcon() {
        if (_icon == null && _refNode != null) {
            
            SgLayeredIcon refIcon = (SgLayeredIcon)_refNode.getIcon();
            _icon = new SgLayeredIcon(refIcon.getImage());
        }
        
        if (_icon != null) {
            
            if (canExpand()) {
                _icon.remove(IGCAPTgui.getInstance().getLayerIconMap().get("aggregateIcon"));
                _icon.add(IGCAPTgui.getInstance().getLayerIconMap().get("expandIcon"));
            }
            else {
                _icon.remove(IGCAPTgui.getInstance().getLayerIconMap().get("expandIcon"));
                _icon.add(IGCAPTgui.getInstance().getLayerIconMap().get("aggregateIcon"));
            }
        }
    }

    @Override
    public boolean canExpand() {
        // Can't expand if the reference node is an aggregate node.
        return !getRefNode().getIsAggregate();
    }
    
    @Override
    public String toString() {
        return getRefNode().getName();
    }
}
