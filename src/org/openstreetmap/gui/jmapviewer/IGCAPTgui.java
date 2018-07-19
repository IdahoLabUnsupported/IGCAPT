/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openstreetmap.gui.jmapviewer;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.LayeredIcon;
import edu.uci.ics.jung.visualization.MultiLayerTransformer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.GraphMouseListener;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
import edu.uci.ics.jung.visualization.control.TranslatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.decorators.DefaultVertexIconTransformer;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.renderers.BasicEdgeRenderer;
import edu.uci.ics.jung.visualization.renderers.Checkmark;
import edu.uci.ics.jung.visualization.subLayout.GraphCollapser;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;
import igcapt.inl.components.AggregationDialog;
import igcapt.inl.components.AnalysisProgress;
import igcapt.inl.components.ApplyUseCaseDialog;
import igcapt.inl.components.ButtonTabComponent;
import igcapt.inl.components.HelpDialog;
import igcapt.inl.components.NodeSettingsDialog;
import igcapt.inl.components.SgComponent;
import igcapt.inl.components.SgComponentGroupList;
import igcapt.inl.components.SgLayeredIcon;
import igcapt.inl.components.SgMapImage;
import igcapt.inl.components.SgUseCase;
import igcapt.inl.components.generated.ObjectFactory;
import igcapt.inl.components.generated.SgApplication;
import igcapt.inl.components.generated.SgComponentListData;
import igcapt.inl.components.generated.SgDataElement;
import igcapt.inl.components.generated.SgUseCaseData;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
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
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
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
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.openstreetmap.gui.jmapviewer.events.JMVCommandEvent;
import org.openstreetmap.gui.jmapviewer.interfaces.JMapViewerEventListener;
import org.openstreetmap.gui.jmapviewer.interfaces.TileLoader;
import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.BingAerialTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;
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

    static final IGCAPTproperties IGCAPTPROPERTIES = IGCAPTproperties.getInstance();
    private static final long serialVersionUID = 1L;

    //private final JMapViewerTree treeMap;
    private final JSGMapViewer treeMap;

    private final JLabel zoomLabel;
    private final JLabel zoomValue;
    private final JLabel xyPosition;

    private final JLabel mperpLabelName;
    private final JLabel mperpLabelValue;
    private String _lastPath = "";

    // When creating an aggregation, place the aggregated component at this offset
    // relative to the aggregate parent.
    private static final Point AGGREGATE_OFFSET = new Point(100, 0);
    private static final Point2D.Double AGGREGATE_LATLON_OFFSET = new Point2D.Double(0.0, 0.1);


    // from TreeCollapseDemoKD.java
    int windowHeight = 1000;
    int windowWidth = 2000;
    double maxNodeCount = Math.pow(2, 6) + 1;

    // Jung
    public VisualizationViewer<SgNodeInterface, SgEdge> vv = null;
    private AbstractLayout<SgNodeInterface, SgEdge> layout = null;

    int edgeIndex = 0;  // edge index in the graph
    int nodeIndex = 0;  // node index in the graph

    JTabbedPane jtp;
    
    JMapViewer currentGisMap;
    boolean toolTipsEnabled = true;
    boolean showAllAnalysisResults = false;

    // Drop Targets - only one can be active at a time
    DropTarget logicalModelDropTarget;
    DropTarget gisModelDropTarget;
    JMenu modeMenu;
    String currentTypeUuidStr = null; // default to Utility DCC
    private Mode _mode;
    private boolean _analysisCanceled = false;
    private SgComponentGroupList _sgComponentGroupList = null;
    private SgNodeInterface _contextClickNode = null;

    private Point _clickPoint = null;

    public Point getClickPoint() {
        return _clickPoint;
    }

    public void setClickPoint(Point _clickPoint) {
        this._clickPoint = _clickPoint;
    }

    private final HashMap<igcapt.inl.components.Pair<String, String>, HashMap<UUID, Integer>> _useCaseApplyData = new HashMap<>();

    private SgUseCaseData _sgUseCaseData = null;

    public SgUseCaseData getUseCaseData() {
        return _sgUseCaseData;
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
    private static IGCAPTgui _IGCAPTgui = null;

    public static IGCAPTgui getInstance() {

        if (_IGCAPTgui == null) {
            _IGCAPTgui = new IGCAPTgui();
        }
        return _IGCAPTgui;
    }

    public void loadLayerIcons() {

        // These correspond to the names from the property file.
        String[] iconKeys = new String[]{
            "expandIcon",
            "collapseIcon",
            "aggregateIcon",
            "selectionIcon"
        };

        for (String iconKey : iconKeys) {
            Icon icon;
            String whichIcon = iconKey;
            String iconPath = IGCAPTPROPERTIES.getPropertyKeyValue(whichIcon);

            if (null != iconPath && !iconPath.isEmpty()) {

                BufferedImage img;
                try {
                    File iconFile = new File(iconPath);
                    img = ImageIO.read(new FileInputStream(iconFile));

                    icon = new ImageIcon(img);
                    _layerIconMap.put(whichIcon, icon);
                } catch (IOException e) {
                    System.out.println("Unable to read " + whichIcon);
                    e.printStackTrace();
                }
            }
        }
    }

    public AbstractLayout<SgNodeInterface, SgEdge> getLocalLayout() {
        return layout;
    }

    // This will not include subgraphs.
    public Graph getGraph() {
        return vv.getGraphLayout().getGraph();
    }

    public SgNodeInterface getContextClickNode() {
        return _contextClickNode;
    }

    public void setContextClickNode(SgNodeInterface contextClickNode) {
        _contextClickNode = contextClickNode;
    }

    public SgComponentGroupList getComponentGroupList() {
        return _sgComponentGroupList;
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

    public boolean fileDirty = false;
    private GraphCollapser collapser;
    private SgGraph tempGraph = null;
    private SgGraph originalGraph = null;

    public SgGraph getOriginalGraph() {
        return originalGraph;
    }

    public void graphChanged() {
        fileDirty = true;

        clearEdgeUtilization();
        updateGISObjects();
    }

    private void initSGComponents(SgComponentListData sgComponentListData) {

        // Construct internal component structure
        _sgComponentGroupList = new SgComponentGroupList(sgComponentListData);
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

    private void loadSGComponents() {

        try {
            //1. We need to create JAXContext instance
            JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);

            //2. Use JAXBContext instance to create the Unmarshaller.
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            //3. Use the Unmarshaller to unmarshal the XML document to get an instance of JAXBElement.
            JAXBElement<SgComponentListData> unmarshalledObject
                    = (JAXBElement<SgComponentListData>) unmarshaller.unmarshal(new File(IGCAPTPROPERTIES.getPropertyKeyValue("sgComponentsFile")));

            //4. Get the instance of the required JAXB Root Class from the JAXBElement.
            SgComponentListData sgComponentListData = unmarshalledObject.getValue();

            initSGComponents(sgComponentListData);

        } catch (JAXBException ex) {
            Logger.getLogger(IGCAPTgui.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadSGUseCases() {

        try {
            //1. We need to create JAXContext instance
            JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);

            //2. Use JAXBContext instance to create the Unmarshaller.
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            //3. Use the Unmarshaller to unmarshal the XML document to get an instance of JAXBElement.
            JAXBElement<SgUseCaseData> unmarshalledObject
                    = (JAXBElement<SgUseCaseData>) unmarshaller.unmarshal(new File(IGCAPTPROPERTIES.getPropertyKeyValue("sgUseCaseFile")));

            //4. Get the instance of the required JAXB Root Class from the JAXBElement.
            _sgUseCaseData = unmarshalledObject.getValue();

        } catch (JAXBException ex) {
            Logger.getLogger(IGCAPTgui.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Redraw the GIS images based upon the current Jung graph
    void updateGISObjects() {

        List<SgNodeInterface> nodes = new ArrayList<SgNodeInterface>(getGraph().getVertices());

        JMapViewer map = map();
        map.removeAllMapImages();
        map.removeAllMapPolygons();
        map.removeAllMapLines();

        for (SgNodeInterface node : nodes) {
            BufferedImage theImage;
            SgLayeredIcon icon = (SgLayeredIcon) node.getIcon();
            theImage = (BufferedImage) icon.getCompositeImage(this, node);
            MapImageImpl myimage = new SgMapImage(node.getLat(), node.getLongit(), theImage, 0, node);
            myimage.setId(node.getName());
            node.setMapImage(myimage);
            map.addMapImage(myimage);
        }

        List<SgEdge> edges = new ArrayList<SgEdge>(getGraph().getEdges());

        for (SgEdge edge : edges) {
            Pair nodePair = getGraph().getEndpoints(edge);
            SgNodeInterface firstNode = (SgNodeInterface) nodePair.getFirst();
            SgNodeInterface secondNode = (SgNodeInterface) nodePair.getSecond();

            double latitude1 = firstNode.getLat();
            double longitude1 = firstNode.getLongit();
            double latitude2 = secondNode.getLat();
            double longitude2 = secondNode.getLongit();
            Coordinate start = new Coordinate(latitude1, longitude1);
            Coordinate end = new Coordinate(latitude2, longitude2);

            MapLineImpl line = new MapLineImpl(start, end);
            line.setId(edge.toString());

            if (edge.isOverHighUtilizationLimit()) {
                line.setColor(Color.red);
            } else if (edge.isOverMidUtilizationLimit()) {
                line.setColor(Color.orange);
            } else if (!edge.isZeroUtilizationLimit()) {
                line.setColor(Color.green);
            } else {
                line.setColor(Color.black);
            }

            edge.setMapLine(line);
            map.addMapLine(line);
        }
    }

    /**
     * Constructs the {@code DemoKD}.
     */
    public IGCAPTgui() {
        super("Intelligent Grid Communications & Analysis Planning Tool");
        setSize(400, 400);

        treeMap = new JSGMapViewer("Components", this);

        // Listen to the map viewer for user operations so components will
        // receive events and update
        map().addJMVListener(this);

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
        mperpLabelValue = new JLabel(String.format("%s", map().getMeterPerPixel()));

        zoomLabel = new JLabel("Zoom: ");
        zoomValue = new JLabel(String.format("%s", map().getZoom()));
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
        tileSourceSelector.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                map().setTileSource((TileSource) e.getItem());
            }
        });

        tileSourceSelector.setVisible(false);

        JComboBox<TileLoader> tileLoaderSelector;
        tileLoaderSelector = new JComboBox<>(new TileLoader[]{new OsmTileLoader(map())});
        tileLoaderSelector.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                map().setTileLoader((TileLoader) e.getItem());
            }
        });
        map().setTileLoader((TileLoader) tileLoaderSelector.getSelectedItem());
        panelTop.add(tileSourceSelector);
        panelTop.add(tileLoaderSelector);

        tileLoaderSelector.setVisible(false);

        // GIS Enabled
        final JCheckBox gisEnabled = new JCheckBox("GIS", true);
        gisEnabled.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gisEnabled.isSelected()) {
                    currentGisMap = map();
                    jtp.add("Geographical Model", currentGisMap);
                } else {
                    jtp.remove(currentGisMap);
                }
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

                if (getGraph() != originalGraph) {
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

                        Graph g = collapser.expand(getGraph(), (Graph) v);
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
        map().setDisplayPosition(new Coordinate(43.5203489, -112.0452956), 5); // WCB INL

        map().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    map().getAttribution().handleAttribution(e.getPoint(), true);
                }
            }
        });

        loadSGComponents();
        loadSGUseCases();

        originalGraph = new SgGraph();
        layout = new StaticLayout<SgNodeInterface, SgEdge>(originalGraph,
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
            boolean dragging = false;

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

                if (v instanceof SgNode) {
                    SgNode node = (SgNode) v;
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
        doNotPaintInvisibleVertices(vv);
        vv.setBackground(Color.white);

        vv.getRenderContext().setEdgeLabelTransformer(e -> e.getName());
        vv.getRenderContext().setVertexLabelTransformer(v -> v.getName());

        // try to transform the nodes to icons -- this did NOT draw the icons!
        // Return the shape that is appropriate for this node.
        // If vertex is an SgNode then return its icon.  If it is a graph (collapsed graph)
        // return the icon of the reference node.
        final DefaultVertexIconTransformer<SgNodeInterface> vertexIconFunction
                = new DefaultVertexIconTransformer<SgNodeInterface>() {

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

        // create the GIS view so that when we load a topology file, the icons can be placed there
        gisEnabled.setSelected(true);
        currentGisMap = map();
        jtp.add("Geographical Model", currentGisMap);

        // ChangeListener is invoked when a user selects a tab
        jtp.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                modeMenu.setEnabled(false);
                JTabbedPane myTabbedPane = (JTabbedPane) e.getSource();
                int selectedIndex = myTabbedPane.getSelectedIndex();
                String title = myTabbedPane.getTitleAt(selectedIndex);

                if (title.equalsIgnoreCase("Geographical Model")) {
                    logicalModelDropTarget.setActive(false);
                    gisModelDropTarget.setActive(true);
                } else if (title.equalsIgnoreCase("Logical Model")) {
                    modeMenu.setEnabled(true);
                    gisModelDropTarget.setActive(false);
                    logicalModelDropTarget.setActive(true);
                }
            }
        });
        splitPane.setDividerLocation(300);
        splitPane.setOneTouchExpandable(true);

        getContentPane().add(splitPane, BorderLayout.CENTER);

        // Set up DROP Target for Logical Model as active
        logicalModelDropTarget = new DropTarget(vv, DnDConstants.ACTION_COPY_OR_MOVE, this);
        // Set up DROP Target for GIS Model as inactive
        gisModelDropTarget = new DropTarget(map(), DnDConstants.ACTION_COPY_OR_MOVE, map(), false);

        // Node Factory is only invoked when a user single clicks on the logical model pane
        Factory<SgNodeInterface> sgvertexFactory = new Factory<SgNodeInterface>() {
            @Override
            public SgNodeInterface create() {
                SgComponent sgComponent = _sgComponentGroupList.getComponentByUuid(currentTypeUuidStr);
                String typeName = sgComponent.getName();
                boolean showAggregationComponent = false;
                SgNodeInterface returnval = null;

                if (typeName.length() >= 11 && typeName.substring(0, 11).equalsIgnoreCase("Aggregation")) {
                    AggregationDialog aggregationDialog = new AggregationDialog(IGCAPTgui.getInstance(), true);
                    showAggregationComponent = aggregationDialog.showDialog();

                    if (showAggregationComponent) {

                        // Create aggregate node, which is the type selected in the dialog
                        // Then create all the subnodes.
                        ArrayList<igcapt.inl.components.Pair<String, Integer>> aggregateConfig = aggregationDialog.getAggregateConfiguration();
                        SgComponent selectedAggregateComponent = aggregationDialog.getSelectedComponent();

                        returnval = createAggregation(aggregateConfig, selectedAggregateComponent, IGCAPTgui.getInstance().getClickPoint(), new Coordinate(0.0, 0.0), aggregationDialog.getDefaultMaxRate());
                    }
                } else {
                    returnval = new SgNode(nodeIndex, currentTypeUuidStr, typeName + "_" + String.valueOf(nodeIndex), true, true, false, 0, 0);
                    nodeIndex++;
                }

                return returnval;
            }
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
        JMenuItem applyUseCase = new JMenuItem("Apply Use Case");
        JMenuItem analyzeTopology = new JMenuItem("Analyze Topology");
        JMenuItem exitItem = new JMenuItem("Exit");
        fileMenu.add(newTopology);
        fileMenu.add(loadTopology);
        fileMenu.add(saveTopology);
        fileMenu.add(new JSeparator()); // SEPARATOR
        fileMenu.add(applyUseCase);
        fileMenu.add(analyzeTopology);
        fileMenu.add(new JSeparator()); // SEPARATOR
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        fileMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent ev) {
                saveTopology.setEnabled(fileDirty);
            }

            @Override
            public void menuCanceled(MenuEvent ev) {
            }

            @Override
            public void menuDeselected(MenuEvent e) {
            }
        });
        // exit the app when Exit menu item selected
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                confirmExit();
            }
        });

        newTopology.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                if (clearGraph()) {
                    graphChanged();
                    fileDirty = false;
                }
            }
        });

        loadTopology.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                JFileChooser chooser = new JFileChooser();

                if (!_lastPath.isEmpty()) {
                    chooser.setCurrentDirectory(new File(_lastPath));
                }

                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            openFile(chooser);
                        }
                    });
                }
            }
        });

        applyUseCase.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                // Remove this line if you want the data to persist after the dialog is closed, in
                // anticipation for it opening again. I don't think we want to do this in case
                // the graph has changed. The data was created specific to the given graph.
                _useCaseApplyData.clear();
                ApplyUseCaseDialog applyUseCaseDialog = new ApplyUseCaseDialog(IGCAPTgui.getInstance(), true, _useCaseApplyData);
                applyUseCaseDialog.setVisible(true);

                // Closed the ApplyUseCase dialog with Ok.
                if (applyUseCaseDialog.getReturnValue() == ApplyUseCaseDialog.ReturnValue.Ok) {
                    applyUseCaseDataToGraph();
                }
            }
        });

        // get the file name for Save Topology
        saveTopology.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                JFileChooser chooser = new JFileChooser();

                if (!_lastPath.isEmpty()) {
                    chooser.setCurrentDirectory(new File(_lastPath));
                }

                if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            saveFile(chooser);
                        }
                    });
                }
            }
        });

        // analyze the current logical topology
        analyzeTopology.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ev) {

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {

                        AnalysisProgress analysisProgress = new AnalysisProgress(null, true);

                        Graph expandedGraph = getOriginalGraph();
                        AnalysisTask analysisTask = new AnalysisTask(expandedGraph);
                        _analysisCanceled = false;

                        analysisTask.addPropertyChangeListener(new PropertyChangeListener() {
                            @Override
                            public void propertyChange(PropertyChangeEvent evt) {
                                if ("progress".equals(evt.getPropertyName())) {
                                    analysisProgress.setProgress((Integer) evt.getNewValue());
                                } else if ("status".equals(evt.getPropertyName())) {
                                    analysisProgress.addStatus((String) evt.getNewValue());
                                } else if (evt.getNewValue().equals(SwingWorker.StateValue.DONE) && !_analysisCanceled) {
                                    JEditorPane ep1;
                                    try {
                                        ep1 = new JEditorPane("text/html", analysisTask.get());

                                        JScrollPane analysisResultsText = new JScrollPane(ep1);
                                        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
                                        Date now = new Date();
                                        String newTabStringLabel = sdfDate.format(now);

                                        String label = "Analysis Results" + newTabStringLabel;

                                        Component add = jtp.add(label, analysisResultsText);
                                        jtp.setTabComponentAt(jtp.indexOfComponent(add), new ButtonTabComponent(jtp));

                                        int count = jtp.getTabCount();
                                        jtp.setSelectedIndex(count - 1);

                                    } catch (InterruptedException | ExecutionException ex) {
                                        Logger.getLogger(IGCAPTgui.class.getName()).log(Level.SEVERE, null, ex);
                                    }

                                    analysisProgress.setVisible(false);
                                }
                            }
                        });

                        analysisProgress.addPropertyChangeListener("abort",
                                new PropertyChangeListener() {
                            @Override
                            public void propertyChange(PropertyChangeEvent evt) {
                                try {
                                    analysisTask.terminate();
                                    _analysisCanceled = true;
                                } catch (CancellationException ex) {
                                    // Don't do anything here.  This exception always is
                                    // thrown when a running task is cancelled.
                                }
                            }
                        }
                        );

                        analysisTask.execute();
                        analysisProgress.setVisible(true);
                    }
                });
            }
        });

        modeMenu = graphMouse.getModeMenu();  // obtain mode menu from the mouse
        modeMenu.setText("Mouse Mode");
        modeMenu.setIcon(null); // using this in a main menu
        modeMenu.setPreferredSize(new Dimension(100, 20)); // change the size
        menuBar.add(modeMenu);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem helpIGCAPT = new JMenuItem("IGCAPT Help");
        JMenuItem aboutIGCAPT = new JMenuItem("About IGCAPT");
        helpMenu.add(helpIGCAPT);
        helpMenu.add(new JSeparator()); // SEPARATOR
        helpMenu.add(aboutIGCAPT);
        menuBar.add(helpMenu);

        helpIGCAPT.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                HelpDialog helpDialog = new HelpDialog(null, true);

                helpDialog.setVisible(true);
            }
        });

        aboutIGCAPT.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
            }
        });

        this.setJMenuBar(menuBar);
        //

        // the EditingGraphMouse will pass mouse event coordinates to the
        // vertexLocations function to set the locations of the vertices as
        // they are created
        vv.setGraphMouse(graphMouse);
        vv.addKeyListener(graphMouse.getModeKeyListener());

        graphMouse.add(new MyGraphMousePlugin());
        graphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);

        // Get the pickedState and add a listener that will decorate the
        // Vertex images with a checkmark icon when they are picked
        PickedState<SgNodeInterface> ps = vv.getPickedVertexState();
        ps.addItemListener(new PickWithIconListener(vertexIconFunction));

        loadLayerIcons();

        pack();
    }

    /**
     * How to know if a device is connected to a device that can collapse the
     * first device into it. WE ONLY DEAL WITH THE EXPANDED GRAPH HERE.
     */
    /**
     * Get the parent node that can collapse this node into it.
     *
     * @param node
     * @return
     */
    private SgNode getParentCollapser(SgNode node) {
        SgNode returnval = null;

        ArrayList<SgEdge> edges = new ArrayList<>(originalGraph.getIncidentEdges(node));

        if (edges != null) {

            for (SgEdge edge : edges) {
                Pair<SgNodeInterface> endNodes = originalGraph.getEndpoints(edge);

                SgNode parent = null;
                if (endNodes.getFirst() == node) {
                    parent = (SgNode) endNodes.getSecond();
                } else {
                    parent = (SgNode) endNodes.getFirst();
                }

                SgComponent parentComponent = parent.getAssociatedComponent();
                if (parentComponent.canCollapseInto(node.getAssociatedComponent())) {
                    returnval = parent;
                    break;
                }
            }
        }

        return returnval;
    }

    /**
     * Return the list of nodes associated with collapser nodes (if contained
     * under a collapser). The first value in the pair will be the node if the
     * second is null. If the second is not null, the first will be the
     * collapser, with the second containing the list of nodes collapsed into
     * the collapser.
     *
     * @return
     */
    private ArrayList<igcapt.inl.components.Pair<SgNode, ArrayList<SgNode>>> getCollapserNodeList() {

        ArrayList<igcapt.inl.components.Pair<SgNode, ArrayList<SgNode>>> returnval = new ArrayList<>();

        ArrayList<SgNodeInterface> nodes = new ArrayList<>(getOriginalGraph().getVertices());

        for (SgNodeInterface node : nodes) {
            if (node instanceof SgNode) {
                SgNode sgNode = (SgNode) node;

                SgNode parentCollapser = getParentCollapser(sgNode);
                if (parentCollapser != null) {
                    // Is there already one here with this collapser?
                    boolean found = false;
                    for (igcapt.inl.components.Pair<SgNode, ArrayList<SgNode>> pair : returnval) {
                        if (pair.first == parentCollapser && pair.second != null) {
                            pair.second.add(sgNode);
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        returnval.add(new igcapt.inl.components.Pair<SgNode, ArrayList<SgNode>>(parentCollapser, new ArrayList<SgNode>(Arrays.asList(sgNode))));
                    }
                } else { // Not under a collapser, store as the first element in the Pair.
                    returnval.add(new igcapt.inl.components.Pair<SgNode, ArrayList<SgNode>>(sgNode, null));
                }
            }
        }

        return returnval;
    }

    /**
     * Return the list filtered by the uuid and paired down to the percent of
     * the total.
     *
     * @param unfilteredList
     * @return
     */
    ArrayList<igcapt.inl.components.Pair<SgNode, ArrayList<SgNode>>> getFilteredList(ArrayList<igcapt.inl.components.Pair<SgNode, ArrayList<SgNode>>> unfilteredList,
            UUID uuid, int percent) {
        ArrayList<igcapt.inl.components.Pair<SgNode, ArrayList<SgNode>>> returnval = new ArrayList<>();

        int uncollapsedNum = 0;

        // Compile entire list filtered by uuid. Then pair down to percent.
        // This must be done in two passes because we don't know how many there are
        // of the uuid type until we compile them all.
        for (igcapt.inl.components.Pair<SgNode, ArrayList<SgNode>> pair : unfilteredList) {

            // Single node not under collapser.
            if (pair.second == null) {
                if (pair.first.getAssociatedComponent().getTypeUuid().equals(uuid)) {
                    returnval.add(new igcapt.inl.components.Pair<>(pair.first, null));
                    uncollapsedNum++;
                }
            } // Need to look through collapser list. Only keep those with type uuid.
            else {
                ArrayList<SgNode> nodeList = new ArrayList<>();

                for (SgNode sgNode : pair.second) {
                    if (sgNode.getAssociatedComponent().getTypeUuid().equals(uuid)) {
                        nodeList.add(sgNode);
                    }
                }

                if (!nodeList.isEmpty()) {
                    returnval.add(new igcapt.inl.components.Pair<>(pair.first, nodeList));
                }
            }
        }

        // Filter nodes not associated with a collapser and those associated in the same pass.
        final int percentUncollapsed = (int) ((percent / 100.0) * uncollapsedNum + 0.5);
        int numUncollapsed = 0;

        Iterator<igcapt.inl.components.Pair<SgNode, ArrayList<SgNode>>> it = returnval.iterator();

        while (it.hasNext()) {

            igcapt.inl.components.Pair<SgNode, ArrayList<SgNode>> pair = it.next();

            if (pair.second == null) {
                numUncollapsed++;

                if (numUncollapsed > percentUncollapsed) {
                    it.remove();
                }
            } else {
                final int percentCollapsed = (int) ((percent / 100.0) * pair.second.size() + 0.5);
                int numCollapsed = 0;

                Iterator<SgNode> it2 = pair.second.iterator();
                while (it2.hasNext()) {
                    SgNode sgNode = it2.next();

                    numCollapsed++;
                    if (numCollapsed > percentCollapsed) {
                        it2.remove();
                    }
                }
            }

        }
        return returnval;
    }

    /**
     * Get the components corresponding to the use cases selected. These are
     * determined by the devices currently in the topology that are of a type
     * that include these use cases.
     */
    private ArrayList<SgNode> getUseCaseDevices(igcapt.inl.components.Pair<String, String> useCase) {

        ArrayList<SgNode> returnval = new ArrayList<>();

        // Looking through all current nodes, find all types that have this use case.
        Graph originalGraph = IGCAPTgui.getInstance().getOriginalGraph();
        List<SgNodeInterface> nodes = new ArrayList<>(originalGraph.getVertices());

        for (SgNodeInterface node : nodes) {

            if (node instanceof SgNode) {
                SgComponent component = ((SgNode) node).getAssociatedComponent();
                ArrayList<igcapt.inl.components.SgUseCase> useCaseData = component.getUseCaseData();

                for (igcapt.inl.components.SgUseCase useCaseEntry : useCaseData) {
                    if (useCase.first.equals(useCaseEntry.getUseCaseName()) && useCase.second.equals(useCaseEntry.getElementName())) {
                        returnval.add((SgNode) node);
                        break;
                    }
                }
            }
        }

        return returnval;
    }
    
    // Find the first device that has this type uuid.
    private SgNode findFirstNode(UUID uuid) {
        SgNode returnval = null;
        
        ArrayList<SgNodeInterface> nodeList = new ArrayList<>(getOriginalGraph().getVertices());
        
        for (SgNodeInterface node : nodeList) {
            if (node instanceof SgNode) {
                if (((SgNode)node).getAssociatedComponent().getTypeUuid().equals(uuid)) {
                    returnval = (SgNode)node;
                }
            }
        }
        
        return returnval;
    }
    
    private void addEndPts(igcapt.inl.components.Pair<String, String> pair, SgNode node) {
        
        SgComponent component = node.getAssociatedComponent();
        
        for(SgUseCase useCase : component.getUseCaseData()) {
            if (useCase.getUseCaseName().equals(pair.first) && useCase.getElementName().equals(pair.second)) {
                for (UUID uuid : useCase.getEndPtList()) {
                    node.getEndPointList().add(findFirstNode(uuid).getId());
                }
            }
        }
    }

    private void applyUseCaseDataToGraph() {
        /**
         * How to apply?
         *
         * 1. For those attached to devices that can collapse, apply the percent
         * relative to those devices. Apply values to the first n%. 2. For the
         * rest of the devices not attached to a device that can collapse this
         * component, apply the values to the first n%.
         *
         */

        ArrayList<igcapt.inl.components.Pair<SgNode, ArrayList<SgNode>>> organizedNodeList = getCollapserNodeList();

        // Example
//        ArrayList<igcapt.inl.components.Pair<SgNode, ArrayList<SgNode>>> filteredList = getFilteredList(organizedNodeList,
//                UUID.fromString("4576de98-9fa1-4bc6-90d3-32edb265551f"), 10);

        // If element was checked in dialog box but there are no apply-to percentages, then we apply it to all nodes that
        // include this element.
        
        // Clear all node latency and payload data in anticipation of setting.
        for (SgNodeInterface node : getOriginalGraph().getVertices()) {
            if (node instanceof SgNode) {
                SgNode sgNode = (SgNode) node;

                sgNode.setMaxLatency(0);
                sgNode.setDataToSend(0);
                
                // This gets set according to the element found in the SGComponents.xml
                sgNode.getEndPointList().clear();
            }
        }

        //Find the use case/element pair.
        for (igcapt.inl.components.Pair<String, String> pair : _useCaseApplyData.keySet()) {

            SgDataElement foundElement = null;

            for (SgApplication sgApp : _sgUseCaseData.getSgApplication()) {

                for (igcapt.inl.components.generated.SgUseCase useCase : sgApp.getSgUseCase()) {
                    if (pair.first.equals(useCase.getName())) {

                        for (SgDataElement element : useCase.getSgDataElement()) {
                            if (element.getName().equals(pair.second)) {
                                foundElement = element;
                                break;
                            }
                        }
                    }
                    if (foundElement != null) {
                        break;
                    }
                }

                if (foundElement != null) {
                    break;
                }
            }

            // Found a UseCase/Element pair
            if (foundElement != null) {

                HashMap<UUID, Integer> applyToData = _useCaseApplyData.get(pair);
                if (applyToData != null) {
                    // Loop through all UUIDs and get the filtered set.
                    // Apply the foundElement payload and maxLatency to each element.
                    // Add the payload and use the minimum maxLatency (greater than zero)
                    for (UUID uuid : applyToData.keySet()) {
                        ArrayList<igcapt.inl.components.Pair<SgNode, ArrayList<SgNode>>> filteredList = getFilteredList(organizedNodeList,
                                uuid, applyToData.get(uuid));

                        // Apply element data (payload/max latency) to each device in filteredList.
                        for (igcapt.inl.components.Pair<SgNode, ArrayList<SgNode>> nodePair : filteredList) {
                            if (nodePair.second == null) {
                                int oldPayload = nodePair.first.getDataToSend();
                                int oldLatency = (nodePair.first.getMaxLatency() > 0) ? nodePair.first.getMaxLatency() : 1;

                                nodePair.first.setDataToSend(nodePair.first.getDataToSend() + foundElement.getPayload());
                                // Combine to find a composite latency that will give the same rate when the new data is added.
                                nodePair.first.setMaxLatency(nodePair.first.getDataToSend() / (oldPayload / oldLatency + foundElement.getPayload() / foundElement.getMaxLatency()));
                                addEndPts(pair, nodePair.first);
                            } else {
                                // Loop through all contained nodes.
                                for (SgNode sgNode : nodePair.second) {
                                    int oldPayload = sgNode.getDataToSend();
                                    int oldLatency = (sgNode.getMaxLatency() > 0) ? sgNode.getMaxLatency() : 1;

                                    sgNode.setDataToSend(sgNode.getDataToSend() + foundElement.getPayload());
                                    // Combine to find a composite latency that will give the same rate when the new data is added.
                                    sgNode.setMaxLatency(sgNode.getDataToSend() / (oldPayload / oldLatency + foundElement.getPayload() / foundElement.getMaxLatency()));
                                    addEndPts(pair, sgNode);
                                }
                            }
                        }
                    }
                } else {
                    // There are active elements, but no uuid/percent info.
                    // Apply to all devices that contain the active use cases.
                    ArrayList<SgNode> useCaseDevices = getUseCaseDevices(pair);

                    for (SgNode node : useCaseDevices) {
                        int oldPayload = node.getDataToSend();
                        int oldLatency = (node.getMaxLatency() > 0) ? node.getMaxLatency() : 1; // Prevent divide by zero if not present.

                        node.setDataToSend(node.getDataToSend() + foundElement.getPayload());
                        // Combine to find a composite latency that will give the same rate when the new data is added.
                        node.setMaxLatency(node.getDataToSend() / (oldPayload / oldLatency + foundElement.getPayload() / foundElement.getMaxLatency()));
                        addEndPts(pair, node);
                    }
                }
            }
        }
    }

    public void collapse() {

        Collection picked = new HashSet(vv.getPickedVertexState().getPicked());
        if (picked.size() > 1) {
            Graph inGraph = getGraph();

            SgNodeInterface contextClickNode = getContextClickNode();

            // Get the selected nodes that comprise the sub-graph.
            Graph clusterGraph = collapser.getClusterGraph(inGraph, picked);
            if (clusterGraph instanceof SgGraph && contextClickNode instanceof SgNode) {
                SgGraph sgGraph = (SgGraph) clusterGraph;
                sgGraph.setRefNode((SgNode) contextClickNode);
            }

            Graph collapseGraph = collapser.collapse(getGraph(), clusterGraph);

            // If available, use the contextClickNode position.
            Point2D cp;

            if (contextClickNode != null) {
                cp = (Point2D) layout.transform(contextClickNode);
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
            vv.repaint();

            updateGISObjects();
        }
    }

    public void expand() {
        Collection picked = new HashSet(vv.getPickedVertexState().getPicked());
        for (Object v : picked) {
            if (v instanceof Graph) {

                Graph g = collapser.expand(getGraph(), (Graph) v);
                vv.getRenderContext().getParallelEdgeIndexFunction().reset();
                layout.setGraph(g);
            }
            vv.getPickedVertexState().clear();
            vv.repaint();
        }

        updateGISObjects();
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
        _mode = mode;
    }

    public Mode getMode() {
        return _mode;
    }

    private void confirmExit() {
        if (fileDirty) {
            if (JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to exit? The current scenario has not been saved.", "Confirm Close",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        } else {
            System.exit(0);
        }
    }

    //jmy
    private <V, E> void doNotPaintInvisibleVertices(
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
    private <V, E> void doPaintEdgesAtLeastOneVertexIsVisible(
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

    // The simplest way to clear the graph is to create a new instance.
    public boolean clearGraph() {

        boolean returnval = false;
        int result = 0;

        edgeIndex = 0;
        nodeIndex = 0;
        
        if (getGraph().getVertexCount() > 0) {
            result = JOptionPane.showConfirmDialog((Component) null, "Are you sure you want to clear the current graph?",
                    "alert", JOptionPane.OK_CANCEL_OPTION);
        }

        if (result == 0) {
            returnval = true;
            
            tempGraph = null;
            originalGraph = new SgGraph();
            vv.getGraphLayout().setGraph(originalGraph);

            // There does not appear to be a way to clear the collapser's state other than creating a new one.
            collapser = new GraphCollapser(originalGraph);

            vv.repaint();
        }
        
        return returnval;
    }

    //add code to this method to create the Logical topology diagram from xml
    void openFile(JFileChooser chooser) {

        if (!clearGraph()) {
            return;
        }

        String selectedOpenFile = chooser.getSelectedFile().toString();
        File lastFile = new File(selectedOpenFile);
        try {
            _lastPath = lastFile.getCanonicalPath();
        } catch (IOException ex) {
            _lastPath = "";
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
                    SgNode n1 = new SgNode(currentNodeIndex,
                            eElement.getElementsByTagName("type").item(0).getTextContent(),
                            eElement.getElementsByTagName("name").item(0).getTextContent(),
                            eElement.getElementsByTagName("enableDataSending").item(0).getTextContent().contentEquals("true"),
                            eElement.getElementsByTagName("enableDataPassThrough").item(0).getTextContent().contentEquals("true"),
                            eElement.getElementsByTagName("isAggregate").item(0).getTextContent().contentEquals("true"),
                            eElement.getElementsByTagName("isCollapsed").item(0).getTextContent().contentEquals("true"),
                            Integer.parseInt(eElement.getElementsByTagName("payload").item(0).getTextContent()),
                            Integer.parseInt(eElement.getElementsByTagName("maxLatency").item(0).getTextContent()));
                    double xPos = Double.parseDouble(eElement.getElementsByTagName("xCoord").item(0).getTextContent());
                    double yPos = Double.parseDouble(eElement.getElementsByTagName("yCoord").item(0).getTextContent());
                    n1.setLat(Double.parseDouble(eElement.getElementsByTagName("lat").item(0).getTextContent()));
                    n1.setLongit(Double.parseDouble(eElement.getElementsByTagName("long").item(0).getTextContent()));

                    List<Integer> endPointList = new ArrayList<>();
                    NodeList endPointListNodes = eElement.getElementsByTagName("endPoint");

                    for (int j = 0; j < endPointListNodes.getLength(); ++j) {
                        Node endPointNode = endPointListNodes.item(j);
                        endPointList.add(Integer.parseInt(endPointNode.getTextContent()));
                    }
                    n1.setEndPointList(endPointList);

                    getGraph().addVertex(n1);

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

                    ArrayList<SgNodeInterface> nodes = new ArrayList<SgNodeInterface>(getGraph().getVertices());

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

                    // If we found both ends
                    if (foundEndPts > 1) {

                        getGraph().addEdge(e1, endPt1, endPt2);

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
        
        originalGraph = (SgGraph)getGraph();

        ArrayList<SgNodeInterface> sgNodes = new ArrayList<>(originalGraph.getVertices());
        for (SgNodeInterface nodeIntf : sgNodes) {

            if (nodeIntf instanceof SgNode) {
                SgNode node = (SgNode)nodeIntf;

                if (node.getIsCollapsed()) {

                    setContextClickNode(node);
                    // Get the component corresponding to this node.
                    SgComponentGroupList sgComponentGroupList = getComponentGroupList();
                    SgComponent sgComponent = sgComponentGroupList.getComponentByUuid(node.getType());

                    // Get the list of connected nodes
                    ArrayList<SgNodeInterface> collapseableNeighborNodes = new ArrayList<>();
                    collapseableNeighborNodes.add(node);
                    List<SgNodeInterface> tempList = node.getConnectedNodes(false, collapseableNeighborNodes);

                    for (SgNodeInterface tempNode : tempList) {
                        if (sgComponent.getCollapseIntoTypeUuids().contains(tempNode.getType())) {
                            collapseableNeighborNodes.add(tempNode);
                            collapseableNeighborNodes = tempNode.getConnectedNodes(true, collapseableNeighborNodes);
                        }
                    }

                    PickedState<SgNodeInterface> pickState = vv.getPickedVertexState();
                    pickState.clear();
                    for (SgNodeInterface collapseNode : collapseableNeighborNodes) {
                        pickState.pick(collapseNode, true);
                    }

                    _IGCAPTgui.collapse();
                }
            }
        }
        
        setContextClickNode(null);

        // Redraw the graph
        vv.repaint();
        
        graphChanged();
        setCursor(Cursor.getDefaultCursor());
        fileDirty = false;
    }

    void writeGraphToDOM(Document doc, Element nodeRoot, Element edgeRoot) {

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

            edgeRoot.appendChild(edge);
        }
    }

    void saveFile(JFileChooser chooser) {
        String selectedSaveFile = chooser.getSelectedFile().toString();

        File lastFile = new File(selectedSaveFile);
        try {
            _lastPath = lastFile.getCanonicalPath();
        } catch (IOException ex) {
            _lastPath = "";
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

            writeGraphToDOM(doc, nodes, edges);

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
        } catch (Exception xmlWrite) {
            xmlWrite.printStackTrace();
        }

        fileDirty = false;
    }

    private void clearEdgeUtilization() {

        // Reset utilization on all SgNodes.  Need to expand the graph
        // in case some are collapsed.
        Graph graph = getOriginalGraph();

        ArrayList<SgEdge> sgEdges = new ArrayList<>(graph.getEdges());
        for (SgEdge sgEdge : sgEdges) {
            sgEdge.setCalcTransRate(0.0);
        }

        vv.repaint();
    }

    private class AnalysisTask extends SwingWorker<String, Integer> {

        private Graph<SgNodeInterface, SgEdge> _graph;
        private volatile boolean _running = true;

        AnalysisTask(Graph<SgNodeInterface, SgEdge> graph) {
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
            
            List<igcapt.inl.components.Pair<SgNode, SgNode>> analyzeList = new ArrayList<>();

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
                if (sgAbstractNode instanceof SgNode) {
                    SgNode sgNode = (SgNode) sgAbstractNode;
                    if (sgNode.getDataToSend() > 0 && sgNode.getEnableDataSending()) {
                        for (int endPointId : sgNode.getEndPointList()) {
                            SgNodeInterface endPointNode = getNode(sgNodeList, endPointId);

                            if (endPointNode != null && endPointId != sgNode.getId()) {
                                if (sgNode.getDataToSend() > 0.0) {
                                    igcapt.inl.components.Pair<SgNode, SgNode> innerList = new igcapt.inl.components.Pair<>(sgNode, (SgNode)endPointNode);
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
                for (igcapt.inl.components.Pair<SgNode, SgNode> pair : analyzeList) {

                    if (!_running) {
                        break;
                    }
                    setProgress(50 + 50 * i++ / analyzeList.size());

                    paths = getComponentPaths(graph, pair.first, pair.second, true);

                    double ackPayload = Double.parseDouble(IGCAPTproperties.getInstance().getPropertyKeyValue("ACKSize"));

                    for (List<Integer> sublist : paths) {
                        for (Integer value : sublist) {
                            SgEdge sgEdge = getEdge(sgEdgeList, value);
                            SgNodeInterface sgAbstractNode = pair.first;

                            if (sgAbstractNode instanceof SgNode) {
                                SgNode sgNode = (SgNode) sgAbstractNode;
                                sgEdge.setCalcTransRate(sgEdge.getCalcTransRate() + sgNode.getComputedRate());
                            }
                        }

                        // Reverse communication flow for ACK
                        for (int j = sublist.size() - 1; j >= 0; j--) {
                            SgEdge sgEdge = getEdge(sgEdgeList, sublist.get(j));
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

            updateGISObjects();

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

    public Icon getLayerIcon(String iconName) {
        return _layerIconMap.get(iconName);
    }

    protected class MyGraphMousePlugin extends TranslatingGraphMousePlugin implements MouseListener {

        @Override
        public void mouseMoved(final MouseEvent e) {

            SwingUtilities.invokeLater(
                    new Runnable() {
                public void run() {
                    VisualizationViewer<SgNodeInterface, SgEdge> vv
                            = (VisualizationViewer<SgNodeInterface, SgEdge>) e.getSource();
                    GraphElementAccessor<SgNodeInterface, SgEdge> pickSupport = vv.getPickSupport();
                    Point2D ivp = e.getPoint();
                    //graph.getVertices();

                    SgNodeInterface vertex = pickSupport.getVertex(layout, ivp.getX(), ivp.getY());
                    SgEdge edge = pickSupport.getEdge(layout, ivp.getX(), ivp.getY());

                    Border border = BorderFactory.createLineBorder(new Color(0, 0, 0)); // The color is #4c4f53.
                    UIManager.put("ToolTip.border", border);
                    if (vertex != null && toolTipsEnabled) {

                        SgNode sgNode = vertex.getRefNode();
                        SgComponent sgComponent = getComponentGroupList().getComponentByUuid(sgNode.getType());

                        String ttText = "<html>";
                        ttText += "<table width='100%' border='0' cellpadding='0'>"
                                + "<tr>"
                                + "<th align='left'>ID: </th>"
                                + "<th align='left'><font color='#ffffff'>" + sgNode.getId() + "</font></th>"
                                + "</tr>"
                                + "<tr>"
                                + "<th align='left'>Type: </th>"
                                + "<th align='left'><font color='#ffffff'>" + ((sgComponent != null) ? sgComponent.getName() : "&lt;Unknown&gt;") + "</font></th>"
                                + "</tr>"
                                + "<tr>"
                                + "<th align='left'>Payload (bytes): </th>"
                                + "<th align='left'><font color='#ffffff'>" + sgNode.getDataToSend() + "</font></th>"
                                + "</tr>"
                                + "<tr>"
                                + "<th align='left'>Interval (sec): </th>"
                                + "<th align='left'><font color='#ffffff'>" + sgNode.getMaxLatency() + "</font></th>"
                                + "</tr>";

                        // If the tooltip is for a collapsed node (an SgGraph node) then display the list of collapsed nodes.
                        if (vertex instanceof SgGraph) {
                            SgGraph sgGraph = (SgGraph) vertex;

                            ArrayList<SgNodeInterface> vertices = new ArrayList<>(sgGraph.getVertices());

                            if (vertices.size() > 0) {
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

                        vv.setToolTipText(ttText);

                    } else if (edge != null & toolTipsEnabled) {

                        // Format the calculated transfer rate.
                        DecimalFormat formatter = new DecimalFormat("#.0####", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
                        formatter.setRoundingMode(RoundingMode.DOWN);
                        String s = formatter.format(edge.getCalcTransRate());

                        Graph<SgNodeInterface, SgEdge> graph = getGraph();
                        SgNodeInterface endPt1 = graph.getEndpoints(edge).getFirst();
                        SgNodeInterface endPt2 = graph.getEndpoints(edge).getSecond();
                        String endPt1Str = "";
                        String endPt2Str = "";

                        if (endPt1 != null) {
                            if (endPt1 instanceof SgGraph) {
                                endPt1 = ((SgGraph) endPt1).getRefNode();
                            }
                            endPt1Str = endPt1.toString();
                        }
                        if (endPt2 != null) {
                            if (endPt2 instanceof SgGraph) {
                                endPt2 = ((SgGraph) endPt2).getRefNode();
                            }
                            endPt2Str = endPt2.toString();
                        }

                        vv.setToolTipText(
                                "<html>"
                                + "<table width='100%' border='0' cellpadding='0'>"
                                + "<tr>"
                                + "<th align='left' width='70'>ID: </th>"
                                + "<th align='left'><font color='#ffffff'>" + edge.getId() + "</font></th>"
                                + "</tr>"
                                + "<tr>"
                                + "<th align='left'>Weight: </th>"
                                + "<th align='left'><font color='#ffffff'>" + edge.getWeight() + "</font></th>"
                                + "</tr>"
                                + "<tr>"
                                + "<th align='left'>Max Rate (Kbits/sec): </th>"
                                + "<th align='left'><font color='#ffffff'>" + edge.getEdgeRate() + "</font></th>"
                                + "</tr>"
                                + "<tr>"
                                + "<th align='left'>End Points: </th>"
                                + "<th align='left'><font color='#ffffff'>" + endPt1Str + ", " + endPt2Str + "</font></th>"
                                + "</tr>"
                                + "<tr>"
                                + "<th align='left'>Calculated Rate (Kbits/sec): </th>"
                                + "<th align='left'><font color='#ffffff'>" + " " + String.format("%.4f", edge.getCalcTransRate()) + "</font></th>"
                                + "</tr>"
                                + "<tr>"
                                + "<th align='left'>Calculated Utilization: </th>"
                                + "<th align='left'><font color='#ffffff'>" + " " + String.format("%.2f", edge.getUtilization() * 100.0) + "%" + "</font></th>"
                                + "</tr>"
                                + "</table>"
                                + "<html>"
                        );
                    } else {
                        vv.setToolTipText(null);
                    }
                }
            }
            );
        }

        public MyGraphMousePlugin(int modifiers) {
            super(modifiers);
        }

        public MyGraphMousePlugin() {
            super();
        }

        @Override
        public void mouseClicked(final MouseEvent e) {
            e.consume();
            //super.mouseClicked(e);
            //System.out.println("mouseClicked-MyGraphMousePlugin x,y = " + e.getX() + ", " + e.getY());
        }

        @Override
        public void mouseEntered(final MouseEvent e) {
            super.mouseEntered(e);
            //System.out.println("mouseEntered-MyGraphMousePlugin x,y = " + e.getX() + ", " + e.getY());
        }

        @Override
        public void mouseExited(final MouseEvent e) {
            //super.mouseExited(e);
            //System.out.println("mouseExited-MyGraphMousePlugin x,y = " + e.getX() + ", " + e.getY());
            /*Point2D p = e.getPoint();
                    double x = p.getX();
                    xMouseLocation = x;
                    double y = p.getY();
                    yMouseLocation = y; */
        }

        @Override
        public void mouseDragged(final MouseEvent e) {
            // do not call super; this will disable drawing of the links between nodes!
            //super.mouseDragged(e);

            // Highlight the target node and remove the target when the mouse moves out.
            VisualizationViewer<SgNodeInterface, SgEdge> vv
                    = (VisualizationViewer<SgNodeInterface, SgEdge>) e.getSource();
            GraphElementAccessor<SgNodeInterface, SgEdge> pickSupport = vv.getPickSupport();
            Point2D ivp = e.getPoint();

            IGCAPTgui igcaptInstance = IGCAPTgui.getInstance();
            SgNodeInterface vertex = pickSupport.getVertex(layout, ivp.getX(), ivp.getY());
            if (vertex != null && vertex instanceof SgNodeInterface && getMode() == Mode.EDITING) {
//            if (vertex != null && vertex instanceof SgNode && getMode() == Mode.EDITING) {
                igcaptInstance.setContextClickNode((SgNodeInterface) vertex);
                Icon selectionIcon = igcaptInstance.getLayerIcon("selectionIcon");
                Icon currentIcon = vertex.getIcon();
                if (selectionIcon != null && currentIcon != null && currentIcon instanceof LayeredIcon) {
                    LayeredIcon currentLayeredIcon = (LayeredIcon) currentIcon;
                    currentLayeredIcon.add(selectionIcon);
                }
            } else {
                SgNodeInterface oldSelectedNode = igcaptInstance.getContextClickNode();

                if (oldSelectedNode != null) {
                    Icon selectionIcon = igcaptInstance.getLayerIcon("selectionIcon");
                    ((LayeredIcon) oldSelectedNode.getIcon()).remove(selectionIcon);
                    igcaptInstance.setContextClickNode(null);
                }
            }
        }

        @Override
        public void mouseReleased(final MouseEvent e) {
            super.mouseReleased(e);

            // We had a selection icon showing for the target node, so remove it.
            IGCAPTgui igcaptInstance = IGCAPTgui.getInstance();
            SgNodeInterface oldSelectedNode = igcaptInstance.getContextClickNode();

            if (oldSelectedNode != null) {
                Icon selectionIcon = igcaptInstance.getLayerIcon("selectionIcon");
                ((LayeredIcon) oldSelectedNode.getIcon()).remove(selectionIcon);
                igcaptInstance.setContextClickNode(null);
                vv.repaint();
            }
        }

        @Override
        public void mousePressed(final MouseEvent e) {
            //super.mousePressed(e);
            //System.out.println("mousePressed-MyGraphMousePlugin x,y = " + e.getX() + ", " + e.getY());
            VisualizationViewer<SgNodeInterface, SgEdge> vv
                    = (VisualizationViewer<SgNodeInterface, SgEdge>) e.getSource();
            GraphElementAccessor<SgNodeInterface, SgEdge> pickSupport = vv.getPickSupport();
            Point2D ivp = e.getPoint();

            SgNodeInterface vertex = pickSupport.getVertex(layout, ivp.getX(), ivp.getY());

            if (vertex instanceof SgNode) {
                IGCAPTgui.getInstance().setContextClickNode((SgNode) vertex);
            }
        }
    }

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

    private JMapViewer map() {
        return treeMap.getViewer();
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
        UIManager.getLookAndFeelDefaults()
                .put("defaultFont", new Font("Arial", Font.BOLD, 16));

        IGCAPTgui igCAPTgui = IGCAPTgui.getInstance();
        igCAPTgui.Initialize();
        igCAPTgui.setVisible(true);
    }

    private void updateZoomParameters() {
        if (mperpLabelValue != null) {
            mperpLabelValue.setText(String.format("%s", map().getMeterPerPixel()));
            //mperpLabelValue.setText(String.format("%s", map().getMeterPerPixel()) + "     x=" + map().getMouseX() + ", y = " + map().getMouseY() );
        }
        if (zoomValue != null) {
            zoomValue.setText(String.format("%s", map().getZoom()));
        }
    }

    // this implements the interface for JMapViewerEventListener
    @Override
    public void processCommand(JMVCommandEvent command) {
        if (command.getCommand().equals(JMVCommandEvent.COMMAND.ZOOM)
                || command.getCommand().equals(JMVCommandEvent.COMMAND.MOVE)) {
            updateZoomParameters();
        }
    }

// Jung code
    private JPanel createTreePanel() {
        JPanel treePanel = new JPanel();
        DragTree7 tree = new DragTree7(this);
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
        jtp.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
        jtp.add("Logical Model", vv);
        return jtp;
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

    public void drop(DropTargetDropEvent e) {
        fileDirty = true;

        try {
            DataFlavor stringFlavor = DataFlavor.stringFlavor;
            Transferable tr = e.getTransferable();

            if (e.isDataFlavorSupported(stringFlavor)) {
                String uuidStr = (String) tr.getTransferData(stringFlavor);
                // start kd
                boolean showAggregationComponent = false;
                SgComponent sgComponent = _sgComponentGroupList.getComponentByUuid(uuidStr);

                e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                Point point = e.getLocation();

                MultiLayerTransformer transformer = vv.getRenderContext().getMultiLayerTransformer();
                Point2D d = transformer.inverseTransform(point);
                Point newPoint = new Point((int) d.getX(), (int) d.getY());

                if (sgComponent.getTypeUuid().equals(UUID.fromString("a169911e-9079-449f-b9b7-9f79efcec135"))) {
                    AggregationDialog aggregationDialog = new AggregationDialog(this, true);
                    showAggregationComponent = aggregationDialog.showDialog();

                    //evaluate both sides of the following if statement iff the first part is false
                    if (showAggregationComponent) {

                        // Create aggregate node, which is the type selected in the dialog
                        // Then create all the subnodes.
                        ArrayList<igcapt.inl.components.Pair<String, Integer>> aggregateConfig = aggregationDialog.getAggregateConfiguration();
                        SgComponent selectedAggregateComponent = aggregationDialog.getSelectedComponent();

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

    public SgNodeInterface createAggregation(ArrayList<igcapt.inl.components.Pair<String, Integer>> aggregateConfig,
            SgComponent selectedAggregateComponent, Point point, Coordinate latLongCoord, double defaultMaxRate) {

        SgNodeInterface returnval = null;

        // Create the aggregate node of the type specified.
        SgNode aggregateNode = new SgNode(nodeIndex, selectedAggregateComponent.getTypeUuid().toString(),
                selectedAggregateComponent.getName() + "_" + String.valueOf(nodeIndex),
                true, selectedAggregateComponent.getIsPassthrough(), true, 0, 0);
        returnval = aggregateNode;

        getGraph().addVertex(aggregateNode);
        layout.setLocation(aggregateNode, point);
        aggregateNode.setLat(latLongCoord.getLat());
        aggregateNode.setLongit(latLongCoord.getLon());
        nodeIndex++;

        // Now create all the aggregated nodes
        ArrayList<SgNode> aggregateNodeList = new ArrayList<>(); // Need this for collapsing.
        for (igcapt.inl.components.Pair<String, Integer> entry : aggregateConfig) {
            String key = entry.first;
            Integer value = entry.second;
            int numComponents = value;

            if (numComponents > 0) {
                SgComponent compToCreate = _sgComponentGroupList.getComponentByUuid(key);

                for (int i = 0; i < numComponents; ++i) {
                    SgNode node = new SgNode(nodeIndex, compToCreate.getTypeUuid().toString(),
                            compToCreate.getName() + "_" + String.valueOf(nodeIndex),
                            true, compToCreate.getIsPassthrough(), false, 0, 0);

                    aggregateNodeList.add(node);

                    Point newPoint = new Point(point.x + AGGREGATE_OFFSET.x, point.y + AGGREGATE_OFFSET.y);
                    getGraph().addVertex(node);
                    layout.setLocation(node, newPoint);
                    node.setLat(latLongCoord.getLat() + AGGREGATE_LATLON_OFFSET.x);
                    node.setLongit(latLongCoord.getLon() + AGGREGATE_LATLON_OFFSET.y);

                    // Connect components with edges
                    // Need to get edgerate from the AggregationDialog.
                    SgEdge edge = new SgEdge(edgeIndex, "e" + edgeIndex,
                            1.0, 0, defaultMaxRate);
                    getGraph().addEdge(edge, aggregateNode, node);
                    getOriginalGraph().addEdge(edge, aggregateNode, node);

                    edgeIndex++;
                    nodeIndex++;
                }
            }
        }

        // Collapse around the aggregate node.
        setContextClickNode(aggregateNode);

        // Pick all the nodes including the aggregating node.
        PickedState<SgNodeInterface> pickState = vv.getPickedVertexState();
        pickState.clear();
        pickState.pick(aggregateNode, true);
        for (SgNodeInterface collapseNode : aggregateNodeList) {
            pickState.pick(collapseNode, true);
        }

        collapse();
        setContextClickNode(null);

        vv.repaint();
        updateGISObjects();

        return returnval;
    }

    private void displayImage(String uuidStr, Point point) {

        SgComponent sgComponent = _sgComponentGroupList.getComponentByUuid(uuidStr);

        try {
            String typeName = sgComponent.getName();

            boolean passThru = sgComponent.getIsPassthrough();
            SgNode n1 = new SgNode(nodeIndex, uuidStr, typeName + "_" + String.valueOf(nodeIndex), true, passThru, false, 0, 0);

            getGraph().addVertex(n1);
            layout.setLocation(n1, point);

            nodeIndex++;
            currentTypeUuidStr = uuidStr;

            vv.repaint();

            updateGISObjects();
        } catch (Exception e) {
        }
    }

    // -------------------------------------------------------------------------
    public void showDialog(SgNode node) {
        NodeSettingsDialog componentSettingsDlg = new NodeSettingsDialog(null, (SgGraph) getGraph(), node);
        componentSettingsDlg.setLocation((int) vv.getCenter().getX(), (int) vv.getCenter().getY());
        componentSettingsDlg.setVisible(true);

        if (componentSettingsDlg.getReturnValue() == NodeSettingsDialog.ReturnValue.OK) {

            node.setName(componentSettingsDlg.getComponentName());
            node.setDataToSend(componentSettingsDlg.getPayloadBytes());
            node.setMaxLatency(componentSettingsDlg.getMaxLatencySecs());
            node.setEnableDataSending(componentSettingsDlg.getEnableDataSending());
            node.setEnableDataPassThrough(componentSettingsDlg.getEnableDataPassthrough());
            node.setIsAggregate(componentSettingsDlg.getIsAggregate());

            node.setEndPointList(componentSettingsDlg.getEndPointList());

            graphChanged();
        }
    }

    public void showDialog(SgEdge edge) {
        final JTextField tbxEdgeName = new JTextField(edge.getName());
        final JTextField tbxEdgeID = new JTextField(String.valueOf(edge.getId()));
        final JTextField tbxWeight = new JTextField(String.valueOf(edge.getWeight()));
        final JTextField tbxEdgeRate = new JTextField(String.valueOf(edge.getEdgeRate()));

        final Pair<SgNodeInterface> endpoints = getGraph().getEndpoints(edge);

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

        int option = JOptionPane.showConfirmDialog(null, inputFields, "Line Settings", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);

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
