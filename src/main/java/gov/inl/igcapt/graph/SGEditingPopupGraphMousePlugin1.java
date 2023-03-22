package gov.inl.igcapt.graph;

/**
 *(c) 2018 BATTELLE ENERGY ALLIANCE, LLC
 *ALL RIGHTS RESERVED 
 *
 * @author kur
 */
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;

import org.apache.commons.collections15.Factory;


import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.picking.PickedState;
import gov.inl.igcapt.components.DataModels.SgComponentData;
import gov.inl.igcapt.view.IGCAPTgui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenuItem;

/**
 * a plugin that uses popup menus to create vertices, undirected edges, and
 * directed edges.
 *
 * @author Tom Nelson
 *
 */
public class SGEditingPopupGraphMousePlugin1<V, E> extends edu.uci.ics.jung.visualization.control.AbstractPopupGraphMousePlugin {

    protected Factory<V> vertexFactory;
    protected Factory<E> edgeFactory;
    protected JPopupMenu popup;
    private IGCAPTgui _IGCAPTgui;
    private GraphManager _GraphManager;

    public SGEditingPopupGraphMousePlugin1(Factory<V> vertexFactory, Factory<E> edgeFactory, IGCAPTgui igcaptGui) {
        this.vertexFactory = vertexFactory;
        this.edgeFactory = edgeFactory;
        _IGCAPTgui = igcaptGui;
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

    @SuppressWarnings({"unchecked", "serial", "serial"})
    @Override
    protected void handlePopup(MouseEvent e) {
        final VisualizationViewer<V, E> vv
                = (VisualizationViewer<V, E>) e.getSource();
        final Layout<V, E> layout = vv.getGraphLayout();
        //final Graph<V, E> graph = layout.getGraph();
        final Point2D p = e.getPoint();
        final Point2D ivp = p;
        GraphElementAccessor<V, E> pickSupport = vv.getPickSupport();

        if (pickSupport != null) {

            final V vertex = pickSupport.getVertex(layout, ivp.getX(), ivp.getY());
            final E edge = pickSupport.getEdge(layout, ivp.getX(), ivp.getY());
            final PickedState<V> pickedVertexState = vv.getPickedVertexState();
            final PickedState<E> pickedEdgeState = vv.getPickedEdgeState();

            if (vertex != null) {
                popup = new JPopupMenu();
                popup.add(new AbstractAction("Delete Vertex") {
                    public void actionPerformed(ActionEvent e) {
                        GraphManager.getInstance().fileDirty = true;
                        
                        pickedVertexState.pick(vertex, false);
                        
                        if (vertex instanceof SgNodeInterface) {
                            SgNodeInterface node = (SgNodeInterface)vertex;

                            Graph currentGraph = _GraphManager.getGraph();
                            currentGraph.removeVertex(node);
                            if (currentGraph != _GraphManager.getOriginalGraph()) {
                                
                                if (node instanceof SgGraph) {
                                    removeNodes((SgGraph)node, _GraphManager.getOriginalGraph());
                                }
                                else {
                                    _GraphManager.getOriginalGraph().removeVertex(node);
                                }
                            }
                        }

                        vv.repaint();
                        _IGCAPTgui.graphChanged();
                    }
                });
                
                // Need to recursively traverse into the subnodes
                JMenuItem collapseItem;
                collapseItem = popup.add(new AbstractAction("Collapse") {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        if (vertex instanceof SgNode) {
                            SgNode node = (SgNode) vertex;
                            GraphManager.getInstance().setContextClickNode(node);
                            
                            // Get the component corresponding to this node.
                            SgComponentData sgComponent = IGCAPTgui.getComponentByUuid(node.getType());
                            
                            // Get the list of connected nodes
                            ArrayList<SgNodeInterface> collapseableNeighborNodes = new ArrayList<>();
                            collapseableNeighborNodes.add(node);
                            List<SgNodeInterface> tempList = node.getConnectedNodes(false, collapseableNeighborNodes);
                            
                            for (SgNodeInterface tempNode : tempList) {
                                if (sgComponent.getSgCollapseIntoUuids().contains(tempNode.getRefNode().getType())) {
                                    collapseableNeighborNodes.add(tempNode);
                                    collapseableNeighborNodes = tempNode.getConnectedNodes(true, collapseableNeighborNodes);
                                }
                            }

                            PickedState<SgNodeInterface> pickState = GraphManager.getInstance().getVisualizationViewer().getPickedVertexState();
                            pickState.clear();
                            for (SgNodeInterface collapseNode : collapseableNeighborNodes) {
                                pickState.pick(collapseNode, true);
                            }
                            
                            _IGCAPTgui.collapse();
                            _GraphManager.setContextClickNode(null);
                            _IGCAPTgui.graphChanged();
                        }
                    }
                });
                collapseItem.setEnabled(vertex instanceof SgNodeInterface && ((SgNodeInterface)vertex).canCollapse());
                
                JMenuItem expandItem = popup.add(new AbstractAction("Expand") {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        if (vertex instanceof SgGraph) {
                            SgGraph graph = (SgGraph) vertex;
                            
                            if (graph.canExpand()) {
                                PickedState<SgNodeInterface> pickState = GraphManager.getInstance().getVisualizationViewer().getPickedVertexState();
                                pickState.clear();
                                pickState.pick(graph, true);

                                _IGCAPTgui.expand();
                                _IGCAPTgui.graphChanged();
                            }
                        }
                    }
                });
                
                // Only enable the Expand menu item if the node is of type SgGraph
                expandItem.setEnabled(vertex instanceof SgGraph && ((SgGraph)vertex).canExpand());               

                popup.addSeparator();
                JMenuItem nodeSettingsItem = popup.add(new AbstractAction("Device Settings") {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        if (vertex instanceof SgNode) {
                            SgNode node = (SgNode) vertex;
                            _IGCAPTgui.showDialog(node);
                        }
                        else if(vertex instanceof SgGraph) {
                            SgNode node = (SgNode)((SgGraph) vertex).getRefNode();
                            _IGCAPTgui.showDialog(node);
                        }
                    }
                });
                nodeSettingsItem.setEnabled(vertex instanceof SgNodeInterface);

            } else if (edge != null) {
                popup = new JPopupMenu();
                popup.add(new AbstractAction("Delete Edge") {
                    public void actionPerformed(ActionEvent e) {
                        _GraphManager.fileDirty = true;
                        pickedEdgeState.pick(edge, false);
                        
                        if (edge instanceof SgEdge) {
                            SgEdge connection = (SgEdge) edge;

                            Graph currentGraph = _GraphManager.getGraph();
                            currentGraph.removeEdge(connection);
                            if (currentGraph != _GraphManager.getOriginalGraph()) {
                                _GraphManager.getOriginalGraph().removeEdge(connection);
                            }
                        }                      

                        vv.repaint();
                        _IGCAPTgui.graphChanged();
                    }
                });
                popup.add(new AbstractAction("Line Settings") {
                    public void actionPerformed(ActionEvent e) {
                        if (edge instanceof SgEdge) {
                            SgEdge connection = (SgEdge) edge;
                            _IGCAPTgui.showDialog(connection);
                        }
                    }
                });
            }

            if (popup != null && popup.getComponentCount() > 0) {
                popup.show(vv, e.getX(), e.getY());
            }
            
            popup = null;
        }
    }
}
