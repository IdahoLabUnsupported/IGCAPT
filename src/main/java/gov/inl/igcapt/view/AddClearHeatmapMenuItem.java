package gov.inl.igcapt.view;


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
