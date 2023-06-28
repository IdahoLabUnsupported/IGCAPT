/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.igcapt.components;

import gov.inl.igcapt.components.DataModels.*;
import gov.inl.igcapt.properties.IGCAPTproperties;
import gov.inl.igcapt.properties.IGCAPTproperties.IgcaptProperty;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.UUID;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

/**
 *
 * @author wadsjc
 */
public class ComponentDialog extends javax.swing.JDialog {

    private final IComponentDao componentDao;
    private DefaultListModel<SgField> fieldListModel;
    private DefaultListModel<SgAttribute> attributeListModel;
    private DefaultListModel<SgComponentData> collapseIntos;
    private DefaultComboBoxModel<SgComponentGroupData> componentGroups;
    private SgComponentData existingComponent;
    private boolean clickedCancel = false;

    private final Color ERROR_COLOR = Color.red;
    private final Color NORMAL_COLOR = Color.white;
    private String DEFAULT_ICON = "sgicons/Unknown.png";
    private String DEFAULT_PATH;
    IGCAPTproperties properties;
    
    /**
     * A return status code - returned if Cancel button has been pressed
     */
    public static final int RET_CANCEL = 0;
    /**
     * A return status code - returned if OK button has been pressed
     */
    public static final int RET_OK = 1;

    /**
     * Creates new form ComponentDialog
     */
    public ComponentDialog(java.awt.Frame parent, boolean modal, IComponentDao componentDao) {
        super(parent, modal);
        initComponents();

        properties = IGCAPTproperties.getInstance();
        try {
            DEFAULT_ICON = properties.getPropertyKeyValue(IgcaptProperty.UNKNOWN_NODE_ICON);
            DEFAULT_PATH = new File(properties.getPropertyKeyValue(IgcaptProperty.LAST_ICON_PATH)).getParentFile().getName();
        } catch (Exception ignored) {
            if (DEFAULT_ICON == null) {
                DEFAULT_ICON = "sgicons/Unknown.png";
            }
            if (DEFAULT_PATH == null) {
                DEFAULT_PATH = "sgicons/";
            }
        }

        // Center within screen.
        setLocationRelativeTo(null);

        this.componentDao = componentDao;
        fieldListModel = new DefaultListModel();
        attributeListModel = new DefaultListModel();
        
        initializeCollapseInto();
        initializeIcon();
        initializeGuid();
        initializeComponentGroups();
        initializeFields();
        initializeAttributes();

//        newFieldBtn.addActionListener(e -> AddField());
//        addIconBtn.addActionListener(e -> collectIconPath());
        
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
    
    private void initializeComponentGroups() {
        componentGroups = new DefaultComboBoxModel<>();

        for (SgComponentGroupData group: componentDao.getComponentGroups()) {
            componentGroups.addElement(group);
        }

        ComponentGroupSelector.setModel(componentGroups);
    }
    private void initializeCollapseInto() {
        CollapseIntoFld.setVisibleRowCount(5);
        CollapseIntoFld.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        collapseIntos = new DefaultListModel<>();

        for (SgComponentData component: componentDao.getComponents()) {
            collapseIntos.addElement(component);
        }

        CollapseIntoFld.setModel(collapseIntos);
    }
    private void initializeIcon() {
        iconPathFld.setEnabled(false); //disable();
        File file = new File(DEFAULT_ICON);
        iconPathFld.setText(file.getPath());
    }
    private void initializeGuid() {
        guidFld.setEnabled(false); //disable();
        guidFld.setText(UUID.randomUUID().toString());
    }
    private void initializeFields() {
        fieldListModel = new DefaultListModel<>();
        FieldList.setModel(fieldListModel);
    }
    private void initializeAttributes() {
        attributeListModel = new DefaultListModel<>();
        attributeList.setModel(attributeListModel);
    }

    // this gets called when the add field button is pushed
    // so now call it when the Add field dialog is closed
    // but get the data from the form
    private void ShowAddField() {
        FieldDialog addFieldDialog = new FieldDialog(null, true, componentDao);
        try {
            addFieldDialog.setComponent(getComponent()); //componentSelector.getSelectedValue());
        }
        catch (Exception e) {
            // what to do here????
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
                existingComponent.addField(field);
                AddField(field.getName(), field.getPayload());

                addFieldDialog.dispose();
                addFieldDialog = null;
            } catch (Exception ignored) {
            }
        }
    }
    
