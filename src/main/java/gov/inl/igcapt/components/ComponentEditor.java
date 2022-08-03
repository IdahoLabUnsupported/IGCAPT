/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.igcapt.components;

import gov.inl.igcapt.components.DataModels.*;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.DefaultListModel;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import org.openstreetmap.gui.jmapviewer.IGCAPTgui;

/**
 *
 * @author wadsjc
 */
public class ComponentEditor extends javax.swing.JDialog {

    private CachedComponentDao dao;
    
    private DefaultListModel<SgComponentData> components;
    private DefaultListModel<SgComponentGroupData> componentGroups;
    private DefaultListModel<SgField> fields;
    private DefaultListModel<SgUseCase> useCases;
    private DefaultListModel<SgField> usecaseFields;

    private ComponentGroupDialog addComponentGroupDialog;
    private ComponentDialog addComponentDialog;
    private FieldDialog addFieldDialog;
    private UseCaseDialog addUseCaseDialog;
    private SelectUseCaseDialog selectUseCaseDialog;
    private SelectFieldDialog selectFieldDialog;
    private boolean clickedCancel = false;

    private List<Object> removed;
    private java.awt.Frame parent;
    private boolean modal;

    /**
     * A return status code - returned if Cancel button has been pressed
     */
    public static final int RET_CANCEL = 0;
    /**
     * A return status code - returned if OK button has been pressed
     */
    public static final int RET_OK = 1;

