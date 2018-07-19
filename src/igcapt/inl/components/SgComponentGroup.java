/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package igcapt.inl.components;

import igcapt.inl.components.generated.SgComponentData;
import igcapt.inl.components.generated.SgComponentGroupData;
import java.util.ArrayList;

/**
 *(c) 2018 BATTELLE ENERGY ALLIANCE, LLC
 *ALL RIGHTS RESERVED 
 *
 * Category of components.  This is the grouping in which the components will appear
 * in the tool palette.
 * @author FRAZJD
 */
public class SgComponentGroup {

    private String _groupName;
    private boolean _isDisplayed;
    private ArrayList<SgComponent> _sgComponents = null;
    
    public SgComponentGroup(SgComponentGroupData sgComponentGroupData) {
        _groupName = sgComponentGroupData.getGroupName();
        _isDisplayed = sgComponentGroupData.isDisplay();
        _sgComponents = new ArrayList<>();

        for (SgComponentData sgComponentData : sgComponentGroupData.getSgComponentData()) {
            _sgComponents.add(new SgComponent(sgComponentData));
        }
    }
    
    public String getGroupName() {
        return _groupName;
    }
    
    public ArrayList<SgComponent> getComponents() {
        return _sgComponents;
    }
    
    public boolean isDisplayed() {
        return _isDisplayed;
    }
}
