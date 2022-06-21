package org.openstreetmap.gui.jmapviewer;

import javax.swing.*;

public class AddApplyPayloadMenuItem extends JMenuItem {

    AddApplyPayloadMenuItem(IGCAPTgui igcaptGui) {
        super("Apply Payload");
        createApplyPayloadMenu(igcaptGui);
    }

    private void createApplyPayloadMenu(IGCAPTgui igcaptGui) {
        
        this.addActionListener(ActionListener -> {
            igcaptGui.applyPayload();
        });            
    }
}
