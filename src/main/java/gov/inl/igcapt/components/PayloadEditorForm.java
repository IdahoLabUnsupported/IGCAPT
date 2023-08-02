/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.igcapt.components;

import gov.inl.igcapt.components.DataModels.ComponentDao;
import gov.inl.igcapt.components.DataModels.SgComponentData;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import gov.inl.igcapt.view.IGCAPTgui;
import java.util.Vector;

/**
 *
 * @author FRAZJD
 */
public class PayloadEditorForm extends javax.swing.JDialog {

    static String m_dependent = "Dependent Collection";
    List<String> m_appliedPayloadList = null;
    public enum ReturnValue {
        Unknown,
        Ok,
        Cancel
    }
        
    private ReturnValue returnValue = ReturnValue.Unknown;
    public ReturnValue getReturnValue() {
        return returnValue;
    }
    
    /**
     * Creates new form PayloadEditorForm
     * @param payload The current payload configuration.
     */
    public PayloadEditorForm(Payload payload) {
        initComponents();
        
        payloadTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        
        initializePayloadTree(payload);
        setLocationRelativeTo(null);
        setModal(true);
    }
    
    private void initializePayloadTree(Payload payload) {
                
        // Get components
        ComponentDao componentDao = new ComponentDao();
        List<SgComponentData> componentDataList = componentDao.getComponents();
        
        // Initialize the payloadTree with the payload.
        DefaultTreeModel payloadTreeModel = (DefaultTreeModel)payloadTree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)payloadTreeModel.getRoot();
        for (UseCaseEntry useCaseEntry:payload.payloadUseCaseList) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(useCaseEntry);
            payloadTreeModel.insertNodeInto(node, root, payloadTreeModel.getChildCount(root));
        }
        
        for (DependentUseCaseEntry depUseCaseEntry:payload.payloadDependentUseCaseList) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(depUseCaseEntry, true);
            payloadTreeModel.insertNodeInto(node, root, payloadTreeModel.getChildCount(root));
            
            for (UseCaseEntry useCaseEntry:depUseCaseEntry.useCases) {
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(useCaseEntry);
                payloadTreeModel.insertNodeInto(childNode, node, payloadTreeModel.getChildCount(node));              
            }
        }
        
        payloadTreeModel.reload();
        TreeUtil.setTreeExpandedState(payloadTree,true);
        TreeUtil.makeTreeUnCollapsible(payloadTree);
        
        if (payloadTree.getComponentCount() > 0) {
            payloadTree.setSelectionRow(0);
        }
    }
    
    /**
     * Return the payload information entered into the dialog.
     */
    public Payload getPayload() {
        Payload returnval = new Payload();
        
        // Fill the payload object from the tree model.
        TreeModel treeModel = payloadTree.getModel();
        Object root = treeModel.getRoot();
        int numChildren = treeModel.getChildCount(root);
        
        for (int i=0; i<numChildren; i++) {
            
            Object child = treeModel.getChild(root, i);
            
            // A dependent use case element
            int childCount = treeModel.getChildCount(child);
            if (childCount > 0)
            {
                DependentUseCaseEntry depUseCase = new DependentUseCaseEntry();
                
                Object userObject = ((DefaultMutableTreeNode)child).getUserObject();
                if (userObject instanceof DependentUseCaseEntry) {
                    depUseCase.setPercentToApply(((DependentUseCaseEntry)userObject).getPercentToApply());
                }
                
                for (int j=0; j<childCount; j++) {
                    Object depUserObject = ((DefaultMutableTreeNode)treeModel.getChild(child, j)).getUserObject();
                    
                    if (depUserObject instanceof UseCaseEntry) {
                        UseCaseEntry depChildUseCase = (UseCaseEntry)depUserObject;
                        
                        depUseCase.useCases.add(depChildUseCase);
                    }
                }
                
                returnval.payloadDependentUseCaseList.add(depUseCase);
            }
            else {
                UseCaseEntry useCaseEntry = (UseCaseEntry)((DefaultMutableTreeNode)child).getUserObject();
                
                returnval.payloadUseCaseList.add(useCaseEntry);
            }
        }
        
        return returnval;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        okBtn = new javax.swing.JButton();
        cancelBtn = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        payloadTree = new javax.swing.JTree();
        addPayloadBtn = new javax.swing.JButton();
        removePayloadBtn = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setTitle("Payload Editor");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        okBtn.setText("Ok");
        okBtn.setMaximumSize(new java.awt.Dimension(66, 23));
        okBtn.setMinimumSize(new java.awt.Dimension(66, 23));
        okBtn.setOpaque(false);
        okBtn.setPreferredSize(new java.awt.Dimension(65, 23));
        okBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okBtnActionPerformed(evt);
            }
        });

        cancelBtn.setText("Cancel");
        cancelBtn.setName("CancelBtn"); // NOI18N
        cancelBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelBtnActionPerformed(evt);
            }
        });

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        payloadTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        payloadTree.setMinimumSize(new java.awt.Dimension(77, 80));
        payloadTree.setRootVisible(false);
        payloadTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                payloadTreeMouseClicked(evt);
            }
        });
        payloadTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                payloadTreeValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(payloadTree);

        addPayloadBtn.setText("Add Payload");
        addPayloadBtn.setMaximumSize(new java.awt.Dimension(113, 23));
        addPayloadBtn.setMinimumSize(new java.awt.Dimension(113, 23));
        addPayloadBtn.setOpaque(false);
        addPayloadBtn.setPreferredSize(new java.awt.Dimension(119, 23));
        addPayloadBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addPayloadBtnActionPerformed(evt);
            }
        });

        removePayloadBtn.setText("Remove Payload");
        removePayloadBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removePayloadBtnActionPerformed(evt);
            }
        });

        jButton1.setText("Save");
        jButton1.setEnabled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Load");
        jButton2.setEnabled(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 658, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cancelBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(okBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(removePayloadBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(addPayloadBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 642, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addPayloadBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removePayloadBtn)))
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancelBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okBtnActionPerformed
        returnValue = ReturnValue.Ok;
        dispose();
    }//GEN-LAST:event_okBtnActionPerformed

    private void cancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelBtnActionPerformed
        returnValue = ReturnValue.Cancel;
        dispose();
    }//GEN-LAST:event_cancelBtnActionPerformed

    private void addPayloadBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addPayloadBtnActionPerformed
        AddUseCaseDlg addUseCaseDlg = new AddUseCaseDlg(null, true, m_appliedPayloadList);
        
        addUseCaseDlg.setLocationRelativeTo(IGCAPTgui.getInstance());
        addUseCaseDlg.setVisible(true);
        
        if (addUseCaseDlg.getReturnValue() == AddUseCaseDlg.ReturnValue.Ok) {
            // Add it below the selected one if a dependent, at the end if not.
            
            DefaultTreeModel treeModel = (DefaultTreeModel)payloadTree.getModel();
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)payloadTree.getModel().getRoot();
            
            if (addUseCaseDlg.getDependent() == true) {
                if (m_appliedPayloadList == null) {
                    m_appliedPayloadList = new Vector<String>();
                    m_appliedPayloadList.add(m_dependent);
                    
                    DependentUseCaseEntry treeEntry = new DependentUseCaseEntry();
                    treeEntry.setPercentToApply(addUseCaseDlg.getPercentApply());

                    DefaultMutableTreeNode child = new DefaultMutableTreeNode(treeEntry, true);
                    treeModel.insertNodeInto(child, root, root.getChildCount());
                    payloadTree.scrollPathToVisible(new TreePath(child.getPath()));                }
                else {
                    if (!m_appliedPayloadList.contains(m_dependent)) {
                        m_appliedPayloadList.add(m_dependent);
                        DependentUseCaseEntry treeEntry = new DependentUseCaseEntry();
                        treeEntry.setPercentToApply(addUseCaseDlg.getPercentApply());

                        DefaultMutableTreeNode child = new DefaultMutableTreeNode(treeEntry, true);
                        treeModel.insertNodeInto(child, root, root.getChildCount());
                        payloadTree.scrollPathToVisible(new TreePath(child.getPath()));
                    }
                }
            }
            else {   
                TreePath selectedNodeTreePath = payloadTree.getSelectionPath();
                
                String name = "";
                DefaultMutableTreeNode node = root;
                DefaultMutableTreeNode selectedNode = null;
                
                if (selectedNodeTreePath != null) {
                    selectedNode = (DefaultMutableTreeNode)selectedNodeTreePath.getLastPathComponent();               
                    
                    if (selectedNode != null) {
                        name = selectedNode.getUserObject().toString();
                    
                        if (name.contains(AddUseCaseDlg.DEPENDENT_LABEL)) {
                            node = selectedNode;
                        }
                    }                  
                }
                
                DefaultMutableTreeNode child = null;
                List<String> newlySelectedUseCases = addUseCaseDlg.getUseCaseNameList();
                for (String useCaseName : newlySelectedUseCases) {
                    if (m_appliedPayloadList != null) {
                        if (!m_appliedPayloadList.contains(useCaseName)) {
                            m_appliedPayloadList.add(useCaseName);
                        }
                    }
                    else {
                        m_appliedPayloadList = new Vector<String>();
                        m_appliedPayloadList.add(useCaseName);
                    }

                }
                for (String useCaseName : newlySelectedUseCases) {
                    UseCaseEntry treeEntry = new UseCaseEntry();
                    treeEntry.setUseCaseName(useCaseName);
                    treeEntry.setPercentToApply(addUseCaseDlg.getPercentApply());
                    child = new DefaultMutableTreeNode(treeEntry);
                    treeModel.insertNodeInto(child, node, node.getChildCount());
                }

                if (selectedNodeTreePath != null) {
                    payloadTree.expandPath(selectedNodeTreePath);
                }
                if (child != null) {
                    payloadTree.scrollPathToVisible(new TreePath(child.getPath()));
                }
            }
        }
    }//GEN-LAST:event_addPayloadBtnActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // Same as clicking the cancel button.
        returnValue = ReturnValue.Cancel;
        dispose();
    }//GEN-LAST:event_formWindowClosing

    private void removePayloadBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removePayloadBtnActionPerformed
        DefaultTreeModel treeModel = (DefaultTreeModel)payloadTree.getModel();
        TreePath selectedNodeTreePath = payloadTree.getSelectionPath();
        while (selectedNodeTreePath != null) {
        
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)selectedNodeTreePath.getLastPathComponent();
            String useCaseName;
            if (node.getUserObject() instanceof UseCaseEntry nodeEntry) {
                useCaseName = nodeEntry.getUseCaseName();
            }
            else {
                useCaseName = m_dependent;
            }
            m_appliedPayloadList.remove(useCaseName);
            if (node.getParent() != null) {
                treeModel.removeNodeFromParent(node);
            }
            selectedNodeTreePath = payloadTree.getSelectionPath();
        } // end while
    }//GEN-LAST:event_removePayloadBtnActionPerformed

    private void payloadTreeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_payloadTreeMouseClicked
        int row=payloadTree.getRowForLocation(evt.getX(),evt.getY());
        
        //When user clicks on the "empty surface"
        if(row==-1) {//When user clicks on the "empty surface"
            payloadTree.clearSelection();
            removePayloadBtn.setEnabled(false);
        }
        else {
            removePayloadBtn.setEnabled(true);
        }
    }//GEN-LAST:event_payloadTreeMouseClicked

    private void payloadTreeValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_payloadTreeValueChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_payloadTreeValueChanged

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Payload payload = getPayload();

        String SERIALIZED_FILE_NAME = "D:\\Development\\SGComms\\igcapt\\scenarios\\payload.xml";
        XMLEncoder encoder = null;
        try {
            encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(SERIALIZED_FILE_NAME)));
        } catch (FileNotFoundException fileNotFound) {
            System.out.println("ERROR: While Creating or Opening the File payload.xml");
        }
        encoder.writeObject(payload);
        encoder.close();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        XMLDecoder decoder=null;
        String SERIALIZED_FILE_NAME = "D:\\Development\\SGComms\\igcapt\\scenarios\\payload.xml";
        
        try {
            decoder=new XMLDecoder(new BufferedInputStream(new FileInputStream(SERIALIZED_FILE_NAME)));
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: File payload.xml not found");
        }
        Payload payload = (Payload)decoder.readObject();
        if (payload != null) {
            initializePayloadTree(payload);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

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
            java.util.logging.Logger.getLogger(PayloadEditorForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PayloadEditorForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PayloadEditorForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PayloadEditorForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PayloadEditorForm(new Payload()).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addPayloadBtn;
    private javax.swing.JButton cancelBtn;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton okBtn;
    private javax.swing.JTree payloadTree;
    private javax.swing.JButton removePayloadBtn;
    // End of variables declaration//GEN-END:variables
}
