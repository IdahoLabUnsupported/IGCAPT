package gov.inl.igcapt.view;

import gov.inl.igcapt.components.HeatMapDialog;

import javax.swing.*;

public class AddShowHeatmapMenuItem extends JMenuItem {

    public AddShowHeatmapMenuItem() {
        super("Show Heatmap");
        createShowHeatmapMenu();
    }

    private void createShowHeatmapMenu() {
        
        this.addActionListener(ActionListener -> {
            
            // Determine min/max extent
            // Set grid_size
            // Set kernel radius - the spread of the kernel, outside of this the kernel will be not have support
            // Save cells in two-dimensional array
            // Find the center of each cell and save in a two dimensional array. A cell is the extent divided into
            // increments in x and y according to the grid size. Cells are of dimension grid_size X grid_size.
            //
            HeatMapDialog dialog = new HeatMapDialog(IGCAPTgui.getInstance(), true);
            dialog.setVisible(true);
        });           
    }
}
