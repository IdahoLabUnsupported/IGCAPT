/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openstreetmap.gui.jmapviewer;

import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.LayeredIcon;
import igcapt.inl.components.SgComponent;
import igcapt.inl.components.SgComponentGroupList;
import igcapt.inl.components.SgLayeredIcon;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import javax.swing.Icon;
import org.openstreetmap.gui.jmapviewer.interfaces.MapImage;

/**
 *(c) 2018 BATTELLE ENERGY ALLIANCE, LLC
 *ALL RIGHTS RESERVED 
 *
 * @author ymj
 */
public class SgNode implements SgNodeInterface {

    public SgNode(int id, String typeUuid, String name, boolean enableDataSending, boolean enableDataPassThrough, boolean isAggregate, int dataToSend, int maxInterval) {
        this(id, typeUuid, name, enableDataSending, enableDataPassThrough, isAggregate, false, dataToSend, maxInterval);
    }

    public SgNode(int id, String typeUuid, String name, boolean enableDataSending, boolean enableDataPassThrough, boolean isAggregate, boolean isCollapsed, int dataToSend, int maxInterval) {
        
        _name = name;
        _id = id;
        _type = UUID.fromString(typeUuid);
        _enableDataSending = enableDataSending;
        _enableDataPassThrough = enableDataPassThrough;
        _dataToSend = dataToSend;
        _maxLatency = maxInterval;
        _isAggregate = isAggregate;
        _isCollapsed = isCollapsed;

        _icon = null;   
        
        // Get the icon from the component.
        SgComponentGroupList sgComponentGroupList = IGCAPTgui.getInstance().getComponentGroupList();
        SgComponent sgComponent = sgComponentGroupList.getComponentByUuid(_type);
        
        // Need to duplicate since it is a layered icon and it uses a layer to
        // indicate selection.
        if (sgComponent != null) {
            _icon = new SgLayeredIcon(((SgLayeredIcon)sgComponent.getIcon()).getImage());
        }
        else {
            _icon = IGCAPTgui.getInstance().getUnknownNodeIcon();
        }
    }

    public SgNode() {
        //Constructor
    }

    String _name;
    int _id = -1;
    UUID _type = null;
    boolean _enableDataSending = true;
    boolean _enableDataPassThrough = true;
    boolean _isAggregate = false;
    int _dataToSend; // in bytes
    boolean _used = false;
    int _maxLatency; // in seconds
    double _xCoord = 0;
    double _yCoord = 0;
    double _lat; // Decimal degrees
    double _longit;
    List<Integer> _endPointList = new ArrayList<Integer>();
    Icon _icon = null;
    MapImage _mapImage = null; // MapImage that corresponds to this node.

    boolean _isCollapsed = false; // Allows the attribute to be saved/loaded so we can preserve
                                  // the collapsed state of the graph.
    public boolean getIsCollapsed() {
        return _isCollapsed;
    }

