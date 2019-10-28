package org.openstreetmap.gui.jmapviewer;


import gov.inl.igcapt.components.DataModels.ComponentDao;
import gov.inl.igcapt.components.DataModels.SgComponentData;
import gov.inl.igcapt.components.DataModels.SgField;
import gov.inl.igcapt.components.DataModels.SgUseCase;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class UsecasePanel extends SgPanel {
    private JTextField name;
    private JTextField latency;
    private SgComponentData component;
    private ComponentDao componentDao;
    private SgUseCase useCase;
    private JList<SgField> fields;
    private JComboBox<SgComponentData> components;

    private JList<SgComponentData> componentMultiSelect;


    public UsecasePanel() {
        title = "Add new Use Case";
    }

    @Override
    boolean save() {

        if(name.getText().isEmpty()) {
            addValidationText("Empty name field");
        }

        useCase = new SgUseCase();
        useCase.setName(name.getText());

        try {
            useCase.setLatency(Integer.parseInt(latency.getText()));
        } catch (Exception ignored) {
            addValidationText("Invalid entry for Latency");
        }

        try {
            useCase.setFields(fields.getSelectedValuesList());
        } catch (Exception ignored) {
            addValidationText("Fields entered are Invalid");
        }

        try {
            component = getSelectedComponent();
        } catch (Exception ignored) {
            addValidationText("Invalid component selected");
        }

        if (formIsValid()) {
            try {
                for (SgField field: useCase.getFields()) {
                    component.addField(field);
                    this.componentDao.saveField(field);
                }
                component.addUsecase(useCase);
                useCase.addComponent(component);

                componentDao.saveComponent(component);
                componentDao.saveUseCase(useCase);
                return true;
            } catch (Exception ignored) {
                addValidationText("Error While Saving");
            }
        }

        return false;
    }

    private SgComponentData getSelectedComponent() {
        return (SgComponentData) components.getSelectedItem();
    }

    @Override
    void reset() {
        this.name.setText("");
        this.latency.setText("");
        try {
            resetFields();
            resetComponents();
        } catch (Exception ignored) {
            System.out.println("Failed to reset");
        }
    }

    private void resetFields() throws Exception {
        List<SgField> fields = getSelectedComponent().getFields();

        if (fields.isEmpty()) {
            this.fields = new JList<>(new SgField[0]);
            throw new Exception();
        }

        if (this.fields != null){
            this.fields.clearSelection();
        }

        this.fields = new JList<>(fields.toArray(new SgField[0]));
    }

    private void resetComponents() throws Exception {
        List<SgComponentData> components = getAllComponents();
        if (components.isEmpty()) {
            throw new Exception();
        }

        this.components = new JComboBox<>();
        this.components.setModel(new DefaultComboBoxModel(components.toArray()));
    }

    private void initialize() {
        this.componentDao = new ComponentDao();
    }

    @Override
    void setupForm() {
        initialize();

        try {
            addNameTextBox();
            addLatencyTextBox();
            addComponentSelector();
            addFieldMultiSelector();
        } catch (Exception ex) {
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

    private void addLatencyTextBox() {
        JLabel label = new JLabel("Latency");
        this.add(label);

        latency = new JTextField();
        latency.setBounds(128, 65, 86, 20);

        label.setLabelFor(latency);

        this.add(latency);
        latency.setColumns(10);
    }

    private void addComponentSelector() throws Exception {
        resetComponents();

        JLabel label = new JLabel("Components");
        this.add(label);

        label.setLabelFor(components);

        this.add(components);

        components.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    resetFields();
                    System.out.println("Reset fields after component selection");
                } catch (Exception ignored) {
                    System.out.println("Error resetting fields after component selection");
                    fields = new JList<>(new SgField[0]);
                } // ToDo: This is not updating
            }
        });
    }

    private void addFieldMultiSelector() throws Exception {
        resetFields();
        fields.setVisibleRowCount(5);
        fields.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JLabel label = new JLabel("Components");
        this.add(label);

        label.setLabelFor(fields);

        this.add(fields);
    }

    private void addComponentMultiSelector() throws Exception {
        resetComponents();
        componentMultiSelect.setVisibleRowCount(5);
        componentMultiSelect.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JLabel label = new JLabel("Components");
        this.add(label);

        label.setLabelFor(componentMultiSelect);

        this.add(componentMultiSelect);
    }
}
