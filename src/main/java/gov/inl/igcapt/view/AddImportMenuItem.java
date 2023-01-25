package gov.inl.igcapt.view;

import gov.inl.igcapt.graph.SgNode;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.openstreetmap.gui.jmapviewer.IGCAPTgui;

import java.io.File;
import java.io.IOException;
import javax.swing.*;

public class AddImportMenuItem extends JMenuItem {
    
    public AddImportMenuItem(java.awt.Frame parent) {
        super("Import...");
        createImportMenuItem(parent);
    }
    private void importGdtafFile(String fileToImport){
        
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
                
                createTopology(gdtaf);
                
            } catch (JAXBException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
    
    private void importFile(JFileChooser chooser) {
        String selectedOpenFile = chooser.getSelectedFile().toString();
        
        importGdtafFile(selectedOpenFile);
    }

    private void createImportMenuItem(java.awt.Frame parent) {
        this.addActionListener(ActionListener -> {
            JFileChooser chooser = new JFileChooser();
            
            if (!IGCAPTgui.getInstance().getLastPath().isEmpty()) {
                chooser.setCurrentDirectory(new File(IGCAPTgui.getInstance().getLastPath()));
            }
            
            if (chooser.showOpenDialog(IGCAPTgui.getInstance()) == JFileChooser.APPROVE_OPTION) {
                SwingUtilities.invokeLater(() -> {
                    importFile(chooser);
                });
            }
        });
    }
    
    private String stripUnderscoreFromUUID(String uuidStr) {
        
        return uuidStr.replace("_", "");
    }
    
    private void createTopology(gov.inl.igcapt.gdtaf.model.GDTAF gdtafData) {
        
        var assetRepo = gdtafData.getAssetRepo();
   
        // Comprised of nodes and edges. Loop through assets to create nodes. Each asset contains parent or children.
        // Use these to construct the edges.
        var igcapt = IGCAPTgui.getInstance();
        var igcaptGraph = igcapt.getGraph();
        
        var assetEquipment = gdtafData.getEquipmentRepo().getEquipment();
        
        int id=0;
        for (gov.inl.igcapt.gdtaf.model.Asset asset : assetRepo.getAsset()) {
        
            try {
                var location = asset.getAtLocation();
                if (location != null) {
                    String equipmentId = asset.getEquipment();
                    var equipmentInstance = assetEquipment.stream()
                        .filter(equipment -> equipmentId.equals(equipment.getUUID()))
                        .findAny()
                        .orElse(null);
                    String name=(equipmentInstance != null)?equipmentInstance.getName():"";

                    SgNode sgNode = new SgNode(id, "78bf0ae2-1a27-462d-b8af-39156e80b75c", name, true, true, false, false, 0, 10, "");

                    if (!equipmentId.isBlank() && !equipmentId.isEmpty()) {
                        sgNode.setType(stripUnderscoreFromUUID(equipmentId));
                    }
                    else {
                        System.out.println("Asset (id: " + asset.getUUID() + ") contains no equipment.");
                    }

                    sgNode.setLat(location.getY());
                    sgNode.setLongit(location.getX());

                    igcaptGraph.addVertex(sgNode);

                    id++;
                }
                else {
                    System.out.println("Asset (id: " + asset.getUUID() + ") contains no location.");
                }
            }
            catch(Exception e) {
                System.out.println("Exception thrown in processing asset (" + asset.getUUID() + ").");
            }
        }
        
        igcapt.refresh();   
    }
}
