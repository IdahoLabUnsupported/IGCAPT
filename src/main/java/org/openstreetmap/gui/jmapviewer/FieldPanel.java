package org.openstreetmap.gui.jmapviewer;

import gov.inl.igcapt.components.DataModels.ComponentDao;
import gov.inl.igcapt.components.DataModels.SgComponentData;
import gov.inl.igcapt.components.DataModels.SgField;

import javax.swing.*;
import java.util.List;

public class FieldPanel extends SgPanel {

    private JTextField name;
    private JComboBox<SgComponentData> componentSelector;
    private ComponentDao componentDao;
    private SgField field;
    private JTextField payload;

    public FieldPanel() {
        this.title = "Add new Field";
    }

    private void initializePanel() {
        field = new SgField();
        this.componentDao = new ComponentDao();
    }

    @Override
    void setupForm() {
        this.initializePanel();

        addNameTextBox();
        addPayloadTextBox();
        try {
            addComponentSelector();
        } catch (Exception e) {
            showErrorMessage();
        }

    }

    private void addNameTextBox() {
        JLabel label = new JLabel("Name");
        this.add(label);

        name = new JTextField();
        name.setBounds(128, 65, 86, 20);

        label.setLabelFor(name);

        this.add(name);
        name.setColumns(10);
    }

    private void addPayloadTextBox() {
        JLabel label = new JLabel("Payload");
        this.add(label);

        payload = new JTextField();
        payload.setBounds(128, 65, 86, 20);

        label.setLabelFor(payload);

        this.add(payload);
        payload.setColumns(10);
    }

    private void addComponentSelector() throws Exception {
        List<SgComponentData> components = getAllComponents();
        if (components.isEmpty()) {
            throw new Exception();
        }

        JLabel label = new JLabel("Parent Component");
        this.add(label);

        componentSelector = new JComboBox<>();
        componentSelector.setModel(new DefaultComboBoxModel(components.toArray()));

        label.setLabelFor(componentSelector);

        this.add(componentSelector);
    }

    @Override
    boolean save() {
        clearValidationText();

        String enteredName = name.getText().trim();
        if (enteredName.isEmpty()) {
            addValidationText("Name field is invalid");
        } else {
            this.field.setName(name.getText());
        }

        try {
            String enteredPayload = payload.getText().trim();
            int intPayload = Integer.parseInt(enteredPayload);

            this.field.setPayload(intPayload);
        } catch (Exception ignored) {
            addValidationText("Payload field is invalid");
        }

        SgComponentData selected = null;
        try {
            selected = (SgComponentData) componentSelector.getSelectedItem();
            selected.addField(this.field);
        } catch (Exception ignored) {
            addValidationText("Selected Component is Invalid");
        }


        if (formIsValid()) {
            this.componentDao.saveComponent(selected);
            this.componentDao.saveField(this.field);
            return true;
        }

        return false;
    }

    @Override
    void reset() {

    }

    List<SgComponentData> getAllComponents() {
        return componentDao.getComponents();
    }
}
