package gov.inl.igcapt.view;

import org.openstreetmap.gui.jmapviewer.IGCAPTgui;

import javax.swing.*;
import java.io.File;

public class AddImportNs3ResultsMenuItem extends JMenuItem {

    public AddImportNs3ResultsMenuItem() {
        super("Import NS3 Results...");
        createImportNs3ResultsMenu();
    }

    private void createImportNs3ResultsMenu() {
        
        this.addActionListener(ActionListener -> {
            JFileChooser chooser = new JFileChooser();

            if (!IGCAPTgui.getInstance().getLastPath().isEmpty()) {
                chooser.setCurrentDirectory(new File(IGCAPTgui.getInstance().getLastPath()));
            }

            if (chooser.showOpenDialog(IGCAPTgui.getInstance()) == JFileChooser.APPROVE_OPTION) {
                SwingUtilities.invokeLater(() -> {
                    IGCAPTgui.getInstance().importResults(chooser);
                });
            }
        });            
    }
}
