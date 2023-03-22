package gov.inl.igcapt.view;

import gov.inl.igcapt.graph.GraphManager;

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
