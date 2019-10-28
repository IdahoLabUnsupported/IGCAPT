package org.openstreetmap.gui.jmapviewer;

import gov.inl.igcapt.components.DataModels.ComponentDao;
import gov.inl.igcapt.components.DataModels.SgComponentData;
import gov.inl.igcapt.components.DataModels.SgComponentGroupData;

import javax.swing.*;
import java.io.File;
import java.util.List;

public class ComponentMenu extends JMenuItem {
    private JPanel panel;
    private SgComponentData componentData;
    private SgComponentGroupData componentGroupData;

    private ComponentDao componentDao;

    ComponentMenu() {
        super("Add new Component");
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        componentData = new SgComponentData();

        componentDao = new ComponentDao();

        createComponentsMenu();
    }

    private void createComponentsMenu() {
        this.addActionListener(ActionListener -> {
            addForm();
            showConfirmDialog();
        });
    }

    private void addForm() {
        try {
            addNameTextBox();
            addIconSelector();
            addComponentGroupSelector();
            addCollapseIntoMultiSelect();
        } catch (Exception ex) {
            showErrorMessage();
        }
    }

    private void showConfirmDialog() {
        JOptionPane.showConfirmDialog(null, panel, "Add new component"
                , JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    }

    private void addNameTextBox() {
        JLabel label = new JLabel("Name");
        panel.add(label);

        JTextField name = new JTextField();
        name.setBounds(128, 65, 86, 20);

        label.setLabelFor(name);

        panel.add(name);
        name.setColumns(10);
    }

    private void addIconSelector() {
        JLabel label = new JLabel("Icon path");
        panel.add(label);

        JFileChooser iconChooser = new JFileChooser();

        JButton icon = new JButton("Select Icon");

        label.setLabelFor(icon);

        panel.add(icon);
        icon.addActionListener(actionEvent -> {
            int returnval = iconChooser.showOpenDialog(panel);

            if (returnval == JFileChooser.APPROVE_OPTION){
                File file = iconChooser.getSelectedFile();
                this.componentData.setIconPath(file.getPath());
            }
        });
    }

    private void addComponentGroupSelector() throws Exception {
        List<SgComponentGroupData> groups = getAllComponentGroups();
        if (groups.isEmpty()) {
            throw new Exception();
        }

        String[] groupNames = new String[groups.size()];

        for (int i = 0; i < groups.size(); ++i) {
            groupNames[i] = groups.get(i).getGroupName();
        }

        JLabel label = new JLabel("Component group");
        panel.add(label);

        JComboBox<String> componentGroup = new JComboBox<>(groupNames);

        label.setLabelFor(componentGroup);

        panel.add(componentGroup);
    }

    private void addCollapseIntoMultiSelect() throws Exception {
        List<SgComponentData> compnents = getAllComponents();
        if (compnents.isEmpty()) {
            throw new Exception();
        }

        String[] componentUuids = new String[compnents.size()];

        for (int i = 0; i < compnents.size(); ++i) {
            componentUuids[i] = compnents.get(i).getUuid();
        }

        JLabel label = new JLabel("Collapse into");
        panel.add(label);

        JList<String> collapseInto = new JList<>(componentUuids);
        collapseInto.setVisibleRowCount(5);
        collapseInto.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        label.setLabelFor(collapseInto);

        panel.add(new JScrollPane(collapseInto));
    }

    private void showErrorMessage() {
        JLabel label = new JLabel("An error occured when trying to connect to the database.\n please try again later");

        panel.add(label);
    }

    private List<SgComponentGroupData> getAllComponentGroups() {
        return componentDao.getFirstComponentList().getSgComponentGroupData();
    }

    private List<SgComponentData> getAllComponents() {
        return componentDao.getComponents();
    }


}
