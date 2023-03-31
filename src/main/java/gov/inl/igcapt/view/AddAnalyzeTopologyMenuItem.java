package gov.inl.igcapt.view;

import edu.uci.ics.jung.graph.Graph;
import gov.inl.igcapt.components.AnalysisProgress;
import gov.inl.igcapt.components.AnalysisTask;
import gov.inl.igcapt.components.ButtonTabComponent;
import gov.inl.igcapt.components.ResultsDialog;
import gov.inl.igcapt.graph.GraphManager;

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

    public AddAnalyzeTopologyMenuItem() {
        super("Analyze Topology...");
        createAnalyzeTopologyMenu();
    }

    private void createAnalyzeTopologyMenu() {
        
        this.addActionListener(ActionListener -> {

            SwingUtilities.invokeLater(() -> {
                AnalysisProgress analysisProgress = new AnalysisProgress(IGCAPTgui.getInstance(), true);
                analysisProgress.setLocationRelativeTo(IGCAPTgui.getInstance());
                Graph expandedGraph = GraphManager.getInstance().getOriginalGraph();
                AnalysisTask analysisTask = new AnalysisTask(expandedGraph);
                IGCAPTgui.getInstance().setAnalysisCanceled(false);

                analysisTask.addPropertyChangeListener((PropertyChangeEvent evt) -> {
                    if ("progress".equals(evt.getPropertyName())) {
                        analysisProgress.setProgress((Integer) evt.getNewValue());
                    } else if ("status".equals(evt.getPropertyName())) {
                        analysisProgress.addStatus((String) evt.getNewValue());
                    } else if (evt.getNewValue().equals(SwingWorker.StateValue.DONE) && !IGCAPTgui.getInstance().isAnalysisCanceled()) {
                        try {
                            // Display results dialog
                            ResultsDialog resultsDialog = new ResultsDialog(IGCAPTgui.getInstance(), false);
                            
                            if (resultsDialog != null) {
                                resultsDialog.UpdateResults();
                                
                                resultsDialog.setVisible(true);
                            }

                        } catch (Exception ex) {
                            Logger.getLogger(IGCAPTgui.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        analysisProgress.setVisible(false);
                    }
                });

                analysisProgress.addPropertyChangeListener("abort", (PropertyChangeEvent evt) -> {
                    try {
                        analysisTask.terminate();
                        IGCAPTgui.getInstance().setAnalysisCanceled(true);
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
