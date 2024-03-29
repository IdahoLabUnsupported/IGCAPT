package gov.inl.igcapt.components;

import gov.inl.igcapt.graph.GraphManager;
import java.awt.Color;
import javax.swing.JOptionPane;
import gov.inl.igcapt.view.IGCAPTgui;
import gov.inl.igcapt.properties.IGCAPTproperties;
import gov.inl.igcapt.properties.IGCAPTproperties.IgcaptProperty;

/**
 *
 * @author CHE
 */
public class HeatMapDialog extends javax.swing.JDialog {

    private final static int GRID_SIZE_DEFAULT = 10;
    private final static int KERNEL_RADIUS_SIZE_DEFAULT = 100;
    private final static Heatmap.KernelTypes KERNEL_TYPE_DEFAULT = Heatmap.KernelTypes.Quartic;
    private final static int START_COLOR_DEFAULT = 120;  // Green
    private final static int END_COLOR_DEFAULT = 240;   // Blue
    private final java.awt.Frame parent;
    private boolean modal=true;
    private int m_savedGridSize;
    private int m_savedKernelRadius;
    private Heatmap.KernelTypes m_savedKernelType;
    private int m_savedStartColor;
    private int m_savedEndColor;
    private int m_startColor = -1;
    private int m_endColor = -1;

    /**
     * Creates new form HeatMapDialog
     */
    public HeatMapDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.parent = parent;
        this.modal = modal;
        initComponents();
        
