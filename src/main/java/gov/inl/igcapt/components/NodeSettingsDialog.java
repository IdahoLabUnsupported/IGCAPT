/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.igcapt.components;

import gov.inl.igcapt.components.DataModels.SgComponentData;
import gov.inl.igcapt.graph.GraphManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.DefaultListModel;
import gov.inl.igcapt.view.IGCAPTgui;
import gov.inl.igcapt.graph.SgGraph;
import gov.inl.igcapt.graph.SgNode;
import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *(c) 2018 BATTELLE ENERGY ALLIANCE, LLC
 *ALL RIGHTS RESERVED 
 *
 */
public class NodeSettingsDialog extends javax.swing.JDialog {

    public enum ReturnValue {
        CANCEL,
        OK
    }
    private ReturnValue m_ReturnValue;
    private String  m_ComponentName;
    private int  m_PayloadBytes;
    private int     m_MaxLatencySecs;
    private boolean m_EnableDataSending;
    private boolean m_EnableDataPassthrough;
    private boolean m_IsAggregate;
    private List<Integer> m_EndpointList = null;
    private final SgNode m_SgNode;
    private SgGraph m_Graph;
    private String m_userData;
    private final DefaultListModel m_EndPointListModel = new DefaultListModel();
    
    /**
     * Creates new form ComponentSettingsDialog
     * @param parent
     * @param graph
     * @param node
     */
    public NodeSettingsDialog(java.awt.Frame parent, SgGraph graph, SgNode node) {
        super(parent, true);
        
        m_EndpointList = new ArrayList<>();
        m_SgNode = node;
        m_Graph = graph;
        
        initComponents();
        
        finalizeInitComponents();
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
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        enableDataSendingCheckbox = new javax.swing.JCheckBox();
        componentNameTextField = new javax.swing.JTextField();
        componentIdTextField = new javax.swing.JTextField();
        componentTypeIdTextField = new javax.swing.JTextField();
        componentTypeTextField = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        addEndPointButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        endPointList = new javax.swing.JList<>();
        removeEndPointButton = new javax.swing.JButton();
        payloadTextField = new javax.swing.JFormattedTextField();
        maxLatencyTextField = new javax.swing.JFormattedTextField();
        aggregateCheckbox = new javax.swing.JCheckBox();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        userData = new javax.swing.JTextArea();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        attributesTable = new javax.swing.JTable();
        addAttributeBtn = new javax.swing.JButton();
        remAttributeBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Device Settings");
        setLocation(new java.awt.Point(100, 100));
        setModal(true);
        setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        setResizable(false);

        okButton.setText("Ok");
        okButton.setMaximumSize(new java.awt.Dimension(65, 23));
        okButton.setMinimumSize(new java.awt.Dimension(65, 23));
        okButton.setPreferredSize(new java.awt.Dimension(65, 23));
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("Device Name");

        jLabel2.setText("Payload (bytes)");

        jLabel3.setText("Max Latency (sec)");

        jLabel4.setText("Device Id");

        jLabel5.setText("Component Type Id");

        jLabel6.setText("Component Type");

        enableDataSendingCheckbox.setText("Enable Data Sending");

        componentIdTextField.setEditable(false);

        componentTypeIdTextField.setEditable(false);

        componentTypeTextField.setEditable(false);

        jLabel7.setText("End Points");

        addEndPointButton.setText("Add");
        addEndPointButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addEndPointButtonActionPerformed(evt);
            }
        });

        jScrollPane1.setViewportView(endPointList);

        removeEndPointButton.setText("Remove");
        removeEndPointButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeEndPointButtonActionPerformed(evt);
            }
        });

        payloadTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        payloadTextField.setName("payloadTextField"); // NOI18N

        maxLatencyTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));

        aggregateCheckbox.setText("Aggregate");

        jLabel8.setText("User Data");
        jLabel8.setToolTipText("");

        userData.setColumns(20);
        userData.setRows(5);
        jScrollPane2.setViewportView(userData);

        jLabel9.setText("Attributes");

        attributesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Key", "Value"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane3.setViewportView(attributesTable);

        addAttributeBtn.setText("Add");
        addAttributeBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addAttributeBtnMouseClicked(evt);
            }
        });

        remAttributeBtn.setText("Remove");
        remAttributeBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                remAttributeBtnMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(jLabel7))
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(enableDataSendingCheckbox)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(removeEndPointButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(aggregateCheckbox)
                                    .addComponent(addEndPointButton))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(payloadTextField, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(componentNameTextField, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(remAttributeBtn)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(addAttributeBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGap(1, 1, 1))))
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(componentTypeTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(componentTypeIdTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(maxLatencyTextField, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(componentIdTextField, javax.swing.GroupLayout.Alignment.LEADING)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42)
                .addComponent(cancelButton)
                .addGap(156, 156, 156))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, okButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(componentNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(payloadTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(maxLatencyTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(componentIdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(componentTypeIdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(componentTypeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addAttributeBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(remAttributeBtn)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(enableDataSendingCheckbox)
                    .addComponent(aggregateCheckbox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(addEndPointButton)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(removeEndPointButton))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cancelButton, okButton});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {addEndPointButton, removeEndPointButton});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public ReturnValue getReturnValue() {
        return m_ReturnValue;
    }
    
    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        
        m_ComponentName = componentNameTextField.getText();
        m_PayloadBytes = Integer.parseInt(payloadTextField.getText());
        m_MaxLatencySecs = Integer.parseInt(maxLatencyTextField.getText());
        m_EnableDataSending = enableDataSendingCheckbox.isSelected();
        m_IsAggregate = aggregateCheckbox.isSelected();
        m_userData = userData.getText();
        
        m_ReturnValue = ReturnValue.OK;
     
        var attributes = m_SgNode.getAttributes();
        attributes.clear();
        
        DefaultTableModel tModel = (DefaultTableModel)attributesTable.getModel();
        final int KEYCOL = 0;
        final int VALUECOL = 1;
        
        for(int i=0; i<tModel.getRowCount(); i++) {
            attributes.put((String)tModel.getValueAt(i, KEYCOL), (String)tModel.getValueAt(i, VALUECOL));
        }
        
        dispose();
    }//GEN-LAST:event_okButtonActionPerformed

    public String getComponentName() {
        return m_ComponentName;
    }
    
    public int getPayloadBytes() {
        return m_PayloadBytes;
    }
    
    public int getMaxLatencySecs() {
        return m_MaxLatencySecs;
    }
    
    public boolean getEnableDataSending() {
        return m_EnableDataSending;
    }
    
    public boolean getEnableDataPassthrough() {
        return m_EnableDataPassthrough;
    }
    
    public boolean getIsAggregate() {
        return m_IsAggregate;
    }
    
    public String getUserData() {
        return m_userData;
    }
    
    public List<Integer> getEndPointList() {
        return m_EndpointList;
    }
    
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        dispose();
        
        m_ReturnValue = ReturnValue.CANCEL;
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void removeEndPointButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeEndPointButtonActionPerformed
        
        int[] selectedIndices = endPointList.getSelectedIndices();
        Arrays.sort(selectedIndices); // Make sure the list is in order.
        List<String> selectedValues = endPointList.getSelectedValuesList();
        
        for (int i = selectedIndices.length; --i >= 0;) {
            m_EndPointListModel.removeElement(selectedValues.get(i));
            m_EndpointList.remove(i);            
        }
        
        endPointList.setModel(m_EndPointListModel);
    }//GEN-LAST:event_removeEndPointButtonActionPerformed

    private void addEndPointButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addEndPointButtonActionPerformed
        EndPointListDialog endPointListDialog = new EndPointListDialog(this, m_Graph, m_SgNode, m_EndpointList);
        endPointListDialog.setLocationRelativeTo(this);
        endPointListDialog.setVisible(true);
        
        if (endPointListDialog.getReturnValue() == EndPointListDialog.ReturnValue.OK) {
            
            List<Integer> endPoints = endPointListDialog.getSelectedEndPointIds();
            
            for (Integer endPoint : endPoints) {
                m_EndPointListModel.addElement(GraphManager.getInstance().getNode(endPoint).getName());
                m_EndpointList.add(endPoint);
            }
        }
        
         endPointList.setModel(m_EndPointListModel);
    }//GEN-LAST:event_addEndPointButtonActionPerformed

    private void addAttributeBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addAttributeBtnMouseClicked
        DefaultTableModel tModel = (DefaultTableModel)attributesTable.getModel();
        tModel.addRow(new Object[]{"",""});
        attributesTable.changeSelection(tModel.getRowCount()-1, 0, false, false);
    }//GEN-LAST:event_addAttributeBtnMouseClicked

    private void remAttributeBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_remAttributeBtnMouseClicked
        DefaultTableModel tModel = (DefaultTableModel)attributesTable.getModel();
        int selectedRow = attributesTable.getSelectedRow();
        
        if (selectedRow > -1 && selectedRow <= attributesTable.getRowCount()) {
            tModel.removeRow(selectedRow);
        }
    }//GEN-LAST:event_remAttributeBtnMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addAttributeBtn;
    private javax.swing.JButton addEndPointButton;
    private javax.swing.JCheckBox aggregateCheckbox;
    private javax.swing.JTable attributesTable;
    private javax.swing.JButton cancelButton;
    private javax.swing.JTextField componentIdTextField;
    private javax.swing.JTextField componentNameTextField;
    private javax.swing.JTextField componentTypeIdTextField;
    private javax.swing.JTextField componentTypeTextField;
    private javax.swing.JCheckBox enableDataSendingCheckbox;
    private javax.swing.JList<String> endPointList;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JFormattedTextField maxLatencyTextField;
    private javax.swing.JButton okButton;
    private javax.swing.JFormattedTextField payloadTextField;
    private javax.swing.JButton remAttributeBtn;
    private javax.swing.JButton removeEndPointButton;
    private javax.swing.JTextArea userData;
    // End of variables declaration//GEN-END:variables

    private void finalizeInitComponents() {
        
        componentNameTextField.setText(m_SgNode.getName());
        payloadTextField.setText(Integer.toString(m_SgNode.getDataToSend()));
        maxLatencyTextField.setText(Integer.toString(m_SgNode.getMaxLatency()));
        componentIdTextField.setText(Integer.toString(m_SgNode.getId()));
        
        String tempStr = "<Unknown>";
        String nodeType = m_SgNode.getType();
        
        SgComponentData sgComponent = IGCAPTgui.getComponentByUuid(nodeType);

        if (sgComponent != null) {
            tempStr = sgComponent.getName();
        }

        componentTypeTextField.setText(tempStr);
        componentTypeIdTextField.setText(nodeType);        
        enableDataSendingCheckbox.setSelected(m_SgNode.getEnableDataSending());
        aggregateCheckbox.setSelected(m_SgNode.getIsAggregate());
        m_EndpointList = m_SgNode.getEndPointList();
        userData.setText(m_SgNode.getUserData());
        
        m_EndPointListModel.clear();
        for (int i : m_EndpointList) {
            m_EndPointListModel.addElement(GraphManager.getInstance().getNode(i).getName());
        }
        
        endPointList.setModel(m_EndPointListModel);
        
        DefaultTableModel tModel = (DefaultTableModel)attributesTable.getModel();
        tModel.setRowCount(0);
        
        DefaultCellEditor cellEditor = new DefaultCellEditor(new JTextField());
        cellEditor.setClickCountToStart(2);

        //set the editor as default on every column
        var columnModel = attributesTable.getColumnModel();
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            columnModel.getColumn(i).setCellEditor(cellEditor);
        } 
        
        var attrs = m_SgNode.getAttributes();
        for (var attrEntry : attrs.entrySet()) {
            tModel.addRow(new Object[]{attrEntry.getKey(), attrEntry.getValue()});
        }
    }
}
