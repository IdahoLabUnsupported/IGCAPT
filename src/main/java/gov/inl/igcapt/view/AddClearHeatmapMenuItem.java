package gov.inl.igcapt.view;

import org.openstreetmap.gui.jmapviewer.IGCAPTgui;

import javax.swing.*;

public class AddClearHeatmapMenuItem extends JMenuItem {

    public AddClearHeatmapMenuItem() {
        super("Clear Heatmap");
        createClearHeatmapMenu();
    }

    private void createClearHeatmapMenu() {
        
        this.addActionListener(ActionListener -> {
            
            IGCAPTgui.getInstance().SetHeatmap(null);
        });           
    }
}