        restoreValues();
        this.setTitle("Heatmap Parameters");
    }
    
    // restore Values upon entering form
    private void restoreValues() {
        getProperties();
        // Set the values to what was saved in properties
        String[] kernelTypes = new String[Heatmap.KernelTypes.values().length];
        int index = 0;
        // setup array for comboBox
        for (Heatmap.KernelTypes type : Heatmap.KernelTypes.values()) {
            kernelTypes[index++] = type.name();
        }
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(kernelTypes));
        jSpinner2.setValue(m_savedGridSize);
        jSpinner1.setValue(m_savedKernelRadius);
        jComboBox1.setSelectedIndex(m_savedKernelType.ordinal());
        jTextField1.setBackground(new Color(Color.HSBtoRGB(m_savedStartColor/360f, 1, 1)));
        jTextField2.setBackground(new Color(Color.HSBtoRGB(m_savedEndColor/360f, 1, 1)));
        m_startColor = m_savedStartColor;
        m_endColor = m_savedEndColor;
    }

    // close the window.
    private void doClose() {
        setVisible(false);
        dispose();
    }
    
    // get the properties from the property file
    private void getProperties() {
        String heatmapGridStr = IGCAPTproperties.getInstance().getPropertyKeyValue(IgcaptProperty.HEATMAP_GRID_SIZE);
        String heatmapKernelTypeStr = IGCAPTproperties.getInstance().getPropertyKeyValue(IgcaptProperty.HEATMAP_KERNEL_TYPE);
        String heatmapKernelRadiusStr = IGCAPTproperties.getInstance().getPropertyKeyValue(IgcaptProperty.HEATMAP_KERNEL_RADIUS);
        String heatmapStartColorStr = IGCAPTproperties.getInstance().getPropertyKeyValue(IgcaptProperty.HEATMAP_START_COLOR);
        String heatmapEndColorStr = IGCAPTproperties.getInstance().getPropertyKeyValue(IgcaptProperty.HEATMAP_END_COLOR);
        
        if (heatmapGridStr == null) {
            m_savedGridSize = GRID_SIZE_DEFAULT;
        }
        else {
            m_savedGridSize = Integer.parseInt(heatmapGridStr);
        }
        if (heatmapKernelTypeStr == null) {
            m_savedKernelType = KERNEL_TYPE_DEFAULT;
        }
        else {
            m_savedKernelType = Heatmap.KernelTypes.valueOf(heatmapKernelTypeStr);  

        }
        if (heatmapKernelRadiusStr == null) {
            m_savedKernelRadius = KERNEL_RADIUS_SIZE_DEFAULT;
        }
        else {
            m_savedKernelRadius = Integer.parseInt(heatmapKernelRadiusStr);
        }
        if (heatmapStartColorStr == null) {
            m_savedStartColor = START_COLOR_DEFAULT;
        }
        else {
            m_savedStartColor = Integer.parseInt(heatmapStartColorStr);
        }
        if (heatmapEndColorStr == null) {
            m_savedEndColor = END_COLOR_DEFAULT;
        }
        else {
            m_savedEndColor = Integer.parseInt(heatmapEndColorStr);
        }
    }
      
    // save values to properties file
    private void saveValues() {
        m_savedGridSize = (Integer)jSpinner2.getValue(); 
        m_savedKernelType = Heatmap.KernelTypes.values()[jComboBox1.getSelectedIndex()];
        m_savedKernelRadius = (Integer)jSpinner1.getValue();
        m_savedStartColor = m_startColor;
        m_savedEndColor = m_endColor;

        IGCAPTproperties.getInstance().setPropertyKeyValue(IgcaptProperty.HEATMAP_GRID_SIZE, String.valueOf(m_savedGridSize));
        IGCAPTproperties.getInstance().setPropertyKeyValue(IgcaptProperty.HEATMAP_KERNEL_TYPE, m_savedKernelType.toString());
        IGCAPTproperties.getInstance().setPropertyKeyValue(IgcaptProperty.HEATMAP_KERNEL_RADIUS, String.valueOf(m_savedKernelRadius));
        IGCAPTproperties.getInstance().setPropertyKeyValue(IgcaptProperty.HEATMAP_START_COLOR, String.valueOf(m_savedStartColor));
        IGCAPTproperties.getInstance().setPropertyKeyValue(IgcaptProperty.HEATMAP_END_COLOR, String.valueOf(m_savedEndColor));
    }
    
    // Generate the heat map
    private void doGenerate() {
        if (m_startColor == -1 || m_endColor == -1) {
            //Put up message telling user to pick color
            JOptionPane.showMessageDialog(this, "Start and End Colors must be selected!");
            return;
        }
        IGCAPTgui igcaptGui = (IGCAPTgui)parent;
        GraphManager graphManager = GraphManager.getInstance();
        igcaptGui.SetHeatmap(null);       
        saveValues();
        igcaptGui.SetHeatmap(new Heatmap(m_savedGridSize, m_savedKernelType, m_savedKernelRadius, 
               Float.valueOf(m_startColor)/360f, Float.valueOf(m_endColor)/360f));
    }
    
    // Reset parameters to default values
    private void resetToDefaults() {
        jSpinner2.setValue(GRID_SIZE_DEFAULT);
        jSpinner1.setValue(KERNEL_RADIUS_SIZE_DEFAULT);
        jComboBox1.setSelectedIndex(KERNEL_TYPE_DEFAULT.ordinal());
        m_startColor = START_COLOR_DEFAULT;
        m_endColor = END_COLOR_DEFAULT;
        // saturation & intensity always 1
        jTextField1.setBackground(new Color(Color.HSBtoRGB(START_COLOR_DEFAULT/360f, 1, 1)));
        jTextField2.setBackground(new Color(Color.HSBtoRGB(END_COLOR_DEFAULT/360f, 1, 1)));
        jButton1.setEnabled(true);
    }

    // Reset parameters to last run values
    private void resetToLastRun() {
        jSpinner2.setValue(m_savedGridSize);
        jSpinner1.setValue(m_savedKernelRadius);
        jComboBox1.setSelectedIndex(m_savedKernelType.ordinal());
        if (m_savedStartColor > -1) {
            m_startColor = m_savedStartColor;
            m_endColor = m_savedEndColor;
            jTextField1.setBackground(new Color(Color.HSBtoRGB(m_savedStartColor/360f, 1, 1)));
            jTextField2.setBackground(new Color(Color.HSBtoRGB(m_savedEndColor/360f, 1, 1)));
            jButton1.setEnabled(true);
        }
        else {
            jButton1.setEnabled(false);
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
        jComboBox1 = new javax.swing.JComboBox<>();
        jSpinner1 = new javax.swing.JSpinner();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jSpinner2 = new javax.swing.JSpinner();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(380, 390));
        setResizable(false);

        jLabel1.setText("Kernel Type");

        jLabel2.setText("Kernel Radius");

        jButton1.setText("Generate");
        jButton1.setToolTipText("Generate the Heatmap");
        jButton1.setActionCommand("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGenerateActionPerformed(evt);
            }
        });

        jButton2.setText("Cancel");
        jButton2.setToolTipText("Close Parameter Dialog");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel3.setText("Grid Size");

        jButton3.setText("Restore Defaults");
        jButton3.setToolTipText("Reset parameters to Defaults");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Reset to Last");
        jButton4.setToolTipText("Reset Parameters to last Generated heatmap");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel4.setText("(Default: Quartic)");

        jLabel5.setText("(Default: 100)");

        jLabel6.setText("(Default: 10)");

        jTextField1.setEditable(false);
        jTextField1.setColumns(4);

        jTextField2.setEditable(false);
        jTextField2.setColumns(4);

        jLabel7.setText("Start Color");
        jLabel7.setToolTipText("High Data Density Color");

        jLabel8.setText("End Color");
        jLabel8.setToolTipText("Low  Data Density Color");

        jButton7.setText("Select Colors");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jLabel9.setText("(Default: Green)");

        jLabel10.setText("(Default: Blue)");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton7)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addGap(25, 25, 25)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jSpinner1)
                            .addComponent(jSpinner2))
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4)
                            .addComponent(jLabel6)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10))))
                .addContainerGap(100, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel3});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButton1, jButton2, jButton3, jButton4});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jSpinner2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6))
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(jButton4))
                .addGap(18, 18, 18)
                .addComponent(jButton7)
                .addGap(9, 9, 9)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField1)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(9, 9, 9)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField2)
                    .addComponent(jLabel10)
                    .addComponent(jLabel8))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton1))
                .addGap(26, 26, 26))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButton1, jButton2, jButton3, jButton4});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jComboBox1, jLabel1, jLabel2, jLabel3, jSpinner1, jSpinner2});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Generate button
    private void jButtonGenerateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGenerateActionPerformed
        saveValues();
        doGenerate();
    }//GEN-LAST:event_jButtonGenerateActionPerformed

    // Cancel button
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        saveValues();
        doClose();
    }//GEN-LAST:event_jButton2ActionPerformed

    // Restore Defaults button
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        resetToDefaults();
    }//GEN-LAST:event_jButton3ActionPerformed

    // Reset to last button
    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        resetToLastRun();
    }//GEN-LAST:event_jButton4ActionPerformed

    // Select Colors button
    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        ColorPickDialog colorPickDialog = new ColorPickDialog(parent, true);
        if (!colorPickDialog.cancelled()) {
            m_startColor = colorPickDialog.getStartColor();
            m_endColor = colorPickDialog.getEndColor();
            jTextField1.setBackground(new Color(Color.HSBtoRGB(m_startColor/360f, 1, 1)));
            jTextField2.setBackground(new Color(Color.HSBtoRGB(m_endColor/360f, 1, 1)));
        }
        if (m_startColor > -1 && m_endColor > -1) {
            jButton1.setEnabled(true);
        }       
        saveValues();
    }//GEN-LAST:event_jButton7ActionPerformed

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
            java.util.logging.Logger.getLogger(HeatMapDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HeatMapDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HeatMapDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HeatMapDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                HeatMapDialog dialog = new HeatMapDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton jButton7;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JSpinner jSpinner2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}
