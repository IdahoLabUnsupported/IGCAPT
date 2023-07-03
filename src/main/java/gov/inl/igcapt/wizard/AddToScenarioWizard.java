/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package gov.inl.igcapt.wizard;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import gov.inl.igcapt.controllers.GDTAFImportController;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import javax.swing.JFileChooser;
import org.json.JSONObject;
import gov.inl.igcapt.properties.IGCAPTproperties;
import gov.inl.igcapt.properties.IGCAPTproperties.IgcaptProperty;
import java.awt.Frame;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
/**
 *
 * @author CHE
 */
// Class to guide user through the process to update scenario with GUCS/CNRM
public class AddToScenarioWizard extends javax.swing.JDialog {
    private List<GucsInformation>m_gucsList = null;
    private List<CnrmInformation>m_cnrmList = null;
    private String m_webServiceHost = null;
    private String m_webServiceKey = null;
    private Frame m_parent = null;
    enum FieldStates {
        GUCS_FIELD,   
        CNRM_FIELD,   
        LOCATION_FIELD, 
        SAVE_CANCEL   
    }
    
    /**
     * Creates new form ScenarioWizard
     */
    public AddToScenarioWizard(Frame parent, boolean modal) {
        super(parent, modal);
        
        initComponents();
        disableAllButtons();
        initGucsList();
        setTitle("Update Scenario with GUCS/CNRM");
        this.setVisible(true);
    }
           
    // Enable the appropriate field
    private void enableField(FieldStates field, boolean state) {
        switch(field) {
            case GUCS_FIELD -> {
                jList1.setEnabled(state);  // GUCS list
            }
            case CNRM_FIELD -> {
                jList2.setEnabled(state);  // CNRM list
            }
            case LOCATION_FIELD -> {
                jButton3.setEnabled(state);  // Browse
                jTextField1.setEnabled(state);  // location
            }
            case SAVE_CANCEL -> {
                jButton5.setEnabled(state);   // Save
            }
        }
    }
    
    // Disable all buttons 
    private void disableAllButtons() {
        enableField(FieldStates.GUCS_FIELD, false);
        jButton2.setEnabled(false);
        enableField(FieldStates.CNRM_FIELD, false);
        jButton4.setEnabled(false);
        enableField(FieldStates.LOCATION_FIELD, false);
        enableField(FieldStates.SAVE_CANCEL, false);
    }
    
