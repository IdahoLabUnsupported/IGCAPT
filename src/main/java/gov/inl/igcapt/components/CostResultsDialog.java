/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package gov.inl.igcapt.components;

import java.awt.Component;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author FRAZJD
 */
public class CostResultsDialog extends javax.swing.JDialog {

    public class TableCellRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
              boolean isSelected, boolean hasFocus, int row, int col) {

            Component c = super.getTableCellRendererComponent(table, value,
                     isSelected, hasFocus, row, col);
            
            // The last row/col will contain the sum of the cost. Highlight it.
            if ((col == 0 || col == 5 || col == 7 || col == 9) && row == table.getRowCount() - 1) {
                
                c.setFont(this.getFont().deriveFont(Font.BOLD));
            }

            return c;
        }       
    }
    /**
     * Creates new form CostResultsDialog
     * @param parent
     * @param modal
     */
    public CostResultsDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        SetColumnWidth(0, 100);
        SetColumnWidth(1, 50);
        SetColumnWidth(2, 100);
        SetColumnWidth(3, 100);
        SetColumnWidth(4, 100);
        SetColumnWidth(5, 100);
        SetColumnWidth(6, 100);
        SetColumnWidth(7, 100);
        SetColumnWidth(8, 100);
        SetColumnWidth(9, 100);
                  
        var tableCellRenderer = new TableCellRenderer();
        tableCellRenderer.setHorizontalAlignment(JLabel.RIGHT);
        resultsTable.setDefaultRenderer(String.class, tableCellRenderer);
        tableCellRenderer = new TableCellRenderer();
        tableCellRenderer.setHorizontalAlignment(JLabel.LEFT);
        resultsTable.getColumnModel().getColumn(0).setCellRenderer(tableCellRenderer);
        
        
    }
    
    private int ZeroIfNegative(int value) {
        int returnval = value;
        
        if (value < 0) {
            returnval = 0;
        }
        
        return returnval;
    }

    public void UpdateResults(String analysisTimeStr, ArrayList<CostAnalysisEntry> resultsData){
        
        if (analysisTimeStr != null && !analysisTimeStr.isBlank()) {
            analysisTimeTxt.setText(analysisTimeStr);
        }

        if (resultsData!= null && !resultsData.isEmpty()) {
            DefaultTableModel tableModel = (DefaultTableModel)resultsTable.getModel();

            // Remove existing rows
            var numRows = tableModel.getRowCount();

            for (int i=numRows-1; i>=0; i--) {
                tableModel.removeRow(i);
            }
        
            int capexProjectedCostSum = 0;
            int capexActualCostSum = 0;
            int opexProjectedCostSum = 0;
            int opexActualCostSum = 0;
            
            // Iterate through the results
            for (var resultEntry : resultsData) {
                
                capexProjectedCostSum += ZeroIfNegative(resultEntry.getCapexProjectedTotal());
                capexActualCostSum += ZeroIfNegative(resultEntry.getCapexActualTotal());
                opexProjectedCostSum += ZeroIfNegative(resultEntry.getOpexPerYearProjectedTotal());
                opexActualCostSum += ZeroIfNegative(resultEntry.getOpexPerYearActualTotal());
                String quantityStr = Integer.toString(resultEntry.getQuantity());
                String capexUnitProjected = "$" + ZeroIfNegative(resultEntry.getCapexUnitProjected()) + ".00";
                String capexProjectedTotal = "$" + ZeroIfNegative(resultEntry.getCapexProjectedTotal()) + ".00";
                String capexUnitActual = "$" + ZeroIfNegative(resultEntry.getCapexUnitActual()) + ".00";
                String capexActualTotal = "$" + ZeroIfNegative(resultEntry.getCapexActualTotal()) + ".00";
                String opexUnitProjected = "$" + ZeroIfNegative(resultEntry.getOpexPerYearUnitProjected()) + ".00";
                String opexProjectedTotal = "$" + ZeroIfNegative(resultEntry.getOpexPerYearProjectedTotal()) + ".00";
                String opexUnitActual = "$" + ZeroIfNegative(resultEntry.getOpexPerYearUnitActual()) + ".00";
                String opexActualTotal = "$" + ZeroIfNegative(resultEntry.getOpexPerYearActualTotal()) + ".00";
                Object[] rowData = {resultEntry.getComponentName(), quantityStr, capexUnitProjected, capexProjectedTotal,
                                                                                 capexUnitActual, capexActualTotal,
                                                                                 opexUnitProjected, opexProjectedTotal,
                                                                                 opexUnitActual, opexActualTotal};
                tableModel.addRow(rowData);
            }
            
            Object[] rowData = {"Total", null, null, "$" + capexProjectedCostSum + ".00", null,
                "$" + capexActualCostSum + ".00", null, "$" + opexProjectedCostSum + ".00", null, "$" + opexActualCostSum + ".00"};
            tableModel.addRow(rowData);
            
            // Hide projected columns that are zero total. Hide both unit and total if total is zero.
            if (capexProjectedCostSum <= 0) {
                resultsTable.getColumnModel().getColumn(2).setMinWidth(0);
                resultsTable.getColumnModel().getColumn(2).setMaxWidth(0);
                resultsTable.getColumnModel().getColumn(3).setMinWidth(0);
                resultsTable.getColumnModel().getColumn(3).setMaxWidth(0);
            }
            else {
                SetColumnWidth(2, 100);
                SetColumnWidth(3, 100);
            }
            
            if (opexProjectedCostSum <= 0) {
                resultsTable.getColumnModel().getColumn(6).setMinWidth(0);
                resultsTable.getColumnModel().getColumn(6).setMaxWidth(0);
                resultsTable.getColumnModel().getColumn(7).setMinWidth(0);
                resultsTable.getColumnModel().getColumn(7).setMaxWidth(0);                
            }
            else {
                SetColumnWidth(6, 100);
                SetColumnWidth(7, 100);
            }
        }
    }
    private void SetColumnWidth(int column, int width) {
                
        var columnModel = resultsTable.getColumnModel();
        columnModel.getColumn(column).setMaxWidth(2*width);
        columnModel.getColumn(column).setMinWidth(width);
        columnModel.getColumn(column).setPreferredWidth(width);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        resultsTable = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        analysisTimeTxt = new javax.swing.JTextPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cost Analysis Results");

        resultsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Component Type", "Quantity", "CAPEX Estimated Unit Cost", "CAPEX Estimated Total", "CAPEX Actual Unit Cost", "CAPEX Actual Total", "OPEX Estimated Unit Cost Per Year", "OPEX Estimated Total Per Year", "OPEX Actual Unit Cost Per Year", "OPEX Actual Total Per Year"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        resultsTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        resultsTable.setInheritsPopupMenu(true);
        resultsTable.setName("costResultsTable"); // NOI18N
        resultsTable.setShowGrid(true);
        resultsTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(resultsTable);
        resultsTable.getAccessibleContext().setAccessibleName("resultsTable");

        analysisTimeTxt.setEditable(false);
        analysisTimeTxt.setText("Time, analysis");
        analysisTimeTxt.setFocusable(false);
        jScrollPane3.setViewportView(analysisTimeTxt);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 946, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE)
                .addContainerGap())
        );

        getAccessibleContext().setAccessibleName("costAnalysisResults");

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CostResultsDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(() -> {
            ResultsDialog dialog = new ResultsDialog(new javax.swing.JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }
            });
            dialog.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextPane analysisTimeTxt;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable resultsTable;
    // End of variables declaration//GEN-END:variables
}
