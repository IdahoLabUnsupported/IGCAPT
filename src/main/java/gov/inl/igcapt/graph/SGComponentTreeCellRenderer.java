/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.igcapt.graph;

import gov.inl.igcapt.components.DataModels.SgComponentData;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *(c) 2018 BATTELLE ENERGY ALLIANCE, LLC
 *ALL RIGHTS RESERVED 
 *
 * @author kur
 */
public class SGComponentTreeCellRenderer implements TreeCellRenderer {

    private final JLabel label;

    public SGComponentTreeCellRenderer() {
        label = new JLabel();
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
            boolean leaf, int row, boolean hasFocus) {
        Object o = ((DefaultMutableTreeNode) value).getUserObject();
        if (o instanceof SgComponentData component) {
            try {
                label.setIcon(component.getIcon());
                label.setText(component.getName());
            } catch (Exception ex) {
                System.err.println("Unable to create tree cell icon.");
            }
        } else {
            label.setIcon(null);
            label.setText("" + value);
        }
        return label;
    }
}
