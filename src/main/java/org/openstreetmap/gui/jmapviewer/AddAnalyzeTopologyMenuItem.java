package org.openstreetmap.gui.jmapviewer;

import edu.uci.ics.jung.graph.Graph;
import gov.inl.igcapt.components.AnalysisProgress;
import gov.inl.igcapt.components.ButtonTabComponent;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class AddAnalyzeTopologyMenuItem extends JMenuItem {

    AddAnalyzeTopologyMenuItem(java.awt.Frame parent) {
        super("Component Editor...");
        createAnalyzeTopologyMenu(parent);
    }

    private void createAnalyzeTopologyMenu(java.awt.Frame parent) {
        
        if (parent instanceof IGCAPTgui igcaptGui) {
            
            this.addActionListener(ActionListener -> {
                
                SwingUtilities.invokeLater(() -> {
                    AnalysisProgress analysisProgress = new AnalysisProgress(null, true);

                    Graph expandedGraph = igcaptGui.getOriginalGraph();
                    IGCAPTgui.AnalysisTask analysisTask = igcaptGui.new AnalysisTask(expandedGraph);
                    igcaptGui.setAnalysisCanceled(false);

                    analysisTask.addPropertyChangeListener((PropertyChangeEvent evt) -> {
                        if ("progress".equals(evt.getPropertyName())) {
                            analysisProgress.setProgress((Integer) evt.getNewValue());
                        } else if ("status".equals(evt.getPropertyName())) {
                            analysisProgress.addStatus((String) evt.getNewValue());
                        } else if (evt.getNewValue().equals(SwingWorker.StateValue.DONE) && !igcaptGui.isAnalysisCanceled()) {
                            JEditorPane ep1;
                            try {
                                ep1 = new JEditorPane("text/html", analysisTask.get());

                                JScrollPane analysisResultsText = new JScrollPane(ep1);
                                SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
                                Date now = new Date();
                                String newTabStringLabel = sdfDate.format(now);

                                String label = "Analysis Results" + newTabStringLabel;

                                Component add = igcaptGui.getJtp().add(label, analysisResultsText);
                                igcaptGui.getJtp().setTabComponentAt(igcaptGui.getJtp().indexOfComponent(add), new ButtonTabComponent(igcaptGui.getJtp()));

                                int count = igcaptGui.getJtp().getTabCount();
                                igcaptGui.getJtp().setSelectedIndex(count - 1);

                            } catch (InterruptedException | ExecutionException ex) {
                                Logger.getLogger(IGCAPTgui.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            analysisProgress.setVisible(false);
                        }
                    });

                    analysisProgress.addPropertyChangeListener("abort", (PropertyChangeEvent evt) -> {
                        try {
                            analysisTask.terminate();
                            igcaptGui.setAnalysisCanceled(true);
                        } catch (CancellationException ex) {
                            // Don't do anything here.  This exception always is
                            // thrown when a running task is cancelled.
                        }
                    });

                    analysisTask.execute();
                    analysisProgress.setVisible(true);
                });
            });            
        }
    }
}
