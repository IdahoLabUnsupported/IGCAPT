package org.openstreetmap.gui.jmapviewer;

import javax.swing.*;

public class AddClearAnalysisResultsMenuItem extends JMenuItem {

    AddClearAnalysisResultsMenuItem(IGCAPTgui igcaptGui) {
        super("Clear Analysis Results");
        createClearAnalysisMenu(igcaptGui);
    }

    private void createClearAnalysisMenu(IGCAPTgui igcaptGui) {
        
        this.addActionListener(ActionListener -> {
            igcaptGui.clearEdgeUtilization();
        });           
    }
}
