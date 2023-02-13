
package gov.inl.igcapt.controllers;

import gov.inl.igcapt.gdtaf.model.Equipment;
import gov.inl.igcapt.gdtaf.model.EquipmentRole;
import gov.inl.igcapt.graph.GraphManager;
import gov.inl.igcapt.graph.SgEdge;
import gov.inl.igcapt.graph.SgNode;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.openstreetmap.gui.jmapviewer.IGCAPTgui;
import gov.inl.igcapt.gdtaf.model.Equipment;
import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.io.IOException;

public class ImportMenuItemController {
    
    public ImportMenuItemController() {
        m_gdtaf2igcaptIDMap = new HashMap<>();
    }

    private Map<String, Integer> m_gdtaf2igcaptIDMap;
    
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
                
                createCommsVertices(gdtaf);
                createGraphEdges();
                
            } catch (JAXBException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
    
    private String stripUnderscoreFromUUID(String uuidStr) {
        
        return uuidStr.replace("_", "");
    }
    
      private void createCommsVertices(gov.inl.igcapt.gdtaf.model.GDTAF gdtafData) {

        var assetRepo = gdtafData.getAssetRepo();
        var scenarioRepo = gdtafData.getScenarioRepo();
        var crnmRepo = gdtafData.getCNRMRepo();
        for (gov.inl.igcapt.gdtaf.model.CNRM crnm : crnmRepo.getCNRM()) {
            System.out.println("CRNM Layout: " + crnm.getLayout());
        }
        

        // Comprised of nodes and edges. Loop through assets to create nodes. Each asset contains parent or children.
        // Use these to construct the edges.
      //  var igcapt = IGCAPTgui.getInstance();
        var igcaptGraph = GraphManager.getInstance().getGraph();

        var assetEquipment = gdtafData.getEquipmentRepo().getEquipment();

        int id = 0;
        for (gov.inl.igcapt.gdtaf.model.Scenario scenario : scenarioRepo.getScenario()) {
            try {
                var plan_mod = scenario.getPlanningModel();
             
                for (gov.inl.igcapt.gdtaf.model.PlanningAsset passet : plan_mod.getPlanningAsset()) {

                    var location = passet.getAtLocation();
                    if (location != null) {
                        String equipmentId = passet.getEquipment();
                        Equipment equipmentInstance = assetEquipment.stream()
                                .filter(equipment -> equipmentId.equals(equipment.getUUID()) &&
                                        isCommEquipment(equipment))
                                .findAny()
                                .orElse(null);
                        if(equipmentInstance != null){


                        String name = (equipmentInstance != null) ? equipmentInstance.getName() : "";
                        m_gdtaf2igcaptIDMap.put(equipmentInstance.getUUID(), id);
                        SgNode sgNode = new SgNode(id,
                                    equipmentInstance, 
                                    "78bf0ae2-1a27-462d-b8af-39156e80b75c", 
                                    equipmentInstance.getName(), 
                                    true, 
                                    true, 
                                    false, 
                                    false, 
                                    0, 
                                    10, 
                                    "");

                        if (!equipmentId.isBlank() && !equipmentId.isEmpty()) {
                            sgNode.setType(stripUnderscoreFromUUID(equipmentId));
                        } else {
                            System.out.println("Asset (id: " + passet.getUUID() + ") contains no equipment.");
                        }

                        sgNode.setLat(location.getY());
                        sgNode.setLongit(location.getX());

                        igcaptGraph.addVertex(sgNode);
                        id++;
                        }
                    } else {
                        System.out.println("Asset (id: " + passet.getUUID() + ") contains no location.");
                    }
                }
                
            } catch (Exception e) {
                System.out.println("Exception thrown in processing scenario planning model (" + scenario.getName() + ").");
            }
        }
        
        IGCAPTgui.getInstance().refresh();
    }

    private void createGraphEdges(){
        var vertexList = GraphManager.getInstance().getOriginalGraph().getVertices();
        for (var vertex: vertexList){
            gov.inl.igcapt.gdtaf.model.Equipment equipModel = vertex.getRefNode().getGDTAFEquipmentModel();
           for(var child: equipModel.getPossibleChildren()){
               for (String key : m_gdtaf2igcaptIDMap.keySet()) {
                   System.out.println(key + " - " + m_gdtaf2igcaptIDMap.get(key));
               }
               System.out.println();

               if(m_gdtaf2igcaptIDMap.containsKey(child)){
                   var childId = m_gdtaf2igcaptIDMap.get(child);
                   var childNode = GraphManager.getInstance().getNode(GraphManager.getInstance().getGraph(),childId);
                   SgEdge e1 = new SgEdge(GraphManager.getInstance().getEdgeIndex()+1,
                           "e" + GraphManager.getInstance().getEdgeIndex()+1,
                           1.0,
                           0,
                           128.0);
                   GraphManager.getInstance().getGraph().addEdge(e1, vertex, childNode.getRefNode());
               }

           }
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
