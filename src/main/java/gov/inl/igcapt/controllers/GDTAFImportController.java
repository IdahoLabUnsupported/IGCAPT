
package gov.inl.igcapt.controllers;

import gov.inl.igcapt.components.Pair;
import gov.inl.igcapt.gdtaf.data.*;
import gov.inl.igcapt.gdtaf.model.*;
import gov.inl.igcapt.graph.GraphManager;
import gov.inl.igcapt.graph.SgNode;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import gov.inl.igcapt.view.IGCAPTgui;
import gov.inl.igcapt.graph.SgEdge;
import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class GDTAFImportController {

    private static Logger logger = Logger.getLogger(GDTAFImportController.class.getName());
    private final Map<String, SgNode> m_assetGuidToNodeMap = new HashMap<>(); // Asset GUID and node instance. Will need this when creating edges.
    private final List<Pair<SgNode,String>> m_edgeList = new ArrayList<>(); // Node instance and child asset GUID that form an edge.
    private  gov.inl.igcapt.gdtaf.model.GDTAF m_gdtafData = null;

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
        }
        else {
            JOptionPane.showMessageDialog(null,
                    "fileToImport == null OR fileToImport.isEmpty() OR fileToImport.isBlank().",
                    "Attention",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
    public void applyGDTAFSelections(){
        try{
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
        
        // Need to look through objectives and grab payloads and latencies to add to each node.
        // It is going to be some work because the objectives are listed by equipment and not assets.
        // So, what needs to be done is loop through all objectives finding their equipment then
        // find the nodes that have this equipment and add the payload and latencies.
        // There is a map linking nodes with solution asset UUIDs. Use that to obtain
        // solution assets currently in play.
    }
    
    private String stripUnderscoreFromUUID(String uuidStr) {
        
        return uuidStr.replace("_", "");
    }

    
    // Recursively add nodes starting with assetUuid and continuing through the "Topology" View's children.
    private void addNodeAndChildren(String assetUuid) {
        
        var igcaptGraph = GraphManager.getInstance().getGraph();
        SolutionAsset solutionAsset = GDTAFScenarioMgr.getInstance().findSolutionAsset(assetUuid);
            
        // This asset already exists. Don't add it again.
        if (!m_assetGuidToNodeMap.containsKey(assetUuid)) {
            if (EquipmentRepoMgr.getInstance().getAllEquip() != null) {
                if (solutionAsset != null) {
                    var location = solutionAsset.getAtLocation();

                    if (location != null) {
                        String equipmentId = solutionAsset.getEquipment();
                        Equipment equipmentInstance = (Equipment) EquipmentRepoMgr.getInstance().getAllEquip().stream()
                                .filter(equipment -> equipmentId.equals(equipment.getUUID()))
                                .findAny()
                                .orElse(null);

                        if (equipmentInstance != null) { 

                            int nodeId = GraphManager.getInstance().getNextNodeIndex();
                            String name = equipmentInstance.getName();
                            SgNode sgNode = new SgNode(nodeId,
                                    equipmentInstance,
                                    EquipmentRepoMgr.getInstance().getICAPTComponentUUID(equipmentInstance.getUUID()),
                                    name,
                                    true,
                                    false,
                                    false,
                                    0,
                                    10,
                                    "assetGuid:"+stripUnderscoreFromUUID(solutionAsset.getUUID()));

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

                            // We have access to the asset so grab the children at this point.
                            // Save the current Node instance and the child GUID. In the
                            // construction of the edges we will need the two Nodes that comprise
                            // the edge. We will have the one and can look up the other using the 
                            // m_assetGuidToNodeMap.
                            var views = solutionAsset.getViews();

                            if (views != null && !views.isEmpty()) {
                                var topologyView = views.stream()
                                        .filter(view -> view.getName().equals("Topology"))
                                        .findAny()
                                        .orElse(null);

                                if (topologyView != null) {
                                    List<EdgeIndexType> children = topologyView.getChildren();

                                    if (children != null && !children.isEmpty()) {

                                        for (var child : children) {
                                            m_edgeList.add(new Pair<>(sgNode, child.getValue()));

                                            addNodeAndChildren(child.getValue());
                                        }
                                    }
                                }
                                else {
                                    JOptionPane.showMessageDialog(null,
                                    "topologyView == null",
                                    "Attention",
                                    JOptionPane.WARNING_MESSAGE);
                                }
                            }
                            else {
                                logger.log(Level.WARNING, 
                                    "views is null or views is empty");
                            }
                        }
                        else {
                            logger.log(Level.WARNING, 
                                "equipmentInstance is null");
                        }
                    }
                    else {
                        System.out.println("Asset (id: " + solutionAsset.getUUID() + ") contains no location");
                    }
                }
            }
            else {
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
                var option = GDTAFScenarioMgr.getInstance().getActiveSolutionOption();
                var topologyHead = GDTAFScenarioMgr.getInstance().getActiveSolutionOption().getTopologyHead();

                if (topologyHead != null) {
                    addNodeAndChildren(topologyHead);
                }
            }
        }
        catch(Exception ex) {
            System.out.println("Exception thrown in processing scenario: " + ex.getLocalizedMessage());
        }
        IGCAPTgui.getInstance().refresh();
    }

    private void createGraphEdges(){
        
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
        }
        catch (Exception ex) {
            logger.log(Level.SEVERE, 
                    "Exception thrown during creation of edges: " + ex.getLocalizedMessage());
        }
        
        IGCAPTgui.getInstance().refresh();
    }
      
      private boolean isCommEquipment(gov.inl.igcapt.gdtaf.model.Equipment equipment){
        boolean retval = false;
        var equipRoleList = equipment.getPossibleRole();
        for( EquipmentRole role : equipRoleList){
            if(role.value().equals(EquipmentRole.ROLE_COMMS_LINK.toString()) ||
                    role.value().equals(EquipmentRole.ROLE_FIREWALL.toString()) ||
                    role.value().equals(EquipmentRole.ROLE_HEADEND.toString())  ||
                    role.value().equals(EquipmentRole.ROLE_REPEATER.toString()) ||
                    role.value().equals(EquipmentRole.ROLE_ROUTER.toString())   ||
                    role.value().equals(EquipmentRole.ROLE_SWITCH.toString())   ||
                    role.value().equals(EquipmentRole.ROLE_TOWER.toString())    ||
                    role.value().equals(EquipmentRole.ROLE_TAKEOUT.toString())){
               retval = true; 
            }
        }
        return retval;
    }
}
