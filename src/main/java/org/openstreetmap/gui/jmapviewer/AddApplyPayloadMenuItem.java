package org.openstreetmap.gui.jmapviewer;

import javax.swing.*;
import gov.inl.igcapt.components.DependentUseCaseEntry;
import gov.inl.igcapt.components.UseCaseEntry;
import java.awt.Cursor;
import java.util.ArrayList;
import java.util.List;

public class AddApplyPayloadMenuItem extends JMenuItem {

    AddApplyPayloadMenuItem(java.awt.Frame parent) {
        super("Apply Payload");
        createApplyPayloadMenu(parent);
    }

    private void createApplyPayloadMenu(java.awt.Frame parent) {
        
        if (parent instanceof IGCAPTgui igcaptGui){
            this.addActionListener(ActionListener -> {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                // Clear all existing payloads.
                for (SgNodeInterface node : igcaptGui.getOriginalGraph().getVertices()) {
                    if (node instanceof SgNode sgNode) {

                        sgNode.setMaxLatency(0);
                        sgNode.setDataToSend(0);
                        sgNode.clearUseCaseUserData();

                        // This gets set according to the element found in the SGComponents.xml
                        // sgNode.getEndPointList().clear();
                    }
                }

                // Get percent of all nodes (exclude aggregate nodes)
                for(UseCaseEntry entry:igcaptGui.getPayload().payloadUseCaseList) {
                    int percentToApply = entry.getPercentToApply();

                    List<SgNode> percentNodes = igcaptGui.getPercentNodes(percentToApply, null);

                    for (SgNode node:percentNodes) {
                        node.applyUseCase(entry.getUseCaseName());
                    }
                }

                for (DependentUseCaseEntry entry:igcaptGui.getPayload().payloadDependentUseCaseList) {
                    int percentToApply = entry.getPercentToApply();

                    // Get the original set from which we will choose the dependent set.
                    List<SgNode> percentNodes = igcaptGui.getPercentNodes(percentToApply, null);
                    List<SgNode> exclusionSet = new ArrayList<>();

                    for (UseCaseEntry depEntry:entry.useCases) {
                        int depPercentToApply = depEntry.getPercentToApply();
                        List<SgNode> depNodesToApply = igcaptGui.getPercentNodes(percentNodes, depPercentToApply, exclusionSet);

                        for (SgNode node:depNodesToApply) {
                            node.applyUseCase(depEntry.getUseCaseName());
                        }

                        exclusionSet.addAll(depNodesToApply);
                    }
                }

                setCursor(Cursor.getDefaultCursor());
            });            
        }
    }
}
