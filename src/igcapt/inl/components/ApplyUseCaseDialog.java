/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package igcapt.inl.components;

import com.sun.java.swing.plaf.windows.WindowsTreeUI;
import igcapt.inl.components.generated.SgApplication;
import igcapt.inl.components.generated.SgDataElement;
import igcapt.inl.components.generated.SgUseCase;
import igcapt.inl.components.generated.SgUseCaseData;
import java.util.HashMap;
import java.util.UUID;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.UIManager;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.openstreetmap.gui.jmapviewer.IGCAPTgui;

/**
 *(c) 2018 BATTELLE ENERGY ALLIANCE, LLC
 *ALL RIGHTS RESERVED 
 *
 * @author FRAZJD
 */
public class ApplyUseCaseDialog extends javax.swing.JDialog {
  
    public enum ReturnValue {
        Unknown,
        Ok,
        Cancel
    }
    
    // private HashMap<Pair<String, String>, HashMap<String, Integer>> applyToData = new HashMap<>();

    private ReturnValue returnValue = ReturnValue.Unknown;
    public ReturnValue getReturnValue() {
        return returnValue;
    }
    
    // Pair<Use case name, Data element name>
    // Map
    //    Component UUID
    //    Percent to apply
    private HashMap<Pair<String, String>, HashMap<UUID, Integer>> _useCaseApplyData = null;
    
    /**
     * Creates new form ApplyUseCaseDialog
     * @param parent
     * @param modal
     * @param useCaseApplyData
     */
    public ApplyUseCaseDialog(java.awt.Frame parent, boolean modal, HashMap<Pair<String, String>, HashMap<UUID, Integer>> useCaseApplyData) {
        super(parent, modal);
        
        UIManager.put("Tree.expandedIcon",  new WindowsTreeUI.ExpandedIcon()); 
        UIManager.put("Tree.collapsedIcon", new WindowsTreeUI.CollapsedIcon());
        
        _useCaseApplyData = useCaseApplyData;
        
        initComponents();
        
        initializeTree();
        
        // Center the dialog within the main window.
        setLocationRelativeTo(parent);
    }
    
    private DefaultMutableTreeNode add(
            final DefaultMutableTreeNode parent, final String text,
            final boolean checked)
    {
            final UseCaseNodeData data = new UseCaseNodeData(text, checked);
            final DefaultMutableTreeNode node = new DefaultMutableTreeNode(data);
            parent.add(node);
            return node;
    }
    
    private void initializeTree() {
        final DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");

        SgUseCaseData sgUseCaseData = IGCAPTgui.getInstance().getUseCaseData();
        
        // Applications and Use Cases
        for (SgApplication app:sgUseCaseData.getSgApplication()) {
            
            DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(app.getName());
            for (SgUseCase useCase:app.getSgUseCase()) {
                DefaultMutableTreeNode useCaseNode = new DefaultMutableTreeNode(useCase.getName());
                treeNode.add(useCaseNode);
                
                for (SgDataElement dataElement:useCase.getSgDataElement()) {
                    
                    boolean isChecked = false;
                    
                    if (_useCaseApplyData.containsKey(new Pair(useCase.getName(), dataElement.getName()))) {
                        isChecked = true;
                    }
                    add(useCaseNode, dataElement.getName(), isChecked);
                }
            }
            
            root.add(treeNode);
        }

        final DefaultTreeModel treeModel = new DefaultTreeModel(root);
        jTree1.setModel(treeModel);
        jTree1.setRootVisible(false);
        jTree1.setShowsRootHandles(true);
          
        // Expand all nodes.
        for (int i=0; i<jTree1.getRowCount(); ++i) {
            jTree1.expandRow(i);
        }

        final UseCaseNodeRenderer renderer = new UseCaseNodeRenderer();
        jTree1.setCellRenderer(renderer);

        final UseCaseNodeEditor editor = new UseCaseNodeEditor(jTree1);
        jTree1.setCellEditor(editor);
        jTree1.setEditable(true);

        // listen for changes in the selection
        jTree1.addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(final TreeSelectionEvent e) {
            }
        });

        // listen for changes in the model (including check box toggles)
        treeModel.addTreeModelListener(new TreeModelListener() {

            @Override
            public void treeNodesChanged(final TreeModelEvent e) {
                
                if (e.getChildren().length > 0) {
                    Object child = e.getChildren()[0];
                    
                    if (child instanceof DefaultMutableTreeNode) {
                        Object userObject = ((DefaultMutableTreeNode)child).getUserObject();
                        
                        if (userObject instanceof UseCaseNodeData) {
                            UseCaseNodeData useCaseNodeData = (UseCaseNodeData)userObject;
                            
                            Object clickObject = useCaseNodeData.getClickObject();
                            if (clickObject instanceof JButton) {
                                if (useCaseNodeData.isChecked()) {
                                    String useCase = (String)((DefaultMutableTreeNode)((DefaultMutableTreeNode) child).getParent()).getUserObject();
                                    String dataElement = useCaseNodeData.getText();
                                    Pair<String, String> key = new Pair<>(useCase, dataElement);
                                    
                                    useCaseNodeData.setUseCaseApplyData(_useCaseApplyData.get(key));
                                    ApplyToDialog applyToDialog = new ApplyToDialog(IGCAPTgui.getInstance(), true, useCaseNodeData);
                                    applyToDialog.setVisible(true);

                                    if (applyToDialog.getReturnValue() == ApplyToDialog.ReturnValue.Ok) {
                                        
                                        HashMap<UUID, Integer> applyToData = applyToDialog.getUseCaseApplyToData();
                                        
                                        _useCaseApplyData.put(new Pair<String, String>(useCase,dataElement), applyToData);
                                    }
                                }
                            }
                            else if (clickObject instanceof JCheckBox) {
                                String useCase = (String)((DefaultMutableTreeNode)((DefaultMutableTreeNode) child).getParent()).getUserObject();
                                String dataElement = useCaseNodeData.getText();
                                Pair<String, String> key = new Pair<>(useCase, dataElement);
                                
                                if (useCaseNodeData.isChecked()) {
                                    _useCaseApplyData.put(key, null);
                                }
                                else {
                                    _useCaseApplyData.remove(key);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void treeNodesInserted(final TreeModelEvent e) {
            }

            @Override
            public void treeNodesRemoved(final TreeModelEvent e) {
            }

            @Override
            public void treeStructureChanged(final TreeModelEvent e) {
            }
        });
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
        jScrollPane1 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Apply Use Case");
        setMaximumSize(new java.awt.Dimension(495, 2147483647));
        setMinimumSize(new java.awt.Dimension(495, 546));
        setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        setResizable(false);

        okButton.setText("Ok");
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

        jScrollPane1.setViewportView(jTree1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(170, 170, 170)
                .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(cancelButton)
                .addContainerGap(174, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 483, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton)
                    .addComponent(cancelButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        returnValue = ReturnValue.Ok;
        dispose();
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        returnValue = ReturnValue.Cancel;
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTree jTree1;
    private javax.swing.JButton okButton;
    // End of variables declaration//GEN-END:variables
}
