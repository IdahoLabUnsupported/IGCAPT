package gov.inl.igcapt.view;

import gov.inl.igcapt.controllers.GDTAFImportController;
import gov.inl.igcapt.properties.IGCAPTproperties;
import gov.inl.igcapt.properties.IGCAPTproperties.IgcaptProperty;

import java.io.File;
import javax.swing.*;

public class AddImportMenuItem extends JMenuItem {
    
    public AddImportMenuItem(java.awt.Frame parent) {
        super("Import...");
        createImportMenuItem(parent);
        m_controller = GDTAFImportController.getInstance();
    }

    private GDTAFImportController m_controller;

    private void importFile(JFileChooser chooser) {
        String selectedOpenFile = chooser.getSelectedFile().toString();
    }

    private void createImportMenuItem(java.awt.Frame parent) {
        this.addActionListener(ActionListener -> {
            JFileChooser chooser = new JFileChooser();
            String lastPath = IGCAPTproperties.getInstance().getPropertyKeyValue(IgcaptProperty.LAST_PATH);
            
            if (!lastPath.isEmpty()) {
                chooser.setCurrentDirectory(new File(lastPath));
            }
            
            if (chooser.showOpenDialog(IGCAPTgui.getInstance()) == JFileChooser.APPROVE_OPTION) {
                SwingUtilities.invokeLater(() -> {
                    importFile(chooser);
                });
                IGCAPTproperties.getInstance().setPropertyKeyValue(IgcaptProperty.LAST_PATH, chooser.getSelectedFile().toString());
            }
        });
    }
}
