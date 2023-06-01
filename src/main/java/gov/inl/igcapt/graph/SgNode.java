/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.igcapt.graph;

import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.LayeredIcon;
import gov.inl.igcapt.components.DataModels.ComponentDao;
import gov.inl.igcapt.components.DataModels.SgComponentData;
import gov.inl.igcapt.components.DataModels.SgField;
import gov.inl.igcapt.components.DataModels.SgUseCase;
import gov.inl.igcapt.components.KeyValueManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.Icon;

import gov.inl.igcapt.view.IGCAPTgui;
import java.util.HashMap;
import java.util.Map;
import org.openstreetmap.gui.jmapviewer.interfaces.MapImage;

/**
 *(c) 2018 BATTELLE ENERGY ALLIANCE, LLC
 *ALL RIGHTS RESERVED 
 *
 * @author ymj
 */
public class SgNode implements SgNodeInterface {

    // userData can hold key value pairs of text in the form "key1|value1;key2|value2". Always append to the list after initial creation.
    public SgNode(int id, 
            String typeUuid, 
            String name, 
            boolean enableDataSending, 
            boolean isAggregate, 
            int dataToSend, 
            int maxInterval, 
            String userData) {
        this(id, 
                typeUuid, 
                name, 
                enableDataSending, 
                isAggregate, 
                false, 
                dataToSend, 
                maxInterval, 
                userData);
    }
    
 
    public SgNode(int id,
            String assetUuid,
            String typeUuid,
            String typeName,
            String name,
            boolean enableDataSending,
            boolean isAggregate,
            boolean isCollapsed,
            int dataToSend,
            int maxInterval,
            String userData){
        this(id, 
                typeUuid, 
                name, 
                enableDataSending, 
                isAggregate,
                isCollapsed, 
                dataToSend, 
                maxInterval, 
                userData);
         _assetUuid = assetUuid;
         _typeName = typeName;
    }
        
    public SgNode(int id, 
            String typeUuid, 
            String name, 
            boolean enableDataSending, 
            boolean isAggregate, 
            boolean isCollapsed, 
            int dataToSend, 
            int maxInterval, 
            String userData) {
        
        _name = name;
        _id = id;
        //_type = UUID.fromString(typeUuid);
        _typeId = typeUuid;
        _enableDataSending = enableDataSending;
        _dataToSend = dataToSend;
        _maxLatency = maxInterval;
        _isAggregate = isAggregate;
        _isCollapsed = isCollapsed;
        _userData = userData;
        _component = IGCAPTgui.getComponentByUuid(typeUuid);

        _icon = null;   
        
        // Need to duplicate since it is a layered icon and it uses a layer to
        // indicate selection.
        if (_component != null) {
            _icon = _component.getIcon();
        }
        
        if (_icon == null || _component == null) {
            _icon = IGCAPTgui.getInstance().getUnknownNodeIcon();
        }
    }

    public SgNode() {
        //Constructor
    }

    private String _name;
    private int _id = -1;
    //private UUID _type = null;
    private String _typeId = null;

    private String _typeName = null;
    private String _assetUuid = null;
    private boolean _enableDataSending = true;
    private boolean _isAggregate = false;
    private int _dataToSend; // in bytes
    private boolean _used = false;
    private int _maxLatency; // in seconds
    private double _xCoord = 0;
    private double _yCoord = 0;
    private double _lat; // Decimal degrees
    private double _longit;
    private String _userData; // Key|Value pairs seperated by semicolon like this Key|Value;Key|Value;
    private SgComponentData _component; // The component that corresponds to this device.
    private Map<String, String> _attributes = new HashMap<>();
    private List<Integer> _endPointList = new ArrayList<>();
    private Icon _icon = null;
    private MapImage _mapImage = null; // MapImage that corresponds to this node.
    private boolean _renderName = true; // Should the name be shown on the map?

    @Override
    public void setRenderName(boolean _renderName) {
        this._renderName = _renderName;
    }

    @Override
    public boolean isRenderName() {
        return _renderName;
    }

    private boolean _isCollapsed = false; // Allows the attribute to be saved/loaded so we can preserve

    public Map<String, String> getAttributes() {
        return _attributes;
    }
    
    // the collapsed state of the graph.
    public boolean getIsCollapsed() {
        return _isCollapsed;
    }

    public void setIsCollapsed(boolean _isCollapsed) {
        this._isCollapsed = _isCollapsed;
    }
                                  
    public String getAssetUUID(){
        return _assetUuid;
    }
    
    public SgComponentData getAssociatedComponent() {
        return _component;
    }

