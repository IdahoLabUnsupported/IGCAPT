package gov.inl.igcapt.view;

import gov.inl.igcapt.components.CostAnalysisTask;
import gov.inl.igcapt.controllers.GDTAFImportController;
import gov.inl.igcapt.graph.GraphManager;
import gov.inl.igcapt.properties.IGCAPTproperties;
import gov.inl.igcapt.properties.IGCAPTproperties.IgcaptProperty;

import javax.swing.*;
import java.io.File;

public class AddCostAnalysisMenuItem extends JMenuItem {

    public AddCostAnalysisMenuItem() {
        super("Cost Analysis...");
        createCostAnalysisMenuItem();
        m_controller = GDTAFImportController.getInstance();
    }

    private GDTAFImportController m_controller;

    private void importFile(JFileChooser chooser) {
        String selectedOpenFile = chooser.getSelectedFile().toString();
    }

    private void createCostAnalysisMenuItem() {

        this.addActionListener(ActionListener -> {

            //System.out.println("COST ANALYSIS CLICKED");
            CostAnalysisTask cat = new CostAnalysisTask(GraphManager.getInstance().getOriginalGraph());
            cat.analyze(GraphManager.getInstance().getOriginalGraph());

        });
    }
}
