package gov.inl.igcapt.gdtaf.data;

import gov.inl.igcapt.gdtaf.model.GDTAF;
import gov.inl.igcapt.gdtaf.model.GridUseCaseScenario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GUCSRepoMgr is a manager/accessor class for GTDAF
 * Grid Use Case Scenario (GUCS) Repository data.  Data is stored in a map keyed
 * on uuid.  The GUCSRepoMgr class must be initialized
 * once the GDTAF Scenario has been loaded into memory
 */
public class GUCSRepoMgr {
    private static final GUCSRepoMgr instance = new GUCSRepoMgr();
    private Map<String, GridUseCaseScenario> m_gucsMap = new HashMap<String, GridUseCaseScenario>();

    /**
     * hook to this Singleton class.
     * @return GUCSRepoMgr
     */
    public static GUCSRepoMgr getInstance(){
        return instance;
    }

    /**
     * populates this class with data from the GDTAF Scenario file
     * Must be called before initial use.
     * @param gdtafData
     */
    public void initRepo(GDTAF gdtafData){
        var gucsList = gdtafData.getGridUseCaseScenarioRepo().getGridUseCaseScenario();
        for (var gucs:gucsList) {
            m_gucsMap.put(gucs.getUUID(), gucs);
        }
    }

    /**
     * getter for Grid Use Case Scenario object based on UUID lookup
     * @param uuid
     * @return GridUseCaseScenario
     */
    public GridUseCaseScenario getGridUseCase(String uuid){
        return m_gucsMap.get(uuid);
    }

    /**
     * the number of gucs stored in the map
     * @return int
     */
    public int count(){
        return m_gucsMap.size();
    }
}
