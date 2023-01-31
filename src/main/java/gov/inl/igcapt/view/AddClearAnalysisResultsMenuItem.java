package gov.inl.igcapt.view;

import gov.inl.igcapt.graph.GraphManager;
import org.openstreetmap.gui.jmapviewer.IGCAPTgui;

import javax.swing.*;

public class AddClearAnalysisResultsMenuItem extends JMenuItem {

    public AddClearAnalysisResultsMenuItem() {
        super("Clear Analysis Results");
        createClearAnalysisMenu();
    }

    private void createClearAnalysisMenu() {
        
        this.addActionListener(ActionListener -> {
            GraphManager.getInstance().clearEdgeUtilization();
        });           
    }
}
