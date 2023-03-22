/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gov.inl.igcapt.components;

import edu.uci.ics.jung.graph.Graph;
import gov.inl.igcapt.graph.GraphManager;
import gov.inl.igcapt.graph.SgEdge;
import gov.inl.igcapt.graph.SgNode;
import gov.inl.igcapt.graph.SgNodeInterface;
import gov.inl.igcapt.properties.IGCAPTproperties;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import gov.inl.igcapt.view.IGCAPTgui;

/**
 *
 * @author FRAZJD
 */
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
            IGCAPTgui.getInstance().clearEdgeUtilization();
            
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
                                
                                int protocolOverhead = sgEdge.getFixedOverhead() + (int)(sgNode.getDataToSend()*sgEdge.getMultiplierOverhead());
                                double protocolOverheadRate = sgNode.computeRate(sgNode.getMaxLatency(), protocolOverhead);
                                
                                sgEdge.setCalcTransRate(sgEdge.getCalcTransRate() + sgNode.getComputedRate() + protocolOverheadRate);
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
                    
                    edu.uci.ics.jung.graph.util.Pair<SgNodeInterface> endPts = graph.getEndpoints(sgEdge);

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
                            if (IGCAPTgui.getInstance().isShowAllAnalysisResults()) { //Diagnostic output
                                analysisResults.append("(" + endPt1.getName() + " - "
                                        + endPt2.getName() + ")/e" + sgEdge.getId()
                                        + ", Network Capacity = " + String.format("%.3f", sgEdge.getEdgeRate())
                                        + ", Network Usage = " + String.format("%.3f", sgEdge.getCalcTransRate())
                                        + ", Utilization = <font size=\"+1\" color=\"green\"><b>"
                                        + String.format("%.2f", sgEdge.getUtilization() * 100.0)
                                        + "%</b></font><br>");
                            }
                        } else {
                            if (IGCAPTgui.getInstance().isShowAllAnalysisResults()) { //Diagnostic output
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

            IGCAPTgui.getInstance().refresh();

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

                // The second part catches when the node will not pass through but is not the sender.
                // In that case we want to treat it just like we are at the destination node.
                if (fromNode == toNode || (!fromNode.getEnableDataPassThrough() && !isSender)) {
                    ArrayList<Integer> x = new ArrayList<>();
                    returnval.add(x);
                } else if (isSender || currentNode.getEnableDataPassThrough()) {
                    // Cycle through all connected components

                    // Get list of connected edges.
                    List<SgEdge> sgEdges = new ArrayList<>(graph.getIncidentEdges(fromSgNode));

                    for (SgEdge sgEdge : sgEdges) {
                        
                        if (sgEdge.isEnabled()) {
                            SgNode nextComponent = null;

                            edu.uci.ics.jung.graph.util.Pair<SgNodeInterface> endpoints = graph.getEndpoints(sgEdge);

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