    public void setIsCollapsed(boolean _isCollapsed) {
        this._isCollapsed = _isCollapsed;
    }
                                  

    
    public SgComponent getAssociatedComponent() {
        SgComponentGroupList sgComponentGroupList = IGCAPTgui.getInstance().getComponentGroupList();
        SgComponent sgComponent = sgComponentGroupList.getComponentByUuid(_type);
        
        return sgComponent;
    }
    
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
        return _name;
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
        return _type.toString();
    }
    
    @Override
    public void setType(String uuidStr) {
        _type = UUID.fromString(uuidStr);
    }
    
    public boolean getEnableDataPassThrough() {
        return _enableDataPassThrough;
    }

    public void setEnableDataPassThrough(boolean enableDataPassThrough) {
        _enableDataPassThrough = enableDataPassThrough;
    }
    
    public boolean getEnableDataSending (){
        return _enableDataSending;
    }
    
    public void setEnableDataSending (boolean enableDataSending){
        _enableDataSending = enableDataSending;
    }

    public int getDataToSend() {
        return _dataToSend;
    }

    public void setDataToSend(int dataToSend) {
        _dataToSend = dataToSend;
    }
    
    public boolean getIsAggregate() {
        return _isAggregate;
    }
    
    public void setIsAggregate(boolean isAggregate) {
        _isAggregate = isAggregate;
    }

    public boolean getUsed() {
        return _used;
    }

    public void setUsed(boolean used) {
        _used = used;
    }

    // Interval in seconds
    public int getMaxLatency() {
        return _maxLatency;
    }

    public void setMaxLatency(int interval) {
        _maxLatency = interval;
    }

    @Override
    public double getLat() {
        return _lat;
    }

    @Override
    public void setLat(double lat) {
        _lat = lat;
    }

    @Override
    public double getLongit() {
        return _longit;
    }

    @Override
    public void setLongit(double longit) {
        _longit = longit;
    }
    
    public void setEndPointList(List<Integer> endPointList) {
        _endPointList = endPointList;
    }
    
    public List<Integer> getEndPointList() {
        return _endPointList;
    }

    // Compute rate in Kilo bits per second
    @Override
    public double getComputedRate() {
        double returnval = 0.0;
        if(_maxLatency > 0.0) {
            returnval = 8 * (double)_dataToSend / _maxLatency / 1000;
        }
        
        return returnval;
    }

 /*  public String toString() {
        return "n" + getId();
    }*/

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public Icon getIcon() {
        updateLayeredIcon();
        return _icon;
    }

    @Override
    public SgNode getRefNode() {
        return this;
    }

    @Override
    public void setRefNode(SgNode refNode) {
        // Don't do anything because an SgNode is its own ref node.  SgGraphs will refer to the SgNode that was collapsed.
    }
    
    @Override
    public ArrayList<SgNodeInterface> getConnectedNodes(boolean recursive, ArrayList<SgNodeInterface> existingNodeList) {
        
        ArrayList<SgNodeInterface> returnval = new ArrayList<>(existingNodeList);
        Collection<SgEdge> incidentEdges = IGCAPTgui.getInstance().getGraph().getIncidentEdges(this);
        
        if (incidentEdges != null) {
            List<SgEdge> connectedEdges = new ArrayList<>(incidentEdges);

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
        }
                            
        return returnval;
    }
    
    public ArrayList<SgNodeInterface> getCollapseableIncidentNodes(boolean recursive) {

        ArrayList<SgNodeInterface> returnval = null;
        
        // Get the component associated with this node.
        SgComponent sgComponent = getAssociatedComponent();
        
        if (sgComponent != null) {
            ArrayList<String> uuidCollapseIntoList = sgComponent.getCollapseIntoTypeUuids();

            if (uuidCollapseIntoList != null && uuidCollapseIntoList.size() > 0) {
                // Get the list of connected nodes
                ArrayList<SgNodeInterface> collapseableNeighborNodes = new ArrayList<>();
                collapseableNeighborNodes.add(this);
                List<SgNodeInterface> tempList = getConnectedNodes(false, collapseableNeighborNodes);

                for (SgNodeInterface tempNode : tempList) {
                    if (uuidCollapseIntoList.contains(tempNode.getType())) {
                        collapseableNeighborNodes.add(tempNode);

                        if (recursive) {
                            collapseableNeighborNodes = tempNode.getConnectedNodes(true, collapseableNeighborNodes);
                        }
                    }
                }

                returnval = collapseableNeighborNodes;
            }
        }
        
        return returnval;       
    }

    @Override
    public boolean canCollapse() {

        ArrayList<SgNodeInterface> collapseableNodeList = getCollapseableIncidentNodes(false);
        
        return (collapseableNodeList != null && collapseableNodeList.size() > 1);
    }

    private void updateLayeredIcon() {
        
        LayeredIcon layeredIcon = (LayeredIcon)_icon;
        
        if (canCollapse()) {
            layeredIcon.remove(IGCAPTgui.getInstance().getLayerIconMap().get("expandIcon"));
            layeredIcon.add(IGCAPTgui.getInstance().getLayerIconMap().get("collapseIcon"));
        }
        else {
            layeredIcon.remove(IGCAPTgui.getInstance().getLayerIconMap().get("expandIcon"));
            layeredIcon.remove(IGCAPTgui.getInstance().getLayerIconMap().get("collapseIcon"));            
        }

    }

    @Override
    // Can never expand an actual node, only SgGraph nodes can expand.
    public boolean canExpand() {
        return false;
    }
}
