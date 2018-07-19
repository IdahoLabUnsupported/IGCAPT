/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package igcapt.inl.components;

import igcapt.inl.components.generated.SgComponentDataElement;
import java.util.ArrayList;
import java.util.UUID;

/**
 *(c) 2018 BATTELLE ENERGY ALLIANCE, LLC
 *ALL RIGHTS RESERVED 
 *
 * @author FRAZJD
 */
public class SgUseCase {

    public SgUseCase(SgComponentDataElement element) {
        
        _useCaseName = element.getUseCase();
        _elementName = element.getName();
        
        _endPtList = new ArrayList<UUID>();
        
        for (String uuidStr : element.getEndPt()) {
            _endPtList.add(UUID.fromString(uuidStr));
        }
    }
    
    private String _useCaseName;
    private String _elementName;
    private ArrayList<UUID> _endPtList;    

    public String getUseCaseName() {
        return _useCaseName;
    }

    public void setUseCaseName(String _useCaseName) {
        this._useCaseName = _useCaseName;
    }

    public String getElementName() {
        return _elementName;
    }

    public void setElementName(String _elementName) {
        this._elementName = _elementName;
    }

    public ArrayList<UUID> getEndPtList() {
        return _endPtList;
    }
}
