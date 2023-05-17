package gov.inl.igcapt.gdtaf.data;

import gov.inl.igcapt.gdtaf.model.CNRM;
import gov.inl.igcapt.gdtaf.model.GDTAF;

import java.util.HashMap;
import java.util.Map;

/**
 * CNRMRepoMgr is a manager/accessor class for GTDAF
 * CNRM Repository data.  Data is stored in a map keyed
 * on uuid.  The CNRMRepoMgr class must be initialized
 * once the GDTAF Scenario has been loaded into memory
 */
public class CNRMRepoMgr {
    private static final CNRMRepoMgr instance = new CNRMRepoMgr();
    private Map<String, CNRM> m_cnrmMap = new HashMap<String, CNRM>();
    private CNRMRepoMgr(){}

    /**
     * hook to the Singleton class instance
     * @return CNRMRepoMgr
     */
    public static CNRMRepoMgr getInstance(){
        return instance;
    }

    /**
     * populates the class with data.  Must be called prior to
     * use
     * @param gdtafData
     */
    public void initRepo(GDTAF gdtafData){
        resetRepo();
        var cnrmList = gdtafData.getCNRMRepo().getCNRM();
        for (var cnrm: cnrmList) {
            m_cnrmMap.put(cnrm.getUUID(), cnrm);
        }
    }

    private void resetRepo(){
        m_cnrmMap.clear();
    }

    /**
     * getter for CNRM based on uuid
     * @param uuid
     */
    public CNRM getCNRM(String uuid){
        return m_cnrmMap.get(uuid);
    }

    /**
     * returns the number of elements in the cnrm map
     * @return int
     */
    public int count(){
        return m_cnrmMap.size();
    }

    /**
     * uuid to name helper function
     * @param uuid
     * @return name
     */
    public String getCNRMName(String uuid){
        return getCNRM(uuid).getName();
    }

}