    /**
     * Creates new form ComponentEditor
     */
    public ComponentEditor(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.parent = parent;
        this.modal = modal;

        dao = new CachedComponentDao();
        removed = new ArrayList<Object>();

        initializeButtons();
        initializeLists();

        // Close the dialog when Esc is pressed
        String cancelName = "cancel";
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), cancelName);
        ActionMap actionMap = getRootPane().getActionMap();
        actionMap.put(cancelName, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                doClose(RET_CANCEL);
            }
        });
    }

    private void initializeLists() {
        initializeComponentGroups();
        initializeComponents();
        initializeFields();
        initializeUseCases();
        initializeUseCaseFields();
    }

    private void initializeComponentGroups() {
        componentGroups = new DefaultListModel<>();

        try {
            for (SgComponentGroupData group : dao.getComponentGroups()) {
                componentGroups.addElement(group);
            }
        } catch (Exception ignored) {
        }

        componentGroupSelector.setModel(componentGroups);
        componentGroupSelector.setSelectedIndex(0);
        componentGroupSelector.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    }

    private void initializeComponents() {
        components = new DefaultListModel<>();
        try {
            for (SgComponentData component : componentGroups.get(0).getComponents()) {
                components.addElement(component);
            }
        } catch (Exception ignored) {
        }

        componentSelector.setModel(components);
        componentSelector.setSelectedIndex(0);
        componentSelector.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void initializeFields() {
        fields = new DefaultListModel<>();

        try {
            for (SgField field : components.get(0).getFields()) {
                fields.addElement(field);
            }
        } catch (Exception ignored) {
        }

        fieldSelector.setModel(fields);
        fieldSelector.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    }

    private void initializeUseCases() {
        useCases = new DefaultListModel<>();
        try {
            for (SgUseCase usecase : components.get(0).getUsecases()) {
                useCases.addElement(usecase);
            }
        } catch (Exception ignored) {
        }

        useCaseSelector.setModel(useCases);
        useCaseSelector.setSelectedIndex(0);
        useCaseSelector.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    }

    private void initializeUseCaseFields() {
        usecaseFields = new DefaultListModel<>();

        try {
            for (SgField field : useCases.get(0).getFields()) {
                usecaseFields.addElement(field);
            }
        } catch (Exception ignored) {
        }

        useCaseFieldSelector.setModel(usecaseFields);
        useCaseFieldSelector.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void initializeButtons() {
    }

    private void removeSelectedUseCase() {
        SgComponentData component = componentSelector.getSelectedValue();
        SgUseCase useCase = useCaseSelector.getSelectedValue();

        component.removeUsecase(useCase);
        useCase.removeComponent(component);

        this.useCases.removeElement(useCase);
    }

    private void removeSelectedUseCaseField() {
        SgUseCase useCase = useCaseSelector.getSelectedValue();
        SgField field = useCaseFieldSelector.getSelectedValue();

        useCase.removeField(field);
        this.usecaseFields.removeElement(field);
    }
    
    private void remove(DefaultListModel lm, int index) {
        removed.add(lm.get(index));
        lm.removeElementAt(index);
    }

    private void showAddComponentGroupDialog() {
        showAddComponentGroupDialog(null);
    }

    private void showAddComponentDialog() {
        showAddComponentDialog(null);
    }

    private void showAddFieldDialog() {
        showAddFieldDialog(null);
    }

    private void showAddUseCaseDialog() {
        showAddUseCaseDialog(null);
    }

    private void showAddComponentGroupDialog(SgComponentGroupData existingGroup) {
        if (addComponentGroupDialog == null) {
            addComponentGroupDialog = new ComponentGroupDialog(null, modal);
        }

        if (existingGroup != null) {
            addComponentGroupDialog.setGroup(existingGroup);
        }

        while (true) {
            if (addComponentGroupDialog == null) {
                break;
            }

            addComponentGroupDialog.setLocationRelativeTo(null);
            addComponentGroupDialog.setVisible(true);
            addComponentGroupDialog.toFront();

            if (addComponentGroupDialog.clickedCancel()) {
                addComponentGroupDialog = null;
                break;
            }

            try {
                SgComponentGroupData group = addComponentGroupDialog.getGroup();
                componentGroups.addElement(group);
                dao.stageComponentGroup(group); // Have to stage it so that its available to components
                if (existingGroup != null) {
                    componentGroups.removeElementAt(componentGroups.size()-1);
                }

                addComponentGroupDialog.dispose();
                addComponentGroupDialog = null;
            } catch (Exception ignored) {
            }
        }
    }

    private void showAddComponentDialog(SgComponentData existingComponent) {
        if (addComponentDialog == null) {
            addComponentDialog = new ComponentDialog(parent, modal, dao);
        }

        if (existingComponent != null) {
            addComponentDialog.setComponent(existingComponent);
        }
        addComponentDialog.setComponentGroup(componentGroupSelector.getSelectedValue());

        while (true) {
            if (addComponentDialog == null) {
                break;
            }

            addComponentDialog.setVisible(true);
            addComponentDialog.toFront();

            if (addComponentDialog.clickedCancel()) {
                addComponentDialog = null;
                break;
            }

            try {
                SgComponentData component = addComponentDialog.getComponent();
                ((SgComponentGroupData) componentGroupSelector.getSelectedValue()).addComponent(component);
                components.addElement(component);
                if (existingComponent != null) {
                    components.removeElementAt(components.size() - 1);
                }

                addComponentDialog.dispose();
                addComponentDialog = null;
            } catch (Exception ignored) {
            }
        }
    }

    private void showAddFieldDialog(SgField existingField) {
        if (addFieldDialog == null) {
            addFieldDialog = new FieldDialog(parent, modal, dao);
            addFieldDialog.setComponent((SgComponentData) componentSelector.getSelectedValue());
        }

        if (existingField != null) {
            addFieldDialog.setField(existingField);
        }

        while (true) {
            if (addFieldDialog == null) {
                break;
            }

            addFieldDialog.setLocationRelativeTo(null);
            addFieldDialog.setVisible(true);
            addFieldDialog.toFront();

            if (addFieldDialog.clickedCancel()) {
                addFieldDialog = null;
                break;
            }

            try {
                SgField field = addFieldDialog.getField();
                ((SgComponentData) componentSelector.getSelectedValue()).addField(field);
                fields.addElement(field);
                if (existingField != null) {
                    fields.removeElementAt(fields.size() - 1);
                }

                addFieldDialog.dispose();
                addFieldDialog = null;
            } catch (Exception ignored) {
            }
        }
    }

    private void showAddUseCaseDialog(SgUseCase existingUseCase) {
        if (addUseCaseDialog == null) {
            addUseCaseDialog = new UseCaseDialog(parent, modal);
        }
        if (existingUseCase != null) {
            addUseCaseDialog.setUseCase(existingUseCase);
        }

        while (true) {
            if (addUseCaseDialog == null) {
                break;
            }

            addUseCaseDialog.setLocationRelativeTo(null);
            addUseCaseDialog.setVisible(true);
            addUseCaseDialog.toFront();

            if (addUseCaseDialog.clickedCancel()) {
                addUseCaseDialog = null;
                break;
            }

            try {
                SgUseCase useCase = addUseCaseDialog.getUsecase();
                ((SgComponentData) componentSelector.getSelectedValue()).addUsecase(useCase);
                useCases.addElement(useCase);
                dao.stageUseCase(useCase); // Have to stage it so that its available to other components
                if (existingUseCase != null) {
                    useCases.removeElementAt(useCases.size()-1);
                }

                addUseCaseDialog.dispose();
                addUseCaseDialog = null;
            } catch (Exception ignored) {
            }
        }
    }

    private void showSelectUseCaseDialog() {
        if (selectUseCaseDialog == null) {
            selectUseCaseDialog = new SelectUseCaseDialog(parent, modal, dao);
        }

        while (true) {
            if (selectUseCaseDialog == null) {
                break;
            }

            selectUseCaseDialog.setLocationRelativeTo(null);
            selectUseCaseDialog.setVisible(true);
            selectUseCaseDialog.toFront();

            if (selectUseCaseDialog.clickedCancel()) {
                selectUseCaseDialog = null;
                break;
            }

            try {
                SgUseCase[] useCases = selectUseCaseDialog.getUseCases();
                SgComponentData component = (SgComponentData) componentSelector.getSelectedValue();

                for (SgUseCase usecase: useCases) {

                    if (!component.getUsecases().contains(usecase)) {
                        component.addUsecase(usecase);
                        this.useCases.addElement(usecase);
                    }
                }

                selectUseCaseDialog.dispose();
                selectUseCaseDialog = null;
            } catch (Exception ignored) {
                System.out.println(ignored);
            }
        }
    }

    private void showSelectFieldDialog() {
        if (selectFieldDialog == null) {
            selectFieldDialog = new SelectFieldDialog(parent, modal, (SgComponentData) componentSelector.getSelectedValue());
        }

        while (true) {
            if (selectFieldDialog == null) {
                break;
            }

            selectFieldDialog.setLocationRelativeTo(null);
            selectFieldDialog.setVisible(true);
            selectFieldDialog.toFront();

            if (selectFieldDialog.clickedCancel()) {
                selectFieldDialog = null;
                break;
            }

            try {
                SgField[] fields = selectFieldDialog.getFields();
                SgUseCase useCase = (SgUseCase) useCaseSelector.getSelectedValue();

                for (SgField field: fields) {
                    if(!useCase.getFields().contains(field)) {
                        useCase.addField(field);
                        this.usecaseFields.addElement(field);
                    }
                }

                selectFieldDialog.dispose();
                selectFieldDialog = null;
            } catch (Exception ignored) {
                System.out.println(ignored);
            }
        }
    }

    private void groupChanged() {
        SgComponentGroupData selected = (SgComponentGroupData) componentGroupSelector.getSelectedValue();

        if (selected == null) {
            return;
        }

        components = new DefaultListModel<>();
        for (SgComponentData component : selected.getComponents()) {
            components.addElement(component);
        }
        componentSelector.setModel(components);
        componentSelector.setSelectedIndex(0);
    }

    private void componentChanged() {
        SgComponentData selected = (SgComponentData) componentSelector.getSelectedValue();

        if (selected == null) {
            return;
        }

        fields = new DefaultListModel<>();
        for (SgField field : selected.getFields()) {
            fields.addElement(field);
        }
        fieldSelector.setModel(fields);
        fieldSelector.setSelectedIndex(0);

        useCases = new DefaultListModel<>();
        for (SgUseCase useCase : selected.getUsecases()) {
            useCases.addElement(useCase);
        }
        useCaseSelector.setModel(useCases);
        useCaseSelector.setSelectedIndex(0);

        useCaseLbl.setText(selected.getName() + "'s Use Cases");
    }

    private void useCaseChanged() {
        SgUseCase selected = (SgUseCase) useCaseSelector.getSelectedValue();
        SgComponentData selectedComponent = (SgComponentData) componentSelector.getSelectedValue();

        if (selected == null || selectedComponent == null) {
            this.useCaseFieldsLbl.setText("Use Case Fields");
            usecaseFields.clear();
            useCaseFieldSelector.setModel(usecaseFields);
            return;
        }

        usecaseFields = new DefaultListModel<>();
        List<SgField> selectedFields = selected.getFields();
        for (SgField field : selectedComponent.getFields()) {
            if (selectedFields.contains(field)) {
                usecaseFields.addElement(field);
            }
        }
        useCaseFieldSelector.setModel(usecaseFields);
        useCaseFieldSelector.setSelectedIndex(0);

        this.useCaseFieldsLbl.setText(selected.getName() + "'s Fields");
    }

    public void clearErrorText() {
        setErrorText("");
    }

    public void setErrorText(String errorText) {
        this.errorLbl.setText(errorText);
    }

    public boolean clickedCancel() {
        return clickedCancel;
    }
    
    public boolean save() {
        try {
            for (int i = 0; i < componentGroups.size(); ++i) {
                SgComponentGroupData group = componentGroups.get(i);
                dao.saveComponentGroup(group);
            }

            for (Object item : this.removed) {
                dao.delete(item);
            }
        } catch (Exception ignored) {
            System.out.println(ignored);
            return false;
        }
        IGCAPTgui.getInstance().refreshTree();
        return true;
    }

    /**
     * @return the return status of this dialog - one of RET_OK or RET_CANCEL
     */

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        componentGroupSelector = new javax.swing.JList<>();
        addComponentGroup = new javax.swing.JButton();
        removeComponentGroup = new javax.swing.JButton();
        editComponentGroup = new javax.swing.JButton();
        removeComponent = new javax.swing.JButton();
        editComponent = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        componentSelector = new javax.swing.JList<>();
        addComponent = new javax.swing.JButton();
        editField = new javax.swing.JButton();
        addField = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        fieldSelector = new javax.swing.JList<>();
        removeField = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        removeUseCase = new javax.swing.JButton();
        addUseCase = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        useCaseSelector = new javax.swing.JList<>();
        addExistingUseCase = new javax.swing.JButton();
        useCaseLbl = new javax.swing.JLabel();
        editUseCase = new javax.swing.JButton();
        addUseCaseField = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        useCaseFieldSelector = new javax.swing.JList<>();
        removeUseCaseField = new javax.swing.JButton();
        useCaseFieldsLbl = new javax.swing.JLabel();
        errorLbl = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(12, 0), new java.awt.Dimension(12, 0), new java.awt.Dimension(12, 32767));

        setTitle("Component Editor");
        setMaximumSize(new java.awt.Dimension(880, 530));
        setMinimumSize(new java.awt.Dimension(880, 530));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        okButton.setText("OK");
        okButton.setMaximumSize(new java.awt.Dimension(80, 30));
        okButton.setMinimumSize(new java.awt.Dimension(80, 30));
        okButton.setPreferredSize(new java.awt.Dimension(80, 30));
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.setMaximumSize(new java.awt.Dimension(80, 30));
        cancelButton.setMinimumSize(new java.awt.Dimension(80, 30));
        cancelButton.setPreferredSize(new java.awt.Dimension(80, 30));
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("Component Groups");

        componentGroupSelector.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                componentGroupSelectorValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(componentGroupSelector);

        addComponentGroup.setText("Add");
        addComponentGroup.setMaximumSize(new java.awt.Dimension(110, 30));
        addComponentGroup.setMinimumSize(new java.awt.Dimension(110, 30));
        addComponentGroup.setPreferredSize(new java.awt.Dimension(110, 30));
        addComponentGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addComponentGroupActionPerformed(evt);
            }
        });

        removeComponentGroup.setText("Remove");
        removeComponentGroup.setMaximumSize(new java.awt.Dimension(110, 30));
        removeComponentGroup.setMinimumSize(new java.awt.Dimension(110, 30));
        removeComponentGroup.setPreferredSize(new java.awt.Dimension(110, 30));
        removeComponentGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeComponentGroupActionPerformed(evt);
            }
        });

        editComponentGroup.setText("Edit");
        editComponentGroup.setMaximumSize(new java.awt.Dimension(110, 30));
        editComponentGroup.setMinimumSize(new java.awt.Dimension(110, 30));
        editComponentGroup.setPreferredSize(new java.awt.Dimension(110, 30));
        editComponentGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editComponentGroupActionPerformed(evt);
            }
        });

        removeComponent.setText("Remove");
        removeComponent.setMaximumSize(new java.awt.Dimension(110, 30));
        removeComponent.setMinimumSize(new java.awt.Dimension(110, 30));
        removeComponent.setPreferredSize(new java.awt.Dimension(110, 30));
        removeComponent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeComponentActionPerformed(evt);
            }
        });

        editComponent.setText("Edit");
        editComponent.setMaximumSize(new java.awt.Dimension(110, 30));
        editComponent.setMinimumSize(new java.awt.Dimension(110, 30));
        editComponent.setPreferredSize(new java.awt.Dimension(110, 30));
        editComponent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editComponentActionPerformed(evt);
            }
        });

        jLabel2.setText("Components");

        componentSelector.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                componentSelectorValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(componentSelector);

        addComponent.setText("Add");
        addComponent.setMaximumSize(new java.awt.Dimension(110, 30));
        addComponent.setMinimumSize(new java.awt.Dimension(110, 30));
        addComponent.setPreferredSize(new java.awt.Dimension(110, 30));
        addComponent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addComponentActionPerformed(evt);
            }
        });

        editField.setText("Edit");
        editField.setMaximumSize(new java.awt.Dimension(110, 30));
        editField.setMinimumSize(new java.awt.Dimension(110, 30));
        editField.setPreferredSize(new java.awt.Dimension(110, 30));
        editField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editFieldActionPerformed(evt);
            }
        });

        addField.setText("Add");
        addField.setMaximumSize(new java.awt.Dimension(110, 30));
        addField.setMinimumSize(new java.awt.Dimension(110, 30));
        addField.setPreferredSize(new java.awt.Dimension(110, 30));
        addField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addFieldActionPerformed(evt);
            }
        });

        jScrollPane3.setViewportView(fieldSelector);

        removeField.setText("Remove");
        removeField.setMaximumSize(new java.awt.Dimension(110, 30));
        removeField.setMinimumSize(new java.awt.Dimension(110, 30));
        removeField.setPreferredSize(new java.awt.Dimension(110, 30));
        removeField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeFieldActionPerformed(evt);
            }
        });

        jLabel3.setText("Fields");

        removeUseCase.setText("Remove");
        removeUseCase.setMaximumSize(new java.awt.Dimension(110, 30));
        removeUseCase.setMinimumSize(new java.awt.Dimension(110, 30));
        removeUseCase.setPreferredSize(new java.awt.Dimension(110, 30));
        removeUseCase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeUseCaseActionPerformed(evt);
            }
        });

        addUseCase.setText("Add");
        addUseCase.setMaximumSize(new java.awt.Dimension(110, 30));
        addUseCase.setMinimumSize(new java.awt.Dimension(110, 30));
        addUseCase.setPreferredSize(new java.awt.Dimension(110, 30));
        addUseCase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addUseCaseActionPerformed(evt);
            }
        });

        useCaseSelector.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                useCaseSelectorValueChanged(evt);
            }
        });
        jScrollPane4.setViewportView(useCaseSelector);

        addExistingUseCase.setText("Add Existing");
        addExistingUseCase.setMaximumSize(new java.awt.Dimension(110, 30));
        addExistingUseCase.setMinimumSize(new java.awt.Dimension(110, 30));
        addExistingUseCase.setPreferredSize(new java.awt.Dimension(110, 30));
        addExistingUseCase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addExistingUseCaseActionPerformed(evt);
            }
        });

        useCaseLbl.setText("Use Cases");

        editUseCase.setText("Edit");
        editUseCase.setMaximumSize(new java.awt.Dimension(110, 30));
        editUseCase.setMinimumSize(new java.awt.Dimension(110, 30));
        editUseCase.setPreferredSize(new java.awt.Dimension(110, 30));
        editUseCase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editUseCaseActionPerformed(evt);
            }
        });

        addUseCaseField.setText("Add");
        addUseCaseField.setMaximumSize(new java.awt.Dimension(110, 30));
        addUseCaseField.setMinimumSize(new java.awt.Dimension(110, 30));
        addUseCaseField.setPreferredSize(new java.awt.Dimension(110, 30));
        addUseCaseField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addUseCaseFieldActionPerformed(evt);
            }
        });

        jScrollPane5.setViewportView(useCaseFieldSelector);

        removeUseCaseField.setText("Remove");
        removeUseCaseField.setMaximumSize(new java.awt.Dimension(110, 30));
        removeUseCaseField.setMinimumSize(new java.awt.Dimension(110, 30));
        removeUseCaseField.setPreferredSize(new java.awt.Dimension(110, 30));
        removeUseCaseField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeUseCaseFieldActionPerformed(evt);
            }
        });

        useCaseFieldsLbl.setText("Use Case Fields");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(editComponentGroup, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(addComponentGroup, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(removeComponentGroup, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(2, 2, 2)
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(addComponent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(removeComponent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(editComponent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addGap(227, 227, 227)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(addField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(removeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(editField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(addExistingUseCase, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(removeUseCase, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(addUseCase, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(editUseCase, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(useCaseLbl))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(addUseCaseField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(removeUseCaseField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(useCaseFieldsLbl))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(errorLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, okButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addComponent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeComponent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editComponent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addComponentGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeComponentGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editComponentGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(useCaseLbl)
                            .addComponent(useCaseFieldsLbl))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(addUseCase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(addExistingUseCase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(removeUseCase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(editUseCase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(addUseCaseField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(removeUseCaseField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(22, 171, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane4))
                                .addContainerGap())))
                    .addComponent(errorLbl, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        getRootPane().setDefaultButton(okButton);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        clickedCancel = false;
        doClose(RET_OK);
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        clickedCancel = true;
        doClose(RET_CANCEL);
    }//GEN-LAST:event_cancelButtonActionPerformed

    /**
     * Closes the dialog
     */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        cancelButtonActionPerformed(null);
    }//GEN-LAST:event_closeDialog

    private void componentGroupSelectorValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_componentGroupSelectorValueChanged
        SgComponentGroupData selected = (SgComponentGroupData) componentGroupSelector.getSelectedValue();

        if (selected == null) {
            components = new DefaultListModel<>();
            componentSelector.setModel(components);

            return;
        }

        components = new DefaultListModel<>();
        for (SgComponentData component: selected.getComponents()) {
            components.addElement(component);
        }
        componentSelector.setModel(components);
        componentSelector.setSelectedIndex(0);
    }//GEN-LAST:event_componentGroupSelectorValueChanged

    private void componentSelectorValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_componentSelectorValueChanged
        SgComponentData selected = (SgComponentData) componentSelector.getSelectedValue();

        if (selected == null) {
            fields = new DefaultListModel<>();
            useCases = new DefaultListModel<>();
            
            fieldSelector.setModel(fields);
            useCaseSelector.setModel(useCases);
            return;
        }

        fields = new DefaultListModel<>();
        for (SgField field : selected.getFields()) {
            fields.addElement(field);
        }
        fieldSelector.setModel(fields);
        fieldSelector.setSelectedIndex(0);

        useCases = new DefaultListModel<>();
        for (SgUseCase useCase : selected.getUsecases()) {
            useCases.addElement(useCase);
        }
        useCaseSelector.setModel(useCases);
        useCaseSelector.setSelectedIndex(0);

        useCaseLbl.setText(selected.getName() + "'s Use Cases");
    }//GEN-LAST:event_componentSelectorValueChanged

    private void useCaseSelectorValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_useCaseSelectorValueChanged
        SgUseCase selected = (SgUseCase) useCaseSelector.getSelectedValue();
        SgComponentData selectedComponent = (SgComponentData) componentSelector.getSelectedValue();

        if (selected == null || selectedComponent == null) {
            this.useCaseFieldsLbl.setText("Use Case Fields");
            usecaseFields.clear();
            useCaseFieldSelector.setModel(usecaseFields);
            return;
        }

        usecaseFields = new DefaultListModel<>();
        List<SgField> selectedFields = selected.getFields();
        for (SgField field : selectedComponent.getFields()) {
            if (selectedFields.contains(field)) {
                usecaseFields.addElement(field);
            }
        }
        useCaseFieldSelector.setModel(usecaseFields);
        useCaseFieldSelector.setSelectedIndex(0);

        this.useCaseFieldsLbl.setText(selected.getName() + "'s Fields");
    }//GEN-LAST:event_useCaseSelectorValueChanged

    private void addComponentGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addComponentGroupActionPerformed
        showAddComponentGroupDialog();
    }//GEN-LAST:event_addComponentGroupActionPerformed

    private void addComponentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addComponentActionPerformed
        showAddComponentDialog();
    }//GEN-LAST:event_addComponentActionPerformed

    private void addFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addFieldActionPerformed
        showAddFieldDialog();
    }//GEN-LAST:event_addFieldActionPerformed

    private void addUseCaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addUseCaseActionPerformed
        showAddUseCaseDialog();
    }//GEN-LAST:event_addUseCaseActionPerformed

    private void addExistingUseCaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addExistingUseCaseActionPerformed
        showSelectUseCaseDialog();
    }//GEN-LAST:event_addExistingUseCaseActionPerformed

    private void addUseCaseFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addUseCaseFieldActionPerformed
        showSelectFieldDialog();
    }//GEN-LAST:event_addUseCaseFieldActionPerformed

    private void editComponentGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editComponentGroupActionPerformed
        showAddComponentGroupDialog((SgComponentGroupData) componentGroupSelector.getSelectedValue());
    }//GEN-LAST:event_editComponentGroupActionPerformed

    private void editComponentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editComponentActionPerformed
        showAddComponentDialog((SgComponentData) componentSelector.getSelectedValue());
    }//GEN-LAST:event_editComponentActionPerformed

    private void editFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editFieldActionPerformed
        showAddFieldDialog((SgField) fieldSelector.getSelectedValue());
    }//GEN-LAST:event_editFieldActionPerformed

    private void editUseCaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editUseCaseActionPerformed
        showAddUseCaseDialog((SgUseCase) useCaseSelector.getSelectedValue());
    }//GEN-LAST:event_editUseCaseActionPerformed

    private void removeComponentGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeComponentGroupActionPerformed
        remove(this.componentGroups, componentGroupSelector.getSelectedIndex());
    }//GEN-LAST:event_removeComponentGroupActionPerformed

    private void removeComponentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeComponentActionPerformed
        remove(this.components, componentSelector.getSelectedIndex());
    }//GEN-LAST:event_removeComponentActionPerformed

    private void removeFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeFieldActionPerformed
        remove(this.fields, fieldSelector.getSelectedIndex());
    }//GEN-LAST:event_removeFieldActionPerformed

    private void removeUseCaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeUseCaseActionPerformed
        remove(this.useCases, useCaseSelector.getSelectedIndex());
    }//GEN-LAST:event_removeUseCaseActionPerformed

    private void removeUseCaseFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeUseCaseFieldActionPerformed
        remove(this.usecaseFields, useCaseFieldSelector.getSelectedIndex());
    }//GEN-LAST:event_removeUseCaseFieldActionPerformed
    
    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ComponentEditor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ComponentEditor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ComponentEditor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ComponentEditor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ComponentEditor dialog = new ComponentEditor(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addComponent;
    private javax.swing.JButton addComponentGroup;
    private javax.swing.JButton addExistingUseCase;
    private javax.swing.JButton addField;
    private javax.swing.JButton addUseCase;
    private javax.swing.JButton addUseCaseField;
    private javax.swing.JButton cancelButton;
    private javax.swing.JList<SgComponentGroupData> componentGroupSelector;
    private javax.swing.JList<SgComponentData> componentSelector;
    private javax.swing.JButton editComponent;
    private javax.swing.JButton editComponentGroup;
    private javax.swing.JButton editField;
    private javax.swing.JButton editUseCase;
    private javax.swing.JLabel errorLbl;
    private javax.swing.JList<SgField> fieldSelector;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JButton okButton;
    private javax.swing.JButton removeComponent;
    private javax.swing.JButton removeComponentGroup;
    private javax.swing.JButton removeField;
    private javax.swing.JButton removeUseCase;
    private javax.swing.JButton removeUseCaseField;
    private javax.swing.JList<SgField> useCaseFieldSelector;
    private javax.swing.JLabel useCaseFieldsLbl;
    private javax.swing.JLabel useCaseLbl;
    private javax.swing.JList<SgUseCase> useCaseSelector;
    // End of variables declaration//GEN-END:variables

    private int returnStatus = RET_CANCEL;
}
