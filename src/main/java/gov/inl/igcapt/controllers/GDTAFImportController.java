
package gov.inl.igcapt.controllers;

import edu.uci.ics.jung.graph.Graph;
import gov.inl.igcapt.components.DataModels.ComponentDao;
import gov.inl.igcapt.components.DataModels.SgComponentData;
import gov.inl.igcapt.components.DataModels.SgField;
import gov.inl.igcapt.components.DataModels.SgUseCase;
import gov.inl.igcapt.gdtaf.data.*;
import gov.inl.igcapt.gdtaf.model.*;
import gov.inl.igcapt.graph.GraphManager;
import gov.inl.igcapt.graph.SgEdge;
import gov.inl.igcapt.graph.SgNode;
import gov.inl.igcapt.graph.SgNodeInterface;
import gov.inl.igcapt.view.IGCAPTgui;
import gov.inl.igcapt.properties.IGCAPTproperties;
import gov.inl.igcapt.properties.IGCAPTproperties.IgcaptProperty;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.javatuples.Triplet;
import org.json.JSONObject;
public class GDTAFImportController {

    private static final Logger logger = Logger.getLogger(GDTAFImportController.class.getName());
    private final Map<String, SgNode> m_assetGuidToNodeMap = new HashMap<>(); // Asset GUID and node instance. Will need this when creating edges.
    private final List<Triplet<SgNode, String, Double>> m_edgeList = new ArrayList<>(); // Node instance and child asset GUID that form an edge.
    private gov.inl.igcapt.gdtaf.model.GDTAF m_gdtafData = null;
    private static GDTAFImportController m_importController = null;
    private ComponentDao m_componentDao = new ComponentDao();

    private GDTAFImportController() {

    }

    public static GDTAFImportController getInstance() {
        if (m_importController == null) {
            m_importController = new GDTAFImportController();
        }
        return m_importController;
    }

