
package gov.inl.igcapt.controllers;

import gov.inl.igcapt.components.DataModels.ComponentDao;
import gov.inl.igcapt.components.DataModels.SgField;
import gov.inl.igcapt.components.DataModels.SgUseCase;
import gov.inl.igcapt.components.Pair;
import gov.inl.igcapt.gdtaf.data.*;
import gov.inl.igcapt.gdtaf.model.*;
import gov.inl.igcapt.graph.GraphManager;
import gov.inl.igcapt.graph.SgEdge;
import gov.inl.igcapt.graph.SgNode;
import gov.inl.igcapt.view.IGCAPTgui;
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
public class GDTAFImportController {

    private static final Logger logger = Logger.getLogger(GDTAFImportController.class.getName());
    private final Map<String, SgNode> m_assetGuidToNodeMap = new HashMap<>(); // Asset GUID and node instance. Will need this when creating edges.
    private final List<Pair<SgNode, String>> m_edgeList = new ArrayList<>(); // Node instance and child asset GUID that form an edge.
    private gov.inl.igcapt.gdtaf.model.GDTAF m_gdtafData = null;
    private static GDTAFImportController m_importController = null;
    private static SgNode m_lastAncestorNode = null;

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
                    IGCAPTgui.getInstance().setLastPath(currentFile.getCanonicalPath());
                } catch (IOException ex) {
                    IGCAPTgui.getInstance().setLastPath("");
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
            setNodeData(); //Payload and latency.

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }


    // Set the payload and latency data for nodes based on GDTAF operational objectives.
    private void setNodeData() {
        ComponentDao componentDao = new ComponentDao();
        List<String> solnAssetUUIDList = new ArrayList<String>();

        var opObjList = getRelevantOperationalObjectives();

        for (var opObj : opObjList) {
            solnAssetUUIDList.addAll(getSolutionAssetsRelatedToEquipUUID(opObj.getSource()));
            for (var solnAssetUuid : solnAssetUUIDList) {
                SgNode node = m_assetGuidToNodeMap.get(solnAssetUuid);
                Payload ooPayload = PayloadRepoMgr.getInstance().getPayload(opObj.getPayload());
                int payloadSize = 0;
                var ooPayloadAttrList = ooPayload.getAttributes();
                for (var attr : ooPayloadAttrList) {
                    if (attr.getType() == PayloadAttributeType.STATIC_PAYLOAD_SIZE) {
                        payloadSize = Integer.parseInt(attr.getValue());
                    }
                }

                SgUseCase ooUseCase = null;
                SgField ooField = null;
                //See if the usecase is in the IGCAPT DB if not
                // create a new one which we will add to the DB
                for (var use_case:componentDao.getUseCases()) {
                    if(use_case.getName().equals(opObj.getName())){
                        ooUseCase = use_case;
                    }
                }
                if( ooUseCase == null) {
                    ooUseCase = new SgUseCase();
                    ooUseCase.setName(opObj.getName());
                }
                //See if the SgField is associated with the UseCase
                //if not create one and associate it to the use_case
                for(var field:ooUseCase.getFields()){
                    if(field.getName().equals(ooPayload.getName())) {
                        ooField = field;
                    }
                    if( ooField == null) {
                        for (var field2 : componentDao.getFields()) {
                            if (field2.getName().equals(ooPayload.getName())) {
                                ooField = field2;
                            }
                        }
                    }
                    if(ooField == null) {
                        ooField = new SgField(ooPayload.getName(), payloadSize);
                       // ooField.setId(node.getAssociatedComponent().getId());
                    }
                        //assert the size in case there have
                        //been changes
                        ooField.setPayload(payloadSize);
                       // ooField.setId(node.getAssociatedComponent().getId());
                    }
                componentDao.saveField(ooField);

                //if the node ComponentData does not contain ooField add it
                if(!node.getAssociatedComponent().getFields().contains(ooField)){
                    node.getAssociatedComponent().addField(ooField);
                }

                // of ooUsecase does not contain the Node data add it
                if(ooUseCase.getComponents().size() == 0){
                    ooUseCase.addComponent(node.getAssociatedComponent());
                }
                else if(!ooUseCase.getComponents().contains(node.getAssociatedComponent()))  {
                    ooUseCase.addComponent(node.getAssociatedComponent());
                }

                // if the ooUsecase does nto contain the ooField add it
                if(ooUseCase.getFields().size() == 0){
                    ooUseCase.addField(ooField);
                }
                else if(!ooUseCase.getFields().contains(ooField)){
                    ooUseCase.addField(ooField);
                }

                ooUseCase.setLatency(OperationalObjectiveRepoMgr.getInstance().getOpObjLatencySec(opObj.getUUID()));
                //TODO figure out a good place to grab the description...
                ooUseCase.setDescription(opObj.getName());

                //Update the Node Component Data by removing and re-adding ooUseCase
            //    node.getAssociatedComponent().removeUsecase(ooUseCase);
                node.getAssociatedComponent().addUsecase(ooUseCase);
                componentDao.saveUseCase(ooUseCase);
                componentDao.saveComponent(node.getAssociatedComponent());

                List<String> destSolnAssetList = new ArrayList<String>();
                List<Integer> endpoints = new ArrayList<Integer>();
                List<String> endpointTypeList = new ArrayList<String>();
                for (var destEquipUUID : opObj.getDestination()) {
                    destSolnAssetList.addAll(getSolutionAssetsRelatedToEquipUUID(destEquipUUID));
                }
                for (var endPointAssetUUID : destSolnAssetList) {
                    endpoints.add(m_assetGuidToNodeMap.get(endPointAssetUUID).getId());
                    endpointTypeList.add(m_assetGuidToNodeMap.get(endPointAssetUUID).getType());
                }
                node.setEndPointList(endpoints);
            }
            //need to clear the list ahead of the next opObj
            solnAssetUUIDList.clear();
        }
    }


    // This method  looks through the solution assets that have the equipment association of the
    //uuid parameter directly, or Solution Assets that have an equipment association that is derived
    // from the uuid parameter.

    private List<String> getSolutionAssetsRelatedToEquipUUID(String uuid) {
        List<String> solnAssetUuidList = new ArrayList<String>();
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
        List<String> appScenarioUUIDList = new ArrayList<String>();
        List<String> opObjUUIDLIst = new ArrayList<String>();
        List<OperationalObjective> ooList = new ArrayList<OperationalObjective>();

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

                    ComponentDao componentDao = new ComponentDao();
                    if (equipmentInstance != null) {
                        if (solutionAsset.getEquipmentRole() != EquipmentRole.ROLE_CONTAINER && location != null) {
                            var equipInstanceIgcaptCompData =
                                    componentDao.getComponentByUUID(EquipmentRepoMgr.getInstance().getICAPTComponentUUID(equipmentInstance.getUUID()));
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

                            m_lastAncestorNode = sgNode;
                            m_assetGuidToNodeMap.put(solutionAsset.getUUID(), sgNode);

                            if (!equipmentId.isBlank() && !equipmentId.isEmpty()) {
                                sgNode.setType(stripUnderscoreFromUUID(equipmentId));
                            } else {
                                JOptionPane.showMessageDialog(null,
                                        "Asset (id: " + solutionAsset.getUUID() + ") contains no equipment.",
                                        "Attention",
                                        JOptionPane.WARNING_MESSAGE);
                            }

                            sgNode.setLat(location.getY());
                            sgNode.setLongit(location.getX());

                            igcaptGraph.addVertex(sgNode);
                        }

                        // We have access to the asset so grab the children at this point.
                        // Save the current Node instance and the child GUID. In the
                        // construction of the edges we will need the two Nodes that comprise
                        // the edge. We will have the one and can look up the other using the
                        // m_assetGuidToNodeMap.
                        var views = solutionAsset.getViews();

                        if (views != null && !views.isEmpty() && m_lastAncestorNode != null) {
                            var assetView = views.stream()
                                    .filter(view -> view.getName().equals(solnAssetView))
                                    .findAny()
                                    .orElse(null);

                            if (assetView != null) {
                                List<EdgeType> children = assetView.getChildren();

                                if (children != null && !children.isEmpty()) {

                                    for (var child : children) {
                                        m_edgeList.add(new Pair<>(m_lastAncestorNode, child.getValue()));

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

    private void createGraphVertices() {

        try {

            if (GDTAFScenarioMgr.getInstance().getActiveScenario() != null &&
                    GDTAFScenarioMgr.getInstance().getActiveSolution() != null &&
                    GDTAFScenarioMgr.getInstance().getActiveSolutionOption() != null &&
                    EquipmentRepoMgr.getInstance().count() > 0) {
                var topologyHead = GDTAFScenarioMgr.getInstance().getActiveSolutionOption().getTopologyHead();
                var gucsHeadList = GDTAFScenarioMgr.getInstance().getActiveSolutionOption().getGucsHead();

                if (topologyHead != null) {
                    addNodeAndChildren(topologyHead, "Topology" );
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
                        128.0);

                var childNode = m_assetGuidToNodeMap.get(edge.second);

                if (childNode != null) {
                    GraphManager.getInstance().getGraph().addEdge(e1, edge.first, childNode);
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
