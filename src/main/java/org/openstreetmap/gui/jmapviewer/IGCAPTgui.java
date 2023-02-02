/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openstreetmap.gui.jmapviewer;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.LayeredIcon;
import edu.uci.ics.jung.visualization.MultiLayerTransformer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.GraphMouseListener;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
import edu.uci.ics.jung.visualization.decorators.DefaultVertexIconTransformer;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.renderers.Checkmark;
import edu.uci.ics.jung.visualization.subLayout.GraphCollapser;
import gov.inl.igcapt.components.AggregationDialog;
import gov.inl.igcapt.components.DependentUseCaseEntry;
import gov.inl.igcapt.components.HelpDialog;
import gov.inl.igcapt.components.NodeSettingsDialog;
import gov.inl.igcapt.components.Payload;
import gov.inl.igcapt.components.PayloadEditorForm;
import gov.inl.igcapt.components.SgLayeredIcon;
import gov.inl.igcapt.components.UseCaseEntry;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import gov.inl.igcapt.components.DataModels.ComponentDao;
import gov.inl.igcapt.components.DataModels.SgComponentData;
import gov.inl.igcapt.components.DataModels.SgComponentGroupData;
import gov.inl.igcapt.components.Heatmap;
import gov.inl.igcapt.components.SgMapImage;
import gov.inl.igcapt.wizard.CreateScenarioWizard;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Collections;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import gov.inl.igcapt.controllers.IGCAPTGraphMousePlugin;
import gov.inl.igcapt.graph.*;
import gov.inl.igcapt.properties.IGCAPTproperties;
import gov.inl.igcapt.view.*;
import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;
import org.openstreetmap.gui.jmapviewer.events.JMVCommandEvent;
import org.openstreetmap.gui.jmapviewer.interfaces.JMapViewerEventListener;
import org.openstreetmap.gui.jmapviewer.interfaces.TileLoader;
import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.BingAerialTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *(c) 2018 BATTELLE ENERGY ALLIANCE, LLC
 *ALL RIGHTS RESERVED 
 *
 * @author kur
 */

public class IGCAPTgui extends JFrame implements JMapViewerEventListener, DropTargetListener {

    /**
     * @return the payloadEditorForm
     */
    public PayloadEditorForm getPayloadEditorForm() {
        return payloadEditorForm;
    }

    /**
     * @param payloadEditorForm the payloadEditorForm to set
     */
    public void setPayloadEditorForm(PayloadEditorForm payloadEditorForm) {
        this.payloadEditorForm = payloadEditorForm;
    }

    /**
     * @return the lastPath
     */
    public String getLastPath() {
        return lastPath;
    }

    /**
     * @param lastPath the lastPath to set
     */
    public void setLastPath(String lastPath) {
        this.lastPath = lastPath;
    }

    /**
     * @return the jtp
     */
    public JTabbedPane getJtp() {
        return jtp;
    }

    /**
     * @return the analysisCanceled
     */
    public boolean isAnalysisCanceled() {
        return analysisCanceled;
    }

    /**
     * @param analysisCanceled the analysisCanceled to set
     */
    public void setAnalysisCanceled(boolean analysisCanceled) {
        this.analysisCanceled = analysisCanceled;
    }

    static final IGCAPTproperties IGCAPTPROPERTIES = IGCAPTproperties.getInstance();
    private static final long serialVersionUID = 1L;

    private final JSGMapViewer treeMap; 

    private final JLabel zoomLabel;
    private final JLabel zoomValue;
    private final JLabel xyPosition;

    private final JLabel mperpLabelName;
    private final JLabel mperpLabelValue;
    private String lastPath = "";
    private DragTree7 tree = null;
    public List<Pair<SgNodeInterface>>nodePairList = new ArrayList<>();
    
