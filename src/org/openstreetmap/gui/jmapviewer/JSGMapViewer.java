/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openstreetmap.gui.jmapviewer;

import edu.uci.ics.jung.graph.Graph;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.openstreetmap.gui.jmapviewer.interfaces.MapObject;

/**
 * Tree of layers for JMapViewer component
 * @author galo
 */
public class JSGMapViewer extends JPanel {
    /** Serial Version UID */
    private static final long serialVersionUID = 3050203054402323972L;
    private JMapViewer map;
    private JSplitPane splitPane;

    public JSGMapViewer(String name, IGCAPTgui igCAPTgui) {
        this(name, false, igCAPTgui);
    }

    public JSGMapViewer(String name, boolean treeVisible, IGCAPTgui igCAPTgui) {
        super();
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        map = new JMapViewer(igCAPTgui);
        map.initialize();

        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(250);

        //Provide minimum sizes for the two components in the split pane
        Dimension minimumSize = new Dimension(100, 50);
        map.setMinimumSize(minimumSize);
        setLayout(new BorderLayout());
        //setTreeVisible(treeVisible);
    }
    
    // KD for turning on and off visibility of the GIS
    public JSplitPane getSplitPane() {
        return splitPane;
    }

    public static int size(List<?> list) {
        return list == null ? 0 : list.size();
    }

    public JMapViewer getViewer() {
        return map;
    }

    public void addMapObject(MapObject o){

    }

    public void setTreeVisible(boolean visible) {
        removeAll();
        revalidate();
        if (visible) {
            splitPane.setRightComponent(map);
            add(splitPane, BorderLayout.CENTER);
        } else add(map, BorderLayout.CENTER);
        repaint();
    }
}
