/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gov.inl.igcapt.graph;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.renderers.BasicEdgeRenderer;
import edu.uci.ics.jung.visualization.subLayout.GraphCollapser;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.dnd.DropTarget;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.commons.collections15.Predicate;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.JSGMapViewer;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author NADADJ
 */
public class GraphManager {
    // The GraphManager class is responsible for managing the 
    // graph by getting and setting: node, edge, GIS Map, other 
    // graph items, Jung library items, etc. It is a singleton 
    // Class.
    
    // private static instance
    private static GraphManager instance;
   
    // private constructor
    private GraphManager() {  
        
        originalGraph = new SgGraph();

        layout = new StaticLayout<>(originalGraph,
                new Dimension(800, 800));
        
        vv = new VisualizationViewer<>(layout);
        
        treeMap = new JSGMapViewer("Components");
        
        collapser = new GraphCollapser(originalGraph);
        
        currentGisMap = map();
         
    }

    // Static method to create instance of GraphManager class
    public static GraphManager getInstance() {

       // creates class if it's not already created
       if(instance == null) {
          instance = new GraphManager();
       }

        // returns the singleton class
        return instance;
    }
    
    // TODO map viewer object call to it from IGCAPT 
    private JSGMapViewer treeMap;
    
    // get and set edge index in the graph
    int edgeIndex = 0;  

    public void setEdgeIndex(int val){
        edgeIndex = val;
    }
    public int getEdgeIndex(){
        return edgeIndex;
    }
    
    // get and set node index in the graph
    int nodeIndex = 0;  

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
    
    // clickpoint
    private Point clickPoint = null;
    public Point getClickPoint() {
        return clickPoint;
    }

    public void setClickPoint(Point clickPoint) {
        this.clickPoint = clickPoint;
    }
    
    // Jung Objects (some may not need to be here)
    private VisualizationViewer<SgNodeInterface, SgEdge> vv = null;
    
    public VisualizationViewer<SgNodeInterface, SgEdge> getVisualizationViewer(){
        return vv;
    }
    
  
    private AbstractLayout<SgNodeInterface, SgEdge> layout = null;
    
    public AbstractLayout<SgNodeInterface, SgEdge> getLayout(){
        return layout;
    }
    
    public AbstractLayout<SgNodeInterface, SgEdge> getAbstractLayout(){
        return layout;
    }
    
    // Drop Targets - only one can be active at a time
    DropTarget logicalModelDropTarget;
    DropTarget gisModelDropTarget;

    private void setCursor(Cursor predefinedCursor) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    public enum IGCAPTDropTarget {
        eLogicalDropTarget,
        eGISDropTarget,
        eUnknownDropTarget
    }