    public void readGDTAFScenarioFile(String fileToRead) {
        if (fileToRead != null && !fileToRead.isEmpty() && !fileToRead.isBlank()) {

            try {

                File currentFile = new File(fileToRead);
                try {
                    IGCAPTproperties.getInstance().setPropertyKeyValue(
                            IgcaptProperty.LAST_PATH, currentFile.getCanonicalPath());
                } catch (IOException ex) {
                    //Setting lastpath to "" assumes that the file didn't open because lastpath was bad
                    //Maybe lastpath was correct and file DNE -- just leave lastpath as it was?
                    //IGCAPTgui.getInstance().setLastPath("");
                }

                JAXBContext jaxbGdtafContext = JAXBContext.newInstance(gov.inl.igcapt.gdtaf.model.GDTAF.class);
                Unmarshaller jaxbGdtafUnmarshaller = jaxbGdtafContext.createUnmarshaller();
                m_gdtafData = (gov.inl.igcapt.gdtaf.model.GDTAF) jaxbGdtafUnmarshaller.unmarshal(currentFile);

                GDTAFScenarioMgr.getInstance().initRepo(m_gdtafData);
                AssetRepoMgr.getInstance().initRepo(m_gdtafData);
                ApplicationScenarioRepoMgr.getInstance().initRepo(m_gdtafData);
                EquipmentRepoMgr.getInstance().initRepo(m_gdtafData);
                CNRMRepoMgr.getInstance().initRepo(m_gdtafData);
                GridCommPathRepoMgr.getInstance().initRepo(m_gdtafData);
                PayloadRepoMgr.getInstance().initRepo(m_gdtafData);
                DataElementRepoMgr.getInstance().initRepo(m_gdtafData);
                GUCSRepoMgr.getInstance().initRepo(m_gdtafData);
                OperationalObjectiveRepoMgr.getInstance().initRepo(m_gdtafData);

            } catch (JAXBException ex) {
                System.out.println(ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null,
                    "fileToImport == null OR fileToImport.isEmpty() OR fileToImport.isBlank().",
                    "Attention",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    public void applyGDTAFSelections() {
        try {
            // Clear the graph
            IGCAPTgui.getInstance().clearGraph();

            // Clear utility maps and lists. They will be populated when the vertices are created for the graph.
            m_assetGuidToNodeMap.clear();
            m_edgeList.clear();

            createGraphVertices();
            createGraphEdges();

            // Remove containers and reconnect connected nodes.
            removeContainers();

            setNodeData(); //Payload and latency.
            
            IGCAPTgui.getInstance().graphChanged();

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Looks to see if an SgField is associated with the componentData parameter.. if so it updates the payload size
     * to ensure it is fresh.... it not it look int he DB to see if it can find an SgField with the payload param name..
     * if it finds it it updates the payload size...  finally if an SgField is not found with the Component or in the DB
     * this method makes one, associates it with the componentData and pushes it to the DB before returning it.
     * @param componentData - IGCAPT Node component data
     * @param payload - GDTAF payload class..
     * @return SgField - The looked up OR created SgField
     */
    private SgField getRelevantField(SgComponentData componentData, Payload payload){
        SgField ooField = null;

        //Determine the payload size
        int payloadSize = 0;
        var ooPayloadAttrList = payload.getAttributes();
        for (var attr : ooPayloadAttrList) {
            if (attr.getType() == PayloadAttributeType.STATIC_PAYLOAD_SIZE) {
                payloadSize = Integer.parseInt(attr.getValue());
            }
        }
        //see if the payload is associated with the component
        for(var field: componentData.getFields()){
            if(field.getName().equals(payload.getName())){
                ooField = field;
                ooField.setPayload(payloadSize);
                //Add Field does an update by delete and add since the field exists
                componentData.addField(ooField);
                m_componentDao.saveField(ooField);
                break;
            }
        }
        if(ooField == null){
            for(var dbfield: m_componentDao.getFields()){
                if(dbfield.getName().equals(payload.getName())){
                    ooField = dbfield;
                    ooField.setPayload(payloadSize);
                    componentData.addField(ooField);
                    break;
                }
            }
        }
        if(ooField == null){
            ooField = new SgField(payload.getName(), payloadSize);
            componentData.addField(ooField);
            m_componentDao.saveField(ooField);
            m_componentDao.saveComponent(componentData);
        }
        return ooField;
    }

    /**
     * This method looks to see if a relevent Use case exists... e.g. one with the same name as
     * the operational objective name.  It first looks to see if the use case is associated with the
     * component data... if not is looks through the database to see if it can find it... otherwise
     * it creates it
     * @param componentData
     * @param opObj
     * @return SgUseCase
     */
    private SgUseCase getRelevantUseCase(SgComponentData componentData, OperationalObjective opObj){

        SgUseCase ooUseCase = null;
        boolean foundincomp = false;
        boolean foundindb = false;

        if(componentData.getUsecases() == null){
            componentData.setUsecases(new ArrayList<>());
        }

        for(var comp_uc : componentData.getUsecases()){
            if(comp_uc.getName().equals(opObj.getName())){
                ooUseCase = comp_uc;
           //     foundincomp = true;
                break;
            }
        }
        if(ooUseCase == null) {
            ooUseCase = m_componentDao.getUseCaseByName(opObj.getName());
            if (ooUseCase != null) {
                componentData.addUsecase(ooUseCase);
                //foundindb = true;
            }
        }
        if(ooUseCase == null) {
            ooUseCase = new SgUseCase();
            ooUseCase.setName(opObj.getName());
            ooUseCase.setDescription(opObj.getName());
            ooUseCase.setFields(new ArrayList<>());
            ooUseCase.setComponents(new ArrayList<>());
            ooUseCase.setLatency(OperationalObjectiveRepoMgr.getInstance().getOpObjLatencySec(opObj.getUUID()));
            m_componentDao.saveUseCase(ooUseCase);
            componentData.addUsecase(ooUseCase);
            m_componentDao.saveComponent(componentData);
        }

        return ooUseCase;
    }

    // Set the payload and latency data for nodes based on GDTAF operational objectives.
    private void setNodeData() {
        List<String> solnAssetUUIDList = new ArrayList<String>();

        var opObjList = getRelevantOperationalObjectives();
        int counter = 0;
        for (var opObj : opObjList) {
            counter ++;
            solnAssetUUIDList.addAll(getSolutionAssetsRelatedToEquipUUID(opObj.getSource()));
            for (var solnAssetUuid : solnAssetUUIDList) {
                SgNode node = m_assetGuidToNodeMap.get(solnAssetUuid);
                if (node != null) {
                    Payload ooPayload = PayloadRepoMgr.getInstance().getPayload(opObj.getPayload());


                    SgField ooField = getRelevantField(node.getAssociatedComponent(), ooPayload);
                    node.refreshAssociatedComponent();
                    SgUseCase ooUseCase = getRelevantUseCase(node.getAssociatedComponent(), opObj);
                    node.refreshAssociatedComponent();

                    //If the componentData is not associated with the use_case add it.
                    if (!ooUseCase.getComponents().contains(node.getAssociatedComponent())) {
                        ooUseCase.addComponent(node.getAssociatedComponent());
                    }

                    //If the oofield is not associated with the use case add it
                    List<String> fieldNameList = new ArrayList<>();
                    for(var ucfield : ooUseCase.getFields()){
                        fieldNameList.add(ucfield.getName());
                    }
                    if (!fieldNameList.contains(ooField.getName())) {
                        ooUseCase.addField(ooField);
                        m_componentDao.saveUseCase(ooUseCase);
                    }

                    //Update the Node Component Data by removing and re-adding ooUseCase
                    node.getAssociatedComponent().updateUseCase(ooUseCase);
                    node.refreshAssociatedComponent();

                    List<String> destSolnAssetList = new ArrayList<>();
                    List<Integer> endpoints = new ArrayList<>();
                    List<String> endpointTypeList = new ArrayList<>();
                    for (var destEquipUUID : opObj.getDestination()) {
                        destSolnAssetList.addAll(getSolutionAssetsRelatedToEquipUUID(destEquipUUID));
                    }
                    for (var endPointAssetUUID : destSolnAssetList) {
                        var lookup = m_assetGuidToNodeMap.get(endPointAssetUUID);
                        if (lookup != null) {
                            endpoints.add(lookup.getId());
                            endpointTypeList.add(lookup.getType());
                        }
                    }
                    if (!endpoints.isEmpty()) {
                        node.setEndPointList(endpoints);
                    } else {
                        System.out.println("No Endpoints Found for source Node: " + node.getName() + " use_case: " + opObj.getName());
                    }
                }
            }
            //need to clear the list ahead of the next opObj
            solnAssetUUIDList.clear();
        }
    }


    // This method  looks through the solution assets that have the equipment association of the
    //uuid parameter directly, or Solution Assets that have an equipment association that is derived
    // from the uuid parameter.

    private List<String> getSolutionAssetsRelatedToEquipUUID(String uuid) {
        List<String> solnAssetUuidList = new ArrayList<>();
        //find any solution assets that have a direct equip association with SourceID
        solnAssetUuidList.addAll(GDTAFScenarioMgr.getInstance().findSolutionAssetsOfEquipmentType(uuid));
        //find any solution asset that has an equipment type derived from the SourceID
        for (var equipUuid : EquipmentRepoMgr.getInstance().getEquipmentDerivedFrom(uuid)) {
            solnAssetUuidList.addAll(GDTAFScenarioMgr.getInstance().findSolutionAssetsOfEquipmentType(equipUuid));
        }
        return solnAssetUuidList;
    }


    private List<OperationalObjective> getRelevantOperationalObjectives() {
        // To get to the operational Objective we need to find the GUCS for the Scenario... and then we need to
        // get a list of Application Scenarios from the GUCS... then each App Scenario will have a list of
        // Operational Objective UUIDs so we can look them up with the OO Repo MGR...
        List<String> selectedGUCSList = GDTAFScenarioMgr.getInstance().getActiveScenario().getSelectedGucs();
        List<String> appScenarioUUIDList = new ArrayList<>();
        List<String> opObjUUIDLIst = new ArrayList<>();
        List<OperationalObjective> ooList = new ArrayList<>();

        // build the list of application scenario UUIDs
        for (var gucsUUID : selectedGUCSList) {
            appScenarioUUIDList.addAll(GUCSRepoMgr.getInstance().getGridUseCase(gucsUUID).getAppScenarios());
        }

        //build the list of Operational Objective UUIDs
        for (var appScenUUID : appScenarioUUIDList) {
            opObjUUIDLIst.addAll(ApplicationScenarioRepoMgr.getInstance().getApplicationScenario(appScenUUID).getObjectives());
        }

        for (var oouuid : opObjUUIDLIst) {
            ooList.add(OperationalObjectiveRepoMgr.getInstance().getOperationalObjective(oouuid));
        }

        return ooList;
    }

    private String stripUnderscoreFromUUID(String uuidStr) {

        return uuidStr.replace("_", "");
    }
    
    private boolean isContainer(SolutionAsset solutionAsset){
        return (solutionAsset.getEquipmentRole() == EquipmentRole.ROLE_CONTAINER);
    }
    
    private double getChildEdgeCapacity(EdgeType child) {
        double returnval = 128.0;
        
        var edgeTypeList = child.getEdges();
        if (!edgeTypeList.isEmpty()) {
            var bandwidthUUID = edgeTypeList.get(0);
            var option = GDTAFScenarioMgr.getInstance().getActiveSolutionOption();
            var bandwidthEdge = option.getEdge().stream().filter(edge -> edge.getUUID().equals(bandwidthUUID) && edge.getType().equals("LINK_CAPACITY"))
                    .findFirst()
                    .orElse(null);
            returnval = getEdgeAttributeCapacityValue(bandwidthEdge);
        }
        
        return returnval;
    }

    private double getEdgeAttributeCapacityValue(SolutionEdgeAttribute bandwidthEdge){
        double returnval = 128.0;

        if(bandwidthEdge != null) {
            String jsonString = bandwidthEdge.getValue();
            var obj = new JSONObject(jsonString);
            var countObj = (JSONObject) obj.get("count");
            var valueObj = countObj.getInt("value");
            var valueUnits = countObj.getString("units");
            var everyObj = (JSONObject) obj.get("every");
            var everyValue = everyObj.getInt("value");
            var everyUnits = everyObj.getString("units");

            double countConversion;
            switch (valueUnits) {
                case "bit" -> countConversion = 1.0 / 1000.0;
                case "kilobit" -> countConversion = 1.0;
                case "byte" -> countConversion = 8.0 / 1000.0;
                case "kilobyte" -> countConversion = 8.0;
                case "megabyte" -> countConversion = 8.0 * 1000.0;
                default -> countConversion = 1.0;
            }

            double everyConversion;
            switch (everyUnits) {
                case "second" -> everyConversion = 1.0;
                case "minute" -> everyConversion = 60.0;
                case "hour" -> everyConversion = 3600.0;
                case "day" -> everyConversion = 86400.0;
                default -> everyConversion = 1.0;
            }

            // Convert to kbits/second.
            returnval = valueObj * countConversion / everyValue / everyConversion;
        }
        return returnval;
    }


    /**
     * Recursively add nodes starting with assetUuid and continuing through the solnAssetView's children.
     * @param assetUuid The uuid of the asset to recursively add.
     * @param solnAssetView The view's children to add.
     */
    private void addNodeAndChildren(String assetUuid, String solnAssetView) {

        var igcaptGraph = GraphManager.getInstance().getGraph();
        SolutionAsset solutionAsset = GDTAFScenarioMgr.getInstance().findSolutionAsset(assetUuid);
        SgNode sgNode = null;

        // This asset already exists. Don't add it again.
        if (!m_assetGuidToNodeMap.containsKey(assetUuid)) {
            if (EquipmentRepoMgr.getInstance().getAllEquip() != null) {
                if (solutionAsset != null) {
                    var location = solutionAsset.getAtLocation();

                    String equipmentId = solutionAsset.getEquipment();
                    //lookup the Equipment Object for the SolutionAsset Equipment
                    Equipment equipmentInstance = EquipmentRepoMgr.getInstance().getEquip(equipmentId);

                    if (equipmentInstance != null) {
                        var equipInstanceIgcaptCompData =
                            IGCAPTgui.getComponentByUuid(EquipmentRepoMgr.getInstance().getICAPTComponentUUID(equipmentInstance.getUUID()));
                        int nodeId = GraphManager.getInstance().getNextNodeIndex();
                        String name = solutionAsset.getName();

                        sgNode = new SgNode(nodeId,
                                solutionAsset.getUUID(),
                                equipInstanceIgcaptCompData.getUuid(),
                                equipInstanceIgcaptCompData.getName(),
                                name,
                                true,
                                false,
                                false,
                                0,
                                10,
                                "assetGuid:" + stripUnderscoreFromUUID(solutionAsset.getUUID()));

                        m_assetGuidToNodeMap.put(solutionAsset.getUUID(), sgNode);

                        if (location == null){
                            location = new GeoLocation();
                            location.setX(0.0f);
                            location.setY(0.0f);
                            location.setZ(0.0f);
                        }
                        sgNode.setLat(location.getY());
                        sgNode.setLongit(location.getX());

                        igcaptGraph.addVertex(sgNode);

                        // We have access to the asset so grab the children at this point.
                        // Save the current Node instance and the child GUID. In the
                        // construction of the edges we will need the two Nodes that comprise
                        // the edge. We will have the one and can look up the other using the
                        // m_assetGuidToNodeMap.
                        var views = solutionAsset.getViews();

                        if (views != null && !views.isEmpty()) {
                            var assetView = views.stream()
                                    .filter(view -> view.getName().equals(solnAssetView))
                                    .findAny()
                                    .orElse(null);

                            if (assetView != null) {
                                List<EdgeType> children = assetView.getChildren();

                                if (children != null && !children.isEmpty()) {

                                    for (var child : children) {
                                        Triplet<SgNode, String, Double> edgeEntry = new Triplet<>(sgNode, child.getValue(), getChildEdgeCapacity(child));
                                        m_edgeList.add(edgeEntry);

                                        addNodeAndChildren(child.getValue(),solnAssetView);
                                    }
                                }
                            } else {
                                JOptionPane.showMessageDialog(null,
                                        "View == null",
                                        "Attention",
                                        JOptionPane.WARNING_MESSAGE);
                            }
                        } else {
                            logger.log(Level.WARNING,
                                    "views is null or views is empty");
                        }
                    } else {
                        logger.log(Level.WARNING,
                                "equipmentInstance is null");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null,
                        "Could not obtain a reference to the equipment repository.",
                        "Attention",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    SgNode getNodeFromAssetId(String assetId) {
        SgNode returnval = null;
        Graph graph = GraphManager.getInstance().getOriginalGraph();
        List<SgNodeInterface> nodes = new ArrayList<>(graph.getVertices());

        for (var node : nodes) {
            if (node.getAssetUUID().equals(assetId)) {
                returnval = (SgNode)node;
                break;
            }
        }
        
        return returnval;
    }
    
    /**
     * Traverse down to the children and return all the children, grandchildren, etc. that are not containers.
     * Stop traversing when only noncontainers exist.
     * @param asset The asset to start at.
     * @return 
     */
    List<SgNode> getContainerChildren(SolutionAsset asset) {
        
        List<SgNode> returnval = new ArrayList<>();
        
        var views = asset.getViews();
        var assetView = views.stream()
                .filter(view -> view.getName().equals("Topology"))
                .findAny()
                .orElse(null);

        if (assetView != null) {
            var children = assetView.getChildren();
        
            for (var child : children) {
                var childAsset = GDTAFScenarioMgr.getInstance().findSolutionAsset(child.getValue());

                if (isContainer(childAsset)) {
                    var containerChildren = getContainerChildren(childAsset);
                    returnval.addAll(containerChildren);
                }
                else {
                    returnval.add(getNodeFromAssetId(child.getValue()));
                }
            }
        }
        
        return returnval;
    }
    
    boolean isParent(SgNode child, SgNode parentCandidate, int maxDepth) {
        
        boolean returnval = false;
        
        String childAssetUuid = child.getAssetUUID();
        var childAsset = GDTAFScenarioMgr.getInstance().findSolutionAsset(childAssetUuid);
        
        if (childAsset != null && child != parentCandidate && maxDepth > 0) {
            var views = childAsset.getViews();
            if (views != null && !views.isEmpty()) {

                outer:
                for (var assetView : views) {
                    if (assetView != null && (assetView.getName().equals("Topology") || assetView.getName().equals("GUCS: SCADA for Observability"))) {
                        var parents = assetView.getParents();

                        if (parents != null && !parents.isEmpty()) {

                            for (var parent : parents) {
                                if (parentCandidate.getAssetUUID().equals(parent.getValue())) {
                                    returnval = true;
                                    break outer;
                                }
                                else {
                                    var parentNode = getNodeFromAssetId(parent.getValue());

                                    if (parentNode != null) {
                                        returnval = isParent(parentNode, parentCandidate, maxDepth-1);
                                        if (returnval) {
                                           break outer;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return returnval;
    }
        
    /**
     * Iterate through all nodes and remove container classes. They don't have location and are just aggregation points for networks.
     */
    private void removeContainers() {
        
        Graph graph = GraphManager.getInstance().getOriginalGraph();
        List<SgNodeInterface> nodes = new ArrayList<>(graph.getVertices());
        
        for(var node : nodes) {
            
            SolutionAsset asset = GDTAFScenarioMgr.getInstance().findSolutionAsset(node.getAssetUUID());
            if (asset != null && isContainer(asset)) {

                // Rewire all connections with the container to all the other connections and then delete the container and edges.
                // Get the list all nodes connected to the container.
                List<SgNodeInterface> connectedNodes = new ArrayList<>(graph.getNeighbors(node));
                List<SgEdge> connectedEdges = new ArrayList<>(graph.getIncidentEdges(node));
                Map<Integer, Double> nodeBandwidthMap = new HashMap<>();
                
                // For each node connected to a container that is a parent of the container, that is not a container itself, reconnect each connection
                // to the nearest children non-container.
                for (var connectedNode : connectedNodes) {
                    var connectedAsset = GDTAFScenarioMgr.getInstance().findSolutionAsset(connectedNode.getAssetUUID());
                    
                    if (isParent((SgNode)node, (SgNode)connectedNode, 1) && connectedAsset != null && !isContainer(connectedAsset)) { // Is the connected node a parent of container?
                        var childrenToConnect = getContainerChildren(asset);
                        
                        for (var childNode : childrenToConnect) {
                            int edgeIndex = GraphManager.getInstance().getNextEdgeIndex();
                            double minEdgeRate = 0.0;
                            
                            // Determine the minimum edge bandwidth.
                            
                            // This function returns the list of edges to get from connectedNode to childNode. There may be more than one path.
                            // Use the first path since there is not a good way to determine which one to use.
                            var paths = gov.inl.igcapt.components.AnalysisTask.getComponentPaths(graph, (SgNode)connectedNode, childNode, true);
                            
                            if (paths != null && !paths.isEmpty()) {
                                
                                minEdgeRate = -1.0;
                                for (var edgeId : paths.get(0)) {
                                    SgEdge sgEdge = GraphManager.getInstance().getEdge(graph, edgeId);
                                    
                                    // We assume that a node with 0.0 bandwidth specified has not been set. Don't use that one if another is available.
                                    if (minEdgeRate < 0.0 || (sgEdge.getEdgeRate() > 0.0 && sgEdge.getEdgeRate() < minEdgeRate))
                                    {
                                        minEdgeRate = sgEdge.getEdgeRate();
                                    }
                                }
                            }

                            // Create the edge
                            graph.addEdge(new SgEdge(edgeIndex, "e" + edgeIndex, 1.0, 0.0, minEdgeRate), connectedNode, childNode);
                        }
                    }
                }

                
                // Remove edges connected to the container after saving its bandwidth.
                for (var edge : connectedEdges) {
                    edu.uci.ics.jung.graph.util.Pair endPts = graph.getEndpoints(edge);
                    if (endPts.getFirst() != node) {
                        nodeBandwidthMap.put(((SgNode)endPts.getFirst()).getId(), edge.getEdgeRate());
                    }
                    else if (endPts.getSecond() != node) {
                        nodeBandwidthMap.put(((SgNode)endPts.getSecond()).getId(), edge.getEdgeRate());
                    }
                    
                    graph.removeEdge(edge);
                }
                                
                graph.removeVertex(node);
            }
        }
        
        IGCAPTgui.getInstance().refresh();
    }

    private void createGraphVertices() {

        try {

            if (GDTAFScenarioMgr.getInstance().getActiveScenario() != null &&
                    GDTAFScenarioMgr.getInstance().getActiveSolution() != null &&
                    GDTAFScenarioMgr.getInstance().getActiveSolutionOption() != null &&
                    EquipmentRepoMgr.getInstance().count() > 0) {
                var topologyHead = GDTAFScenarioMgr.getInstance().getActiveSolutionOption().getTopologyHead();
                var gucsHeadList = GDTAFScenarioMgr.getInstance().getActiveSolutionOption().getGucsHead();

                if (topologyHead != null) {
                    addNodeAndChildren(topologyHead, "Topology");
                }
                if (gucsHeadList != null){
                    for( var gucsUUID: GDTAFScenarioMgr.getInstance().getActiveScenario().getSelectedGucs()){
                        String gucsViewString = "GUCS: " + GUCSRepoMgr.getInstance().getGridUseCase(gucsUUID).getName();
                        addNodeAndChildren(gucsHeadList.get(0), gucsViewString);
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("Exception thrown in processing scenario: " + ex.getLocalizedMessage());
        }
        IGCAPTgui.getInstance().refresh();
    }

    private void createGraphEdges() {

        try {
            for (var edge : m_edgeList) {

                int edgeId = GraphManager.getInstance().getNextEdgeIndex();
                SgEdge e1 = new SgEdge(edgeId,
                        "e" + edgeId,
                        1.0,
                        0,
                        edge.getValue2());

                var childNode = m_assetGuidToNodeMap.get(edge.getValue1());

                if (childNode != null) {
                    GraphManager.getInstance().getGraph().addEdge(e1, edge.getValue0(), childNode);
                }

            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE,
                    "Exception thrown during creation of edges: " + ex.getLocalizedMessage());
        }

        IGCAPTgui.getInstance().refresh();
    }

    private boolean isCommEquipment(gov.inl.igcapt.gdtaf.model.Equipment equipment) {
        boolean retval = false;
        var equipRoleList = equipment.getPossibleRole();
        for (EquipmentRole role : equipRoleList) {
            if (role.value().equals(EquipmentRole.ROLE_COMMS_LINK.toString()) ||
                    role.value().equals(EquipmentRole.ROLE_FIREWALL.toString()) ||
                    role.value().equals(EquipmentRole.ROLE_HEADEND.toString()) ||
                    role.value().equals(EquipmentRole.ROLE_REPEATER.toString()) ||
                    role.value().equals(EquipmentRole.ROLE_ROUTER.toString()) ||
                    role.value().equals(EquipmentRole.ROLE_SWITCH.toString()) ||
                    role.value().equals(EquipmentRole.ROLE_TOWER.toString()) ||
                    role.value().equals(EquipmentRole.ROLE_TAKEOUT.toString())) {
                retval = true;
            }
        }
        return retval;
    }
}
