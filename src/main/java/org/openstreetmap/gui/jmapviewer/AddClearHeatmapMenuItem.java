package org.openstreetmap.gui.jmapviewer;

import javax.swing.*;

public class AddClearHeatmapMenuItem extends JMenuItem {

    public AddClearHeatmapMenuItem(IGCAPTgui igcaptGui) {
        super("Clear Heatmap");
        createClearHeatmapMenu(igcaptGui);
    }

    private void createClearHeatmapMenu(IGCAPTgui igcaptGui) {
        
        this.addActionListener(ActionListener -> {
            
            igcaptGui.SetHeatmap(null);
            igcaptGui.updateGISObjects();
        });           
    }
}
