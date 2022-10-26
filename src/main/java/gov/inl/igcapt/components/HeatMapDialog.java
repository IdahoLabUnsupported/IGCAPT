package gov.inl.igcapt.components;

import java.awt.Color;
import org.openstreetmap.gui.jmapviewer.IGCAPTgui;
import org.openstreetmap.gui.jmapviewer.IGCAPTproperties;

/**
 *
 * @author CHE
 */
public class HeatMapDialog extends javax.swing.JDialog {

    private final static int GRID_SIZE_DEFAULT = 10;
    private final static int KERNEL_RADIUS_SIZE_DEFAULT = 100;
    private final static Heatmap.KernelTypes KERNEL_TYPE_DEFAULT = Heatmap.KernelTypes.Quartic;
    private final static int START_COLOR_DEFAULT = 120;
    private final static int END_COLOR_DEFAULT = 240;
    private final static String GRID_SIZE_PROPERTY_NAME = "heatmapGridSize";
    private final static String KERNEL_TYPE_PROPERTY_NAME = "heatmapKernelType";
    private final static String KERNEL_RADIUS_PROPERTY_NAME = "heatmapKernelRadius";
    private final static String START_COLOR_PROPERTY_NAME = "heatmapStartColor";
    private final static String END_COLOR_PROPERTY_NAME = "heatmapEndColor";
    private final java.awt.Frame parent;
    private boolean modal=true;
    private int m_savedGridSize;
    private int m_savedKernelRadius;
    private Heatmap.KernelTypes m_savedKernelType;
    private int m_savedStartColor;
    private int m_savedEndColor;

    /**
     * Creates new form HeatMapDialog
     */
    public HeatMapDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.parent = parent;
        this.modal = modal;
        initComponents();
        
