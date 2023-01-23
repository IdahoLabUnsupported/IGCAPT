package org.openstreetmap.gui.jmapviewer;

import gov.inl.igcapt.components.PayloadEditorForm;
import java.awt.event.ActionEvent;
import javax.swing.*;

public class AddApplyPayloadMenuItem extends JMenuItem {

    AddApplyPayloadMenuItem(IGCAPTgui igcaptGui) {
        super("Apply Payload...");
        createApplyPayloadMenu(igcaptGui);
    }

    private void createApplyPayloadMenu(IGCAPTgui igcaptGui) {
        
        this.addActionListener((ActionEvent ev) -> {
            PayloadEditorForm payloadEditorForm = igcaptGui.getPayloadEditorForm();
            if (payloadEditorForm == null) {
                igcaptGui.setPayloadEditorForm(payloadEditorForm = new PayloadEditorForm(igcaptGui.getPayload()));
                payloadEditorForm.setLocationRelativeTo(IGCAPTgui.getInstance());
                payloadEditorForm.setVisible(true);
                
                // Closed the Payload Editor dialog with Ok.
                if (payloadEditorForm.getReturnValue() == PayloadEditorForm.ReturnValue.Ok) {
                    igcaptGui.setPayload(payloadEditorForm.getPayload());
                    
                    // Apply the payload
                    igcaptGui.applyPayload();
                }
                
                igcaptGui.setPayloadEditorForm(payloadEditorForm = null);
            }
            else {
                payloadEditorForm.setLocationRelativeTo(IGCAPTgui.getInstance());
                payloadEditorForm.setVisible(true);
                payloadEditorForm.toFront();
                
                // Closed the Payload Editor dialog with Ok.
                if (payloadEditorForm.getReturnValue() == PayloadEditorForm.ReturnValue.Ok) {
                    igcaptGui.setPayload(payloadEditorForm.getPayload());
                    
                    // Apply the payload
                    igcaptGui.applyPayload();
                }
            }
        });            
    }
}
