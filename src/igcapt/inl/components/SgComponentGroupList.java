/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package igcapt.inl.components;

import igcapt.inl.components.generated.SgComponentGroupData;
import igcapt.inl.components.generated.SgComponentListData;
import java.util.ArrayList;
import java.util.UUID;

/**
 *(c) 2018 BATTELLE ENERGY ALLIANCE, LLC
 *ALL RIGHTS RESERVED 
 *
 * @author FRAZJD
 */
public class SgComponentGroupList {
    
    public SgComponentGroupList(SgComponentListData sgComponentListData) {
        
        for (SgComponentGroupData sgComponentGroupData : sgComponentListData.getSgComponentGroupData()) {
            
            SgComponentGroup sgComponentGroup = new SgComponentGroup(sgComponentGroupData);
            _sgComponentGroups.add(sgComponentGroup);
        }
        
    }
    
    private ArrayList<SgComponentGroup> _sgComponentGroups = new ArrayList<>();
    
    public ArrayList<SgComponentGroup> getSgComponentGroups() {
        return _sgComponentGroups;
    }
    
    public SgComponent getComponentByUuid(UUID uuid) {
        SgComponent returnval = null;
        
        // Search all groups for this Uuid.
        boolean found = false;
        for (SgComponentGroup sgComponentGroup : _sgComponentGroups) {
            for(SgComponent sgComponent : sgComponentGroup.getComponents()) {
                if (sgComponent.getTypeUuid().equals(uuid)) {
                    returnval = sgComponent;
                    found=true;
                    break;
                }
            }
            // Short circuit the outer loop if the component was found.
            if (found) {
                break;
            }
        }
        
        return returnval;
    }
    
    public SgComponent getComponentByName(String componentName) {
        SgComponent returnval = null;
        
        // Search all groups for this Uuid.
        boolean found = false;
        for (SgComponentGroup sgComponentGroup : _sgComponentGroups) {
            for(SgComponent sgComponent : sgComponentGroup.getComponents()) {
                if (sgComponent.getName().equalsIgnoreCase(componentName)) {
                    returnval = sgComponent;
                    found=true;
                    break;
                }
            }
            // Short circuit the outer loop if the component was found.
            if (found) {
                break;
            }
        }
        
        return returnval;
    }
    
    public ArrayList getAggregateComponents() {
        ArrayList aggregateComponentList = new ArrayList();
        
        for (SgComponentGroup sgComponentGroup : _sgComponentGroups) {
            for(SgComponent sgComponent : sgComponentGroup.getComponents()) {
                if (sgComponent.isAggregate()) {
                    aggregateComponentList.add(sgComponent);
                }
            }
        }
        
        return aggregateComponentList;
    }
    
    public SgComponent getComponentByUuid(String uuidStr) {
        UUID uuid = UUID.fromString(uuidStr);
        
        return getComponentByUuid(uuid);
    }
}
