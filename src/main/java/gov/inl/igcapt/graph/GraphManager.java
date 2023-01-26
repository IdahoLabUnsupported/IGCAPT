/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gov.inl.igcapt.graph;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.subLayout.GraphCollapser;
import gov.inl.igcapt.components.Heatmap;
import gov.inl.igcapt.components.SgLayeredIcon;
import gov.inl.igcapt.components.SgMapImage;
import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.JSGMapViewer;
import org.openstreetmap.gui.jmapviewer.MapImageImpl;
import org.openstreetmap.gui.jmapviewer.MapLineImpl;

/**
 *
 * @author NADADJ
 */
public class GraphManager {
    // The GraphManager class is responsible for managing the 
    // graph by getting and setting: node, edge, GIS Map, graph,
    // Jung library items, etc. This is a singleton Class.
    
    // private static instance
    private static GraphManager GraphManagerObject;
   
    // private constructor
    private GraphManager() {      
    }

    // Static method to create instance of GraphManager class
    public static GraphManager getGraphManagerInstance() {

       // creates object if it's not already created
       if(GraphManagerObject == null) {
          GraphManagerObject = new GraphManager();
       }

        // returns the singleton object
        return GraphManagerObject;
    }
    
    // map viewer object (NEEDS TO BE FINAL??)
    private JSGMapViewer treeMap;
    
    // get and set edge index
    int edgeIndex = 0;  // edge index in the graph

    public void setEdgeIndex(int val){
        edgeIndex = val;
    }
    public int getEdgeIndex(){
        return edgeIndex;
    }
    
    // get and set node index
    int nodeIndex = 0;  // node index in the graph

    public void setNodeIndex(int val){
        nodeIndex = val;
    }
    public int getNodeIndex(){
        return nodeIndex;
    }
    
    // get current gis map
    JMapViewer currentGisMap;
    public JMapViewer getCurrentGisMap(){
        return currentGisMap;
    }
    
    // get and set clickpoint
    private Point clickPoint = null;
    public Point getClickPoint() {
        return clickPoint;
    }

    public void setClickPoint(Point clickPoint) {
        this.clickPoint = clickPoint;
    }
    
    // Jung Objects
    public VisualizationViewer<SgNodeInterface, SgEdge> vv = null;
    private AbstractLayout<SgNodeInterface, SgEdge> layout = null;
    public AbstractLayout<SgNodeInterface, SgEdge> getAbstractLayout(){
        return layout;
    }
    
    // get local layouts
    public AbstractLayout<SgNodeInterface, SgEdge> getLocalLayout() {
        return layout;
    }

    // Graph get and set etc.
    
    // Get graph, this will not include subgraphs.
    public Graph getGraph() {
        return vv.getGraphLayout().getGraph();
    }
    
    // graph objects
    public boolean fileDirty = false;
    private GraphCollapser collapser;
    private SgGraph tempGraph = null;
    private SgGraph originalGraph = null;
    private Heatmap heatmap;
    
    private JMapViewer map() {
        return treeMap.getViewer();
    }
    
    public SgGraph getOriginalGraph() {
        return originalGraph;
    }
    
    // Redraw the GIS images based upon the current Jung graph contents.
    public void updateGISObjects() {
        List<SgNodeInterface> nodes = new ArrayList<>(getGraph().getVertices());

        JMapViewer map = map();
        map.removeAllMapImages();
        map.removeAllMapPolygons();
        map.removeAllMapLines();

        for (SgNodeInterface node : nodes) {
            BufferedImage theImage;
            SgLayeredIcon icon = (SgLayeredIcon) node.getIcon();
            theImage = (BufferedImage) icon.getCompositeImage();
            MapImageImpl myimage = new SgMapImage(node.getLat(), node.getLongit(), theImage, 0, node);
            myimage.setId(node.getName());
            node.setMapImage(myimage);
            map.addMapImage(myimage);
        }

        List<SgEdge> edges = new ArrayList<>(getGraph().getEdges());
        
        for (SgEdge edge : edges) {
            Pair nodePair = getGraph().getEndpoints(edge);
            
            SgNodeInterface firstNode = (SgNodeInterface) nodePair.getFirst();
            SgNodeInterface secondNode = (SgNodeInterface) nodePair.getSecond();
            double latitude1 = firstNode.getLat();
            double longitude1 = firstNode.getLongit();
            double latitude2 = secondNode.getLat();
            double longitude2 = secondNode.getLongit();
            //Always set the first node to the lower id
            //This helps edge labels to alternate for readability
            Coordinate start;
            Coordinate end;
            if (secondNode.getId() < firstNode.getId()) {
                start = new Coordinate(latitude2, longitude2);
                end = new Coordinate(latitude1, longitude1);
            }
            else {
                start = new Coordinate(latitude1, longitude1);              
                end = new Coordinate(latitude2, longitude2);
            }

            Coordinate midPt = edge.getMidPoint();
            MapLineImpl line = null;
            MapLineImpl line2 = null;
            if (edge.getMidPoint() != null) {
                line = new MapLineImpl(start, midPt);
                line2 = new MapLineImpl(midPt, end);
                //Even ID label on line, Odd ID label on line2
                if (edge.getId() % 2 == 0) {
                    line.setId(edge.toString());
                }
                else {
                    line2.setId(edge.toString());
                }
                map.addMapLine(line2);
            }
            else {
                line = new MapLineImpl(start, end);
                line.setId(edge.toString());
            }
              
            map.addMapLine(line);
            if (edge.isOverHighUtilizationLimit()) {
                line.setColor(Color.red);
                if (line2 != null) {
                    line2.setColor(Color.red);
                }
            } else if (edge.isOverMidUtilizationLimit()) {
                line.setColor(Color.orange);
                if (line2 != null) {
                    line2.setColor(Color.orange);
                }
            } else if (!edge.isZeroUtilizationLimit()) {
                line.setColor(Color.green);
                if (line2 != null) {
                    line2.setColor(Color.green);
                }
            } else {
                line.setColor(Color.black);
                if (line2 != null) {
                    line2.setColor(Color.black);
                }
            }
        }

        // May have a heatmap that needs to be drawn.
        if (heatmap != null) {
            heatmap.Draw(map);
        }
    }
    
   
    // Cause the displays to redraw, both the logical and GIS views.
    public void refresh() {
        vv.repaint(); // logical refresh
        updateGISObjects(); // GIS refresh
    }

    public void clearEdgeUtilization() {

        // Reset utilization on all SgNodes.  Need to expand the graph
        // in case some are collapsed.
        Graph graph = getOriginalGraph();

        ArrayList<SgEdge> sgEdges = new ArrayList<>(graph.getEdges());
        for (SgEdge sgEdge : sgEdges) {
            sgEdge.setCalcTransRate(0.0);
        }

        refresh();
    }
    public void graphChanged() {
        fileDirty = true;

        clearEdgeUtilization();
        refresh();
    }
    
    public void SetHeatmap(Heatmap lheatmap){
        
        if (lheatmap != heatmap) {
            heatmap = lheatmap;
            this.updateGISObjects();            
        }
    }
    
    
    
    
}
