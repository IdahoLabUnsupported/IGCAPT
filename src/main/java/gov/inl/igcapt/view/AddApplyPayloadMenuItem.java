package gov.inl.igcapt.view;

import gov.inl.igcapt.components.PayloadEditorForm;

import java.awt.event.ActionEvent;
import javax.swing.*;

public class AddApplyPayloadMenuItem extends JMenuItem {

    public AddApplyPayloadMenuItem() {
        super("Apply Payload...");
        createApplyPayloadMenu();
    }

    private void createApplyPayloadMenu() {
        
        this.addActionListener((ActionEvent ev) -> {
            PayloadEditorForm payloadEditorForm = IGCAPTgui.getInstance().getPayloadEditorForm();
            if (payloadEditorForm == null) {
                IGCAPTgui.getInstance().setPayloadEditorForm(payloadEditorForm = new PayloadEditorForm(IGCAPTgui.getInstance().getPayload()));
            }

            payloadEditorForm.setLocationRelativeTo(IGCAPTgui.getInstance());
            payloadEditorForm.setVisible(true);
            payloadEditorForm.toFront();

            // Closed the Payload Editor dialog with Ok.
            if (payloadEditorForm.getReturnValue() == PayloadEditorForm.ReturnValue.Ok) {
                IGCAPTgui.getInstance().setPayload(payloadEditorForm.getPayload());

                // Apply the payload
                IGCAPTgui.getInstance().applyPayload();
            }
        });            
    }
}
