package gov.inl.igcapt.view;

import gov.inl.igcapt.controllers.ImportMenuItemController;

import java.io.File;
import javax.swing.*;

public class AddImportMenuItem extends JMenuItem {
    
    public AddImportMenuItem(java.awt.Frame parent) {
        super("Import...");
        createImportMenuItem(parent);
        m_controller = new ImportMenuItemController();
    }

    private ImportMenuItemController m_controller;

    private void importFile(JFileChooser chooser) {
        String selectedOpenFile = chooser.getSelectedFile().toString();
        m_controller.importGdtafScenarioFile(selectedOpenFile);
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
}
