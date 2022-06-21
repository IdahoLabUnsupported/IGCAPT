package org.openstreetmap.gui.jmapviewer;

import javax.swing.*;
import gov.inl.igcapt.components.ComponentEditor;
import java.io.File;

public class AddImportNs3ResultsMenuItem extends JMenuItem {

    AddImportNs3ResultsMenuItem(java.awt.Frame parent) {
        super("Component Editor...");
        createImportNs3ResultsMenu(parent);
    }

    private void createImportNs3ResultsMenu(java.awt.Frame parent) {
        
        if (parent instanceof IGCAPTgui igcaptGui){
            this.addActionListener(ActionListener -> {
                JFileChooser chooser = new JFileChooser();

                if (!igcaptGui.getLastPath().isEmpty()) {
                    chooser.setCurrentDirectory(new File(igcaptGui.getLastPath()));
                }

                if (chooser.showOpenDialog(IGCAPTgui.getInstance()) == JFileChooser.APPROVE_OPTION) {
                    SwingUtilities.invokeLater(() -> {
                        igcaptGui.importResults(chooser);
                    });
                }
            });            
        }
    }
}
