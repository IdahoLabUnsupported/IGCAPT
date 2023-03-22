package gov.inl.igcapt.view;


import javax.swing.*;

public class AddFieldMenuItem extends JMenuItem {
    private final FieldPanel panel;

    public AddFieldMenuItem() {
        super("Add new Field");
        panel = new FieldPanel();
        createFieldMenu();
    }

    private void createFieldMenu() {
        this.addActionListener(ActionListener -> {
            panel.showConfirmDialog();
        });
    }
}
