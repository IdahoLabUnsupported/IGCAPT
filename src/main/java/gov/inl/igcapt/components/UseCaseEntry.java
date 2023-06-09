/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.igcapt.components;

/**
 *
 * @author FRAZJD
 */
public class UseCaseEntry implements java.io.Serializable {
    
    private int percentToApply = 100;
    public int getPercentToApply() {
        return percentToApply;
    }

    public void setPercentToApply(int percentToApply) {
        this.percentToApply = percentToApply;
    }

    private String useCaseName;
    public String getUseCaseName() {
        return useCaseName;
    }

    public void setUseCaseName(String useCaseName) {
        this.useCaseName = useCaseName;
    }
    
    /**
     *
     * @return The string representation of this UseCaseEntry.
     */
    @Override
    public String toString() {
        
        return useCaseName + "    " + Integer.toString(percentToApply) + "%";
    }
}