    // Enables/Disables buttons according to stage of the process
    private void disableEnableButtons(FieldStates stage) {
        switch (stage) {
            case LOCATION_FIELD -> {
                enableField(FieldStates.GUCS_FIELD, false);
                enableField(FieldStates.CNRM_FIELD, false);
                enableField(FieldStates.LOCATION_FIELD, true);
                enableField(FieldStates.SAVE_CANCEL, false);
            }
            case SAVE_CANCEL -> {
                enableField(FieldStates.GUCS_FIELD, false);
                enableField(FieldStates.CNRM_FIELD, false);
                enableField(FieldStates.LOCATION_FIELD, true);
                enableField(FieldStates.SAVE_CANCEL, true);
            }


        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Update Scenario with GUCS/CNRM");

        jLabel1.setText("Select GUCS");
        jLabel1.setToolTipText("");

        jLabel2.setText("Select CNRM");

        jButton2.setText(">");
        jButton2.setToolTipText("Apply GUCS");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton1.setText("Cancel");
        jButton1.setToolTipText("Close form with no changes");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        jLabel3.setText("Save Location");
        jLabel3.setToolTipText("Select Location for the Scenario file");

        jButton3.setText("Browse");
        jButton3.setToolTipText("Browse to save location");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText(">");
        jButton4.setToolTipText("Apply CNRM");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("   Save  ");
        jButton5.setToolTipText("Save file to specified location");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList1ValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jList1);

        jList2.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList2ValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(jList2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addGap(47, 47, 47))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                                        .addComponent(jButton2)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jButton4))))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField1)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(28, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, 14, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(53, 53, 53)
                                .addComponent(jButton2)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton4)
                                .addGap(67, 67, 67)))))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jButton3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton5))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    // Initialize the GUCS combo box with the retrieved GUCS List
    private void initGucsList() {
        m_gucsList = WizardDriver.getHandle().getGucs();
        DefaultListModel listModel = new DefaultListModel();
       
        if (m_gucsList == null) {
            return;
        }

        for (GucsInformation gucs : m_gucsList) {
            listModel.addElement(gucs.getName());
        }
        jList1.setModel(listModel);
        
        jList1.setEnabled(true);
    }
    
    // Initialize the CNRM combo box with the retrieved CNRM List
    private void initCnrmList() {
        m_cnrmList = WizardDriver.getHandle().getCnrm();
        DefaultListModel listModel = new DefaultListModel();
                
        if (m_cnrmList == null) {
            return;
        }
        
        for (CnrmInformation cnrm : m_cnrmList) {
            listModel.addElement(cnrm.getName());
        }
        jList2.setModel(listModel);
        
        jList2.setEnabled(true);
    }
       
    // This is test code
    private void cleanupScenarios() {
        String output;

        m_webServiceKey = "";
        m_webServiceHost = "";
        List<ScenarioInformation>scenarioList = null;
        ScenarioInformation scenarioInformation = null;
        try {
            URL url = new URL("https://" + m_webServiceHost + "/scenarios" +
                              "?subscription-key=" + m_webServiceKey);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP Error code : "+ conn.getResponseCode());
            }
            
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            BufferedReader br = new BufferedReader(in);
            ObjectMapper objMapper = new ObjectMapper();
            TypeFactory typeFactory = objMapper.getTypeFactory();
            CollectionType collectionType = typeFactory.constructCollectionType(List.class, ScenarioInformation.class);
            while ((output = br.readLine()) != null) {
                scenarioList = objMapper.readValue(output, collectionType);
            }
            conn.disconnect();
            // at this point need to parse the results and add to the combo
        }
        catch (Exception e2) {
            System.out.println("Exception!"+e2.getMessage());
            //return e2.getMessage();
        }
        if (scenarioList != null) {
            for (ScenarioInformation scenario : scenarioList) {
                if (scenario.getDescription().contains("herie") || scenario.getName().contains("herie")) {
                    System.out.println("Delete scenario named:"+scenario.getName());
                    deleteScenario(scenario.getId());
                }
            }
        }

    }

    // delete scenario 
    private void deleteScenario(String id) {
        JSONObject json = new JSONObject();
        json.put("id", id);
        HttpURLConnection conn = null;
        
        try {
            URL url = new URL("https://" + m_webServiceHost + "/scenarios/"
                + id + "?subscription-key=" + m_webServiceKey);
            
            conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            
            OutputStream os = conn.getOutputStream();
            byte[] input = json.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
            if (conn.getResponseCode() != 200) {
                JOptionPane.showMessageDialog(this, 
                        "Web Service exception -- HTTP Error code : " + conn.getResponseCode());
                return;
            }
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Exception: " + e.getMessage());
        }
        if (conn != null) {
            conn.disconnect();
        }
    }

    // Next ( > ) button - Select GUCS
    // Uses the wizard driver to activate thread that will update scenario with GUCS list
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        
        if (jList1.isSelectionEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select at least one GUCS!");
            return;
        }
        WizardDriver.getHandle().startGucsUpdateScenarioThread(jList1.getSelectedIndices());
        disableAllButtons();
        initCnrmList();
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_jButton2ActionPerformed
    
    // Cancel - close form
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    // Browse button
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        String lastPath = IGCAPTproperties.getInstance().getPropertyKeyValue(IgcaptProperty.LAST_PATH);
        // Is the path empty?
        if (lastPath != null && !lastPath.isEmpty()) {
            File lastPathDir = new File(lastPath);
            if (lastPathDir.exists()) {
                chooser.setCurrentDirectory(lastPathDir);
            }
        }

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            lastPath = chooser.getSelectedFile().toString();
            jTextField1.setText(lastPath);
            ScenarioInformation scenarioInfo = WizardDriver.getHandle().getScenarioInfo();
            if (scenarioInfo == null) {
                JOptionPane.showMessageDialog(this, "Error Creating Scenario!");
                //close window??
                return;
            }
            lastPath = lastPath + File.separator + scenarioInfo.getName() + ".xml";
            IGCAPTproperties.getInstance().setPropertyKeyValue(IgcaptProperty.LAST_PATH, 
                    lastPath);            
            if (jTextField1.getText() != null) {
                disableEnableButtons(FieldStates.SAVE_CANCEL);
            }
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    // Next ( > ) button - Selected CNRM
    // Uses the wizard driver to activate thread that will update scenario with CNRM list
    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        if (jList2.isSelectionEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select at least one CNRM!");
            return;
        }        
        
        WizardDriver.getHandle().startCnrmUpdateScenarioThread(jList2.getSelectedIndices());
        jButton4.setEnabled(false);
        disableEnableButtons(FieldStates.LOCATION_FIELD);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_jButton4ActionPerformed

    // Save button
    // Uses the Wizard driver to activate the thread that will retrieve the 
    // scenario and write it to file system.
    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        if (jTextField1.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Location cannot be Empty!");
            return;
        }
 
        WizardDriver.getHandle().startSaveFileThread(jTextField1.getText());
        if (!(jTextField1.getText() == null) && !(jTextField1.getText().isEmpty()) &&
            JOptionPane.showConfirmDialog(this, "Do you want to import the Scenario now?",
                "Import Scenario?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            
            
            WizardDriver.getHandle().joinSaveFileThread();
            GDTAFImportController importController = GDTAFImportController.getInstance();
            String importFile = jTextField1.getText() + File.separator +
                    WizardDriver.getHandle().getScenarioInfo().getName() + ".xml";
            dispose();
            new ImportGdtafScenario(null, true, importFile);
        }

        dispose();
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_jButton5ActionPerformed

    // As soon as user makes selection, enable the Next button
    private void jList2ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList2ValueChanged
        jButton4.setEnabled(true);
    }//GEN-LAST:event_jList2ValueChanged

    // As soon as user makes selection, enable the Next button
    private void jList1ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList1ValueChanged
        jButton2.setEnabled(true);
    }//GEN-LAST:event_jList1ValueChanged

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
            java.util.logging.Logger.getLogger(AddToScenarioWizard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AddToScenarioWizard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AddToScenarioWizard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AddToScenarioWizard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                // Scenario hard coded for testing
                ScenarioInformation scenario = new ScenarioInformation();
                scenario.setId("_0f39c303-9794-41c7-a9c2-f798e5f8a9b2");
                scenario.setName("ForDougStaging");
                scenario.setSourceId("_0f39c303-9794-41c7-a9c2-f798e5f8a9b2");
                AddToScenarioWizard dialog = new AddToScenarioWizard(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JList<String> jList1;
    private javax.swing.JList<String> jList2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
