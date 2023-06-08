/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gov.inl.igcapt.controllers;

import gov.inl.igcapt.view.IGCAPTgui;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.picking.PickedState;
import gov.inl.igcapt.components.DataModels.SgComponentData;
import gov.inl.igcapt.components.NodeSelectionDialog;
import gov.inl.igcapt.components.SgLayeredIcon;
import gov.inl.igcapt.components.SgMapImage;
import gov.inl.igcapt.graph.GraphManager;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.border.Border;
import static org.openstreetmap.gui.jmapviewer.JMapViewer.SGNODECLICKHEIGHT;
import static org.openstreetmap.gui.jmapviewer.JMapViewer.SGNODECLICKWIDTH;

import gov.inl.igcapt.graph.SgEdge;
import gov.inl.igcapt.graph.SgGraph;
import gov.inl.igcapt.graph.SgNode;
import gov.inl.igcapt.graph.SgNodeInterface;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import org.openstreetmap.gui.jmapviewer.*;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;
import org.openstreetmap.gui.jmapviewer.interfaces.MapImage;
import org.openstreetmap.gui.jmapviewer.interfaces.MapLine;


/**
 *
 * @author FRAZJD
 */
public class IGCAPTMapController extends JMapController implements MouseListener, MouseMotionListener,
MouseWheelListener {

    public IGCAPTMapController(JMapViewer map) {
        super(map);
    }
   
    private static final boolean WHEELZOOMENABLED = true;
       
    private class ClickInfo {

        ClickInfo(SgNodeInterface clickNode, Point clickPoint) {
            _clickNode = clickNode;
            _clickPoint = clickPoint;
        }

        private SgNodeInterface _clickNode = null;

        public void setClickNode(SgNode clickNode) {
            this._clickNode = clickNode;
        }

        public SgNodeInterface getClickNode() {
            return _clickNode;
        }

        public Point _clickPoint = null;

        public void setClickPoint(Point clickPoint) {
            this._clickPoint = clickPoint;
        }

        public Point getClickPoint() {
            return _clickPoint;
        }
    }
    private ClickInfo _clickInfo = null;
    private Point _pastMovePoint = null;
    private MapLine prevMapLine = null;
    private Point lastDragPoint;
    private boolean isMoving = false;
    // Flag helps user so they don't move node when creating line if release
    // the mouse too quickly
    private boolean shiftDownDuringDrag = false;
    private SgNodeInterface m_prevNode = null;  
    // image for indicating node has been selected
    private MapImageImpl m_selectionImageStartNode = null;
    private MapImageImpl m_selectionImageEndNode = null;


    @Override
    public void mouseClicked(MouseEvent e) {
    }

    private SgNodeInterface ptInNode(int x, int y) {
        SgNodeInterface returnval = null;
        
        List<SgNodeInterface> nodes = new ArrayList<>(GraphManager.getInstance().getGraph().getVertices());

        for (SgNodeInterface node : nodes) {
            double latitude = node.getLat();
            double longitude = node.getLongit();
            Point nodePoint = map.getMapPosition(latitude, longitude);
            if (nodePoint != null) { //eliminates exception thrown when trying to move a node, but lat,long are Ok
                Rectangle nodeRect = new Rectangle(nodePoint.x - SGNODECLICKWIDTH / 2, nodePoint.y - SGNODECLICKHEIGHT / 2, SGNODECLICKWIDTH, SGNODECLICKHEIGHT);

                if (nodeRect.contains(x, y)) {
                    returnval = node;
                    break;
                }
            }
        }

        return returnval;
    }

    // Recursively remove the graphNodes from the baseGraph.
    private void removeNodes(SgGraph graphNode, SgGraph baseGraph) {
        
        ArrayList<SgNodeInterface> nodeList = new ArrayList<>(graphNode.getVertices());
        
        for (SgNodeInterface node : nodeList) {
            if (node instanceof SgNode) {
                baseGraph.removeVertex(node);
            }
            else {
                removeNodes((SgGraph)node, baseGraph);
            }
        }
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        
        SgNodeInterface clickNode = ptInNode(e.getX(), e.getY());
        JPopupMenu popup;
        
        if (e.getButton() == MouseEvent.BUTTON1) {            
            if (clickNode != null) {
                _clickInfo = new ClickInfo(clickNode, new Point(e.getX(), e.getY()));
                // draw the selection image i.e. box around the node
                m_selectionImageStartNode = createSelectionImage(clickNode);
                map.addMapImage(m_selectionImageStartNode);
                if (e.isShiftDown()) {
                    // Creating an edge so do nothing until the release
                    return;
                }
                map.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }
            // Preserve default behavior of double-clicking left button to zoom in.
            else if (e.getClickCount() == 2) {
                map.zoomIn(e.getPoint());
            }
        } // check if mouse context button menu was pushed
        else if (e.getButton() == MouseEvent.BUTTON2 | e.getButton() == MouseEvent.BUTTON3) {
            // check if this is a node or an edge
            boolean mousePointIsOnLine = false;
            boolean mousePointIsOnNode = false;
            double circleRadius = 10;
            SgEdge selectedEdge = null;
            SgNodeInterface selectedNode = null;
            final SgEdge edgeToUse;
            final SgNodeInterface nodeToUse;
            SgNodeInterface endPt1;
            SgNodeInterface endPt2;
            Point mousePoint = e.getPoint();
            List<SgNodeInterface> nodes = new ArrayList<>(GraphManager.getInstance().getGraph().getVertices());
            List<SgEdge> edges = new ArrayList<>(GraphManager.getInstance().getGraph().getEdges());

            if (clickNode != null) {
                mousePointIsOnNode = true;
                selectedNode = clickNode;
            }
                       
            if (!mousePointIsOnNode) {
                // check to see if the mouse is on an edge
                for (SgEdge edge : edges) {
                    if (edge instanceof SgEdge) {
                        Pair<SgNodeInterface> endpoints = GraphManager.getInstance().getGraph().getEndpoints(edge);

                        if (endpoints.getFirst() instanceof SgNodeInterface && endpoints.getSecond() instanceof SgNodeInterface) {
                            endPt1 = endpoints.getFirst();
                            endPt2 = endpoints.getSecond();

                            double latitude1 = endPt1.getLat();
                            double longitude1 = endPt1.getLongit();
                            double latitude2 = endPt2.getLat();
                            double longitude2 = endPt2.getLongit();

                            // If this is a duplicate edge replace one side with midpoint
                            if (edge.getMidPoint() != null && edge.getName() != null) {
                                // if the id of 2 < id of 1 then swap the values
                                // this helps alternate edge labels
                                if (endPt2.getId() < endPt1.getId()) {
                                    endPt1 = endpoints.getSecond();
                                    endPt2 = endpoints.getFirst();
                                    latitude1 = endPt1.getLat();
                                    longitude1 = endPt1.getLongit();
                                    latitude2 = endPt2.getLat();
                                    longitude2 = endPt2.getLongit();
                                }
                                // even edges replace second point with midpoint
                                if (edge.getId() % 2 == 0){  // even
                                    latitude2 = edge.getMidPoint().getLat();
                                    longitude2 = edge.getMidPoint().getLon();
                                }
                                // odd edges replace first point with midpoint
                                else {
                                    latitude1 = edge.getMidPoint().getLat();
                                    longitude1 = edge.getMidPoint().getLon();
                                }
                            }

                            /// this helped determine that we should be using node1Point and node2Point
                            // by examining these values and the cursor position printed on top of the screen
                            Point node1Point = map.getMapPosition(latitude1, longitude1, false);
                            Point node2Point = map.getMapPosition(latitude2, longitude2, false);

                            int newY = (int) ((node2Point.y - node1Point.y) / 2 + node1Point.y);
                            int newX = (int) ((node2Point.x - node1Point.x) / 2 + node1Point.x);

                            double distanceFromCenter = Math.sqrt(Math.pow((mousePoint.getX() - newX), 2) + Math.pow((mousePoint.getY() - newY), 2));
                            if (edge.getMidPoint() != null && edge.getName() != null) {
                           }
                            if (distanceFromCenter < circleRadius) {
                                mousePointIsOnLine = true;
                                selectedEdge = edge;
                                break;
                            }
                        }
                    }
                }
            }

            nodeToUse = selectedNode;
            edgeToUse = selectedEdge;

            if (mousePointIsOnNode) {
                //System.out.println("Mouse button 2 or 3 pressed on a node = " + nodeToUse.toString());
                popup = new JPopupMenu();
                popup.add(new AbstractAction("Delete Vertex") {
                    public void actionPerformed(ActionEvent e) {
                        GraphManager.getInstance().setFileDirty(true);
                        
                        if (nodeToUse instanceof SgNodeInterface) {
                            SgNodeInterface node = nodeToUse;

                            Graph currentGraph = GraphManager.getInstance().getGraph();
                            currentGraph.removeVertex(node);
                            if (currentGraph != GraphManager.getInstance().getOriginalGraph()) {
                                
                                if (node instanceof SgGraph sgGraph) {
                                    removeNodes(sgGraph, GraphManager.getInstance().getOriginalGraph());
                                }
                                else {
                                    GraphManager.getInstance().getOriginalGraph().removeVertex(node);
                                }
                            }
                        }

                        IGCAPTgui.getInstance().graphChanged();
                    }
                });
                // Remove the menu item to remove the popup menu for adding an edge
                // down to the end remove #EdgeMenu
                JMenuItem addEdgeItem = popup.add(new AbstractAction("Add Edge") {
                    public void actionPerformed(ActionEvent e) {

                        // remove the node and any edge connected to this node
                        List<SgNodeInterface> nodesMinusSelectedNodes = nodes;
                        nodesMinusSelectedNodes.remove(nodeToUse);

                        NodeSelectionDialog nsd = new NodeSelectionDialog(IGCAPTgui.getInstance(), true, nodeToUse.getName(), new DefaultComboBoxModel(nodesMinusSelectedNodes.toArray()));
                        nsd.setVisible(true);
                        int idNumber = nsd.getToNodeIdNumber();

                        int edgeIndex = GraphManager.getInstance().getNextEdgeIndex();
                        SgEdge e2 = new SgEdge(edgeIndex, "e" + edgeIndex, 1.0, 0.0, 0.0);
                        // get the end point SgNode selected by the user
                        SgNodeInterface endNodeSpecifiedByUser = null;
                        for (SgNodeInterface node : nodes) {
                            if (node.getId() == idNumber) {
                                endNodeSpecifiedByUser = node;
                                break;
                            }
                        }
                        // add the edge to the jung graph
                        GraphManager.getInstance().getGraph().addEdge(e2, nodeToUse, endNodeSpecifiedByUser);
                        IGCAPTgui.getInstance().graphChanged();
                    }
                });
                addEdgeItem.setEnabled(nodeToUse instanceof SgNodeInterface);
                // end remove
                
                JMenuItem collapseItem = popup.add(new AbstractAction("Collapse") {
                    public void actionPerformed(ActionEvent e) {
                        map.collapseNode(nodeToUse);
                    }
                });
                
                boolean collapseEnabled = false;
                if (nodeToUse instanceof SgNode sgNode) {
                    ArrayList<SgNodeInterface> collapseableNodes = sgNode.getCollapseableIncidentNodes(false);
                    collapseEnabled = (collapseableNodes != null && collapseableNodes.size() > 1);
                }

                collapseItem.setEnabled(collapseEnabled);
                
                JMenuItem expandItem = popup.add(new AbstractAction("Expand") {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        if (nodeToUse instanceof SgGraph graph) {
                            
                            PickedState<SgNodeInterface> pickState = GraphManager.getInstance().getVisualizationViewer().getPickedVertexState();
                            pickState.clear();
                            pickState.pick(graph, true);
                            
                            IGCAPTgui.getInstance().expand();
                        }
                    }
                });
                
                // Only enable the Expand menu item if the node is of type SgGraph
                expandItem.setEnabled(nodeToUse.canExpand());                       
                
                JMenuItem settingsItem = popup.add(new AbstractAction("Device Settings") {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                       if (nodeToUse instanceof SgNode node) {
                            IGCAPTgui.getInstance().showDialog(node);
                        }
                        else if(nodeToUse instanceof SgGraph sgGraph) {
                            SgNode node = (SgNode)sgGraph.getRefNode();
                            IGCAPTgui.getInstance().showDialog(node);
                        }
                    }
                });
                settingsItem.setEnabled(nodeToUse instanceof SgNodeInterface);
                popup.show(map, (int)mousePoint.getX(), (int)mousePoint.getY());

            }
            else if (mousePointIsOnLine) {
                popup = new JPopupMenu();
                popup.add(new AbstractAction("Delete Edge") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Graph currentGraph = GraphManager.getInstance().getGraph();
                        currentGraph.removeEdge(edgeToUse);
                        if (currentGraph != GraphManager.getInstance().getOriginalGraph()) {
                            GraphManager.getInstance().getOriginalGraph().removeEdge(edgeToUse);
                        }
                        IGCAPTgui.getInstance().graphChanged();
                    }
                });
                
                
                popup.add(new AbstractAction("Line Settings") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                       IGCAPTgui.getInstance().showDialog(edgeToUse);
                    }
                }
                );
                popup.show(map, (int)mousePoint.getX(), (int)mousePoint.getY());
            }
            else {
                // Not on line or node
                isMoving = true;
                lastDragPoint = null;
            }
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {

        Point releasePoint = new Point(e.getX(), e.getY());
        
        isMoving = false;
        lastDragPoint = null;
        map.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      
        map.removeMapImage(m_selectionImageStartNode);
        m_selectionImageStartNode = null;
        if (shiftDownDuringDrag && _clickInfo != null) {
            // Remove line drawn during the drag and set it to null for next drag
            GraphManager.getInstance().getCurrentGisMap().removeMapLine(prevMapLine);
            prevMapLine = null;
            shiftDownDuringDrag = false;
            if (m_prevNode != null) {
                // mouse was release so remove the selection image i.e. the box
                map.removeMapImage(m_selectionImageEndNode);
                m_selectionImageEndNode = null;
                m_prevNode = null;
            }
            // get the end point SgNode selected by the user (or null)
            SgNodeInterface endNodeSpecifiedByUser = ptInNode(e.getX(), e.getY());
            // if the same node was selected or no node was selected don't make edge
            if (endNodeSpecifiedByUser != _clickInfo.getClickNode() &&
                endNodeSpecifiedByUser != null) {
                // drawedge
                int edgeIndex = GraphManager.getInstance().getNextEdgeIndex();
                SgEdge e2 = new SgEdge(edgeIndex, "e" + edgeIndex, 1.0, 0.0, 0.0);
                // Always store the pair from smaller node id to greater node id so
                // don't have to worry about which direction the user drew the edge
                // and the pair will be found
                Pair edgePair = null;
                if (endNodeSpecifiedByUser.getId() < _clickInfo.getClickNode().getId()) {
                    edgePair = new Pair(endNodeSpecifiedByUser, _clickInfo.getClickNode());
                }
                else {
                    edgePair = new Pair(_clickInfo.getClickNode(), endNodeSpecifiedByUser);
                }
                if (IGCAPTgui.getInstance().nodePairList.contains(edgePair)) {
                    int occurrences = java.util.Collections.frequency(IGCAPTgui.getInstance().nodePairList, edgePair);
                    //SgNode p1 = (SgNode)edgePair.getFirst();
                    //SgNode p2 = (SgNode) edgePair.getSecond();
                    SgNodeInterface p1 = (SgNodeInterface)edgePair.getFirst();
                    SgNodeInterface p2 = (SgNodeInterface) edgePair.getSecond();
                    Coordinate mid = GraphManager.getInstance().calcNewMidPoint(p1.getLat(), p1.getLongit(),
                        p2.getLat(), p2.getLongit(), occurrences);
                    e2.setMidPoint(mid);
                }
                IGCAPTgui.getInstance().nodePairList.add(edgePair);
                // add the edge to the jung graph
                GraphManager.getInstance().getGraph().addEdge(e2, _clickInfo._clickNode, endNodeSpecifiedByUser);
                IGCAPTgui.getInstance().graphChanged();
            }
            _clickInfo = null;
            _pastMovePoint = null;
        }
        // no shift so move the node
        else if (_clickInfo != null && !_clickInfo.getClickPoint().equals(releasePoint)) {

            ICoordinate coordinate = map.getPosition(releasePoint);
            double latitude = coordinate.getLat();
            double longitude = coordinate.getLon();
            _clickInfo.getClickNode().setLat(latitude);
            _clickInfo.getClickNode().setLongit(longitude);

            MapImage mapImage = _clickInfo.getClickNode().getMapImage();
            if (mapImage != null) {
                mapImage.setLat(latitude);
                mapImage.setLon(longitude);
                map.paintImage(map.getGraphics(), mapImage);
            }
            map.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

            _clickInfo = null;
            _pastMovePoint = null;

        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
    
    // Create the image (i.e. box around node) to add to the map to show node is selected
    private MapImageImpl createSelectionImage(SgNodeInterface node) {
        
        MapImageImpl returnval = null;
        Icon selectionIcon = IGCAPTgui.getInstance().getLayerIcon("selectionIcon");
        
        if (selectionIcon != null) {
            SgLayeredIcon layeredIcon = (SgLayeredIcon)selectionIcon;
            
            BufferedImage theImage = (BufferedImage)layeredIcon.getCompositeImage();

            if (theImage != null) {
                returnval = new SgMapImage(node.getLat(), node.getLongit(), theImage, 0, node);

                if (node.isRenderName()) {
                    returnval.setId(node.getName());                                                        
                }
            }
        }

        return returnval;
    }
    
    // Drag the image around with the cursor while the button is still down.
    @Override
    public void mouseDragged(MouseEvent e) {
        if (_clickInfo != null) {
            if (e.isShiftDown()) {
                shiftDownDuringDrag = true;
                // Creating an edge - draw line during the drag
                Coordinate start = new Coordinate(_clickInfo.getClickNode().getLat(), 
                                                  _clickInfo.getClickNode().getLongit());
                Point movePoint = new Point(e.getX(), e.getY());
                ICoordinate end = map.getPosition(movePoint);
                
                SgNodeInterface endNode = ptInNode(e.getX(), e.getY());
                if (endNode != null) {
                    if (endNode != _clickInfo._clickNode) {
                        if (endNode != m_prevNode) {
                            // end node changed so remove box from prev node and add to new node
                            map.removeMapImage(m_selectionImageEndNode);
                            m_prevNode = endNode;
                            m_selectionImageEndNode = createSelectionImage(endNode);
                            map.addMapImage(m_selectionImageEndNode);
                        }
                    }
                }
                else {
                    // no end node so remove section
                    map.removeMapImage(m_selectionImageEndNode);
                    m_selectionImageEndNode = null;
                    m_prevNode = null;
                }
                
                MapLineImpl line = new MapLineImpl(start, end);
                if (prevMapLine != null) {
                    GraphManager.getInstance().getCurrentGisMap().removeMapLine(prevMapLine);
                }
                GraphManager.getInstance().getCurrentGisMap().addMapLine(line);
                prevMapLine = line;
                return;
            }
            if (_pastMovePoint == null) {
                _pastMovePoint = _clickInfo.getClickPoint();
            }

            Point movePoint = new Point(e.getX(), e.getY());
            if (!_pastMovePoint.equals(movePoint)) {

                ICoordinate coordinate = map.getPosition(movePoint);
                double latitude = coordinate.getLat();
                double longitude = coordinate.getLon();
                _clickInfo.getClickNode().setLat(latitude);
                _clickInfo.getClickNode().setLongit(longitude);

                MapImage mapImage = _clickInfo.getClickNode().getMapImage();
                if (mapImage != null) {
                    mapImage.setLat(latitude);
                    mapImage.setLon(longitude);
                    // Need to clear out old image before drawing new image.
                    map.paintImage(map.getGraphics(), mapImage);
                    IGCAPTgui.getInstance().updateGISObjects();
                }

                _pastMovePoint = movePoint;
            }
        }
        else {
            if (isMoving) {
                if (e.isShiftDown()) {
                    // Creating an edge so do nothing during the drag
                    return;
                }
                map.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));

                Point p = e.getPoint();
                if (lastDragPoint != null) {
                    int diffx = lastDragPoint.x - p.x;
                    int diffy = lastDragPoint.y - p.y;
                    map.moveMap(diffx, diffy);
                }
                lastDragPoint = p;
            }
        }

        e.consume();
        GraphManager.getInstance().setFileDirty(true);
        
        map.repaint(); // KD; eliminates the trail of a dragged object.
        // after repaint must readd the selection image to the start node
        if (_clickInfo != null) {
            m_selectionImageStartNode = createSelectionImage(_clickInfo.getClickNode());
            map.addMapImage(m_selectionImageStartNode);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // This is for tool tips
        String positionString = String.valueOf(e.getX()) + ", " + String.valueOf(e.getY());
        IGCAPTgui.getInstance().setXyPosition(positionString);
        if (IGCAPTgui.getInstance().isToolTipsEnabled()) {
            boolean mousePointIsOnLine = false;
            boolean mousePointIsOnNode = false;
            double circleRadius = 10;
            SgEdge selectedEdge = null;
            SgNodeInterface selectedNode = null;
            SgNodeInterface endPt1 = null;
            SgNodeInterface endPt2 = null;
            Point mousePoint = e.getPoint();
            String ttText = "<html>";
            Border border = BorderFactory.createLineBorder(new Color(0, 0, 0)); // The color is #4c4f53.
            UIManager.put("ToolTip.border", border);
            List<SgNodeInterface> nodes = new ArrayList<>(GraphManager.getInstance().getGraph().getVertices());
            List<SgEdge> edges = new ArrayList<>(GraphManager.getInstance().getGraph().getEdges());
            SgComponentData sgComponent = null;

            // check to see if the mouse is on an edge
            for (SgEdge edge : edges) {
                if (edge instanceof SgEdge) {
                    Pair<SgNodeInterface> endpoints = GraphManager.getInstance().getGraph().getEndpoints(edge);

                    if (endpoints.getFirst() instanceof SgNodeInterface && endpoints.getSecond() instanceof SgNodeInterface) {
                        endPt1 = endpoints.getFirst();
                        endPt2 = endpoints.getSecond();

                        double latitude1 = endPt1.getLat();
                        double longitude1 = endPt1.getLongit();
                        double latitude2 = endPt2.getLat();
                        double longitude2 = endPt2.getLongit();

                        /// this helped determine that we should be using node1Point and node2Point
                        // by examining these values and the cursor position printed on top of the screen
                        Point node1Point = map.getMapPosition(latitude1, longitude1, false);
                        Point node2Point = map.getMapPosition(latitude2, longitude2, false);
                        
                        int newY = (int) ((node2Point.y - node1Point.y) / 2 + node1Point.y);
                        int newX = (int) ((node2Point.x - node1Point.x) / 2 + node1Point.x);

                        double distanceFromCenter = Math.sqrt(Math.pow((mousePoint.getX() - newX), 2) + Math.pow((mousePoint.getY() - newY), 2));
                        if (distanceFromCenter < circleRadius) {
                            mousePointIsOnLine = true;
                            selectedEdge = edge;
                            break;
                        }
                    }
                }
            }
            //check if mouse is on a node.  The node can be either an SgNode or an SgGraph.
            // Use the general SgNodeInterface type and then get the reference node to
            // refer to the actual node if it is an SgNode or the underlying node if
            // it is an SgGraph.
            for (SgNodeInterface node : nodes) {
                sgComponent = IGCAPTgui.getComponentByUuid(node.getRefNode().getType());
                double latitude = node.getRefNode().getLat();
                double longitude = node.getRefNode().getLongit();

                Point nodePoint = map.getMapPosition(latitude, longitude);
                if (nodePoint != null && IGCAPTgui.getInstance().isToolTipsEnabled()) { //eliminates exception thrown when trying to move a node, but lat,long are Ok
                    Rectangle nodeRect = new Rectangle(nodePoint.x - SGNODECLICKWIDTH / 2, nodePoint.y - SGNODECLICKHEIGHT / 2, SGNODECLICKWIDTH, SGNODECLICKHEIGHT);

                    if (nodeRect.contains(e.getX(), e.getY())) {
                        mousePointIsOnNode = true;
                        selectedNode = node;
                        break;
                    }
                }
            }

            // mouse point is on an edge!
            if (mousePointIsOnLine) {
                ttText += "<html>"
                        + "<table width='100%' border='0' cellpadding='0'>"
                        + "<tr>"
                        + "<th align='left' width='70'>ID: </th>"
                        + "<th align='left'><font color='#ffffff'>" + selectedEdge.getId() + "</font></th>"
                        + "</tr>"
                        + "<tr>"
                        + "<th align='left'>Weight: </th>"
                        + "<th align='left'><font color='#ffffff'>" + selectedEdge.getWeight() + "</font></th>"
                        + "</tr>"
                        + "<tr>"
                        + "<th align='left'>Max Rate (Kbits/sec): </th>"
                        + "<th align='left'><font color='#ffffff'>" + selectedEdge.getEdgeRate() + "</font></th>"
                        + "</tr>"
                        + "<tr>"
                        + "<th align='left'>End Points: </th>"
                        + "<th align='left'><font color='#ffffff'>" + endPt1.toString() + ", " + endPt2.toString() + "</font></th>"
                        + "</tr>"
                        + "<tr>"
                        + "<th align='left'>Calculated Rate (Kbits/sec): </th>"
                        + "<th align='left'><font color='#ffffff'>" + " " + String.format("%.4f", selectedEdge.getCalcTransRate()) + "</font></th>"                                       
                        + "</tr>"
                        + "<tr>"
                        + "<th align='left'>Calculated Utilization: </th>"
                        + "<th align='left'><font color='#ffffff'>" + " " + String.format("%.2f",selectedEdge.getUtilization()*100.0) + "%" + "</font></th>"                                       
                        + "</tr>"
                        + "</table>"
                        + "<html>";

                map.setToolTipText(ttText);
            } else if (mousePointIsOnNode) {

                ttText = "<html>";
                ttText += "<table width='100%' border='0' cellpadding='0'>"
                        + "<tr>"
                        + "<th align='left'>ID: </th>"
                        + "<th align='left'><font color='#ffffff'>" + selectedNode.getRefNode().getId() + "</font></th>"
                        + "</tr>"
                        + "<tr>"
                        + "<th align='left'>Type: </th>"
                        + "<th align='left'><font color='#ffffff'>" + ((sgComponent!=null)?sgComponent.getName():"&lt;Unknown&gt;") + "</font></th>"
                        + "</tr>"
                        + "<tr>"
                        + "<th align='left'>Payload (bytes): </th>"
                        + "<th align='left'><font color='#ffffff'>" + selectedNode.getRefNode().getDataToSend() + "</font></th>"
                        + "</tr>"
                        + "<tr>"
                        + "<th align='left'>Max Latency (sec): </th>"
                        + "<th align='left'><font color='#ffffff'>" + selectedNode.getRefNode().getMaxLatency() + "</font></th>"
                        + "</tr>";

                // If the tooltip is for a collapsed node (an SgGraph node) then display the list of collapsed nodes.
                if (selectedNode instanceof SgGraph) {
                    SgGraph sgGraph = (SgGraph)selectedNode;

                    ArrayList<SgNodeInterface> vertices = new ArrayList<>(sgGraph.getVertices());

                    if (!vertices.isEmpty())
                    {
                        ttText += "<tr/><tr>"
                        + "<th align='left'>" + "Collapsed Nodes" + "</font></th>"
                        + "</tr>";                                
                    }

                    for (SgNodeInterface vtx : vertices) {
                        ttText += "<tr>"
                        + "<th align='left'><font color='#ffffff'>" + vtx.getName() + "</font></th>"
                        + "</tr>";
                    }
                }
                ttText += "</table>"
                        + "<html>";
      
                map.setToolTipText(ttText);
            } else {
                map.setToolTipText(null);
            }
        }
        map.repaint();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (WHEELZOOMENABLED) {
            int rotation = JMapViewer.zoomReverseWheel ? e.getWheelRotation() : -e.getWheelRotation();
            map.setZoom(map.getZoom() - rotation, e.getPoint());
        }
    }
}
