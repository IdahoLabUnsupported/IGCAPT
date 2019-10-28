package org.openstreetmap.gui.jmapviewer;

import javax.swing.*;

public class AddFieldMenuItem extends JMenuItem {
    private FieldPanel panel;

    AddFieldMenuItem() {
        super("Add new Field");
        panel = new FieldPanel();
        createFieldMenu();
    }

    private void createFieldMenu() {
        this.addActionListener(ActionListener -> {
            panel.showConfirmDialog();
        });
    }
}
