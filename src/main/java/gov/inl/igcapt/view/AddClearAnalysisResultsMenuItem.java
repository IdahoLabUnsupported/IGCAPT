package gov.inl.igcapt.view;

import org.openstreetmap.gui.jmapviewer.IGCAPTgui;

import javax.swing.*;

public class AddClearAnalysisResultsMenuItem extends JMenuItem {

    public AddClearAnalysisResultsMenuItem() {
        super("Clear Analysis Results");
        createClearAnalysisMenu();
    }

    private void createClearAnalysisMenu() {
        
        this.addActionListener(ActionListener -> {
            IGCAPTgui.getInstance().clearEdgeUtilization();
        });           
    }
}
