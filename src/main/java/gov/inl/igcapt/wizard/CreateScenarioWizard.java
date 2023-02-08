/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package gov.inl.igcapt.wizard;

import java.awt.Frame;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import javax.swing.JFileChooser;
import org.json.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import javax.swing.JOptionPane;
import org.openstreetmap.gui.jmapviewer.IGCAPTgui;
import gov.inl.igcapt.properties.IGCAPTproperties;

/**
 *
 * @author CHE
 */
public class CreateScenarioWizard extends javax.swing.JDialog {

    private Frame localParent;
    private String m_webServiceHost;
    private String m_webServiceKey;
    /**
     * Creates new form ScenarioWizard
     */
    public CreateScenarioWizard(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        localParent = parent;
        m_webServiceHost = IGCAPTproperties.getInstance().getPropertyKeyValue("WebServiceHost");
        m_webServiceKey = IGCAPTproperties.getInstance().getPropertyKeyValue("WebServiceKey");
        if (m_webServiceHost == null || m_webServiceKey == null) {
            JOptionPane.showMessageDialog(this, "You must Initialize the Web Service Connection" +
                    " before running the wizard!\n (File->Initialize Connection)");
            return;
        }
        initComponents();
        jButton2.setEnabled(false);
        this.setVisible(true);
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
        jButton1 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("New Scenario Wizard");

        jLabel1.setText("Specify CIM/RDF:");
        jLabel1.setToolTipText("");

        jButton1.setText("Browse");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTextField1.setName(""); // NOI18N
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField1KeyTyped(evt);
            }
        });

        jLabel2.setText("Specify Scenario name:");

        jTextField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField2KeyTyped(evt);
            }
        });

        jButton2.setText("Next");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Cancel");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel3.setText("Description:");

        jTextField3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField3KeyTyped(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jTextField1)
                            .addComponent(jTextField3, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton3))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // check the three text fields and enable/disable Next
    private void enableNext() {
        if (!jTextField2.getText().isEmpty() && !jTextField1.getText().isEmpty() && 
            !jTextField3.getText().isEmpty()) {
            jButton2.setEnabled(true);
        }
        else {
            jButton2.setEnabled(false);
        }
    }
    
    // Select the CNRM file
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        JFileChooser chooser = new JFileChooser();
        String lastPath = IGCAPTgui.getInstance().getLastPath(); 
        if (lastPath != null && !lastPath.isEmpty()) {
            File lastPath1 = new File(lastPath);
            if (lastPath1.exists()) {
                chooser.setCurrentDirectory(lastPath1);
            }
        }
        
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            jTextField1.setText(chooser.getSelectedFile().toString());
            enableNext();
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    // Scenario name text field
    private void jTextField2KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField2KeyTyped
        enableNext();
    }//GEN-LAST:event_jTextField2KeyTyped

    private String getCimrdf(String filename) {
        String cimRdfEncodedFile = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(filename);
            byte[] bytes = new byte[(int)fileInputStream.available()];
            fileInputStream.read(bytes);
            cimRdfEncodedFile = Base64.getEncoder().encodeToString(bytes);
            fileInputStream.close();
        }
        catch (Exception e) {
            System.out.println("Exception:"+e.getMessage());
        }
        return cimRdfEncodedFile;
    }
        
    // Next button in the wizard
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // Call Rest service to create scenario
        JSONObject json = new JSONObject();
        json.put("name", jTextField2.getText());
        json.put("topology", getCimrdf(jTextField1.getText()));
        if (jTextField3.getText() != null && jTextField3.getText().trim().length() > 0) {
            json.put("description", jTextField3.getText().trim());
        }
        
        ScenarioInformation scenInfo;
        try {
            
            URL url = new URL("https://" + m_webServiceHost + "/core/scenarios?subscription-key="+m_webServiceKey);
            
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            
            OutputStream os = conn.getOutputStream();
            byte[] input = json.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP Error code : "+ conn.getResponseCode());
            }       
                        
            InputStreamReader isr = new InputStreamReader(conn.getInputStream(), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            ObjectMapper objMapper = new ObjectMapper();
            
            String response = br.readLine();
            scenInfo = objMapper.readValue(response, ScenarioInformation.class);
                
            conn.disconnect();
        }
        catch (Exception e2) {
            System.out.println("Exception:"+e2.getMessage());
            return;
        }
        

        dispose();
        //Now go to next step in Wizard
        new AddToScenarioWizard(localParent, true, scenInfo);

    }//GEN-LAST:event_jButton2ActionPerformed

    // Cancel button - do nothing close window
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        dispose();
    }//GEN-LAST:event_jButton3ActionPerformed
 
    // Description text field - currently not allowing empty but web service will accept empty
    private void jTextField3KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField3KeyTyped
        enableNext();
    }//GEN-LAST:event_jTextField3KeyTyped

    // CIM/RDF filepath text field
    private void jTextField1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyTyped
        enableNext();
    }//GEN-LAST:event_jTextField1KeyTyped

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
            java.util.logging.Logger.getLogger(CreateScenarioWizard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CreateScenarioWizard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CreateScenarioWizard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CreateScenarioWizard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                CreateScenarioWizard dialog = new CreateScenarioWizard(new javax.swing.JFrame(), true);
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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    // End of variables declaration//GEN-END:variables
}
