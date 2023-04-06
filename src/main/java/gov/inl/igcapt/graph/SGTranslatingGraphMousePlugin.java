/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.igcapt.graph;

import edu.uci.ics.jung.visualization.control.TranslatingGraphMousePlugin;
import gov.inl.igcapt.view.IGCAPTgui;

import java.awt.event.MouseEvent;

/**
 *(c) 2018 BATTELLE ENERGY ALLIANCE, LLC
 *ALL RIGHTS RESERVED 
 *
 * @author FRAZJD
 */
public class SGTranslatingGraphMousePlugin extends TranslatingGraphMousePlugin {

    public SGTranslatingGraphMousePlugin() {
        super();
    }

    public SGTranslatingGraphMousePlugin(int modifiers) {
        super(modifiers);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        
        GraphManager.getInstance().setFileDirty(true);
    }

}
