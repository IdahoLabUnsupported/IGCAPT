package gov.inl.igcapt.gdtaf.data;

import gov.inl.igcapt.gdtaf.model.GDTAF;
import gov.inl.igcapt.gdtaf.model.GridCommunicationPath;

import java.util.HashMap;
import java.util.Map;

/**
 * GridCommPathMgr is a manager/accessor class for GTDAF
 * Grid Communication Path Repository data.  Data is stored in a map keyed
 * on uuid.  The GridCommPathMgr class must be initialized
 * once the GDTAF Scenario has been loaded into memory
 */
public class GridCommPathRepoMgr {
    private static final GridCommPathRepoMgr instance = new GridCommPathRepoMgr();
    private Map<String, GridCommunicationPath> m_gridCommPathMap = new HashMap<String, GridCommunicationPath>();

    private GridCommPathRepoMgr(){}

    /**
     * class singleton accessor method
     * @return GridCommPathRepoMgr
     */
    public static GridCommPathRepoMgr getInstance(){
        return instance;
    }

    /**
     * populates the class with data from the GDTAF Scenario
     * Must be called before class use.
     * @param gdtafData
     */
    public void initRepo(GDTAF gdtafData){
        var gcpList = gdtafData.getGridCommunicationPathRepo().getGridCommunicationPath();
        for (var gcp: gcpList) {
            m_gridCommPathMap.put(gcp.getUUID(), gcp);
        }
    }

    /**
     * getter of the GridCommunicationPath object based on UUID
     * @param uuid
     * @return GridCommunicationPath
     */
    public GridCommunicationPath getGridCommunicationPath(String uuid){
        return m_gridCommPathMap.get(uuid);
    }

    /**
     * number of elements stored in map
     * @return
     */
    public int count(){
        return m_gridCommPathMap.size();
    }
}
