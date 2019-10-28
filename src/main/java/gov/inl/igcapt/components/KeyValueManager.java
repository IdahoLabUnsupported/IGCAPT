/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.igcapt.components;

import java.util.HashMap;
import java.util.Map;
import com.google.common.base.Splitter;

/**
 *
 * @author FRAZJD
 * Take a string of the form: key|value;key|value and parse it into the dictionary.
 */
public class KeyValueManager {
    Map<String, String> _keyValues;
    
    public KeyValueManager(String keyValues) {
        if (keyValues != null && !keyValues.isEmpty()) {
            
            _keyValues = Splitter.on(';')
                .trimResults()
                .omitEmptyStrings()
                .withKeyValueSeparator(
                    Splitter.on('|')
                        .limit(2)
                        .trimResults())
                .split(keyValues);
        }
    }
    
    public Map<String, String> KeyValues() {
        return _keyValues;
    }
    
    public String Value(String key){
        
        String returnval = null;
        
        if (_keyValues != null)
        {
            returnval = _keyValues.get(key);
        }
        
        return returnval;
    }
}
