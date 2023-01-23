package org.openstreetmap.gui.jmapviewer;

import javax.swing.*;
import gov.inl.igcapt.components.ComponentEditor;

public class AddComponentMenuItem extends JMenuItem {
    private ComponentEditor componentEditor = null;

    AddComponentMenuItem(java.awt.Frame parent) {
        super("Component Editor...");
        createComponentsEditorMenu(parent);
    }

    private void createComponentsEditorMenu(java.awt.Frame parent) {
        this.addActionListener(ActionListener -> {
            while (true) {
                if (componentEditor == null) {
                    componentEditor = new ComponentEditor(parent, true);
                }

                
                // This will center it within the screen.
                componentEditor.setLocationRelativeTo(IGCAPTgui.getInstance());
                componentEditor.setVisible(true);

                if(componentEditor.clickedCancel()) {
                    componentEditor = null;
                    break;
                }

                componentEditor.clearErrorText();

                if(componentEditor.save()){
                    componentEditor = null;
                    break;
                } else {
                    componentEditor.setErrorText("Error while Saving");
                }
            }
        });
    }
}
