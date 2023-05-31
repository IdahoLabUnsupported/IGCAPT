package gov.inl.igcapt.view;


import javax.swing.*;
import java.io.File;
import gov.inl.igcapt.properties.IGCAPTproperties;
import gov.inl.igcapt.properties.IGCAPTproperties.IgcaptProperty;

public class AddImportNs3ResultsMenuItem extends JMenuItem {

    public AddImportNs3ResultsMenuItem() {
        super("Import NS3 Results...");
        createImportNs3ResultsMenu();
    }

    private void createImportNs3ResultsMenu() {
        
        this.addActionListener(ActionListener -> {
            JFileChooser chooser = new JFileChooser();
            String lastPath = 
                    IGCAPTproperties.getInstance().getPropertyKeyValue(IgcaptProperty.LAST_PATH);
            if (!lastPath.isEmpty()) {
                chooser.setCurrentDirectory(new File(lastPath));
            }

            if (chooser.showOpenDialog(IGCAPTgui.getInstance()) == JFileChooser.APPROVE_OPTION) {
                SwingUtilities.invokeLater(() -> {
                    IGCAPTgui.getInstance().importResults(chooser);
                });
            }
        });            
    }
}
