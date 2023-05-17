package gov.inl.igcapt.gdtaf.data;

import gov.inl.igcapt.gdtaf.model.DataElement;
import gov.inl.igcapt.gdtaf.model.GDTAF;

import java.util.HashMap;
import java.util.Map;

/**
 * DataElemementRepoMgr is a manager/accessor class for GTDAF
 * Data Element Repository data.  Data is stored in a map keyed
 * on data element uuid.  The DataElementRepoMgr class must be initialized
 * once the GDTAF Scenario has been loaded into memory
 */
public class DataElementRepoMgr {

    private static final DataElementRepoMgr instance = new DataElementRepoMgr();
    private Map<String, DataElement> m_dataElementMap = new HashMap<String, DataElement>();

    private DataElementRepoMgr(){}

    /**
     * The DataElementRepoMgr singleton accessor method
     * @return DataElementRepoMgr
     */
    public static DataElementRepoMgr getInstance(){
        return instance;
    }

    /**
     * Must be called before DataElementRepoMgr use.. this
     * method populates the Map with data from the GDTAF
     * scenario
     * @param gdtafData
     */
    public void initRepo(GDTAF gdtafData){
        resetRepo();
        var dataElementList = gdtafData.getDataElementRepo().getDataElement();
        for (var de : dataElementList) {
            m_dataElementMap.put(de.getUUID(), de);
        }
    }

    private void resetRepo(){
        m_dataElementMap.clear();
    }

    /**
     * getter for a data element out of the map
     * @param uuid
     * @return DataElement
     */
    public DataElement getDataElement(String uuid){
        return m_dataElementMap.get(uuid);
    }

    /**
     * get number of elements in the map
     * @return int
     */
    public int count(){
        return m_dataElementMap.size();
    }

}