        String[] kernelTypes = new String[Heatmap.KernelTypes.values().length];
        int index = 0;
        // setup array for comboBox
        for (Heatmap.KernelTypes type : Heatmap.KernelTypes.values()) {
            kernelTypes[index++] = type.name();
        }
        getProperties();
        // Set the values to what was saved in properties
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(kernelTypes));
        jSpinner2.setValue(m_savedGridSize);
        jSpinner1.setValue(m_savedKernelRadius);
        jComboBox1.setSelectedIndex(m_savedKernelType.ordinal());
        this.setTitle("Heatmap Parameters");
    }
    
    // close the window.
    private void doClose() {
        setVisible(false);
        dispose();
    }
    
    private void getProperties() {
        String heatmapGridStr = IGCAPTproperties.getInstance().getPropertyKeyValue(GRID_SIZE_PROPERTY_NAME);
        String heatmapKernelTypeStr = IGCAPTproperties.getInstance().getPropertyKeyValue(KERNEL_TYPE_PROPERTY_NAME);
        String heatmapKernelRadiusStr = IGCAPTproperties.getInstance().getPropertyKeyValue(KERNEL_RADIUS_PROPERTY_NAME);
        String heatmapStartColorStr = IGCAPTproperties.getInstance().getPropertyKeyValue(START_COLOR_PROPERTY_NAME);
        String heatmapEndColorStr = IGCAPTproperties.getInstance().getPropertyKeyValue(END_COLOR_PROPERTY_NAME);
        
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
    
    private void savePropertyValues() {
        IGCAPTproperties.getInstance().setPropertyKeyValue(GRID_SIZE_PROPERTY_NAME, String.valueOf(m_savedGridSize));
        IGCAPTproperties.getInstance().setPropertyKeyValue(KERNEL_TYPE_PROPERTY_NAME, m_savedKernelType.toString());
        IGCAPTproperties.getInstance().setPropertyKeyValue(KERNEL_RADIUS_PROPERTY_NAME, String.valueOf(m_savedKernelRadius));
        IGCAPTproperties.getInstance().setPropertyKeyValue(START_COLOR_PROPERTY_NAME, String.valueOf(m_savedStartColor));
        IGCAPTproperties.getInstance().setPropertyKeyValue(END_COLOR_PROPERTY_NAME, String.valueOf(m_savedEndColor));
    }
    
    //
    private void doGenerate() {
        if (jTextField1.getToolTipText() == null || jTextField2.getToolTipText() == null) {
            //Put up an error telling user to pick color
            System.out.println("Must select start and end colors");
            return;
        }
        IGCAPTgui igcaptGui = (IGCAPTgui)parent;
        igcaptGui.SetHeatmap(null);
        m_savedGridSize = (Integer)jSpinner2.getValue(); 
        m_savedKernelType = Heatmap.KernelTypes.values()[jComboBox1.getSelectedIndex()];
        m_savedKernelRadius = (Integer)jSpinner1.getValue();
        
        igcaptGui.SetHeatmap(new Heatmap(m_savedGridSize, m_savedKernelType, m_savedKernelRadius, 
               Float.valueOf(jTextField1.getToolTipText()), Float.valueOf(jTextField2.getToolTipText()) ));
        float startColor = Float.parseFloat(jTextField1.getToolTipText());
        float endColor = Float.parseFloat(jTextField2.getToolTipText());
        m_savedStartColor = Math.round(startColor*360);
        m_savedEndColor = Math.round(endColor*360);
        
        savePropertyValues();
    }
    
    private void resetToDefaults() {
        jSpinner2.setValue(GRID_SIZE_DEFAULT);
        jSpinner1.setValue(KERNEL_RADIUS_SIZE_DEFAULT);
        jComboBox1.setSelectedIndex(KERNEL_TYPE_DEFAULT.ordinal());
        jTextField1.setToolTipText(Float.toString(START_COLOR_DEFAULT/360));
        jTextField2.setToolTipText(Float.toString(END_COLOR_DEFAULT/360));
        
        // saturation & intensity always 1
        jTextField1.setBackground(new Color(Color.HSBtoRGB(START_COLOR_DEFAULT/360f, 1, 1)));
        jTextField2.setBackground(new Color(Color.HSBtoRGB(END_COLOR_DEFAULT/360f, 1, 1)));
    }

    private void resetToLastRun() {
        jSpinner2.setValue(m_savedGridSize);
        jSpinner1.setValue(m_savedKernelRadius);
        jComboBox1.setSelectedIndex(m_savedKernelType.ordinal());
        jTextField1.setToolTipText(Float.toString(m_savedStartColor/360));
        jTextField2.setToolTipText(Float.toString(m_savedEndColor/360));
        jTextField1.setBackground(new Color(Color.HSBtoRGB(m_savedStartColor/360f, 1, 1)));
        jTextField2.setBackground(new Color(Color.HSBtoRGB(m_savedEndColor/360f, 1, 1)));
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
        jButton5 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jButton6 = new javax.swing.JButton();
        jTextField2 = new javax.swing.JTextField();
        hsvColorPanel2 = new gov.inl.igcapt.components.HsvColorPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(402, 508));

        jLabel1.setText("Kernel Type");

        jLabel2.setText("Kernel Radius");

        jButton1.setText("Generate");
        jButton1.setToolTipText("Generate the Heatmap");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGenerateActionPerformed(evt);
            }
        });

        jButton2.setText("Close");
        jButton2.setToolTipText("Close Parameter Dialog");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel3.setText("Grid Size");

        jButton3.setText("Defaults");
        jButton3.setToolTipText("Reset parameters to Defaults");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Reset");
        jButton4.setToolTipText("Reset Parameters to last Generated heatmap");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel4.setText("(Default: Quartic)");

        jLabel5.setText("(Default: 100)");

        jLabel6.setText("(Default: 10)");

        jButton5.setLabel("Set Start Color");
        jButton5.setMargin(new java.awt.Insets(1, 1, 1, 1));
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jTextField1.setEditable(false);
        jTextField1.setColumns(4);

        jButton6.setLabel("Set End Color");
        jButton6.setMargin(new java.awt.Insets(1, 1, 1, 1));
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jTextField2.setEditable(false);
        jTextField2.setColumns(4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(55, 55, 55)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jButton1)
                                    .addComponent(jButton3))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButton2)
                                    .addComponent(jButton4)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                                    .addComponent(jLabel6)))))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(hsvColorPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel3});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButton1, jButton2, jButton3, jButton4});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(42, 42, 42)
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
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton4)
                    .addComponent(jButton3))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton5)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton6)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(hsvColorPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButton1, jButton2, jButton3, jButton4});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jComboBox1, jLabel1, jLabel2, jLabel3, jSpinner1, jSpinner2});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButton5, jButton6});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonGenerateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGenerateActionPerformed
        doGenerate();
    }//GEN-LAST:event_jButtonGenerateActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        doClose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        resetToDefaults();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        resetToLastRun();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        float hue = hsvColorPanel2.getSelectedHueValue();

        jTextField1.setToolTipText(Float.toString(hue));
        // Use 1 for saturation and intensity
        jTextField1.setBackground(new Color(Color.HSBtoRGB(hue, 1, 1)));
        
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        float hue = hsvColorPanel2.getSelectedHueValue();
        jTextField2.setToolTipText(Float.toString(hue));
        // Use 1 for satruation and intensity
        jTextField2.setBackground(new Color(Color.HSBtoRGB(hue, 1, 1)));
    }//GEN-LAST:event_jButton6ActionPerformed

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
    private gov.inl.igcapt.components.HsvColorPanel hsvColorPanel2;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JSpinner jSpinner2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}
