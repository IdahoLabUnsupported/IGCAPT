/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package gov.inl.igcapt.components;

import edu.uci.ics.jung.graph.Graph;
import gov.inl.igcapt.graph.GraphManager;
import gov.inl.igcapt.graph.SgEdge;
import gov.inl.igcapt.graph.SgNodeInterface;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author FRAZJD
 */
public class ResultsDialog extends javax.swing.JDialog {

    public class TableCellRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
              boolean isSelected, boolean hasFocus, int row, int col) {

            Component c = super.getTableCellRendererComponent(table, value,
                     isSelected, hasFocus, row, col);
            Object valueAt = table.getModel().getValueAt(table.convertRowIndexToModel(row), table.convertColumnIndexToModel(col));

            if (col == table.getColumnCount() - 1) {
                
                if (valueAt instanceof Double cellValueD) {
                    
                    if (SgEdge.getHighUtilizationLimit() <= cellValueD) {
                        c.setForeground(Color.RED);
                        c.setBackground(Color.WHITE);                        
                    }
                    else if (SgEdge.getMediumUtilizationLimit() <= cellValueD) {
                        c.setForeground(Color.ORANGE);
                        c.setBackground(Color.WHITE);      
                    }
                    else if (cellValueD > 0.0f) {
                        c.setForeground(Color.GREEN);
                        c.setBackground(Color.WHITE);      
                    }
                    else {
                        c.setForeground(Color.BLACK);
                        c.setBackground(Color.WHITE);                
                    }
                }
            }
            else {
                c.setForeground(Color.BLACK);
                c.setBackground(Color.WHITE);                
            }

            return c;
        }       
    }
    /**
     * Creates new form ResultsDialog
     */
    public ResultsDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        SetColumnWidth(0, 115);
        SetColumnWidth(1, 130);
        SetColumnWidth(2, 130);
        SetColumnWidth(3, 190);
        SetColumnWidth(4, 190);
        SetColumnWidth(5, 190);
        
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(resultsTable.getModel());
        resultsTable.setRowSorter(sorter);
        
        List<RowSorter.SortKey> sortKeys = new ArrayList<>(4);
        sortKeys.add(new RowSorter.SortKey(5, SortOrder.DESCENDING));
        sorter.setSortKeys(sortKeys);
        
        resultsTable.setDefaultRenderer(Double.class, new TableCellRenderer());
    }

    public void UpdateResults(String analysisTimeStr){
        
        if (analysisTimeStr != null && !analysisTimeStr.isBlank() && !analysisTimeStr.isEmpty()) {
            analysisTimeTxt.setText(analysisTimeStr);
        }
        
        Graph graph = null;
        var graphManager = GraphManager.getInstance();
        
        if (graphManager != null) {
            graph = graphManager.getGraph();
        }
        
        // Clear old graph results.
        if (graph != null && graph.getVertexCount() > 0) {
        
            DefaultTableModel tableModel = (DefaultTableModel)resultsTable.getModel();

            // Remove existing rows
            var numRows = tableModel.getRowCount();

            for (int i=numRows-1; i>=0; i--) {
                tableModel.removeRow(i);
            }
        
            // Iterate through edges
            for (var edge : graph.getEdges()) {
                var lEdge = (SgEdge)edge;

                if (lEdge != null) {
                    String edgeName = lEdge.getName();
                    double edgeRate = lEdge.getEdgeRate();
                    double utilization = lEdge.getUtilization();
                    double transRate = lEdge.getCalcTransRate();
                    edgeRate = Math.round(edgeRate*1000)/1000.0;
                    utilization = Math.round(utilization*100000)/1000.0;
                    transRate = Math.round(transRate*1000)/1000.0;
                    
                    edu.uci.ics.jung.graph.util.Pair<SgNodeInterface> endPts = graph.getEndpoints(lEdge);
                    String end1Name = endPts.getFirst().getName();
                    String end2Name = endPts.getSecond().getName();
                    
                    Object[] rowData = {edgeName, end1Name, end2Name, edgeRate, transRate, utilization};
                    tableModel.addRow(rowData);
                }
            }
        }
    }
    private void SetColumnWidth(int column, int width) {
                
        var columnModel = resultsTable.getColumnModel();
        columnModel.getColumn(column).setMaxWidth(width);
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
        setTitle("Analysis Results");
        setAlwaysOnTop(true);
        setResizable(false);

        resultsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Edge ID", "From Node", "To Node", "Bandwidth Capacity (kbits/sec)", "Bandwidth Utilization (kbits/sec)", "Utilization Percentage"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        resultsTable.setName("resultsTable"); // NOI18N
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
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 491, Short.MAX_VALUE)
                .addContainerGap())
        );

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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ResultsDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ResultsDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ResultsDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ResultsDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ResultsDialog dialog = new ResultsDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JTextPane analysisTimeTxt;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable resultsTable;
    // End of variables declaration//GEN-END:variables
}
