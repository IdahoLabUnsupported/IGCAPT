package gov.inl.igcapt.gdtaf.data;

import gov.inl.igcapt.gdtaf.model.GDTAF;
import gov.inl.igcapt.gdtaf.model.OperationalObjective;
import jdk.dynalink.Operation;

import java.util.HashMap;
import java.util.Map;

/**
 * OperationalObjectivRepoMgr is a manager/accessor class for GTDAF
 * Operational Objective Repository data.  Data is stored in a map keyed
 * on uuid.  The OperationalObjectivRepoMgr class must be initialized
 * once the GDTAF Scenario has been loaded into memory
 */

public class OperationalObjectiveRepoMgr {

    private Map<String, OperationalObjective> m_opobj_map = new HashMap<String, OperationalObjective>();
    private static final OperationalObjectiveRepoMgr instance = new OperationalObjectiveRepoMgr();
    private OperationalObjectiveRepoMgr(){

    }

    /**
     * hook to the Singleton class
     * @return OperationalObjectiveRepoMgr
     */
    public static OperationalObjectiveRepoMgr getInstance(){
        return instance;
    }

    /**
     * initializes the class with  data from the GDTAF scenario
     * Must be called before use.
     * @param gdtaf
     */
    public void initRepo(gov.inl.igcapt.gdtaf.model.GDTAF gdtaf){
        var ooList = gdtaf.getOperationalObjectiveRepo().getOperationalObjective();
        for(var oo : ooList){
            m_opobj_map.put(oo.getUUID(), oo);
        }
    }

    /**
     * getter for an OperationalObjective object
     * @param uuid
     * @return OperationalObjective
     */
    public OperationalObjective getOperationalObjective(String uuid){
        return m_opobj_map.get(uuid);
    }

    /**
     * number of Operational Objectives stored
     * @return int
     */
    public int count(){return m_opobj_map.size();}

}
