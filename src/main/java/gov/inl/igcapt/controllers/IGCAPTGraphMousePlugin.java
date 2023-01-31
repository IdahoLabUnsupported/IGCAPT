package gov.inl.igcapt.controllers;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.LayeredIcon;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.TranslatingGraphMousePlugin;
import gov.inl.igcapt.components.DataModels.SgComponentData;
import gov.inl.igcapt.graph.GraphManager;
import gov.inl.igcapt.graph.SgEdge;
import gov.inl.igcapt.graph.SgGraph;
import gov.inl.igcapt.graph.SgNode;
import gov.inl.igcapt.graph.SgNodeInterface;
import org.openstreetmap.gui.jmapviewer.*;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

public class IGCAPTGraphMousePlugin extends TranslatingGraphMousePlugin implements MouseListener{

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

                        SgNodeInterface vertex = pickSupport.getVertex(IGCAPTgui.getInstance().getAbstractLayout(), ivp.getX(), ivp.getY());
                        SgEdge edge = pickSupport.getEdge(IGCAPTgui.getInstance().getAbstractLayout(), ivp.getX(), ivp.getY());

                        Border border = BorderFactory.createLineBorder(new Color(0, 0, 0)); // The color is #4c4f53.
                        UIManager.put("ToolTip.border", border);
                        if (vertex != null && IGCAPTgui.getInstance().isToolTipsEnabled()) {

                            SgNode sgNode = vertex.getRefNode();
                            SgComponentData sgComponent = IGCAPTgui.getInstance().getComponentByUuid(sgNode.getType());

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

                        } else if (edge != null & IGCAPTgui.getInstance().isToolTipsEnabled()) {

                            // Format the calculated transfer rate.
                            DecimalFormat formatter = new DecimalFormat("#.0####", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
                            formatter.setRoundingMode(RoundingMode.DOWN);
                            String s = formatter.format(edge.getCalcTransRate());

                            Graph<SgNodeInterface, SgEdge> graph = GraphManager.getInstance().getGraph();
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

    public IGCAPTGraphMousePlugin(int modifiers) {
        super(modifiers);
    }

    public IGCAPTGraphMousePlugin() {
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
        GraphManager graphManagerInstance = GraphManager.getInstance();
        SgNodeInterface vertex = pickSupport.getVertex(igcaptInstance.getAbstractLayout(), ivp.getX(), ivp.getY());
        if (vertex != null && vertex instanceof SgNodeInterface && IGCAPTgui.getInstance().getMode() == ModalGraphMouse.Mode.EDITING) {
//            if (vertex != null && vertex instanceof SgNode && getMode() == Mode.EDITING) {
            graphManagerInstance.setContextClickNode((SgNodeInterface) vertex);
            Icon selectionIcon = igcaptInstance.getLayerIcon("selectionIcon");
            Icon currentIcon = vertex.getIcon();
            if (selectionIcon != null && currentIcon != null && currentIcon instanceof LayeredIcon) {
                LayeredIcon currentLayeredIcon = (LayeredIcon) currentIcon;
                currentLayeredIcon.add(selectionIcon);
            }
        } else {
            SgNodeInterface oldSelectedNode = graphManagerInstance.getContextClickNode();

            if (oldSelectedNode != null) {
                Icon selectionIcon = igcaptInstance.getLayerIcon("selectionIcon");
                ((LayeredIcon) oldSelectedNode.getIcon()).remove(selectionIcon);
                graphManagerInstance.setContextClickNode(null);
            }
        }
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        super.mouseReleased(e);

        // We had a selection icon showing for the target node, so remove it.
        IGCAPTgui igcaptInstance = IGCAPTgui.getInstance();
        GraphManager graphManagerInstance = GraphManager.getInstance();
        SgNodeInterface oldSelectedNode = graphManagerInstance.getContextClickNode();

        if (oldSelectedNode != null) {
            Icon selectionIcon = igcaptInstance.getLayerIcon("selectionIcon");
            ((LayeredIcon) oldSelectedNode.getIcon()).remove(selectionIcon);
            graphManagerInstance.setContextClickNode(null);
            IGCAPTgui.getInstance().vv.repaint();
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

        SgNodeInterface vertex = pickSupport.getVertex(IGCAPTgui.getInstance().getAbstractLayout(), ivp.getX(), ivp.getY());

        if (vertex instanceof SgNode) {
            GraphManager.getInstance().setContextClickNode((SgNode) vertex);
        }
    }
}