/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openstreetmap.gui.jmapviewer;

import igcapt.inl.components.SgComponent;
import java.awt.Component;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
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

    private JLabel label;

    SGComponentTreeCellRenderer() {
        label = new JLabel();
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
            boolean leaf, int row, boolean hasFocus) {
        Object o = ((DefaultMutableTreeNode) value).getUserObject();
        if (o instanceof SgComponent) {
            SgComponent component = (SgComponent) o;
            try {
                BufferedImage img = component.getIconImage();
//                try {
//                    //img = ImageIO.read(new File(name));
//                    //img = ImageIO.read(SGComponentTreeCellRenderer.class.getResourceAsStream(name));                    
//                    img = ImageIO.read(SGComponentTreeCellRenderer.class.getResourceAsStream(newName));                    
//                } catch (Exception e) {
//                }
                ImageIcon newicon = new ImageIcon(img);
                label.setIcon(newicon);
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
