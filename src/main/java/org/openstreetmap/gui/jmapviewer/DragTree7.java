/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openstreetmap.gui.jmapviewer;

/**
 *(c) 2018 BATTELLE ENERGY ALLIANCE, LLC
 *ALL RIGHTS RESERVED 
 *
 * @author kur
 **/
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
import gov.inl.igcapt.components.DataModels.ComponentDao;
import gov.inl.igcapt.components.DataModels.SgComponentData;
import gov.inl.igcapt.components.DataModels.SgComponentGroupData;
import gov.inl.igcapt.graph.SGComponentTreeCellRenderer;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;

class DragTree7 extends JTree implements DragGestureListener,
        DragSourceListener, TreeSelectionListener {
    
    public static Color selectedBorderColor = new Color(57, 105, 138);
    private final IGCAPTgui _IGCAPTgui;
    
    final JTree myTree;
    
    public DragTree7(IGCAPTgui igCAPTgui) {
        _IGCAPTgui = igCAPTgui;
        
        myTree = this;
        //System.out.println(" the test7 directory is " + IGCAPTgui.directory);
        DragSource dragSource = DragSource.getDefaultDragSource();
        
        dragSource.createDefaultDragGestureRecognizer(
                this, // component where drag originates
                DnDConstants.ACTION_COPY_OR_MOVE, // actions
                this); // drag gesture recognizer

        ComponentDao componentDao = new ComponentDao();
        setModel(createTreeModel(componentDao.getComponentGroups()));
        
        myTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    }
    
    public void dragGestureRecognized(DragGestureEvent e) {
        //System.out.println("getFilename = " + getFilename());
        
        Cursor cursor = DragSource.DefaultCopyNoDrop;
        
        // If the drop target is the logical view then make sure editing mode is active.  If the drop
        // target is the GIS view, then just let the cursor reflect the ability to drop.
        if ((_IGCAPTgui.getActiveDropTarget() == IGCAPTgui.IGCAPTDropTarget.eLogicalDropTarget && _IGCAPTgui.getMode() == Mode.EDITING) ||
                _IGCAPTgui.getActiveDropTarget() == IGCAPTgui.IGCAPTDropTarget.eGISDropTarget)
        {
            cursor = DragSource.DefaultCopyDrop;
        }
        
        e.startDrag(cursor, // cursor
                new StringSelection(getUuidStr()), // transferable
                this);  // drag source listener
        //System.out.println("dragGestureRecognized - DragTree7 - tree item selected");
    }
    
    public void dragDropEnd(DragSourceDropEvent e) {
        //System.out.println("dragDropEnd - DragTree7 - tree item selected");
    }
    
    public void dragEnter(DragSourceDragEvent e) {
        //System.out.println("dragEnter - DragTree7 - tree item selected");
    }
    
    public void dragExit(DragSourceEvent e) {
        //System.out.println("dragExit - DragTree7 - tree item selected");
    }
    
    public void dragOver(DragSourceDragEvent e) {
        //System.out.println("dragOver - DragTree7 - tree item selected");
    }
    
    public void dropActionChanged(DragSourceDragEvent e) {
        //System.out.println("dropActionChanged - DragTree7 - tree item selected");
    }
    
    public String getUuidStr() {
        String returnval = "";
        TreePath path = getLeadSelectionPath();
        if (path != null) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) (path.getLastPathComponent());
            SgComponentData sgc = (SgComponentData) node.getUserObject();
            if (sgc != null) {
                returnval = sgc.getUuid();
            }
        }
        return returnval;
    }
    
    private DefaultTreeModel createTreeModel(List<SgComponentGroupData> sgComponentGroupList) {
        
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Smart Grid Components");
        for (SgComponentGroupData sgComponentGroup : sgComponentGroupList) {
            if (sgComponentGroup.isDisplay()) {
                DefaultMutableTreeNode groupTreeNode = new DefaultMutableTreeNode(sgComponentGroup.getGroupName());
                
                for (SgComponentData sgComponent : sgComponentGroup.getComponents()) {
                    groupTreeNode.add(new DefaultMutableTreeNode(sgComponent));
                }
            
                root.add(groupTreeNode);
            }
        }

        setCellRenderer(new SGComponentTreeCellRenderer());
        return new DefaultTreeModel(root);
    }
    
    private String getExpansionState() {
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < myTree.getRowCount(); i++) {
            TreePath tp = myTree.getPathForRow(i);
            if (myTree.isExpanded(i)) {
                sb.append(tp.toString());
                sb.append(",");
            }
        }

        return sb.toString();
    }
    
    public void refreshTreeModel() {
        String expansionString = getExpansionState();
        myTree.removeAll();
                
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Smart Grid Components");
        ComponentDao dao = new ComponentDao();
        List<SgComponentGroupData>list = dao.getComponentGroups();
        
        for (SgComponentGroupData sgComponentGroup : list) {
            if (sgComponentGroup.isDisplay()) {
                DefaultMutableTreeNode groupTreeNode = new DefaultMutableTreeNode(sgComponentGroup.getGroupName());
                
                for (SgComponentData sgComponent : sgComponentGroup.getComponents()) {
                    groupTreeNode.add(new DefaultMutableTreeNode(sgComponent));
                }
            
                root.add(groupTreeNode);
            }
        }
        setCellRenderer(new SGComponentTreeCellRenderer());
        DefaultTreeModel treeModel = new DefaultTreeModel(root);
        setModel(treeModel);
        expandTree(expansionString);
    }
    
    private void expandTree(String s) {
        for (int i = 0; i < myTree.getRowCount(); i++) {
            TreePath tp = myTree.getPathForRow(i);
            if (s.contains(tp.toString())) {
                myTree.expandRow(i);
            }
        }
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getSelectionPath() != null) {
            Rectangle r = this.getRowBounds(getRowForPath(getSelectionPath()));
            g.setColor(selectedBorderColor);
            g.drawRect(0, r.y, getWidth() - 1, r.height - 1);
        }
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                           this.getLastSelectedPathComponent();
        
        if (node != null) {
            Object obj = node.getUserObject();
            if (node.isLeaf()) {
                if (obj instanceof SgComponentData component) {
                    IGCAPTgui.getInstance().setCurrentType(component.getUuid());
                }
            }
        }
    }
}
