package gov.inl.igcapt.view;

import gov.inl.igcapt.view.UsecasePanel;

import javax.swing.*;

public class AddUseCaseMenuItem extends JMenuItem {
    private UsecasePanel panel;

    public AddUseCaseMenuItem() {
        super("Add new Use Case");
        panel = new UsecasePanel();
        createUseCaseMenu();
    }

    private void createUseCaseMenu() {
        this.addActionListener(ActionListener -> {
            panel.showConfirmDialog();
        });
    }
}