    public IGCAPTDropTarget getActiveDropTarget() {
        IGCAPTDropTarget returnval = IGCAPTDropTarget.eUnknownDropTarget;

        if (logicalModelDropTarget.isActive()) {
            returnval = IGCAPTDropTarget.eLogicalDropTarget;
        } else if (gisModelDropTarget.isActive()) {
            returnval = IGCAPTDropTarget.eGISDropTarget;
        }

        return returnval;
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
    
    private SgNodeInterface contextClickNode = null;
    
    public SgNodeInterface getContextClickNode() {
        return contextClickNode;
    }
    
    public void setContextClickNode(SgNodeInterface contextClickNode) {
        this.contextClickNode = contextClickNode;
    }
      
    // TODO graph objects (? Need to get and set or have here?)
    public boolean fileDirty = false;
    private GraphCollapser collapser;
    private SgGraph tempGraph = null;
    private SgGraph originalGraph = null;
    
    // private final JSGMapViewer treeMap;

    public JMapViewer map() {
        return treeMap.getViewer();
    }
    
    public SgGraph getOriginalGraph() {
        return originalGraph;
    }
    
    public void setOriginalGraph(SgGraph newOriginalGraph) {
        originalGraph = newOriginalGraph;
        getVisualizationViewer().getGraphLayout().setGraph(originalGraph);
    }
    
    public SgGraph getTempGraph() {
        return tempGraph;
    }
    
    public GraphCollapser getCollapser() {
        return collapser;
    } 
    
    public boolean getFileDirty() {
        return fileDirty;
    }
   
    /**
     * Get the percent nodes from the entire graph, excluding aggregate nodes.
     * @param percentOfNodes
     * @param exclusionList
     * @return 
     */
    public List<SgNode> getPercentNodes(int percentOfNodes, List<SgNode> exclusionList) {
        
        // Create a set of all nodes excluding aggregate nodes.
        SgGraph graph = getOriginalGraph();
        List<SgNode> nodesWOAggregates = new ArrayList<>();
        List<SgNodeInterface> allNodes = new ArrayList<>(graph.getVertices());
        
        for (SgNodeInterface node:allNodes) {
            
            if (!node.getRefNode().getIsAggregate()) {
                nodesWOAggregates.add(node.getRefNode());
            }
        }
        
        return getPercentNodes(nodesWOAggregates, percentOfNodes, exclusionList);
    }
    
    /**
     * Get percentOfNodes percent nodes from nodeList. Exclude those nodes listed in the exclusion list.
     * @param nodeList
     * @param percentOfNodes
     * @param exclusionList
     * @return 
     */
    public List<SgNode> getPercentNodes(List<SgNode> nodeList, int percentOfNodes, List<SgNode> exclusionList) {
        List<SgNode> returnval = new ArrayList<>();
        int listSize = nodeList.size();
        
        // Get a set of N unique random nodes
        int numRandomNumbers = (int)Math.floor(nodeList.size() * (percentOfNodes / 100.0));
        Random rand = new Random();
        
        int i = 0;
        while(i<numRandomNumbers) {
            int randNum = rand.nextInt(listSize);
            SgNode randNode = nodeList.get(randNum);
            
            boolean isExcluded = false;
            if (exclusionList != null) {
                isExcluded = exclusionList.contains(randNode);
            }
            
            boolean isAlreadyInList = returnval.contains(randNode);
            
            if (!isExcluded && !isAlreadyInList) {
                returnval.add(randNode);
                i++;
            }
        }
        
        return returnval;
    }
    
    // Jung Visualization Veiwer code
    public <V, E> void doNotPaintInvisibleVertices(
            VisualizationViewer<V, E> vv) {
        Predicate<Context<Graph<V, E>, V>> vertexIncludePredicate
                = new Predicate<Context<Graph<V, E>, V>>() {
            Dimension size = new Dimension();

            @Override
            public boolean evaluate(Context<Graph<V, E>, V> c) {
                vv.getSize(size);
                Point2D point = vv.getGraphLayout().transform(c.element);
                Point2D transformed
                        = vv.getRenderContext().getMultiLayerTransformer()
                                .transform(point);
                if (transformed.getX() < 0 || transformed.getX() > size.width) {
                    return false;
                }
                if (transformed.getY() < 0 || transformed.getY() > size.height) {
                    return false;
                }
                return true;
            }
        };
        vv.getRenderContext().setVertexIncludePredicate(vertexIncludePredicate);

        // NOTE: By default, edges will NOT be included in the visualization
        // when ONE of their vertices is NOT included in the visualization.
        // This may look a bit odd when zooming and panning over the graph.
        // Calling the following method will cause the edges to be skipped
        // ONLY when BOTH their vertices are NOT included in the visualization,
        // which may look nicer and more intuitive
        doPaintEdgesAtLeastOneVertexIsVisible(vv);
    }
    
    // See note at end of "doNotPaintInvisibleVertices"
    public <V, E> void doPaintEdgesAtLeastOneVertexIsVisible(
            VisualizationViewer<V, E> vv) {
        vv.getRenderer().setEdgeRenderer(new BasicEdgeRenderer<V, E>() {
            @Override
            public void paintEdge(RenderContext<V, E> rc, Layout<V, E> layout, E e) {
                GraphicsDecorator g2d = rc.getGraphicsContext();
                Graph<V, E> graph = layout.getGraph();
                if (!rc.getEdgeIncludePredicate().evaluate(
                        Context.<Graph<V, E>, E>getInstance(graph, e))) {
                    return;
                }

                Pair<V> endpoints = graph.getEndpoints(e);
                V v1 = endpoints.getFirst();
                V v2 = endpoints.getSecond();
                if (!rc.getVertexIncludePredicate().evaluate(
                        Context.<Graph<V, E>, V>getInstance(graph, v1))
                        && !rc.getVertexIncludePredicate().evaluate(
                                Context.<Graph<V, E>, V>getInstance(graph, v2))) {
                    return;
                }

                Stroke new_stroke = rc.getEdgeStrokeTransformer().transform(e);
                Stroke old_stroke = g2d.getStroke();
                if (new_stroke != null) {
                    g2d.setStroke(new_stroke);
                }

                drawSimpleEdge(rc, layout, e);

                // restore paint and stroke
                if (new_stroke != null) {
                    g2d.setStroke(old_stroke);
                }
            }
        });
    }
    
    // Caclulate the midpoint between two points
    private final double NEW_LINE_ADDER = 10;  //2;
    
    private Point calcMidPoint(Point p1, Point p2) {
        double newX = (p2.getX() + p1.getX()) / 2;
        double newY = (p2.getY() + p1.getY()) / 2;
        Point midpoint = new Point();
        midpoint.setLocation(newX, newY);
        return midpoint;
    }

    // occurrences indicates how many occurrences and arbitrarily adjusts the mid point
    public Coordinate calcNewMidPoint(double latDeg1, double lonDeg1, double latDeg2, double lonDeg2, int occurrences) {

        // checkOutside - when false calculate even if not displayed
        boolean checkOutside = false;
        // Convert lat/lon to point
        Point p1 = map().getMapPosition(latDeg1, lonDeg1, checkOutside);
        Point p2 = map().getMapPosition(latDeg2, lonDeg2, checkOutside);
        // If both nodes are same location return null
        if (p1.equals(p2)) {
            return null;
        }
        // get the midpoint of the two points
        Point midPoint = calcMidPoint(p1, p2);
        double slope = ((double)p2.y - (double)p1.y) / ((double)p2.x - (double)p1.x);
        double perpendicular_slope = -1 * (1 / slope);

        // y = mx + b (solve for b using the midPoint)
        double b = midPoint.getY() - (perpendicular_slope * midPoint.getX());
        
        // calculate a new x with the adder 
        // alternate adder / - adder for even/odd occurrences
        double adder;
        if (occurrences % 2 == 0) {
            adder = Math.ceil((double)occurrences / 2) * NEW_LINE_ADDER;
        }
        else {
            adder = Math.ceil((double)occurrences / 2) * NEW_LINE_ADDER * -1;
        }

        double newX = midPoint.getX() + adder;
        // calculate a new y with the equation y = mx + b
        double newY = (perpendicular_slope * newX) + b;
        Point newMidPoint = new Point();
        newMidPoint.setLocation(newX, newY);
        
        // Convert back to coordinate
        Coordinate c = new Coordinate(0.0, 0.0);
        ICoordinate ic = map().getPosition(newMidPoint.x, newMidPoint.y);
        c.setLat(ic.getLat());
        c.setLon(ic.getLon());
        return c;
    }
    
    public void writeGraphToDOM(Document doc, Element nodeRoot, Element edgeRoot) {

        // Mark collapsed nodes
        SgGraph graph = (SgGraph)getGraph();
        ArrayList<SgNodeInterface> graphNodes = new ArrayList<>(graph.getVertices()); 
        for (SgNodeInterface node : graphNodes) {
            if (node instanceof SgGraph) {
                node.getRefNode().setIsCollapsed(true);
            }
            else if (node instanceof SgNode) {
                ((SgNode)node).setIsCollapsed(false);
            }
        }

        // Now save out the uncollapsed graph.
        graph = getOriginalGraph();
        graphNodes = new ArrayList<>(getOriginalGraph().getVertices());
        for (SgNodeInterface graphNode : graphNodes) {

            if (graphNode instanceof SgNode) {

                SgNode sgNode = (SgNode) graphNode;

                // node element
                Element node = doc.createElement("node");
                nodeRoot.appendChild(node);

                // id element
                Element id = doc.createElement("id");
                id.appendChild(doc.createTextNode(Integer.toString(sgNode.getId())));
                node.appendChild(id);

                // type element
                Element type = doc.createElement("type");
                type.appendChild(doc.createTextNode(sgNode.getType()));
                node.appendChild(type);

                // canPassThru element
                Element enableDataSending = doc.createElement("enableDataSending");
                enableDataSending.appendChild(doc.createTextNode(Boolean.toString(sgNode.getEnableDataSending())));
                node.appendChild(enableDataSending);

                // canPassThru element
                Element enableDataPassThrough = doc.createElement("enableDataPassThrough");
                enableDataPassThrough.appendChild(doc.createTextNode(Boolean.toString(sgNode.getEnableDataPassThrough())));
                node.appendChild(enableDataPassThrough);

                // isAggregate element
                Element isAggregate = doc.createElement("isAggregate");
                isAggregate.appendChild(doc.createTextNode(Boolean.toString(sgNode.getIsAggregate())));
                node.appendChild(isAggregate);

                // isAggregate element
                Element isCollapsed = doc.createElement("isCollapsed");
                isCollapsed.appendChild(doc.createTextNode(Boolean.toString(sgNode.getIsCollapsed())));
                node.appendChild(isCollapsed);

                // payload element
                Element payload = doc.createElement("payload");
                payload.appendChild(doc.createTextNode(Integer.toString(sgNode.getDataToSend())));
                node.appendChild(payload);

                // maxLatency element
                Element maxLatency = doc.createElement("maxLatency");
                maxLatency.appendChild(doc.createTextNode(Integer.toString(sgNode.getMaxLatency())));
                node.appendChild(maxLatency);

                // x coordinate element
                Element xCoord = doc.createElement("xCoord");
                xCoord.appendChild(doc.createTextNode(Double.toString(layout.getX(sgNode))));
                node.appendChild(xCoord);

                // y coordinate element
                Element yCoord = doc.createElement("yCoord");
                yCoord.appendChild(doc.createTextNode(Double.toString(layout.getY(sgNode))));
                node.appendChild(yCoord);

                // These will need to be updated from the GIS.
                // latitude element
                Element latitude = doc.createElement("lat");
                latitude.appendChild(doc.createTextNode(Double.toString(sgNode.getLat())));
                node.appendChild(latitude);

                // longitude element
                Element longitude = doc.createElement("long");
                longitude.appendChild(doc.createTextNode(Double.toString(sgNode.getLongit())));
                node.appendChild(longitude);

                // name element
                Element name = doc.createElement("name");
                name.appendChild(doc.createTextNode(sgNode.getName()));
                node.appendChild(name);

                // End points element
                Element endPoints = doc.createElement("endPoints");
                node.appendChild(endPoints);

                List<Integer> endPointList = sgNode.getEndPointList();

                for (Integer endPointId : endPointList) {
                    Element endPointNode = doc.createElement("endPoint");
                    endPointNode.appendChild(doc.createTextNode(Integer.toString(endPointId)));
                    endPoints.appendChild(endPointNode);
                }
                
                // UserData
                Element userData = doc.createElement("userData");
                userData.appendChild(doc.createTextNode(sgNode.getUserData()));
                node.appendChild(userData);
            }
        }

        ArrayList<SgEdge> sgEdges = new ArrayList<SgEdge>(graph.getEdges());

        for (SgEdge sgEdge : sgEdges) {

            // edge element
            Element edge = doc.createElement("edge");
            edge.setAttribute("id", Integer.toString(sgEdge.getId()));
            Pair<SgNodeInterface> endpoints = graph.getEndpoints(sgEdge);

            SgNodeInterface endPt1 = endpoints.getFirst();
            SgNodeInterface endPt2 = endpoints.getSecond();

            if (endPt1 instanceof SgNode && endPt2 instanceof SgNode) {
                SgNode sgEndPt1 = (SgNode) endPt1;
                SgNode sgEndPt2 = (SgNode) endPt2;

                edge.setAttribute("source", Integer.toString(sgEndPt1.getId()));
                edge.setAttribute("target", Integer.toString(sgEndPt2.getId()));
            }

            // capacity element
            Element capacity = doc.createElement("capacity");
            capacity.appendChild(doc.createTextNode(Double.toString(sgEdge.getEdgeRate())));
            edge.appendChild(capacity);

            // name element
            Element nameElement = doc.createElement("name");
            nameElement.appendChild(doc.createTextNode(sgEdge.getName()));
            edge.appendChild(nameElement);

            edgeRoot.appendChild(edge);
        }
    }
    
    public void writeGraphToCSV(String fileName) {

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        // Mark collapsed nodes
        SgGraph graph = (SgGraph)getGraph();
        ArrayList<SgNodeInterface> graphNodes = new ArrayList<>(graph.getVertices()); 
        for (SgNodeInterface node : graphNodes) {
            if (node instanceof SgGraph) {
                node.getRefNode().setIsCollapsed(true);
            }
            else if (node instanceof SgNode) {
                ((SgNode)node).setIsCollapsed(false);
            }
        }

        // Now save out the uncollapsed graph.
        graph = getOriginalGraph();
        ArrayList<SgEdge> edges = new ArrayList<>(graph.getEdges());
        
        try (PrintWriter writer = new PrintWriter(new File(fileName))) {
            StringBuilder sb = new StringBuilder();
            
            // Write nodes
            graphNodes = new ArrayList<>(graph.getVertices());

            // Write field labels
            sb.append("Id,Name,Type,EnableDataSending,EnableDataPassThrough,IsAggregate,IsCollapsed,DataToSend,MaxLatency,X,Y,Latitude,Longitude,EndPt,UserData");
            sb.append("\n");
            
            for (SgNodeInterface graphNode : graphNodes) {
                if (graphNode instanceof SgNode sgNode) {

                    sb.append(sgNode.getId());
                    sb.append(",");
                    sb.append(sgNode.getName());
                    sb.append(",");
                    sb.append(sgNode.getAssociatedComponent().getName());
                    sb.append(",");
                    sb.append(sgNode.getEnableDataSending());
                    sb.append(",");
                    sb.append(sgNode.getEnableDataPassThrough());
                    sb.append(",");
                    sb.append(sgNode.getIsAggregate());
                    sb.append(",");
                    sb.append(sgNode.getIsCollapsed());
                    sb.append(",");
                    sb.append(sgNode.getDataToSend());
                    sb.append(",");
                    sb.append(sgNode.getMaxLatency());
                    sb.append(",");
                    sb.append(layout.getX(sgNode));
                    sb.append(",");
                    sb.append(layout.getY(sgNode));
                    sb.append(",");
                    sb.append(sgNode.getLat());
                    sb.append(",");
                    sb.append(sgNode.getLongit());
                    sb.append(",");
                    
                    sb.append("[");
                    if (sgNode != null && sgNode.getEndPointList() != null &&
                            sgNode.getEndPointList().size() > 0)
                    {
                        boolean first = true;
                        for(var endpt:sgNode.getEndPointList()) {
                            if (endpt != null && endpt.toString() != null && !endpt.toString().isBlank()) {
                                String endPt = endpt.toString();
                                if (!first) {
                                    sb.append(",");
                                    first = false;
                                }
                                sb.append(endPt);                               
                            }
                        }
                    }
                    sb.append("]");
                    sb.append(",");
                    sb.append(sgNode.getUserData());
                    sb.append('\n');
                }
            }
            
            sb.append("\n");
            
            // Write edges as pair of ids representing nodes at the ends.
            sb.append("node1,node2\n");
            for(var edge:edges) {
                Pair<SgNodeInterface> edgeEndPts = graph.getEndpoints(edge);

                sb.append(edgeEndPts.getFirst().getId());
                sb.append(",");
                sb.append(edgeEndPts.getSecond().getId());
                sb.append("\n");
            }
            
            writer.write(sb.toString());
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        setCursor(Cursor.getDefaultCursor());
    }
    
    // Get Node and Edge
    public SgNodeInterface getNode(Graph graph, int nodeID) {
        return ((SgGraph) graph).getVertices().stream().filter(node -> node.getId() == nodeID).findFirst().get();
    }

    public SgNodeInterface getNode(ArrayList<SgNodeInterface> nodeList, int nodeID) {
        SgNodeInterface returnval = null;

        for (SgNodeInterface node : nodeList) {
            if (node.getId() == nodeID) {
                returnval = node;
                break;
            }
        }

        return returnval;
    }

    public SgEdge getEdge(Graph graph, int edgeID) {
        return ((SgGraph) graph).getEdges().stream().filter(edge -> edge.getId() == edgeID).findFirst().get();
    }

    public SgEdge getEdge(ArrayList<SgEdge> edgeList, int edgeID) {
        SgEdge returnval = null;

        for (SgEdge edge : edgeList) {
            if (edge.getId() == edgeID) {
                returnval = edge;
                break;
            }
        }

        return returnval;
    }

    public SgNodeInterface getNode(int nodeID) {
        return (getNode(getGraph(), nodeID));
    }

    public SgEdge getEdge(int edgeID) {
        return (getEdge(getGraph(), edgeID));
    }
    
}