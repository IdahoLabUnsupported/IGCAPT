package gov.inl.igcapt.view;

import gov.inl.igcapt.controllers.GDTAFImportController;

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
