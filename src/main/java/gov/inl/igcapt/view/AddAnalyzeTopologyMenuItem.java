package gov.inl.igcapt.view;

import edu.uci.ics.jung.graph.Graph;
import gov.inl.igcapt.components.AnalysisProgress;
import gov.inl.igcapt.components.ButtonTabComponent;
import gov.inl.igcapt.graph.GraphManager;
import org.openstreetmap.gui.jmapviewer.IGCAPTgui;

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
                AnalysisProgress analysisProgress = new AnalysisProgress(null, true);

                Graph expandedGraph = GraphManager.getInstance().getOriginalGraph();
                IGCAPTgui.AnalysisTask analysisTask = IGCAPTgui.getInstance().new AnalysisTask(expandedGraph);
                IGCAPTgui.getInstance().setAnalysisCanceled(false);

                analysisTask.addPropertyChangeListener((PropertyChangeEvent evt) -> {
                    if ("progress".equals(evt.getPropertyName())) {
                        analysisProgress.setProgress((Integer) evt.getNewValue());
                    } else if ("status".equals(evt.getPropertyName())) {
                        analysisProgress.addStatus((String) evt.getNewValue());
                    } else if (evt.getNewValue().equals(SwingWorker.StateValue.DONE) && !IGCAPTgui.getInstance().isAnalysisCanceled()) {
                        JEditorPane ep1;
                        try {
                            ep1 = new JEditorPane("text/html", analysisTask.get());

                            JScrollPane analysisResultsText = new JScrollPane(ep1);
                            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
                            Date now = new Date();
                            String newTabStringLabel = sdfDate.format(now);

                            String label = "Analysis Results" + newTabStringLabel;

                            Component add = IGCAPTgui.getInstance().getJtp().add(label, analysisResultsText);
                            IGCAPTgui.getInstance().getJtp().setTabComponentAt(IGCAPTgui.getInstance().getJtp().indexOfComponent(add), new ButtonTabComponent(IGCAPTgui.getInstance().getJtp()));

                            int count = IGCAPTgui.getInstance().getJtp().getTabCount();
                            IGCAPTgui.getInstance().getJtp().setSelectedIndex(count - 1);

                        } catch (InterruptedException | ExecutionException ex) {
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
