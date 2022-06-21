package org.openstreetmap.gui.jmapviewer;

import javax.swing.*;

public class AddClearAnalysisResultsMenuItem extends JMenuItem {

    AddClearAnalysisResultsMenuItem(java.awt.Frame parent) {
        super("Component Editor...");
        createClearAnalysisMenu(parent);
    }

    private void createClearAnalysisMenu(java.awt.Frame parent) {
        
        if (parent instanceof IGCAPTgui igcaptGui) {
            this.addActionListener(ActionListener -> {
                igcaptGui.clearEdgeUtilization();
            });           
        }
    }
}