    // Redraw the GIS images based upon the current Jung graph contents.
    public void updateGISObjects() {
        List<SgNodeInterface> nodes = new ArrayList<>(GraphManager.getInstance().getGraph().getVertices());

        JMapViewer map = GraphManager.getInstance().map();
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

        List<SgEdge> edges = new ArrayList<>(GraphManager.getInstance().getGraph().getEdges());
        
        for (SgEdge edge : edges) {
            Pair nodePair = GraphManager.getInstance().getGraph().getEndpoints(edge);
            
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
    
    private Heatmap heatmap;
    public Heatmap getHeatmap() {
        return heatmap;
    }
    
    public void SetHeatmap(Heatmap lheatmap){
        
        if (lheatmap != heatmap) {
            heatmap = lheatmap;
            updateGISObjects();            
        }
    }
    private GraphCollapser collapser = GraphManager.getInstance().getCollapser();
    private JMapViewer currentGisMap = GraphManager.getInstance().getCurrentGisMap();
    private SgGraph tempGraph = GraphManager.getInstance().getTempGraph();
    private SgGraph originalGraph = GraphManager.getInstance().getOriginalGraph();
    public boolean fileDirty = GraphManager.getInstance().getFileDirty();
    
    int nodeIndex = GraphManager.getInstance().getNodeIndex();
    int edgeIndex = GraphManager.getInstance().getEdgeIndex();

    public void refreshTree() {
        tree.refreshTreeModel();
     }
    
    // Cause the displays to redraw, both the logical and GIS views.
    public void refresh() {
        vv.repaint(); // logical refresh
        updateGISObjects(); // GIS refresh
    }
    
    public void clearEdgeUtilization() {

        // Reset utilization on all SgNodes.  Need to expand the graph
        // in case some are collapsed.
        Graph graph = GraphManager.getInstance().getOriginalGraph();

        ArrayList<SgEdge> sgEdges = new ArrayList<>(graph.getEdges());
        for (SgEdge sgEdge : sgEdges) {
            sgEdge.setCalcTransRate(0.0);
        }

        refresh();
    }
    
    // Jung
    public VisualizationViewer<SgNodeInterface, SgEdge> vv = null;
    private AbstractLayout<SgNodeInterface, SgEdge> layout = null;
    public AbstractLayout<SgNodeInterface, SgEdge> getAbstractLayout(){
        return layout;
    }
    
    private JTabbedPane jtp;
    
    boolean toolTipsEnabled = true;
    public boolean isToolTipsEnabled(){
        return toolTipsEnabled;
    }
    boolean showAllAnalysisResults = false;

    // Drop Targets - only one can be active at a time
    DropTarget logicalModelDropTarget;
    DropTarget gisModelDropTarget;
    JMenu modeMenu;
    String currentTypeUuidStr = null; // default to Utility DCC
    private Mode mode;
    private boolean analysisCanceled = false;
    private List<SgComponentGroupData> sgComponentGroupList = null;
 
    private PayloadEditorForm payloadEditorForm = null;
    private Payload payload = new Payload();
    public Payload getPayload() {
        return payload;
    }
    
    public void setPayload(Payload lpayload) {
        payload = lpayload;
    }
    
    private HashMap<String, Icon> _layerIconMap = new HashMap<>();

    public HashMap<String, Icon> getLayerIconMap() {
        return _layerIconMap;
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

    private Icon _unknownNodeIcon = null;

    public Icon getUnknownNodeIcon() {

        if (_unknownNodeIcon == null) {
            String unknownNodeIconPath = IGCAPTPROPERTIES.getPropertyKeyValue("unknownNodeIcon");
            _unknownNodeIcon = loadIcon(unknownNodeIconPath);
        }

        return _unknownNodeIcon;
    }

    //private boolean analysisCompleted = false;
    private static final IGCAPTgui _IGCAPTgui = new IGCAPTgui();

    public static IGCAPTgui getInstance() {
        return _IGCAPTgui;
    }

    public final void loadLayerIcons() {

        // These correspond to the names from the property file.
        String[] iconKeys = new String[]{
            "expandIcon",
            "collapseIcon",
            "aggregateIcon",
            "selectionIcon"
        };

        for (String iconKey : iconKeys) {
            String whichIcon = iconKey;
            String iconPath = IGCAPTPROPERTIES.getPropertyKeyValue(whichIcon);

            if (null != iconPath && !iconPath.isEmpty()) {
                
                _layerIconMap.put(whichIcon, loadIcon(iconPath));
            }
        }
    }

    public List<SgComponentGroupData> getComponentGroupList() {
        return sgComponentGroupList;
    }

    public void setCurrentType(String currentType) {
        this.currentTypeUuidStr = currentType;
    }

    public void setCurrentType(UUID currentTypeUuid) {
        currentTypeUuidStr = currentTypeUuid.toString();
    }

    public String getCurrentTypeUuidStr() {
        return currentTypeUuidStr;
    }

    private Icon loadIcon(String path) {

        Icon returnval = null;

        if (path != null && !path.isEmpty()) {
            BufferedImage img = null;
            try {
                File iconFile = new File(path);
                img = ImageIO.read(new FileInputStream(iconFile));
            } catch (IOException e) {
                e.printStackTrace();
            }

            ImageIcon newicon = new ImageIcon(img);
            returnval = new SgLayeredIcon(newicon.getImage()); // LayeredIcon used for checking an icon
        }

        return returnval;
    }
    
    /**
     * Constructs the {@code DemoKD}.
     */
    public IGCAPTgui() {
        super("Intelligent Grid Communications & Analysis Planning Tool");
        setSize(400, 400);
        
        lastPath = IGCAPTproperties.getInstance().getPropertyKeyValue("LastPath");

        treeMap = new JSGMapViewer("Components");

        // Listen to the map viewer for user operations so components will
        // receive events and update
        GraphManager.getInstance().map().addJMVListener(this);

        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                confirmExit();
            }
        });
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        JPanel panel = new JPanel(new BorderLayout());
        JPanel panelTop = new JPanel();
        JPanel panelBottom = new JPanel();
        JPanel helpPanel = new JPanel();

        mperpLabelName = new JLabel("Meters/Pixels: ");
        mperpLabelValue = new JLabel(String.format("%s", GraphManager.getInstance().map().getMeterPerPixel()));

        zoomLabel = new JLabel("Zoom: ");
        zoomValue = new JLabel(String.format("%s", GraphManager.getInstance().map().getZoom()));
        xyPosition = new JLabel("x,y = ");
        add(panel, BorderLayout.NORTH);
        add(helpPanel, BorderLayout.SOUTH);
        panel.add(panelTop, BorderLayout.NORTH);
        panel.add(panelBottom, BorderLayout.SOUTH);

        mperpLabelName.setVisible(false);
        mperpLabelValue.setVisible(false);
        zoomLabel.setVisible(false);
        zoomValue.setVisible(false);
        xyPosition.setVisible(false);

        JLabel helpLabel = new JLabel("Use right mouse button to move,\n "
                + "left double click or mouse wheel to zoom.");
        helpPanel.add(helpLabel);
        JComboBox<TileSource> tileSourceSelector = new JComboBox<>(new TileSource[]{
            new OsmTileSource.Mapnik(),
            new OsmTileSource.CycleMap(),
            new BingAerialTileSource(),});
        tileSourceSelector.addItemListener((ItemEvent e) -> {
            GraphManager.getInstance().map().setTileSource((TileSource) e.getItem());
        });

        tileSourceSelector.setVisible(false);

        JComboBox<TileLoader> tileLoaderSelector;
        tileLoaderSelector = new JComboBox<>(new TileLoader[]{new OsmTileLoader(GraphManager.getInstance().map())});
        tileLoaderSelector.addItemListener((ItemEvent e) -> {
            GraphManager.getInstance().map().setTileLoader((TileLoader) e.getItem());
        });
        GraphManager.getInstance().map().setTileLoader((TileLoader) tileLoaderSelector.getSelectedItem());
        panelTop.add(tileSourceSelector);
        panelTop.add(tileLoaderSelector);

        tileLoaderSelector.setVisible(false);

        // GIS Enabled
        final JCheckBox gisEnabled = new JCheckBox("GIS", true);
        gisEnabled.addActionListener((ActionEvent e) -> {
            if (gisEnabled.isSelected()) {
                currentGisMap = GraphManager.getInstance().map();
                jtp.add("Geographical Model", currentGisMap);
                jtp.setSelectedIndex(1);
                logicalModelDropTarget.setActive(false);
                gisModelDropTarget.setActive(true);
            } else {
                jtp.remove(currentGisMap);
            }
        });

        // Tool Tips Enabbled
        final JCheckBox toolTipsCheckbox = new JCheckBox("ToolTips", true);
        toolTipsCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (toolTipsCheckbox.isSelected()) {
                    toolTipsEnabled = true;
                } else {
                    toolTipsEnabled = false;
                }
            }
        });

        // Show All Analysis Results
        final JCheckBox allAnalysisResultsCheckbox = new JCheckBox("Show All Analysis Results", false);
        allAnalysisResultsCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (allAnalysisResultsCheckbox.isSelected()) {
                    showAllAnalysisResults = true;
                } else {
                    showAllAnalysisResults = false;
                }
            }
        });

        JButton collapse = new JButton("Swap Graphs");
        collapse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if (GraphManager.getInstance().getGraph() != originalGraph) {
                    tempGraph = (SgGraph) layout.getGraph();
                    layout.setGraph(originalGraph);
                    collapse.setText("Swap Graphs");
                } else if (tempGraph != null) {
                    layout.setGraph(tempGraph);
                    collapse.setText("Swap Graphs*");
                }

                vv.repaint();
            }
        });
        collapse.setVisible(false);

        JButton expand = new JButton("Expand");
        expand.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Collection picked = new HashSet(vv.getPickedVertexState().getPicked());
                for (Object v : picked) {
                    if (v instanceof Graph) {

                        Graph g = collapser.expand(GraphManager.getInstance().getGraph(), (Graph) v);
                        vv.getRenderContext().getParallelEdgeIndexFunction().reset();
                        layout.setGraph(g);
                    }
                    vv.getPickedVertexState().clear();
                    vv.repaint();
                }
            }
        });
        expand.setVisible(false);

        panelBottom.add(gisEnabled);
        panelBottom.add(toolTipsCheckbox);
        panelBottom.add(allAnalysisResultsCheckbox);
        panelBottom.add(collapse);
        panelBottom.add(expand);

        panelTop.add(zoomLabel);
        panelTop.add(zoomValue);
        panelTop.add(mperpLabelName);
        panelTop.add(mperpLabelValue);
        panelTop.add(xyPosition);

        add(treeMap, BorderLayout.CENTER);

        // set initial map location or position
        GraphManager.getInstance().map().setDisplayPosition(new Coordinate(43.5203489, -112.0452956), 5); // WCB INL

        GraphManager.getInstance().map().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    GraphManager.getInstance().map().getAttribution().handleAttribution(e.getPoint(), true);
                }
            }
        });

        originalGraph = new SgGraph();
        layout = new StaticLayout<>(originalGraph,
                new Dimension(800, 800));

        vv = new VisualizationViewer<>(layout);

        collapser = new GraphCollapser(originalGraph);

        // This class GraphMouseListener is used to snoop mouse interactions
        // with the graph. It is used here to detect when the graph has 
        // changed.
        vv.addGraphMouseListener(new GraphMouseListener() {

            // Hold the starting position of a vertex so we can detect
            // when its position changed in response to click and drag.
            double oldX, oldY;

            @Override
            public void graphClicked(Object v, MouseEvent me) {
                int clickCount = me.getClickCount();

                if (clickCount > 1) {
                    me.consume();
                }
            }

            @Override
            public void graphPressed(Object v, MouseEvent me) {
                if (v instanceof SgNode) {
                    SgNode node = (SgNode) v;
                    oldX = layout.getX(node);
                    oldY = layout.getY(node);
                }
            }

            @Override
            public void graphReleased(Object v, MouseEvent me) {

                if (v instanceof SgNode node) {
                    double newX, newY;
                    newX = layout.getX(node);
                    newY = layout.getY(node);

                    if (newX != oldX || newY != oldY) {
                        fileDirty = true;
                    }
                }
            }
        });

        Transformer<SgEdge, Paint> colorTransformer = (SgEdge e) -> {

            Color returnval = Color.BLACK;

            if (e.isOverHighUtilizationLimit()) {
                returnval = Color.RED;
            } else if (e.isOverMidUtilizationLimit()) {
                returnval = Color.ORANGE;
            } else if (!e.isZeroUtilizationLimit()) {
                returnval = Color.GREEN;
            }

            return returnval;
        };
        vv.getRenderContext().setEdgeDrawPaintTransformer(colorTransformer);

        //jmy
        vv.getRenderingHints().remove(RenderingHints.KEY_ANTIALIASING);
        GraphManager.getInstance().doNotPaintInvisibleVertices(vv);
        vv.setBackground(Color.white);

        vv.getRenderContext().setEdgeLabelTransformer(e -> e.getName());
        vv.getRenderContext().setVertexLabelTransformer(v -> v.getName());

        // try to transform the nodes to icons -- this did NOT draw the icons!
        // Return the shape that is appropriate for this node.
        // If vertex is an SgNode then return its icon.  If it is a graph (collapsed graph)
        // return the icon of the reference node.
        final DefaultVertexIconTransformer<SgNodeInterface> vertexIconFunction
                = new DefaultVertexIconTransformer<SgNodeInterface>() {

            @Override
            public Icon transform(final SgNodeInterface v) {

                Icon returnval = (Icon) v.getIcon();
                return returnval;
            }
        };

        vv.getRenderContext().setVertexIconTransformer(vertexIconFunction);

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                createTreePanel(),
                createTabbedPane());

        // Set up DROP Target for Logical Model as active
        logicalModelDropTarget = new DropTarget(vv, DnDConstants.ACTION_COPY_OR_MOVE, this);
        // Set up DROP Target for GIS Model as inactive
        gisModelDropTarget = new DropTarget(GraphManager.getInstance().map(), DnDConstants.ACTION_COPY_OR_MOVE, GraphManager.getInstance().map(), false);

        // create the GIS view so that when we load a topology file, the icons can be placed there
        gisEnabled.setSelected(true);
        currentGisMap = GraphManager.getInstance().map();
        jtp.add("Geographical Model", currentGisMap);
        jtp.setSelectedIndex(1);
        logicalModelDropTarget.setActive(false);
        gisModelDropTarget.setActive(true);

        // ChangeListener is invoked when a user selects a tab
        jtp.addChangeListener((ChangeEvent e) -> {
            modeMenu.setEnabled(false);
            JTabbedPane myTabbedPane = (JTabbedPane) e.getSource();
            int selectedIndex = myTabbedPane.getSelectedIndex();
            String title1 = myTabbedPane.getTitleAt(selectedIndex);
            if (title1.equalsIgnoreCase("Geographical Model")) {
                logicalModelDropTarget.setActive(false);
                gisModelDropTarget.setActive(true);
            } else if (title1.equalsIgnoreCase("Logical Model")) {
                modeMenu.setEnabled(true);
                gisModelDropTarget.setActive(false);
                logicalModelDropTarget.setActive(true);
            }
        });
        splitPane.setDividerLocation(300);
        splitPane.setOneTouchExpandable(true);

        getContentPane().add(splitPane, BorderLayout.CENTER);

        // Node Factory is only invoked when a user single clicks on the logical model pane
        Factory<SgNodeInterface> sgvertexFactory = () -> {
            SgComponentData sgComponent = getComponentByUuid(currentTypeUuidStr);
            String typeName = sgComponent.getName();
            boolean showAggregationComponent = false;
            SgNodeInterface returnval = null;
            
            if (typeName.length() >= 11 && typeName.substring(0, 11).equalsIgnoreCase("Aggregation")) {
                AggregationDialog aggregationDialog = new AggregationDialog(IGCAPTgui.getInstance(), true);
                showAggregationComponent = aggregationDialog.showDialog();
                
                if (showAggregationComponent) {
                    
                    // Create aggregate node, which is the type selected in the dialog
                    // Then create all the subnodes.
                    ArrayList<gov.inl.igcapt.components.Pair<String, Integer>> aggregateConfig = aggregationDialog.getAggregateConfiguration();
                    SgComponentData selectedAggregateComponent = aggregationDialog.getSelectedComponent();
                    
                    returnval = createAggregation(aggregateConfig, selectedAggregateComponent, GraphManager.getInstance().getClickPoint(), new Coordinate(0.0, 0.0), aggregationDialog.getDefaultMaxRate());
                }
            } else {
                returnval = new SgNode(nodeIndex, currentTypeUuidStr, typeName + "_" + String.valueOf(nodeIndex), true, true, false, 0, 0, "");
                nodeIndex++;
            }
            
            return returnval;
        };

        // Edge Factory is called whenever a user connects 2 nodes with an edge
        Factory<SgEdge> sgedgeFactory = new Factory<SgEdge>() {
            int i = 0;

            @Override
            public SgEdge create() {
                SgEdge e1 = new SgEdge(edgeIndex, "e" + edgeIndex, 1.0, 0.0, 0.0);
                edgeIndex++;

                return e1;
            }
        };

        final SGEditingModalGraphMouse<SgNodeInterface, SgEdge> graphMouse
                = new SGEditingModalGraphMouse<>(vv.getRenderContext(), sgvertexFactory, sgedgeFactory, this);

        // allow user to switch between different mouse modes
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem newTopology = new JMenuItem("New Topology");
        JMenuItem loadTopology = new JMenuItem("Load Topology");
        JMenuItem saveTopology = new JMenuItem("Save Topology");
        JMenuItem newScenario = new JMenuItem("New Scenario");
        JMenuItem exportData = new JMenuItem("Export...");
        JMenuItem exitItem = new JMenuItem("Exit");
        fileMenu.add(newTopology);
        fileMenu.add(loadTopology);
        fileMenu.add(saveTopology);
        fileMenu.add(new JSeparator()); // SEPARATOR
        fileMenu.add(newScenario);
        fileMenu.add(new JSeparator()); // SEPARATOR
        fileMenu.add(new AddImportMenuItem(null));
        fileMenu.add(exportData);
        fileMenu.add(new JSeparator());
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        fileMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent ev) {
                saveTopology.setEnabled(fileDirty);
                
                boolean isGraphPresent = GraphManager.getInstance().getOriginalGraph().getVertexCount() > 0;
                exportData.setEnabled(isGraphPresent);
            }

            @Override
            public void menuCanceled(MenuEvent ev) {
            }

            @Override
            public void menuDeselected(MenuEvent e) {
            }
        });

        // exit the app when Exit menu item selected
        exitItem.addActionListener((ActionEvent ev) -> {
            confirmExit();
        });

        newTopology.addActionListener((ActionEvent ev) -> {
            if (clearGraph()) {
                graphChanged();
                fileDirty = false;
            }
        });
        
        newScenario.addActionListener((ActionEvent ev) -> {
            CreateScenarioWizard createScenarioWizard = new CreateScenarioWizard(this, true);
        });

        loadTopology.addActionListener((ActionEvent ev) -> {
            
            JFileChooser chooser = new JFileChooser();
            FileFilter filter = new FileNameExtensionFilter("IGCAP Files", "igc");
            chooser.setFileFilter(filter);
            
            if (lastPath != null && !lastPath.isEmpty()) {
                File lastPath1 = new File(IGCAPTgui.this.getLastPath());
                if (lastPath1.exists()) {
                    chooser.setCurrentDirectory(lastPath1);
                }
            }
            if (chooser.showOpenDialog(IGCAPTgui.getInstance()) == JFileChooser.APPROVE_OPTION) {
                heatmap = null; // Don't call SetHeatmap because it will redraw.
                SwingUtilities.invokeLater(() -> {
                    openFile(chooser);
                });
            }
        });

        // get the file name for Save Topology
        saveTopology.addActionListener((ActionEvent ev) -> {
            JFileChooser chooser = new JFileChooser();
            
            if (!lastPath.isEmpty()) {
                chooser.setCurrentDirectory(new File(getLastPath()));
            }
            
            chooser.setFileFilter(new FileNameExtensionFilter("IGCAP Files", "igc"));
            
            if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                SwingUtilities.invokeLater(() -> {
                    saveFile(chooser);
                });
            }
        });
        
        exportData.addActionListener((ActionEvent ev) -> {
            JFileChooser chooser = new JFileChooser();
            
            if (!lastPath.isEmpty()) {
                chooser.setCurrentDirectory(new File(getLastPath()));
            }
            
            if (chooser.showSaveDialog(IGCAPTgui.getInstance()) == JFileChooser.APPROVE_OPTION) {
                SwingUtilities.invokeLater(() -> {
                    exportFile(chooser);
                });
            }
        });

        modeMenu = graphMouse.getModeMenu();  // obtain mode menu from the mouse
        modeMenu.setText("Mouse Mode");
        modeMenu.setIcon(null); // using this in a main menu
        modeMenu.setPreferredSize(new Dimension(100, 20)); // change the size
        menuBar.add(modeMenu);

        menuBar.add(createComponentsMenu());
        menuBar.add(createAnalysisMenu());

        JMenu helpMenu = new JMenu("Help");
        JMenuItem helpIGCAPT = new JMenuItem("IGCAPT Help");
        JMenuItem aboutIGCAPT = new JMenuItem("About IGCAPT");
        helpMenu.add(helpIGCAPT);
        helpMenu.add(new JSeparator()); // SEPARATOR
        helpMenu.add(aboutIGCAPT);
        menuBar.add(helpMenu);

        helpIGCAPT.addActionListener((ActionEvent ev) -> {
            HelpDialog helpDialog = new HelpDialog(null, true);
            
            helpDialog.setVisible(true);
        });

        aboutIGCAPT.addActionListener((ActionEvent ev) -> {
        });

        this.setJMenuBar(menuBar);
        //

        // the EditingGraphMouse will pass mouse event coordinates to the
        // vertexLocations function to set the locations of the vertices as
        // they are created
        vv.setGraphMouse(graphMouse);
        vv.addKeyListener(graphMouse.getModeKeyListener());

        graphMouse.add(myGraphMousePlugin);
        graphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);

        // Get the pickedState and add a listener that will decorate the
        // Vertex images with a checkmark icon when they are picked
        PickedState<SgNodeInterface> ps = vv.getPickedVertexState();
        ps.addItemListener(new PickWithIconListener(vertexIconFunction));

        loadLayerIcons();

        pack();
    }

    private JMenu createComponentsMenu() {
        JMenu componentsMenu = new JMenu("Components");
        componentsMenu.add(new AddComponentMenuItem(null));
        
        return componentsMenu;
    }

    private JMenu createAnalysisMenu() {
        JMenu analysisMenu = new JMenu("Analysis");
        JMenuItem applyPayloadItem;
        JMenuItem analyzeTopologyItem;
        JMenuItem importResultsItem;
        JMenuItem showHeatmapItem;
        JMenuItem clearHeatmapItem;
        
        analysisMenu.add(applyPayloadItem = new AddApplyPayloadMenuItem());
        analysisMenu.add(analyzeTopologyItem = new AddAnalyzeTopologyMenuItem());
        analysisMenu.add(importResultsItem = new AddImportNs3ResultsMenuItem());
        analysisMenu.add(new AddClearAnalysisResultsMenuItem());
        analysisMenu.add(new JSeparator()); // SEPARATOR
        analysisMenu.add(showHeatmapItem = new AddShowHeatmapMenuItem());
        analysisMenu.add(clearHeatmapItem = new AddClearHeatmapMenuItem());
        
        analysisMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent ev) {

                boolean isGraphPresent = GraphManager.getInstance().getOriginalGraph().getVertexCount() > 0;
                analyzeTopologyItem.setEnabled(isGraphPresent);
                applyPayloadItem.setEnabled(isGraphPresent);
                importResultsItem.setEnabled(isGraphPresent);
                showHeatmapItem.setEnabled(isGraphPresent);
                clearHeatmapItem.setEnabled(heatmap != null);
            }
            
            @Override
            public void menuCanceled(MenuEvent ev) {
            }

            @Override
            public void menuDeselected(MenuEvent e) {
            }
        });
        
        return analysisMenu;
    }

    public void applyPayload() {
        
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        // Clear all existing payloads.
        for (SgNodeInterface node : GraphManager.getInstance().getOriginalGraph().getVertices()) {
            if (node instanceof SgNode sgNode) {

                sgNode.setMaxLatency(0);
                sgNode.setDataToSend(0);
                sgNode.clearUseCaseUserData();
                
                // This gets set according to the element found in the SGComponents.xml
                // sgNode.getEndPointList().clear();
            }
        }
        
        // Get percent of all nodes (exclude aggregate nodes)
        for(UseCaseEntry entry:payload.payloadUseCaseList) {
            int percentToApply = entry.getPercentToApply();
            
            List<SgNode> percentNodes = GraphManager.getInstance().getPercentNodes(percentToApply, null);
            
            for (SgNode node:percentNodes) {
                node.applyUseCase(entry.getUseCaseName());
            }
        }
        
        for (DependentUseCaseEntry entry:payload.payloadDependentUseCaseList) {
            int percentToApply = entry.getPercentToApply();
            
            // Get the original set from which we will choose the dependent set.
            List<SgNode> percentNodes = GraphManager.getInstance().getPercentNodes(percentToApply, null);
            List<SgNode> exclusionSet = new ArrayList<>();
            
            for (UseCaseEntry depEntry:entry.useCases) {
                int depPercentToApply = depEntry.getPercentToApply();
                List<SgNode> depNodesToApply = GraphManager.getInstance().getPercentNodes(percentNodes, depPercentToApply, exclusionSet);
                
                for (SgNode node:depNodesToApply) {
                    node.applyUseCase(depEntry.getUseCaseName());
                }
                
                exclusionSet.addAll(depNodesToApply);
            }
        }
        
        setCursor(Cursor.getDefaultCursor());
   }
    
    // The simplest way to clear the graph is to create a new instance.
    public boolean clearGraph() {

        boolean returnval = false;
        int result = 0;

        edgeIndex = 0;
        nodeIndex = 0;
        
        if (GraphManager.getInstance().getGraph().getVertexCount() > 0) {
            result = JOptionPane.showConfirmDialog(IGCAPTgui.getInstance(), "Are you sure you want to clear the current graph?",
                    "alert", JOptionPane.OK_CANCEL_OPTION);
        }

        if (result == 0) {
            returnval = true;
        
            // Clear heat map
            heatmap = null; // Don't call SetHeatmap or it will redraw.
            tempGraph = null;
            originalGraph = new SgGraph();
            vv.getGraphLayout().setGraph(originalGraph);

            // There does not appear to be a way to clear the collapser's state other than creating a new one.
            collapser = new GraphCollapser(originalGraph);

            vv.repaint();
        }
        
        return returnval;
    }
    
    public void graphChanged() {
        fileDirty = true;

        clearEdgeUtilization();
        refresh();
    }

    public String getXyPosition() {
        return xyPosition.getText();
    }

    public void setXyPosition(String newtext) {
        xyPosition.setText("x,y = " + newtext);
    }

    public void Initialize() {
    } 

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Mode getMode() {
        return mode;
    }

    private void editSgComponents() {

    }

    private void confirmExit() {
        if (fileDirty) {
            if (JOptionPane.showConfirmDialog(IGCAPTgui.getInstance(),
                    "Are you sure you want to exit? The current scenario has not been saved.", "Confirm Close",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {

                IGCAPTproperties.getInstance().storeProperties();
                System.exit(0);
            }
        } else {
            IGCAPTproperties.getInstance().storeProperties();
            System.exit(0);
        }
    }

    public void expand() {
        Collection picked = new HashSet(vv.getPickedVertexState().getPicked());
        for (Object v : picked) {
            if (v instanceof Graph) {

                Graph g = collapser.expand(GraphManager.getInstance().getGraph(), (Graph) v);
                vv.getRenderContext().getParallelEdgeIndexFunction().reset();
                layout.setGraph(g);
            }
            vv.getPickedVertexState().clear();
            vv.repaint();
        }

        updateGISObjects();
    }
    
    public void collapse() {

        Collection picked = new HashSet(vv.getPickedVertexState().getPicked());
        if (picked.size() > 1) {
            Graph inGraph = GraphManager.getInstance().getGraph();

            SgNodeInterface ctextClickNode = GraphManager.getInstance().getContextClickNode();

            // Get the selected nodes that comprise the sub-graph.
            Graph clusterGraph = collapser.getClusterGraph(inGraph, picked);
            if (clusterGraph instanceof SgGraph && ctextClickNode instanceof SgNode) {
                SgGraph sgGraph = (SgGraph) clusterGraph;
                sgGraph.setRefNode((SgNode) ctextClickNode);
            }

            Graph collapseGraph = collapser.collapse(GraphManager.getInstance().getGraph(), clusterGraph);

            // If available, use the contextClickNode position.
            Point2D cp;

            if (ctextClickNode != null) {
                cp = (Point2D) layout.transform(ctextClickNode);
            } else {
                double sumx = 0;
                double sumy = 0;
                for (Object v : picked) {
                    Point2D p = (Point2D) layout.transform((SgNodeInterface) v);
                    sumx += p.getX();
                    sumy += p.getY();
                }
                cp = new Point2D.Double(sumx / picked.size(), sumy / picked.size());
            }
            vv.getRenderContext().getParallelEdgeIndexFunction().reset();
            layout.setGraph(collapseGraph);

            // This will always be the case...unless something goes wrong, of course.
            if (clusterGraph instanceof SgNodeInterface) {
                layout.setLocation((SgNodeInterface) clusterGraph, cp);
            }
            vv.getPickedVertexState().clear();
            
            refresh();
        }
    }

    //add code to this method to create the Logical topology diagram from xml
    void openFile(JFileChooser chooser) {

        if (!clearGraph()) {
            return;
        }

        String selectedOpenFile = chooser.getSelectedFile().toString();
        File lastFile = new File(selectedOpenFile);
        try {
            setLastPath(lastFile.getCanonicalPath());
            IGCAPTproperties.getInstance().setPropertyKeyValue("LastPath", getLastPath());
        } catch (IOException ex) {
            setLastPath("");
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(selectedOpenFile);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("node");

            /*
             * Nodes
             */
            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    int currentNodeIndex = Integer.parseInt(eElement.getElementsByTagName("id").item(0).getTextContent());
                    
                    NodeList userDataList = eElement.getElementsByTagName("userData");
                    String userData = "";
                    
                    if (userDataList != null && userDataList.item(0) != null) {
                        userData = userDataList.item(0).getTextContent();
                        
                        if (userData == null) {
                            userData = "";
                        }
                    }
                    
                    SgNode n1 = new SgNode(currentNodeIndex,
                            eElement.getElementsByTagName("type").item(0).getTextContent(),
                            eElement.getElementsByTagName("name").item(0).getTextContent(),
                            eElement.getElementsByTagName("enableDataSending").item(0).getTextContent().contentEquals("true"),
                            eElement.getElementsByTagName("enableDataPassThrough").item(0).getTextContent().contentEquals("true"),
                            eElement.getElementsByTagName("isAggregate").item(0).getTextContent().contentEquals("true"),
                            eElement.getElementsByTagName("isCollapsed").item(0).getTextContent().contentEquals("true"),
                            Integer.parseInt(eElement.getElementsByTagName("payload").item(0).getTextContent()),
                            Integer.parseInt(eElement.getElementsByTagName("maxLatency").item(0).getTextContent()),
                            userData);
                    double xPos = Double.parseDouble(eElement.getElementsByTagName("xCoord").item(0).getTextContent());
                    double yPos = Double.parseDouble(eElement.getElementsByTagName("yCoord").item(0).getTextContent());
                    n1.setLat(Double.parseDouble(eElement.getElementsByTagName("lat").item(0).getTextContent()));
                    n1.setLongit(Double.parseDouble(eElement.getElementsByTagName("long").item(0).getTextContent()));

                    Element endPtElement = (Element)(eElement.getElementsByTagName("endPoints").item(0));
                    List<Integer> endPointList = new ArrayList<>();
                    NodeList endPointListNodes = endPtElement.getElementsByTagName("endPoint");

                    for (int j = 0; j < endPointListNodes.getLength(); ++j) {
                        Node endPointNode = endPointListNodes.item(j);
                        endPointList.add(Integer.parseInt(endPointNode.getTextContent()));
                    }
                    n1.setEndPointList(endPointList);
                    
                    GraphManager.getInstance().getGraph().addVertex(n1);
                    
                    layout.setLocation(n1, xPos, yPos);
                    if (currentNodeIndex > nodeIndex) {
                        nodeIndex = currentNodeIndex;
                    }
                }
            }
            nodeIndex++;

            NodeList eList = doc.getElementsByTagName("edge");

            for (int j = 0; j < eList.getLength(); j++) {

                Node eEdge = eList.item(j);
                if (eEdge.getNodeType() == Node.ELEMENT_NODE) {
                    Element edgeElement = (Element) eEdge;
                    int currentEdgeIndex = Integer.parseInt(edgeElement.getAttribute("id"));

                    SgEdge e1 = new SgEdge(currentEdgeIndex, "e" + currentEdgeIndex,
                            1.0, 0,
                            Double.parseDouble(edgeElement.getElementsByTagName("capacity").item(0).getTextContent()));
                    
                    if (edgeElement.getElementsByTagName("name") != null && edgeElement.getElementsByTagName("name").item(0) != null &&
                            edgeElement.getElementsByTagName("name").item(0).getTextContent() != null && !edgeElement.getElementsByTagName("name").item(0).getTextContent().isEmpty()) {
                        e1.setName(edgeElement.getElementsByTagName("name").item(0).getTextContent());                        
                    }

                    ArrayList<SgNodeInterface> nodes = new ArrayList<>(GraphManager.getInstance().getGraph().getVertices());
                    String source = edgeElement.getAttribute("source");
                    Integer sourceId = Integer.parseInt(source);
                    Integer targetId = Integer.parseInt(edgeElement.getAttribute("target"));
                    int foundEndPts = 0;

                    SgNode endPt1 = null;
                    SgNode endPt2 = null;

                    for (SgNodeInterface node : nodes) {

                        if (node instanceof SgNode) {
                            SgNode sgNode = (SgNode) node;

                            if (sourceId == sgNode.getId()) {
                                endPt1 = sgNode;
                                foundEndPts++;
                            } else if (targetId == sgNode.getId()) {
                                endPt2 = sgNode;
                                foundEndPts++;
                            }
                        }

                        if (foundEndPts > 1) {
                            break;
                        }
                    }

                    // If we found both ends (endPt1 != null && endPt2 != null)
                    if (foundEndPts > 1) {
                        Pair currentPair = null;
                        if (endPt1.getId() < endPt2.getId()) {
                            currentPair = new Pair(endPt1, endPt2);
                        }
                        else {
                            currentPair = new Pair(endPt2, endPt1);
                        }
                            
                        // determine if an edge between these endpoints already exists
                        if (nodePairList.contains(currentPair)) {
                            int occurrences = Collections.frequency(nodePairList, currentPair);
                            
                            // send the endPts in the order of the pair
                            endPt1 = (SgNode)currentPair.getFirst();
                            endPt2 = (SgNode)currentPair.getSecond();
                            Coordinate midPoint = GraphManager.getInstance().calcNewMidPoint(endPt1.getLat(), endPt1.getLongit(),
                                    endPt2.getLat(), endPt2.getLongit(), occurrences);
                            if (midPoint != null) {
                                e1.setMidPoint(midPoint);
                            }
                        }
                        GraphManager.getInstance().getGraph().addEdge(e1, endPt1, endPt2);
                        nodePairList.add(currentPair);
                        if (currentEdgeIndex > edgeIndex) {
                            edgeIndex = currentEdgeIndex;
                        }
                    }
                }
            }
            edgeIndex++;

        } catch (Exception excep) {
            excep.printStackTrace();
        }
        
        originalGraph = (SgGraph)GraphManager.getInstance().getGraph();

        ArrayList<SgNodeInterface> sgNodes = new ArrayList<>(originalGraph.getVertices());
        for (SgNodeInterface nodeIntf : sgNodes) {

            if (nodeIntf instanceof SgNode) {
                SgNode node = (SgNode)nodeIntf;

                if (node.getIsCollapsed()) {

                    GraphManager.getInstance().setContextClickNode(node);
                    // Get the component corresponding to this node.
                    SgComponentData sgComponent = getComponentByUuid(node.getType());

                    // Get the list of connected nodes
                    ArrayList<SgNodeInterface> collapseableNeighborNodes = new ArrayList<>();
                    collapseableNeighborNodes.add(node);
                    List<SgNodeInterface> tempList = node.getConnectedNodes(false, collapseableNeighborNodes);

                    List<String> uuidList = sgComponent.getSgCollapseIntoUuids();
                    for (SgNodeInterface tempNode : tempList) {
                        
                        if (uuidList.contains(tempNode.getType())) {
                                collapseableNeighborNodes.add(tempNode);
                                collapseableNeighborNodes = tempNode.getConnectedNodes(true, collapseableNeighborNodes);
                        }
                    }

                    PickedState<SgNodeInterface> pickState = vv.getPickedVertexState();
                    pickState.clear();
                    for (SgNodeInterface collapseNode : collapseableNeighborNodes) {
                        pickState.pick(collapseNode, true);
                    }

                    collapse();
                }
            }
        }
        
        GraphManager.getInstance().setContextClickNode(null);

        // Redraw the graph
        vv.repaint();
        
        graphChanged();
        setCursor(Cursor.getDefaultCursor());
        fileDirty = false;
    }

    void saveFile(JFileChooser chooser) {
        String selectedSaveFile = chooser.getSelectedFile().toString();

        File lastFile = new File(selectedSaveFile);
        try {
            setLastPath(lastFile.getCanonicalPath());
        } catch (IOException ex) {
            setLastPath("");
        }

        try {
            DocumentBuilderFactory dbFactory
                    = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder
                    = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();
            // root element
            Element rootElement = doc.createElement("Gen");
            doc.appendChild(rootElement);

            //  Nodes element
            Element nodes = doc.createElement("Nodes");
            rootElement.appendChild(nodes);

            Element edges = doc.createElement("Edges");
            rootElement.appendChild(edges);

            GraphManager.getInstance().writeGraphToDOM(doc, nodes, edges);

            // write the content into xml file
            javax.xml.transform.TransformerFactory transformerFactory
                    = javax.xml.transform.TransformerFactory.newInstance();
            javax.xml.transform.Transformer transformer
                    = transformerFactory.newTransformer();

            DOMSource source = new DOMSource(doc);
            StreamResult result
                    = new StreamResult(new File(selectedSaveFile));
            transformer.transform(source, result);

            // Output to console for testing
            //StreamResult consoleResult = new StreamResult(System.out);
            //transformer.transform(source, consoleResult);
        } catch (ParserConfigurationException | TransformerException | DOMException xmlWrite) {
            xmlWrite.printStackTrace();
        }

        fileDirty = false;
    }
    
    private void exportFile(JFileChooser chooser) {
        String selectedSaveFile = chooser.getSelectedFile().toString();

        GraphManager.getInstance().writeGraphToCSV(selectedSaveFile);
    }
    
    public void setUtilization(List<double[]> utilList) {
        Graph expandedGraph = GraphManager.getInstance().getOriginalGraph();
        ArrayList<SgEdge> sgEdges = new ArrayList<>(expandedGraph.getEdges());

        for (double[] element : utilList) {

            for (SgEdge edge : sgEdges) {
                Pair<SgNodeInterface> endNodes = expandedGraph.getEndpoints(edge);

                if ((endNodes.getFirst().getId() == (int)element[0] && endNodes.getSecond().getId() == (int)element[1]) ||
                    (endNodes.getFirst().getId() == (int)element[1] && endNodes.getSecond().getId() == (int)element[0])) {

                    edge.setEdgeRate(element[2]);
                    edge.setCalcTransRate(element[3]*element[2]*0.01);
                    break;
                }
            }
        }
        
        refresh();
    }
    
    public void importResults(JFileChooser chooser) {
        
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        // Read the file and process.
        try (BufferedReader br = new BufferedReader(new FileReader(chooser.getSelectedFile()))) {
            String line;
            List<double[]> utilList = new ArrayList<>();
            
            boolean headerPresent = true;
            
            if (headerPresent) {
                br.readLine(); // Throw away the header.                
            }
            
            while ((line = br.readLine()) != null) {
                // Parse four doubles
                double[] utilDoubles = Arrays.stream(line.split(","))
                                        .map(String::trim)
                                        .mapToDouble(Double::valueOf)
                                        .toArray();
                
                if (utilDoubles.length >= 4) {
                    utilList.add(utilDoubles);
                }
                else {
                    System.out.println("Error in reading line: " + line);
                    break;
                }
            }
            
            setUtilization(utilList);
        } catch(IOException ex) {
            ex.printStackTrace();
        }
        
        setCursor(Cursor.getDefaultCursor());
    }

    public class AnalysisTask extends SwingWorker<String, Integer> {

        private Graph<SgNodeInterface, SgEdge> _graph;
        private volatile boolean _running = true;

        public AnalysisTask(Graph<SgNodeInterface, SgEdge> graph) {
            _graph = graph;
        }

        public void terminate() {
            _running = false;
        }

        @Override
        protected String doInBackground() {
            return analyze(_graph);
        }

        private ArrayList<SgNodeInterface> sgNodeList = null;
        private ArrayList<SgEdge> sgEdgeList = null;

        String analyze(Graph graph) {

            String returnval = null;
            
            List<gov.inl.igcapt.components.Pair<SgNode, SgNode>> analyzeList = new ArrayList<>();

            Date startDate = new Date();

            // reset utilization
            clearEdgeUtilization();

            // Clear list of data flows to be analyzed
            analyzeList.clear();

            // start and end points
            List<List<Integer>> paths;
            StringBuilder analysisResults = new StringBuilder();

            // Build list of data flows to be analyzed
            sgNodeList = new ArrayList<>(graph.getVertices());
            sgEdgeList = new ArrayList<>(graph.getEdges());

            int i = 0;

            firePropertyChange("status", "old", "Compiling endpoint pair list.");
            for (SgNodeInterface sgAbstractNode : sgNodeList) {

                if (!_running) {
                    break;
                }

                // Only take 50% of our progress in this phase.  Take the rest below.
                setProgress(50 * i++ / sgNodeList.size());

                // If there is data to send and sending is enabled, generate the list
                // of start/end nodes for which the paths will be generated.
                if (sgAbstractNode instanceof SgNode sgNode) {
                    if (sgNode.getDataToSend() > 0 && sgNode.getEnableDataSending()) {
                        for (int endPointId : sgNode.getEndPointList()) {
                            SgNodeInterface endPointNode = GraphManager.getInstance().getNode(sgNodeList, endPointId);

                            if (endPointNode != null && endPointId != sgNode.getId()) {
                                if (sgNode.getDataToSend() > 0.0) {
                                    gov.inl.igcapt.components.Pair<SgNode, SgNode> innerList = new gov.inl.igcapt.components.Pair<>(sgNode, (SgNode)endPointNode);
                                    analyzeList.add(innerList);
                                }
                            }
                        }
                    }
                }
            }

            if (_running) {
                i = 0;
                firePropertyChange("status", "old", "Determing paths for each endpoint pair.");
                for (gov.inl.igcapt.components.Pair<SgNode, SgNode> pair : analyzeList) {

                    if (!_running) {
                        break;
                    }
                    setProgress(50 + 50 * i++ / analyzeList.size());

                    paths = getComponentPaths(graph, pair.first, pair.second, true);

                    double ackPayload = Double.parseDouble(IGCAPTproperties.getInstance().getPropertyKeyValue("ACKSize"));

                    for (List<Integer> sublist : paths) {
                        for (Integer value : sublist) {
                            SgEdge sgEdge = GraphManager.getInstance().getEdge(sgEdgeList, value);
                            SgNodeInterface sgAbstractNode = pair.first;

                            if (sgAbstractNode instanceof SgNode sgNode) {
                                sgEdge.setCalcTransRate(sgEdge.getCalcTransRate() + sgNode.getComputedRate());
                            }
                        }

                        // Reverse communication flow for ACK
                        for (int j = sublist.size() - 1; j >= 0; j--) {
                            SgEdge sgEdge = GraphManager.getInstance().getEdge(sgEdgeList, sublist.get(j));
                            SgNode sgSrcNode = pair.first;

                            // This is an ACK coming back from the destination.  Use the timing from the
                            // source and a fixed ACK payload as specified in the properties file.
                            double ackUtilization = ackPayload * 8.0 / sgSrcNode.getMaxLatency() / 1000;
                            sgEdge.setCalcTransRate(sgEdge.getCalcTransRate() + ackUtilization);
                        }
                    }
                }
            }

            if (_running) {
                int numExceptions = 0;

                analysisResults.append("Color Legend<br> High: &gt; <font size=\"+1\" color=\"red\"><b>"
                        + String.format("%.2f", SgEdge.getHighUtilizationLimit() * 100.0)
                        + "%</b></font>");
                analysisResults.append("   Medium: &gt; <font size=\"+1\" color=\"orange\"><b>"
                        + String.format("%.2f", SgEdge.getMediumUtilizationLimit() * 100.0)
                        + "%</b></font>");
                analysisResults.append("   Low: &gt; <font size=\"+1\" color=\"green\"><b>"
                        + "0.0"
                        + "%</b></font>");
                analysisResults.append("   Zero: = <font size=\"+1\" color=\"black\"><b>"
                        + "0.0"
                        + "%</b></font><br><br>");

                i = 0;
                for (SgEdge sgEdge : sgEdgeList) {
                    
                    Pair<SgNodeInterface> endPts = graph.getEndpoints(sgEdge);

                    if (endPts.getFirst() instanceof SgNode && endPts.getSecond() instanceof SgNode) {

                        SgNode endPt1 = (SgNode) endPts.getFirst();
                        SgNode endPt2 = (SgNode) endPts.getSecond();

                        if (sgEdge.isOverHighUtilizationLimit()) {
                            analysisResults.append("(" + endPt1.getName() + " - "
                                    + endPt2.getName()
                                    + ")/e" + sgEdge.getId()
                                    + ", Network Capacity = " + String.format("%.3f", sgEdge.getEdgeRate())
                                    + ", Network Usage = " + String.format("%.3f", sgEdge.getCalcTransRate())
                                    + ", Utilization = <font size=\"+1\" color=\"red\"><b>"
                                    + String.format("%.2f", sgEdge.getUtilization() * 100.0)
                                    + "%</b></font><br>");
                            numExceptions++;
                        } else if (sgEdge.isOverMidUtilizationLimit()) {
                            analysisResults.append("(" + endPt1.getName() + " - "
                                    + endPt2.getName() + ")/e" + sgEdge.getId()
                                    + ", Network Capacity = " + String.format("%.3f", sgEdge.getEdgeRate())
                                    + ", Network Usage = " + String.format("%.3f", sgEdge.getCalcTransRate())
                                    + ", Utilization = <font size=\"+1\" color=\"orange\"><b>"
                                    + String.format("%.2f", sgEdge.getUtilization() * 100.0)
                                    + "%</b></font><br>");
                            numExceptions++;
                        } else if (!sgEdge.isZeroUtilizationLimit()) {
                            if (showAllAnalysisResults) { //Diagnostic output
                                analysisResults.append("(" + endPt1.getName() + " - "
                                        + endPt2.getName() + ")/e" + sgEdge.getId()
                                        + ", Network Capacity = " + String.format("%.3f", sgEdge.getEdgeRate())
                                        + ", Network Usage = " + String.format("%.3f", sgEdge.getCalcTransRate())
                                        + ", Utilization = <font size=\"+1\" color=\"green\"><b>"
                                        + String.format("%.2f", sgEdge.getUtilization() * 100.0)
                                        + "%</b></font><br>");
                            }
                        } else {
                            if (showAllAnalysisResults) { //Diagnostic output
                                analysisResults.append("(" + endPt1.getName() + " - "
                                        + endPt2.getName() + ")/e" + sgEdge.getId()
                                        + ", Network Capacity = " + String.format("%.3f", sgEdge.getEdgeRate())
                                        + ", Network Usage = " + String.format("%.3f", sgEdge.getCalcTransRate())
                                        + ", Utilization = <font size=\"+1\" color=\"black\"><b>"
                                        + String.format("%.2f", sgEdge.getUtilization() * 100.0)
                                        + "%</b></font><br>");
                            }
                        }
                    }
                }
                Date endDate = new Date();

                analysisResults.append(numExceptions);
                analysisResults.append(" exceptions found.");
                analysisResults.append("<br>");

                JTextArea ta = new JTextArea(50, 100);
                ta.setWrapStyleWord(true);
                ta.setLineWrap(true);
                ta.setCaretPosition(0);
                ta.setEditable(false);

                analysisResults.append("Analysis start time: ");
                analysisResults.append(startDate.toString());
                analysisResults.append("<br>");
                analysisResults.append("Analysis end time: ");
                analysisResults.append(endDate.toString());

                returnval = analysisResults.toString();
                ta.setText(returnval);
            }

            refresh();

            return returnval;
        }

        private List<List<Integer>> getComponentPaths(Graph graph, SgNode fromNode, SgNode toNode, boolean isSender) {
            List<List<Integer>> returnval = new ArrayList<>();
            SgNode currentNode;

            SgNodeInterface fromSgAbstractNode = fromNode;
            SgNodeInterface toSgAbstractNode = toNode;

            if (fromSgAbstractNode instanceof SgNode && toSgAbstractNode instanceof SgNode) {

                SgNode fromSgNode = (SgNode) fromSgAbstractNode;
                SgNode toSgNode = (SgNode) toSgAbstractNode;

                currentNode = fromSgNode;
                currentNode.setUsed(true);         // Prevent a component from being looped back on

                if (fromNode == toNode) {
                    ArrayList<Integer> x = new ArrayList<>();
                    returnval.add(x);
                } else if (isSender || currentNode.getEnableDataPassThrough()) {
                    // Cycle through all connected components

                    // Get list of connected edges.
                    List<SgEdge> sgEdges = new ArrayList<>(graph.getIncidentEdges(fromSgNode));

                    for (SgEdge sgEdge : sgEdges) {
                        
                        if (sgEdge.isEnabled()) {
                            SgNode nextComponent = null;

                            Pair<SgNodeInterface> endpoints = graph.getEndpoints(sgEdge);

                            if (endpoints.getFirst() instanceof SgNode && endpoints.getSecond() instanceof SgNode) {
                                SgNode endPt1 = (SgNode) endpoints.getFirst();
                                SgNode endPt2 = (SgNode) endpoints.getSecond();

                                if (endPt1 != null && endPt2 != null) {
                                    if (!endPt1.getUsed()) {
                                        nextComponent = endPt1;
                                    } else if (!endPt2.getUsed()) {
                                        nextComponent = endPt2;
                                    }
                                }
                            }   

                            if (nextComponent != null) {
                                List<List<Integer>> returnPaths = getComponentPaths(graph, nextComponent, toSgNode, false);

                                // We received a path, add our current edge to the head of each list and return it.
                                if (returnPaths.size() > 0) {
                                    for (List<Integer> path : returnPaths) {
                                        path.add(0, sgEdge.getId());
                                    }
                                    returnval.addAll(0, returnPaths);
                                    break; // Stop after first path found.
                                }
                            }
                        }
                    }
                }
                currentNode.setUsed(false); // Return it to the pool, just need to make sure it does not loop back downstream
            }
            return returnval;
        }
    }

    public Icon getLayerIcon(String iconName) {
        return _layerIconMap.get(iconName);
    }

    protected IGCAPTGraphMousePlugin myGraphMousePlugin;

    public class PickWithIconListener implements ItemListener {

        DefaultVertexIconTransformer<SgNodeInterface> imager;
        Icon checked;

        public PickWithIconListener(DefaultVertexIconTransformer<SgNodeInterface> imager) {
            this.imager = imager;
            checked = new Checkmark(Color.red);
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            //System.out.println("PickWithIconListener itemStateChanged");
            Icon icon = imager.transform((SgNodeInterface) e.getItem());
            if (icon != null && icon instanceof LayeredIcon) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    ((LayeredIcon) icon).add(checked);
                } else {
                    ((LayeredIcon) icon).remove(checked);
                }
            }
        }
    }

    private Coordinate c(double lat, double lon) {
        return new Coordinate(lat, lon);
    }

    /**
     * @param args Main program arguments
     */
    public static void main(String[] args) {
        //final String dir = System.getProperty("user.dir");
        //System.out.println("current dir = " + dir);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                UIManager.getLookAndFeelDefaults()
                        .put("defaultFont", new Font("Arial", Font.BOLD, 16));
                IGCAPTgui igCAPTgui = IGCAPTgui.getInstance();
                igCAPTgui.Initialize();
                igCAPTgui.setVisible(true);
            }
        });

    }

    private void updateZoomParameters() {
        if (mperpLabelValue != null) {
            mperpLabelValue.setText(String.format("%s", GraphManager.getInstance().map().getMeterPerPixel()));
            //mperpLabelValue.setText(String.format("%s", map().getMeterPerPixel()) + "     x=" + map().getMouseX() + ", y = " + map().getMouseY() );
        }
        if (zoomValue != null) {
            zoomValue.setText(String.format("%s", GraphManager.getInstance().map().getZoom()));
        }
    }

    // this implements the interface for JMapViewerEventListener
    @Override
    public void processCommand(JMVCommandEvent command) {
        if (command.getCommand().equals(JMVCommandEvent.COMMAND.ZOOM)
                || command.getCommand().equals(JMVCommandEvent.COMMAND.MOVE)) {
            updateZoomParameters();
            
            updateGISObjects();
        }
    }

