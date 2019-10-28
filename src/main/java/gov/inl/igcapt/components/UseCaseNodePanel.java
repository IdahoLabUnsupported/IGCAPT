package gov.inl.igcapt.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 *(c) 2018 BATTELLE ENERGY ALLIANCE, LLC
 *ALL RIGHTS RESERVED 
 */

public class UseCaseNodePanel extends JPanel {

    public final JLabel label = new JLabel();
    public final JCheckBox check = new JCheckBox();
    public final JButton button = new JButton("Apply To...");

    private final UseCaseNodeData useCaseNodeData = new UseCaseNodeData("", false);

    public UseCaseNodeData getUseCaseNodeData() {
        useCaseNodeData.setText(label.getText());
        useCaseNodeData.setChecked(check.isSelected());
        return useCaseNodeData;
    }

    public UseCaseNodePanel() {
        check.setMargin(new Insets(0, 0, 0, 0));
        check.setBackground(Color.WHITE);

        setLayout(new BorderLayout());
        add(check, BorderLayout.LINE_START);

        label.setBorder(new EmptyBorder(0, 0, 0, 0));
        label.setPreferredSize(new Dimension(300, 20));
        add(label, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);

        button.setMargin(new Insets(0, 0, 0, 0));
        button.setFont(new Font("Arial", Font.BOLD, 9));
        button.setPreferredSize(new Dimension(70, 15));
        button.setEnabled(true);
        buttonPanel.add(button);
        add(buttonPanel, BorderLayout.LINE_END);
    }
}
