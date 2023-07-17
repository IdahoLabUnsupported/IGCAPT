/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gov.inl.igcapt.components;

import edu.uci.ics.jung.graph.Graph;
import gov.inl.igcapt.components.DataModels.SgComponentData;
import gov.inl.igcapt.gdtaf.data.GDTAFScenarioMgr;
import gov.inl.igcapt.graph.GraphManager;
import gov.inl.igcapt.graph.SgEdge;
import gov.inl.igcapt.graph.SgNode;
import gov.inl.igcapt.graph.SgNodeInterface;
import gov.inl.igcapt.view.IGCAPTgui;

import javax.swing.*;
import java.util.*;

/**
 *
 * @author fewda
 */
    public class CostAnalysisTask extends SwingWorker<String, Integer> {

    private Graph<SgNodeInterface, SgEdge> _graph;
    private static volatile boolean _running = true;

    private StringBuilder timestampStr;

    public CostAnalysisTask(Graph<SgNodeInterface, SgEdge> graph) {
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

    public String analyze(Graph graph) {

        List<Pair<SgNode, SgNode>> analyzeList = new ArrayList<>();

        Date startDate = new Date();

        int i = 0;

        firePropertyChange("status", "old", "Crunching Cost Data");
        sgNodeList = new ArrayList<SgNodeInterface>(graph.getVertices());
        Map<String, CostAnalysisEntry> m_costReportData = new HashMap<>();
        for (SgNodeInterface sgAbstractNode : sgNodeList) {

            if (!_running) {
                return null;
            }

            // Only take 50% of our progress in this phase.  Take the rest below.
            setProgress(100 * ++i / sgNodeList.size());

            // If there is data to send and sending is enabled, generate the list
            // of start/end nodes for which the paths will be generated.
            if (sgAbstractNode instanceof SgNode sgNode) {
                SgComponentData componentData = sgNode.getAssociatedComponent();
                if (m_costReportData.containsKey(componentData.getName()) == false) {
                    m_costReportData.put(componentData.getName(), new CostAnalysisEntry(componentData.getName()));
                }
                var entry = m_costReportData.get(componentData.getName());
                entry.addEntry();
                for (var attr : componentData.getAttributes()) {
                    switch (attr.getName()) {
                        case "CAPEX_PROJECTED" -> entry.setCapexProjected(attr.getValue());
                        case "CAPEX_ACTUAL" -> entry.setCapexActual(attr.getValue());
                        case "OPEX_PROJECTED" -> entry.setOpexProjected(attr.getValue());
                        case "OPEX_ACTUAL" -> entry.setOpexActual(attr.getValue());
                        default -> {
                        }
                    }
                }
            }

        }
        ArrayList<CostAnalysisEntry> reportData = new ArrayList<>();
        for (var entry: m_costReportData.entrySet()){
            if( entry.getValue().getCapexActualTotal() >= 0 ||
                entry.getValue().getCapexProjectedTotal() >= 0 ||
                entry.getValue().getOpexActualTotal() >= 0 ||
                entry.getValue().getOpexProjectedTotal() >= 0){
                System.out.println(entry.getValue().getComponentName() + ":");
                System.out.println("\t Quantity: " + entry.getValue().getQuantity());
                System.out.println("\t Capex/unit actual: " + entry.getValue().getCapexUnitActual());
                System.out.println("\t Capex Total: " + entry.getValue().getCapexActualTotal());
                reportData.add(entry.getValue());
            }
        }

        if (_running) {
            Date endDate = new Date();
            timestampStr = new StringBuilder();
            timestampStr.append(GDTAFScenarioMgr.getInstance().getActiveScenario().getName());
            timestampStr.append(GDTAFScenarioMgr.getInstance().getActiveSolution().getName());
            timestampStr.append(GDTAFScenarioMgr.getInstance().getActiveSolutionOption().getName());
            timestampStr.append("Analysis start time: ");
            timestampStr.append(startDate);
            timestampStr.append("\nAnalysis end time: ");
            timestampStr.append(endDate);

            GraphManager.getInstance().setAnalysisDirty(false);
            IGCAPTgui.getInstance().refresh();
        }
        return timestampStr.toString();
    }
}
