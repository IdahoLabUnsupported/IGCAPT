package gov.inl.igcapt.gdtaf.data;

import gov.inl.igcapt.gdtaf.model.GDTAF;
import gov.inl.igcapt.gdtaf.model.Payload;

import java.util.HashMap;
import java.util.Map;

/**
 * PayloadRepoMgr is a manager/accessor class for GTDAF
 * Payload Repository data.  Data is stored in a map keyed
 * on uuid.  The PayloadRepoMgr class must be initialized
 * once the GDTAF Scenario has been loaded into memory
 */
public class PayloadRepoMgr {
    private static final PayloadRepoMgr instance = new PayloadRepoMgr();
    private Map<String, Payload> m_payLoadRepo = new HashMap<String, Payload>();
    private PayloadRepoMgr(){}

    /**
     * hook method to the singleton class
     * @return PayloadRepoMgr
     */
    public static PayloadRepoMgr getInstance(){
        return instance;
    }

    /**
     * initialize the class with data from the GDTAF Scenario
     * Must be called before PayloadRepoMgr use
     * @param gdtaf
     */
    public void initRepo(GDTAF gdtaf){
        resetRepo();
        var payloadList = gdtaf.getPayloadRepo().getPayload();
        for (var payload:payloadList) {
            m_payLoadRepo.put(payload.getUUID(), payload);
        }
    }

    private void resetRepo(){
        m_payLoadRepo.clear();
    }

    /**
     * getter for a Payload object based on uuid lookup
     * @param uuid
     * @return Payload
     */
    public Payload getPayload(String uuid){
        return m_payLoadRepo.get(uuid);
    }

    /**
     * nuber of Payload Objects stored
     * @return int
     */
    public int count(){ return m_payLoadRepo.size();}
}
