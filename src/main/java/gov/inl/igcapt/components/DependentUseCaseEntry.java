/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.igcapt.components;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author FRAZJD
 */
public class DependentUseCaseEntry implements java.io.Serializable {
    private int percentToApply;
    public int getPercentToApply() {
        return percentToApply;
    }

    public void setPercentToApply(int percentToApply) {
        this.percentToApply = percentToApply;
    }
    
    public List<UseCaseEntry> useCases = new ArrayList<>();
    
    /**
     *
     * @return The string representation of this UseCaseEntry.
     */
    @Override
    public String toString() {
        
        return AddUseCaseDlg.DEPENDENT_LABEL + "    " + Integer.toString(percentToApply) + "%";
    }
}
