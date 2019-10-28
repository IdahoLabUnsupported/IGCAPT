package org.openstreetmap.gui.jmapviewer;

import javax.swing.*;

public class AddUseCaseMenuItem extends JMenuItem {
    private UsecasePanel panel;

    AddUseCaseMenuItem() {
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
