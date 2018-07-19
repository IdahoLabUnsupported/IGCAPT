/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openstreetmap.gui.jmapviewer;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.EditingGraphMousePlugin;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import org.apache.commons.collections15.Factory;

/**
 *(c) 2018 BATTELLE ENERGY ALLIANCE, LLC
 *ALL RIGHTS RESERVED 
 *
 * @author FRAZJD
 */
public class SGEditingGraphMousePlugin<V, E> extends EditingGraphMousePlugin<V, E> {
    
    public SGEditingGraphMousePlugin(Factory<V> vertexFactory, Factory<E> edgeFactory) {
        super(vertexFactory, edgeFactory);
    }
    
    private void addNodeToGraph(SgNodeInterface node, SgGraph graph) {
        if (node instanceof SgNode) {
            graph.addVertex(node);
        }
        else if (node instanceof SgGraph) {
            ArrayList<SgNodeInterface> subnodes = new ArrayList<>(((SgGraph)node).getVertices());
            
            for (SgNodeInterface subnode : subnodes) {
                if (subnode instanceof SgNode) {
                    graph.addVertex(subnode);
                }
                else if (subnode instanceof SgGraph) {
                    addNodeToGraph(subnode, graph);
                }
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void mousePressed(MouseEvent e) {
        
        IGCAPTgui.getInstance().setClickPoint(e.getPoint());
        super.mousePressed(e);
    }

        /**
     * If startVertex is non-null, and the mouse is released over an
     * existing vertex, create an undirected edge from startVertex to
     * the vertex under the mouse pointer. If shift was also pressed,
     * create a directed edge instead.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void mouseReleased(MouseEvent e) {
        if(checkModifiers(e)) {
            final VisualizationViewer<V,E> vv =
                (VisualizationViewer<V,E>)e.getSource();
            final Point2D p = e.getPoint();
            Layout<V,E> layout = vv.getModel().getGraphLayout();
            GraphElementAccessor<V,E> pickSupport = vv.getPickSupport();
            if(pickSupport != null) {
                final V vertex = pickSupport.getVertex(layout, p.getX(), p.getY());
                if(vertex != null && startVertex != null && vertex instanceof SgNodeInterface) {
                    Graph<V,E> graph = vv.getGraphLayout().getGraph();
                    
                    // Need to handle the three cases where either end, or both ends, are SgGraph nodes. On the end that
                    // is an SgGraph node we must connect the original graph to the RefNode.
                    E edge = edgeFactory.create();
                    graph.addEdge(edge,startVertex, vertex);

                    IGCAPTgui.getInstance().updateGISObjects();
                }
            }
            
            vv.repaint();
            startVertex = null;
            down = null;
            edgeIsDirected = EdgeType.UNDIRECTED;
            vv.removePostRenderPaintable(edgePaintable);
            vv.removePostRenderPaintable(arrowPaintable);
        }
    }
}
