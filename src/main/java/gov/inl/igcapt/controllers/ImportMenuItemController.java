
package gov.inl.igcapt.controllers;

import gov.inl.igcapt.components.Pair;
import gov.inl.igcapt.gdtaf.model.EdgeIndexType;
import gov.inl.igcapt.gdtaf.model.EquipmentRole;
import gov.inl.igcapt.graph.GraphManager;
import gov.inl.igcapt.graph.SgNode;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.openstreetmap.gui.jmapviewer.IGCAPTgui;
import gov.inl.igcapt.gdtaf.model.Equipment;
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

public class ImportMenuItemController {

    private final Map<String, SgNode> m_assetGuidToNodeMap = new HashMap<>(); // Asset GUID and node instance. Will need this when creating edges.
    private final List<Pair<SgNode,String>> m_edgeList = new ArrayList<>(); // Node instance and child asset GUID that form an edge.
    
    public void importGdtafFile(String fileToImport){
        
        if (fileToImport != null && !fileToImport.isEmpty() && !fileToImport.isBlank()) {
            
            try {
                
                File currentFile = new File(fileToImport);
                try {
                    IGCAPTgui.getInstance().setLastPath(currentFile.getCanonicalPath());
                } catch (IOException ex) {
                    IGCAPTgui.getInstance().setLastPath("");
                }
                
                JAXBContext jaxbGdtafContext = JAXBContext.newInstance(gov.inl.igcapt.gdtaf.model.GDTAF.class);
                Unmarshaller jaxbGdtafUnmarshaller = jaxbGdtafContext.createUnmarshaller();
                var gdtaf = (gov.inl.igcapt.gdtaf.model.GDTAF)jaxbGdtafUnmarshaller.unmarshal(currentFile);
                
                // Clear the graph
                IGCAPTgui.getInstance().clearGraph();
                GraphManager.getInstance().setNodeIndex(-1);
                GraphManager.getInstance().setEdgeIndex(-1);
                
                // Clear utility maps and lists. They will be populated when the vertices are created for the graph.
                m_assetGuidToNodeMap.clear();
                m_edgeList.clear();
                
                createGraphVertices(gdtaf);
                createGraphEdges();
                
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
    
    private String stripUnderscoreFromUUID(String uuidStr) {
        
        return uuidStr.replace("_", "");
    }
    
      private void createGraphVertices(gov.inl.igcapt.gdtaf.model.GDTAF gdtafData) {

        var scenarioRepo = gdtafData.getScenarioRepo();
        
        var crnmRepo = gdtafData.getCNRMRepo();
        for (gov.inl.igcapt.gdtaf.model.CNRM crnm : crnmRepo.getCNRM()) {
            System.out.println("CRNM Layout: " + crnm.getLayout());
        }
        
        // Comprised of nodes and edges. Loop through assets to create nodes. Each asset contains parent and/or children.
        // Use these to construct the edges.
        var igcaptGraph = GraphManager.getInstance().getGraph();
        
        // Get the first scenario, first solution, first option
        var scenarioList = scenarioRepo.getScenario();
        var assetEquipment = gdtafData.getEquipmentRepo().getEquipment();
        
        try {
            if (scenarioList != null && !scenarioList.isEmpty() && assetEquipment != null && !assetEquipment.isEmpty()) {

                var scenario = scenarioList.get(0);

                if (scenario != null) {
                    var solutionList = scenario.getSolution();

                    if (solutionList != null && !solutionList.isEmpty()) {
                        var solution = solutionList.get(0);

                        if (solution != null) {
                            var optionList = solution.getOption();

                            if (optionList != null && !optionList.isEmpty()) {
                                var option = optionList.get(0);

                                if (option != null) {
                                    var solutionAssetList = option.getSolutionAsset();

                                    if (solutionAssetList != null && !solutionAssetList.isEmpty()) {

                                        for (var solutionAsset : solutionAssetList) {

                                            var location = solutionAsset.getAtLocation();

                                            if (location != null) {
                                                String equipmentId = solutionAsset.getEquipment();
                                                Equipment equipmentInstance = assetEquipment.stream()
                                                        .filter(equipment -> equipmentId.equals(equipment.getUUID())
                                                        && isCommEquipment(equipment))
                                                        .findAny()
                                                        .orElse(null);

                                                if (equipmentInstance != null) { 

                                                    int nodeId = GraphManager.getInstance().getNodeIndex() + 1;
                                                    String name = equipmentInstance.getName();
                                                    SgNode sgNode = new SgNode(nodeId,
                                                            equipmentInstance,
                                                            "78bf0ae2-1a27-462d-b8af-39156e80b75c",
                                                            name,
                                                            true,
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
                                                        System.out.println("Asset (id: " + solutionAsset.getUUID() + ") contains no equipment.");
                                                    }

                                                    sgNode.setLat(location.getY());
                                                    sgNode.setLongit(location.getX());

                                                    igcaptGraph.addVertex(sgNode);
                                                    GraphManager.getInstance().setNodeIndex(nodeId);

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
                                                                    m_edgeList.add(new Pair<SgNode, String>(sgNode, child.getValue()));
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
                                                        Logger.getLogger(ImportMenuItemController.class.getName()).log(Level.WARNING, 
                                                            "views is null or views is empty");
                                                    }
                                                }
                                                else {
                                                    Logger.getLogger(ImportMenuItemController.class.getName()).log(Level.WARNING, 
                                                        "equiptmentInstace is null");
                                                }
                                            }
                                            else {
                                                System.out.println("Asset (id: " + solutionAsset.getUUID() + ") contains no location");
                                            }
                                        }
                                    }
                                    else {
                                        Logger.getLogger(ImportMenuItemController.class.getName()).log(Level.WARNING, 
                                            "solutionAssetList is null or solutionAssetList is empty");
                                    }
                                }
                                else {
                                    JOptionPane.showMessageDialog(null,
                                    "option == null",
                                    "Attention",
                                    JOptionPane.WARNING_MESSAGE);
                                }
                            }
                            else {
                                Logger.getLogger(ImportMenuItemController.class.getName()).log(Level.WARNING, 
                                    "optionList is null or optionList is empty");
                            }
                        }
                        else {
                            JOptionPane.showMessageDialog(null,
                            "solution == null",
                            "Attention",
                            JOptionPane.WARNING_MESSAGE);
                        }
                    }
                    else {
                        Logger.getLogger(ImportMenuItemController.class.getName()).log(Level.WARNING, 
                            "solutionList is null or solutionList is empty");
                    }
                }
                else {
                    JOptionPane.showMessageDialog(null,
                    "scenario == null",
                    "Attention",
                    JOptionPane.WARNING_MESSAGE);
                }
            } 
            else {
                Logger.getLogger(ImportMenuItemController.class.getName()).log(Level.WARNING, 
                    "scenarioList == null OR scenarioList.isEmpty() OR assetEquipment == null OR assetEquipment.isEmpty()");
            }
        }
        catch(Exception ex) {
            System.out.println("Exception thrown in processing scenario");
        }

        IGCAPTgui.getInstance().refresh();
    }

    private void createGraphEdges(){
        
        try {
            for (var edge : m_edgeList) {

                int edgeId = GraphManager.getInstance().getEdgeIndex()+1;
                SgEdge e1 = new SgEdge(edgeId,
                        "e" + edgeId,
                        1.0,
                        0,
                        128.0);
                GraphManager.getInstance().setEdgeIndex(edgeId); // Increment the edgeId.

                var childNode = m_assetGuidToNodeMap.get(edge.second);
                
                if (childNode != null) {
                    GraphManager.getInstance().getGraph().addEdge(e1, edge.first, childNode);                   
                }
                
            }            
        }
        catch (Exception ex) {
            System.out.println("Exception thrown while creating edges.");
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
