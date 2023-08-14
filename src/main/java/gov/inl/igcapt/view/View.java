package gov.inl.igcapt.view;

import java.awt.BorderLayout;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import gov.inl.igcapt.properties.IGCAPTproperties;
import org.openstreetmap.gui.jmapviewer.JSGMapViewer;
import org.openstreetmap.gui.jmapviewer.events.JMVCommandEvent;
import org.openstreetmap.gui.jmapviewer.interfaces.JMapViewerEventListener;

public class View extends JFrame implements JMapViewerEventListener, DropTargetListener {

    ///Private Variables
    private static View _view = new View();
    private final JSGMapViewer treeMap = new JSGMapViewer("Component");
    
    public View() {
        super("Intelligent Grid Communications & Analysis Planning Tool");
        this.initialize();
    }

    public int initialize(){
        setSize(400, 400);
        // Listen to the map viewer for user operations so components will
        // receive events and update
        treeMap.getViewer().addJMVListener(this);

        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                confirmExit();
            }
        });
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        JPanel panel = new JPanel(new BorderLayout());
        JPanel panelTop = new JPanel();
        JPanel panelBottom = new JPanel();
        JPanel helpPanel = new JPanel();


        return(1);
    }

    private void confirmExit() {
        if (false ){//fileDirty) {
            if (JOptionPane.showConfirmDialog(IGCAPTgui.getInstance(),
                    "Are you sure you want to exit? The current scenario has not been saved.", "Confirm Close",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {

                IGCAPTproperties.getInstance().storeProperties();
                System.exit(0);
            }
        } else {
            IGCAPTproperties.getInstance().storeProperties();
            System.exit(0);
        }
    }

    /**
     * @param dtde the {@code DropTargetDragEvent}
     */
    @Override
    public void dragEnter(DropTargetDragEvent dtde) {

    }

    /**
     * @param dtde the {@code DropTargetDragEvent}
     */
    @Override
    public void dragOver(DropTargetDragEvent dtde) {

    }

    /**
     * @param dtde the {@code DropTargetDragEvent}
     */
    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {

    }

    /**
     * @param dte the {@code DropTargetEvent}
     */
    @Override
    public void dragExit(DropTargetEvent dte) {

    }

    /**
     * @param dtde the {@code DropTargetDropEvent}
     */
    @Override
    public void drop(DropTargetDropEvent dtde) {

    }

    /**
     * @param command
     */
    @Override
    public void processCommand(JMVCommandEvent command) {

    }
}