    private void AddField(String name, int payload) {    
        boolean error = false;
        try {
            if (name.isBlank()) {
                throw new Exception();
            }
        } catch (Exception ignored) {
            error = true;
        }

        if (error) { return; }

        fieldListModel.addElement(new SgField(name, payload));
        FieldList.updateUI();
    }

    // this gets called when the add field button is pushed
    // so now call it when the Add field dialog is closed
    // but get the data from the form
    private void showAddAttribute() {
        AttributeDialog addAttributeDialog = new AttributeDialog(null, true, componentDao);
        try {
            addAttributeDialog.setComponent(getComponent()); //componentSelector.getSelectedValue());
        }
        catch (Exception e) {
            // what to do here????
        }
    
        while (true) {
            if (addAttributeDialog == null) {
                break;
            }

            addAttributeDialog.setLocationRelativeTo(null);
            addAttributeDialog.setVisible(true);
            addAttributeDialog.toFront();

            if (addAttributeDialog.clickedCancel()) {
                addAttributeDialog = null;
                break;
            }

            try {
                SgAttribute attribute = addAttributeDialog.getAttribute();
                existingComponent.addAttribute(attribute);
                //addAttribute(attribute.getName(), attribute.getValue());
                attributeListModel.addElement(new SgAttribute(attribute.getName(), attribute.getValue()));
                attributeList.updateUI();
                addAttributeDialog.dispose();
                addAttributeDialog = null;
            } catch (Exception ignored) {
            }
        }
    }

    private void addAttribute(String name, int value) {    
        boolean error = false;
        try {
            if (name.isBlank()) {
                throw new Exception();
            }
        } catch (Exception ignored) {
            error = true;
        }

        if (error) { return; }
        attributeListModel.addElement(new SgAttribute(name, value));
        attributeList.updateUI();
    }

    public void setComponentGroup(SgComponentGroupData componentGroup) {
        componentGroups = new DefaultComboBoxModel<>();
        componentGroups.addElement(componentGroup);
        ComponentGroupSelector.setModel(componentGroups);
        ComponentGroupSelector.setSelectedIndex(0);

        ComponentGroupSelector.disable();
        ComponentGroupSelector.setBackground(NORMAL_COLOR);
    }

    public void setComponent(SgComponentData component) {
        existingComponent = component;

        nameFld.setText(component.getName());
        iconPathFld.setText(component.getIconPath());
        guidFld.setText(component.getUuid());
        isAggregate.setSelected(component.isAggregate());
        descriptionFld.setText(component.getDescription());
        formalIdentFld.setText(component.getFormalIdentifier());
        identDescFld.setText(component.getIdentifierType());
        ComponentGroupSelector.setSelectedItem(component);

        ArrayList<Integer> selectedIndexes = new ArrayList<Integer>();
        for (int i = 0; i < collapseIntos.getSize(); ++i) {
            for (SgCollapseInto collapseInto: component.getSgCollapseIntos()) {
                if (collapseInto.getGuid().equals(collapseIntos.get(i).getUuid())) {
                    selectedIndexes.add(i);
                }
            }
        }
        CollapseIntoFld.setSelectedIndices(selectedIndexes.stream().mapToInt(i->i).toArray());

        fieldListModel = new DefaultListModel<>();
        for (SgField field: component.getFields()) {
            fieldListModel.addElement(field);
        }
        FieldList.setModel(fieldListModel);
        
        attributeListModel = new DefaultListModel<>();
        for (SgAttribute attribute: component.getAttributes()) {
            attributeListModel.addElement(attribute);
        }
        attributeList.setModel(attributeListModel);
    }

