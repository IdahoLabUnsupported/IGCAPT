package org.openstreetmap.gui.jmapviewer;

import javax.swing.*;
import java.io.File;

public class AddImportNs3ResultsMenuItem extends JMenuItem {

    AddImportNs3ResultsMenuItem(IGCAPTgui igcaptGui) {
        super("Component Editor...");
        createImportNs3ResultsMenu(igcaptGui);
    }

    private void createImportNs3ResultsMenu(IGCAPTgui igcaptGui) {
        
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
