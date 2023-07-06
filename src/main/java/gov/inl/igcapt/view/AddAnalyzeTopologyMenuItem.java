package gov.inl.igcapt.view;

import edu.uci.ics.jung.graph.Graph;
import gov.inl.igcapt.components.AnalysisProgress;
import gov.inl.igcapt.components.AnalysisTask;
import gov.inl.igcapt.components.ResultsDialog;
import gov.inl.igcapt.graph.GraphManager;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.concurrent.CancellationException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class AddAnalyzeTopologyMenuItem extends JMenuItem {

    private static ResultsDialog currentResultsDialog = null;
    
    public AddAnalyzeTopologyMenuItem() {
        super("Analyze Topology...");
        createAnalyzeTopologyMenu();
    }

    private void createAnalyzeTopologyMenu() {
        
        this.addActionListener((ActionEvent ActionListener) -> {

            if (!GraphManager.getInstance().getAnalysisDirty() && currentResultsDialog != null) {
                if (!currentResultsDialog.isVisible()) {
                    currentResultsDialog.setVisible(true);
                }
            }
            else {
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
                                currentResultsDialog = new ResultsDialog(IGCAPTgui.getInstance(), false);
                                currentResultsDialog.UpdateResults(analysisTask.get());
                                currentResultsDialog.setVisible(true);

                            } catch (Exception ex) {
                                Logger.getLogger(IGCAPTgui.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            analysisProgress.setVisible(false);
                        }
                    });

                    analysisProgress.addPropertyChangeListener("abort", (PropertyChangeEvent evt) -> {
                        try {
                            analysisTask.terminate();
                            analysisTask.cancel(true);
                            IGCAPTgui.getInstance().setAnalysisCanceled(true);
                        } catch (CancellationException ex) {
                            // Don't do anything here.  This exception always is
                            // thrown when a running task is cancelled.
                        }
                    });

                    analysisTask.execute();
                    analysisProgress.setVisible(true);
                }); 
            }
        });            
    }
}
