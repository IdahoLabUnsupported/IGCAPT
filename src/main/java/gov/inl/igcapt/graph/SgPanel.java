package gov.inl.igcapt.graph;

import gov.inl.igcapt.components.DataModels.*;
import org.openstreetmap.gui.jmapviewer.IGCAPTgui;

import javax.swing.*;
import java.util.List;

public abstract class SgPanel extends JPanel {
    private final ComponentDao componentDao;
    private JLabel validationLabel;
    protected String title = "Add new component";

    public SgPanel() {
        componentDao = new ComponentDao();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        create();
    }

    public void showConfirmDialog() {
        while (true) {
            int selection = JOptionPane.showConfirmDialog(IGCAPTgui.getInstance(), this, title
                    , JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (selection == JOptionPane.YES_OPTION) {
                boolean saved = save();
                if (saved) {
                    System.out.println("SAVED!!!!");
                    reset();
                    break;
                }
            } else {
                reset(); // Remove this to save state when you push cancel
                break;
            }
        }
    }

    public abstract boolean save();
    protected abstract void reset();

    protected abstract void setupForm();

    public void create() {
        setupForm();
        addValidationLabel();
    }

    public  void addValidationLabel() {
        validationLabel = new JLabel();
        this.add(validationLabel);
    }

    public void addValidationText(String newText) {
        String currentText = validationLabel.getText();
        if (!currentText.isEmpty()) {
            currentText = currentText + ", ";
        }
        validationLabel.setText(currentText + newText);

        System.out.println("Added Validation text: " + newText);
    }

    public void clearValidationText() {
        validationLabel.setText("");
    }

    public boolean formIsValid() { return validationLabel.getText().isEmpty(); }

    public void showErrorMessage() {
        JLabel label = new JLabel("An error occured when trying to connect to the database.\n please try again later");

        this.add(label);
    }

    List<SgComponentGroupData> getAllComponentGroups() {
        return componentDao.getFirstComponentList().getSgComponentGroupData();
    }

    protected List<SgComponentData> getAllComponents() {
        return componentDao.getComponents();
    }

    List<SgCollapseInto> getAllColapseIntos() {
        return componentDao.getCollapseInto();
    }

    List<SgUseCase> getAllUsecases() { return this.componentDao.getUseCases(); }

    List<SgField> getAllFields() { return this.componentDao.getFields(); }

}
