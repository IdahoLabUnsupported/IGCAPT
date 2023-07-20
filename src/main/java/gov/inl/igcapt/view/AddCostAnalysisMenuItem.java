package gov.inl.igcapt.view;

import gov.inl.igcapt.components.CostAnalysis;
import gov.inl.igcapt.graph.GraphManager;

import javax.swing.*;

public class AddCostAnalysisMenuItem extends JMenuItem {

    public AddCostAnalysisMenuItem() {
        super("Cost Analysis...");
        createCostAnalysisMenuItem();
    }

    private void createCostAnalysisMenuItem() {

        this.addActionListener(ActionListener -> {

            CostAnalysis cat = new CostAnalysis(GraphManager.getInstance().getOriginalGraph());
            cat.analyze();

        });
    }
}