    public SgComponentData getComponent() throws Exception {
        SgComponentData component = existingComponent != null ? existingComponent : new SgComponentData();

        component.setName(retrieveName());
        component.setIconPath(retrieveIconPath());
        component.setUuid(retrieveGuid());
        component.setAggregate(isAggregate.isSelected());
        component.setDescription(retrieveDescription());
        component.setFormalIdentifier(formalIdentFld.getText());
        component.setIdentifierType(identDescFld.getText());

        SgComponentGroupData group = retrieveComponentGroup();
        if(group != null) { component.setComponentGroupId(group.getId()); }

        SgCollapseInto[] collapseIntos = retrieveCollapseInto();
        if (collapseIntos != null) { component.setSgCollapseIntos(Arrays.asList(collapseIntos)); }

        //if (!newFieldNameFld.getText().isBlank() || !newFieldPayloadFld.getText().isBlank()) {
          //  AddField();
        //}
        
        SgField[] fields = retrieveFields();
        if (fields != null) { component.setFields(new ArrayList<SgField>(Arrays.asList(fields))); }

        SgAttribute[] attributes = retrieveAttributes();
        if (attributes != null) { component.setAttributes(new ArrayList<SgAttribute>(Arrays.asList(attributes)));
        
        }
        if (!validateComponent()) {
            throw new Exception();
        }
        return component;
    }

    private String retrieveName() {
        String name = nameFld.getText();
        nameFld.setBackground(NORMAL_COLOR);

        if (name.isEmpty()) {
            nameFld.setBackground(ERROR_COLOR);
        }

        return name;
    }
    private SgComponentGroupData retrieveComponentGroup() {
        ComponentGroupSelector.setBackground(NORMAL_COLOR);
        try {
            return (SgComponentGroupData)ComponentGroupSelector.getSelectedItem();
        } catch (Exception ignored) {
            ComponentGroupSelector.setBackground(ERROR_COLOR);
            return null;
        }
    }
    private String retrieveIconPath() {
        iconPathFld.setBackground(NORMAL_COLOR);
        String path = iconPathFld.getText();

        if( new File(path).exists() == false){
            iconPathFld.setBackground(ERROR_COLOR);
            return "";
        }

        return path;
    }
    private String retrieveGuid(){
        try {
            UUID.fromString(guidFld.getText());
        } catch (Exception ignored) {
            guidFld.setBackground(ERROR_COLOR);
            return "";
        }
        return guidFld.getText();
    }
    private String retrieveDescription() {
        String description = descriptionFld.getText();
        return description == null ? "": description;
    }
    private SgCollapseInto[] retrieveCollapseInto() {
        CollapseIntoFld.setBackground(NORMAL_COLOR);
        try {
            Object[] selected = CollapseIntoFld.getSelectedValues();
            SgCollapseInto[] components = new SgCollapseInto[selected.length];

            // ToDo: Make sure this isn't adding extra items to the DB
            for(int i =0; i < selected.length; ++i) {
                components[i] = new SgCollapseInto();
                components[i].setGuid(((SgComponentData) selected[i]).getUuid());
            }

            return components;
        } catch (Exception ignored) {
            CollapseIntoFld.setBackground(ERROR_COLOR);
            return null;
        }
    }
    private SgField[] retrieveFields() {
        FieldList.setBackground(NORMAL_COLOR);
        try {
            SgField[] fields = new SgField[fieldListModel.getSize()];

            for(int i=0; i < fieldListModel.getSize(); ++i) {
                fields[i] = fieldListModel.get(i);
            }
            return fields;
        } catch (Exception ignored) {
            FieldList.setBackground(ERROR_COLOR);
            return null;
        }
    }

    private SgAttribute[] retrieveAttributes() {
        attributeList.setBackground(NORMAL_COLOR);
        try {
            SgAttribute[] attributes = new SgAttribute[attributeListModel.getSize()];

            for(int i=0; i < attributeListModel.getSize(); ++i) {
                attributes[i] = attributeListModel.get(i);
            }
            return attributes;
        } catch (Exception ignored) {
            attributeList.setBackground(ERROR_COLOR);
            return null;
        }
    }