// Jung code
    private JPanel createTreePanel() {
        JPanel treePanel = new JPanel();
        tree = new DragTree7(this);
        tree.addTreeSelectionListener(tree);

        treePanel.setLayout(new BorderLayout());
        treePanel.add(new JScrollPane(tree), BorderLayout.CENTER);
        treePanel.setBorder(
                BorderFactory.createTitledBorder(
                        //"Drag source for filenames"));
                        "Drag Components to Modeling Panel"));

        return treePanel;
    }

    private JTabbedPane createTabbedPane() {
        jtp = new JTabbedPane();
        getJtp().setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
        getJtp().add("Logical Model", vv);
        return getJtp();
    }

    // start of 5 methods for DropTargetListener
    public void dragEnter(DropTargetDragEvent e) {
    }

    public void dragExit(DropTargetEvent e) {
    }

    public void dragOver(DropTargetDragEvent e) {
        if (getMode() != Mode.EDITING) {
            e.rejectDrag();
        } else {
            e.acceptDrag(e.getDropAction());
        }
    }

    public void dropActionChanged(DropTargetDragEvent e) {
    }
    
    private static HashMap<String, SgComponentData> componentUuidMap = new HashMap<>();
    public static SgComponentData getComponentByUuid(String uuidStr) {
        
        SgComponentData returnval = componentUuidMap.get(uuidStr);
        
        if (returnval == null) {
            ComponentDao componentDao = new ComponentDao();
            returnval = componentDao.getComponentByUUID(uuidStr);
            componentUuidMap.put(uuidStr, returnval);
        }
        
        return returnval;
    }

    public void drop(DropTargetDropEvent e) {
        fileDirty = true;

        try {
            DataFlavor stringFlavor = DataFlavor.stringFlavor;
            Transferable tr = e.getTransferable();

            if (e.isDataFlavorSupported(stringFlavor)) {
                String uuidStr = (String) tr.getTransferData(stringFlavor);
                // start kd
                boolean showAggregationComponent = false;
                SgComponentData sgComponent = getComponentByUuid(uuidStr);

                e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                Point point = e.getLocation();

                MultiLayerTransformer transformer = vv.getRenderContext().getMultiLayerTransformer();
                Point2D d = transformer.inverseTransform(point);
                Point newPoint = new Point((int) d.getX(), (int) d.getY());

                if (sgComponent.getUuid().equals("a169911e-9079-449f-b9b7-9f79efcec135")) {
                    AggregationDialog aggregationDialog = new AggregationDialog(this, true);
                    showAggregationComponent = aggregationDialog.showDialog();

                    //evaluate both sides of the following if statement iff the first part is false
                    if (showAggregationComponent) {

                        // Create aggregate node, which is the type selected in the dialog
                        // Then create all the subnodes.
                        ArrayList<gov.inl.igcapt.components.Pair<String, Integer>> aggregateConfig = aggregationDialog.getAggregateConfiguration();
                        SgComponentData selectedAggregateComponent = aggregationDialog.getSelectedComponent();

                        createAggregation(aggregateConfig, selectedAggregateComponent, point, new Coordinate(0.0, 0.0), aggregationDialog.getDefaultMaxRate());
                        currentTypeUuidStr = uuidStr;
                    }
                } else {
                    displayImage(uuidStr, newPoint);
                }
                e.dropComplete(true);
            } else {
                e.rejectDrop();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (UnsupportedFlavorException ufe) {
            ufe.printStackTrace();
        }
    }
    // end of 5 methods for DropTargetListener
    
    // When creating an aggregation, place the aggregated component at this offset
    // relative to the aggregate parent.
    private static final Point AGGREGATE_OFFSET = new Point(100, 0);
    private static final Point2D.Double AGGREGATE_LATLON_OFFSET = new Point2D.Double(0.0, 0.1);
    
    public SgNodeInterface createAggregation(ArrayList<gov.inl.igcapt.components.Pair<String, Integer>> aggregateConfig,
            SgComponentData selectedAggregateComponent, Point point, Coordinate latLongCoord, double defaultMaxRate) {

        SgNodeInterface returnval = null;

        // Create the aggregate node of the type specified.
        SgNode aggregateNode = new SgNode(nodeIndex, selectedAggregateComponent.getUuid().toString(),
                selectedAggregateComponent.getName() + "_" + String.valueOf(nodeIndex),
                true, selectedAggregateComponent.isPassthrough(), true, 0, 0, "");
        returnval = aggregateNode;

        GraphManager.getInstance().getGraph().addVertex(aggregateNode);
        layout.setLocation(aggregateNode, point);
        aggregateNode.setLat(latLongCoord.getLat());
        aggregateNode.setLongit(latLongCoord.getLon());
        nodeIndex++;

        // Now create all the aggregated nodes
        ArrayList<SgNode> aggregateNodeList = new ArrayList<>(); // Need this for collapsing.
        for (gov.inl.igcapt.components.Pair<String, Integer> entry : aggregateConfig) {
            String key = entry.first;
            Integer value = entry.second;
            int numComponents = value;

            if (numComponents > 0) {
                SgComponentData compToCreate = getComponentByUuid(key);

                for (int i = 0; i < numComponents; ++i) {
                    SgNode node = new SgNode(nodeIndex, compToCreate.getUuid().toString(),
                            compToCreate.getName() + "_" + String.valueOf(nodeIndex),
                            true, compToCreate.isPassthrough(), false, 0, 0, "");

                    aggregateNodeList.add(node);

                    Point newPoint = new Point(point.x + AGGREGATE_OFFSET.x, point.y + AGGREGATE_OFFSET.y);
                    GraphManager.getInstance().getGraph().addVertex(node);
                    layout.setLocation(node, newPoint);
                    node.setLat(latLongCoord.getLat() + AGGREGATE_LATLON_OFFSET.x);
                    node.setLongit(latLongCoord.getLon() + AGGREGATE_LATLON_OFFSET.y);

                    // Connect components with edges
                    // Need to get edgerate from the AggregationDialog.
                    SgEdge edge = new SgEdge(edgeIndex, "e" + edgeIndex,
                            1.0, 0, defaultMaxRate);
                    GraphManager.getInstance().getGraph().addEdge(edge, aggregateNode, node);
                    GraphManager.getInstance().getOriginalGraph().addEdge(edge, aggregateNode, node);

                    edgeIndex++;
                    nodeIndex++;
                }
            }
        }

        // Collapse around the aggregate node.
        GraphManager.getInstance().setContextClickNode(aggregateNode);

        // Pick all the nodes including the aggregating node.
        PickedState<SgNodeInterface> pickState = vv.getPickedVertexState();
        pickState.clear();
        pickState.pick(aggregateNode, true);
        for (SgNodeInterface collapseNode : aggregateNodeList) {
            pickState.pick(collapseNode, true);
        }

        collapse();
        GraphManager.getInstance().setContextClickNode(null);

        refresh();

        return returnval;
    }


    private void displayImage(String uuidStr, Point point) {

        SgComponentData sgComponent = getComponentByUuid(uuidStr);

        try {
            String typeName = sgComponent.getName();

            boolean passThru = sgComponent.isPassthrough();
            SgNode n1 = new SgNode(nodeIndex, uuidStr, typeName + "_" + String.valueOf(nodeIndex), true, passThru, false, 0, 0, "");

            GraphManager.getInstance().getGraph().addVertex(n1);
            layout.setLocation(n1, point);

            nodeIndex++;
            currentTypeUuidStr = uuidStr;

            refresh();
        } catch (Exception e) {
        }
    }

    // -------------------------------------------------------------------------
    public void showDialog(SgNode node) {
        NodeSettingsDialog nodeSettingsDlg = new NodeSettingsDialog(null, (SgGraph) GraphManager.getInstance().getGraph(), node);
        nodeSettingsDlg.setLocation((int) vv.getCenter().getX(), (int) vv.getCenter().getY());
        nodeSettingsDlg.setVisible(true);

        if (nodeSettingsDlg.getReturnValue() == NodeSettingsDialog.ReturnValue.OK) {

            node.setName(nodeSettingsDlg.getComponentName());
            node.setDataToSend(nodeSettingsDlg.getPayloadBytes());
            node.setMaxLatency(nodeSettingsDlg.getMaxLatencySecs());
            node.setEnableDataSending(nodeSettingsDlg.getEnableDataSending());
            node.setEnableDataPassThrough(nodeSettingsDlg.getEnableDataPassthrough());
            node.setIsAggregate(nodeSettingsDlg.getIsAggregate());
            node.setUserData(nodeSettingsDlg.getUserData());

            node.setEndPointList(nodeSettingsDlg.getEndPointList());

            graphChanged();
        }
    }

    public void showDialog(SgEdge edge) {
        final JTextField tbxEdgeName = new JTextField(edge.getName());
        final JTextField tbxEdgeID = new JTextField(String.valueOf(edge.getId()));
        final JTextField tbxWeight = new JTextField(String.valueOf(edge.getWeight()));
        final JTextField tbxEdgeRate = new JTextField(String.valueOf(edge.getEdgeRate()));

        final Pair<SgNodeInterface> endpoints = GraphManager.getInstance().getGraph().getEndpoints(edge);

        final JTextField tbxEndPoint1 = new JTextField(String.valueOf(endpoints.getFirst().getId()));
        final JTextField tbxEndPoint2 = new JTextField(String.valueOf(endpoints.getSecond().getId()));
        final JCheckBox cbxEnable = new JCheckBox();

        tbxEdgeID.setEnabled(false);
        tbxEndPoint1.setEnabled(false);
        tbxEndPoint2.setEnabled(false);
        cbxEnable.setSelected(edge.isEnabled());

        Object[] inputFields = {"Name", tbxEdgeName,
            "Weight", tbxWeight,
            //            "Maximum Rate (bytes/sec)", tbxEdgeRate,
            "Maximum Rate (Kbits/sec)", tbxEdgeRate,
            "Edge ID", tbxEdgeID,
            "End Point 1", tbxEndPoint1,
            "End Point 2", tbxEndPoint2,
            "Enable", cbxEnable};

        int option = JOptionPane.showConfirmDialog(IGCAPTgui.getInstance(), inputFields, "Line Settings", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);

        if (option == 0) {
            fileDirty = true;

            edge.setName(tbxEdgeName.getText());
            edge.setWeight(Double.parseDouble(tbxWeight.getText()));
            edge.setEdgeRate(Double.parseDouble(tbxEdgeRate.getText()));
            edge.setIsEnabled(cbxEnable.isSelected());

            graphChanged();
        }
    }

}
