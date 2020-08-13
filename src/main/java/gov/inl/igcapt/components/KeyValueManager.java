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
    private Map<String, String> _keyValues;
    
    public KeyValueManager(String keyValues) {
        if (keyValues != null && !keyValues.isEmpty()) {
            
            var unmodmap = Splitter.on(';')
                .trimResults()
                .omitEmptyStrings()
                .withKeyValueSeparator(
                    Splitter.on('|')
                        .limit(2)
                        .trimResults())
                .split(keyValues);
            
            _keyValues = new HashMap<>(unmodmap);
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
    
    // Convert the key/values to Key|Value;Key|Value...
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        for (var entry : _keyValues.entrySet()){
            sb.append(entry.getKey());
            sb.append("|");
            sb.append(entry.getValue());
            sb.append(";");
        }
        
        return sb.toString();
    }
}