    private void collectIconPath() {
        final JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(DEFAULT_PATH));
        int returnVal = fc.showOpenDialog(iconPathFld);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            if(!file.exists()) {
                iconPathFld.setBackground(ERROR_COLOR);
                iconPathFld.setText("File Doesn't Exist");
            }

            String path = file.getPath();
            int i = path.lastIndexOf('.');
            String extension = i>0 ? path.substring(i+1): "";

            if (extension.equalsIgnoreCase("png")) {
                iconPathFld.setBackground(NORMAL_COLOR);
                String iconPath = new File(".").getAbsoluteFile().toURI().relativize(file.toURI()).getPath();
                properties.setPropertyKeyValue(IGCAPTproperties.IgcaptProperty.LAST_ICON_PATH, iconPath);
                iconPathFld.setText(iconPath);
            } else {
                iconPathFld.setBackground(ERROR_COLOR);
                iconPathFld.setText("Incompatible file");
            }
        }
    }

    private boolean validateComponent() {
        return fieldIsNormal(nameFld) && fieldIsNormal(ComponentGroupSelector)
                && fieldIsNormal(CollapseIntoFld) && fieldIsNormal(FieldList)
                && fieldIsNormal(attributeList) && fieldIsNormal(iconPathFld);
    }

    private boolean fieldIsNormal(JComponent field) {
        return field.getBackground() == NORMAL_COLOR;
    }
    
    public boolean clickedCancel() {
        return clickedCancel;
    }
    
    /**
     * @return the return status of this dialog - one of RET_OK or RET_CANCEL
     */
    public int getReturnStatus() {
        return returnStatus;
    }

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
        nameFld = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        ComponentGroupSelector = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        iconPathFld = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        descriptionFld = new javax.swing.JTextArea();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        CollapseIntoFld = new javax.swing.JList<>();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        FieldList = new javax.swing.JList<>();
        newFieldBtn = new javax.swing.JButton();
        addIconBtn = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        guidFld = new javax.swing.JTextField();
        guidEditBtn = new javax.swing.JButton();
        isAggregate = new javax.swing.JCheckBox();
        identDescFld = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        formalIdentFld = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        removeFieldButton = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        attributeList = new javax.swing.JList<>();
        jLabel7 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        newAttributeButton = new javax.swing.JButton();
        removeAttributeButton = new javax.swing.JButton();

        setTitle("Component");
        setMaximumSize(new java.awt.Dimension(540, 582));
        setMinimumSize(new java.awt.Dimension(540, 582));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        okButton.setText("   OK   ");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.setActionCommand("  Cancel  ");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("Name");

        jLabel2.setText("Component Group");

        jLabel3.setText("Icon");

        jLabel4.setText("Description");

        descriptionFld.setColumns(20);
        descriptionFld.setRows(5);
        jScrollPane1.setViewportView(descriptionFld);

        jLabel5.setText("Collapse Into");

        jScrollPane2.setViewportView(CollapseIntoFld);

        jLabel6.setText("Fields");

        jScrollPane3.setViewportView(FieldList);

        newFieldBtn.setText("    Add    ");
        newFieldBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newFieldBtnActionPerformed(evt);
            }
        });

        addIconBtn.setText("Select Icon");
        addIconBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addIconBtnActionPerformed(evt);
            }
        });

        jLabel10.setText("Guid");

        guidEditBtn.setText("Edit");
        guidEditBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guidEditBtnActionPerformed(evt);
            }
        });

        isAggregate.setText("Aggregate");

        identDescFld.setToolTipText("Use this field to store the formal designation of this component such as C37.2 ANSI/IEEE standard designations.");

        jLabel11.setText("Formal Identifier");
        jLabel11.setToolTipText("");

        jLabel12.setText("Identifier Description");

        removeFieldButton.setText("Remove");
        removeFieldButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeFieldButtonActionPerformed(evt);
            }
        });

        jScrollPane4.setViewportView(attributeList);

        jLabel7.setText("Attributes");
        jLabel7.setToolTipText("");

        jButton2.setText("    Add    ");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        newAttributeButton.setText("    Add    ");
        newAttributeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newAttributeButtonActionPerformed(evt);
            }
        });

        removeAttributeButton.setText("Remove");
        removeAttributeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeAttributeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(jLabel5)
                    .addComponent(jLabel12)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(identDescFld)
                            .addComponent(isAggregate)
                            .addComponent(iconPathFld, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(addIconBtn)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(formalIdentFld, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(guidFld, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 41, Short.MAX_VALUE)
                                        .addComponent(guidEditBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addComponent(ComponentGroupSelector, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(nameFld)))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 412, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(okButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cancelButton))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
                                    .addComponent(jScrollPane4))
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(84, 84, 84)
                                    .addComponent(jButton2))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(newFieldBtn)
                            .addComponent(removeFieldButton)
                            .addComponent(newAttributeButton)
                            .addComponent(removeAttributeButton))))
                .addContainerGap(10, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(nameFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ComponentGroupSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(iconPathFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(addIconBtn)
                            .addComponent(jLabel3))
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(guidFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(guidEditBtn)
                            .addComponent(jLabel10))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(isAggregate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(formalIdentFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(identDescFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel6)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(newFieldBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(removeFieldButton)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(newAttributeButton)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(removeAttributeButton))
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jButton2)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton)
                    .addComponent(cancelButton))
                .addContainerGap())
        );

        getRootPane().setDefaultButton(okButton);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        doClose(RET_OK);
        clickedCancel = false;
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

    private void newFieldBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newFieldBtnActionPerformed
        ShowAddField();
    }//GEN-LAST:event_newFieldBtnActionPerformed

    private void guidEditBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guidEditBtnActionPerformed
        guidFld.setEnabled(!guidFld.isEnabled());
    }//GEN-LAST:event_guidEditBtnActionPerformed

    private void addIconBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addIconBtnActionPerformed
        collectIconPath();
    }//GEN-LAST:event_addIconBtnActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void newAttributeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newAttributeButtonActionPerformed
        showAddAttribute();
    }//GEN-LAST:event_newAttributeButtonActionPerformed

    private void removeFieldButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeFieldButtonActionPerformed
        int index = FieldList.getSelectedIndex();
        if (index > -1) {
            fieldListModel.removeElementAt(index);
        }
    }//GEN-LAST:event_removeFieldButtonActionPerformed

    private void removeAttributeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeAttributeButtonActionPerformed
        int index = attributeList.getSelectedIndex();
        if (index > -1) {
            attributeListModel.removeElementAt(index);
        }
    }//GEN-LAST:event_removeAttributeButtonActionPerformed
    
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
            java.util.logging.Logger.getLogger(ComponentDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ComponentDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ComponentDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ComponentDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ComponentDialog dialog = new ComponentDialog(new javax.swing.JFrame(), true, null);
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
    private javax.swing.JList<SgComponentData> CollapseIntoFld;
    private javax.swing.JComboBox<SgComponentGroupData> ComponentGroupSelector;
    private javax.swing.JList<SgField> FieldList;
    private javax.swing.JButton addIconBtn;
    private javax.swing.JList<SgAttribute> attributeList;
    private javax.swing.JButton cancelButton;
    private javax.swing.JTextArea descriptionFld;
    private javax.swing.JTextField formalIdentFld;
    private javax.swing.JButton guidEditBtn;
    private javax.swing.JTextField guidFld;
    private javax.swing.JTextField iconPathFld;
    private javax.swing.JTextField identDescFld;
    private javax.swing.JCheckBox isAggregate;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextField nameFld;
    private javax.swing.JButton newAttributeButton;
    private javax.swing.JButton newFieldBtn;
    private javax.swing.JButton okButton;
    private javax.swing.JButton removeAttributeButton;
    private javax.swing.JButton removeFieldButton;
    // End of variables declaration//GEN-END:variables

    private int returnStatus = RET_CANCEL;
}
