package igcapt.inl.components;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;

/**
 *(c) 2018 BATTELLE ENERGY ALLIANCE, LLC
 *ALL RIGHTS RESERVED 
 */
public class UseCaseNodeEditor extends AbstractCellEditor implements
        TreeCellEditor {

    private final UseCaseNodeRenderer renderer = new UseCaseNodeRenderer();
    private final JTree theTree;

    public UseCaseNodeEditor(final JTree tree) {
        theTree = tree;
    }

    @Override
    public Object getCellEditorValue() {
        final UseCaseNodePanel panel = renderer.getPanel();
        final UseCaseNodeData useCaseNodeData = new UseCaseNodeData(panel.getUseCaseNodeData());
        return useCaseNodeData;
    }

    @Override
    public boolean isCellEditable(final EventObject event) {
        if (!(event instanceof MouseEvent)) {
            return false;
        }
        final MouseEvent mouseEvent = (MouseEvent) event;

        final TreePath path
                = theTree.getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
        if (path == null) {
            return false;
        }

        final Object node = path.getLastPathComponent();
        if (!(node instanceof DefaultMutableTreeNode)) {
            return false;
        }
        final DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) node;

        final Object userObject = treeNode.getUserObject();
        return userObject instanceof UseCaseNodeData;
    }

    @Override
    public Component getTreeCellEditorComponent(final JTree tree,
            final Object value, final boolean selected, final boolean expanded,
            final boolean leaf, final int row) {

        final Component nodePanel
                = renderer.getTreeCellRendererComponent(tree, value, true, expanded, leaf,
                        row, true);

        // editor always selected / focused
        final ItemListener itemListener = new ItemListener() {

            @Override
            public void itemStateChanged(final ItemEvent itemEvent) {
                if (nodePanel instanceof UseCaseNodePanel) {
                    final UseCaseNodePanel panel = (UseCaseNodePanel) nodePanel;
                    panel.getUseCaseNodeData().setClickObject(itemEvent.getSource());
                }

                stopCellEditing();
            }
        };

        final ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (nodePanel instanceof UseCaseNodePanel) {
                    final UseCaseNodePanel panel = (UseCaseNodePanel) nodePanel;
                    panel.getUseCaseNodeData().setClickObject(e.getSource());
                }

                stopCellEditing();
            }
        };

        if (nodePanel instanceof UseCaseNodePanel) {
            final UseCaseNodePanel panel = (UseCaseNodePanel) nodePanel;
            panel.check.addItemListener(itemListener);
            panel.button.addActionListener(actionListener);
        }

        return nodePanel;
    }
}