    public void refreshAssociatedComponent(){
        ComponentDao compDao = new ComponentDao();
        _component = compDao.getComponentByUUID(_component.getUuid());
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
    
    public void setUserData(String userData) {
        _userData = userData;
    }
    
    public String getUserData() {
        return _userData;
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
        return _typeId.toString();
    }

    public String getTypeName(){
        return _typeName;
    }
    
    @Override
    public void setType(String uuidStr) {
        //_type = UUID.fromString(uuidStr);
        _typeId = uuidStr;
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
    
    // Compute rate in kilobits per second
    public double computeRate(int latency, int dataToSend) {
        
        double returnval = 0.0;
        
        if (latency > 0) {
            // 8 bits per byte * data bytes / latency in secs. / 1000 bits per kilobit
            returnval = 8 * (double)dataToSend / latency / 1000;
        }
        
        return returnval;
    }

    // Compute rate in Kilo bits per second
    @Override
    public double getComputedRate() {
        double returnval = computeRate(_maxLatency, _dataToSend);
        
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
    
    public void clearUseCaseUserData(){
                // Remove old userData that are useCase entries (they begin with "@$")
        KeyValueManager kVManager = new KeyValueManager(this.getUserData());
        if (kVManager.KeyValues() != null && kVManager.KeyValues().entrySet() != null && !kVManager.KeyValues().entrySet().isEmpty()) {
            kVManager.KeyValues().entrySet().removeIf(e -> e.getKey().startsWith("@$"));
            setUserData(kVManager.toString());
        }
    }
        
    public void applyUseCase(String useCaseName) {
        SgComponentData component = getAssociatedComponent();
        
        if (component != null) {
            List<SgUseCase> useCases = component.getUsecases();
            List<SgField> componentFields = component.getFields();
        
            if (useCases != null && componentFields != null) {
                for (SgUseCase useCase:useCases) {

                    if (useCase.getName().equals(useCaseName)) {

                        int newPayload = 0;
                        List<SgField> useCaseFields = useCase.getFields();
                        
                        if (useCaseFields != null) {
                            for (SgField field:useCaseFields) {
                                boolean found = false;

                                for (SgField componentField:componentFields){
                                    if (componentField.getName().equals(field.getName())){
                                        found = true;
                                        break;
                                    }
                                }

                                if (found) {
                                    newPayload += field.getPayload();
                                }                                   
                            }                         
                        }

                        int currentLatency = getMaxLatency();
                        int currentPayload = getDataToSend();
                        int newLatency = useCase.getLatency();

                        int combinedPayload = currentPayload + newPayload;
                        double combinedBandwidth = 0.0;

                        if (currentLatency > 0) {
                            combinedBandwidth += currentPayload/currentLatency;
                        }
                        if (newLatency > 0) {
                            combinedBandwidth += (double)newPayload/newLatency;
                        }
                        int effectiveLatency = (int)((double)combinedPayload/combinedBandwidth);
                        
                        // How do we clear out old user data? We need to make sure that we remove
                        // previous use case data when this is done.
                        String currentUserData = getUserData();
                        
                        // "@$" indicate use case data.
                        String newUserData = currentUserData + "@$" + useCaseName + "|" + newPayload + ";";
                        setUserData(newUserData);

                        setDataToSend(combinedPayload);
                        setMaxLatency(effectiveLatency);
                    }
                }
            }
        }
    }
    
    @Override
    public ArrayList<SgNodeInterface> getConnectedNodes(boolean recursive, ArrayList<SgNodeInterface> existingNodeList) {
        
        ArrayList<SgNodeInterface> returnval = new ArrayList<>(existingNodeList);
        Collection<SgEdge> incidentEdges = GraphManager.getInstance().getGraph().getIncidentEdges(this);
        
        if (incidentEdges != null) {
            List<SgEdge> connectedEdges = new ArrayList<>(incidentEdges);

            for (SgEdge edge : connectedEdges) {
                Pair<SgNodeInterface> endPointPair = GraphManager.getInstance().getGraph().getEndpoints(edge);

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
        SgComponentData sgComponent = getAssociatedComponent();
        
        if (sgComponent != null) {
            List<String> collapseIntoUuids = sgComponent.getSgCollapseIntoUuids();
            
            if (collapseIntoUuids != null && !collapseIntoUuids.isEmpty()) {
                // Get the list of connected nodes
                ArrayList<SgNodeInterface> collapseableNeighborNodes = new ArrayList<>();
                collapseableNeighborNodes.add(this);
                List<SgNodeInterface> tempList = getConnectedNodes(false, collapseableNeighborNodes);

                for (SgNodeInterface tempNode : tempList) {
                    SgNode refNode = tempNode.getRefNode();
                    if (collapseIntoUuids.contains(refNode.getType())) {
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
        
        // Need to check for greater than 1 because the node itself counts as 1.
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
