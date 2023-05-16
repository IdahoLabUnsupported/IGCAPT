package gov.inl.igcapt.gdtaf.data;

import gov.inl.igcapt.gdtaf.model.ApplicationScenario;

import java.util.*;

public class ApplicationScenarioRepoMgr {
    private static final ApplicationScenarioRepoMgr instance = new ApplicationScenarioRepoMgr();
    private Map<String, ApplicationScenario> m_appScenMap = new HashMap<String, ApplicationScenario>();
    private ApplicationScenarioRepoMgr(){}

    /**
     * hook to the application scenario repo singelton
     * @return instance
     */
    public static ApplicationScenarioRepoMgr getInstance(){
        return instance;
    }

    /**
     * initializes the class with data from the GDTAF scenario.. must be called
     * prior to use
     * @param gdtaf
     */
    public void initRepo(gov.inl.igcapt.gdtaf.model.GDTAF gdtaf){
        var appscenList = gdtaf.getApplicationScenarioRepo().getApplicationScenario();
        for(var appscen : appscenList){
            m_appScenMap.put(appscen.getUUID(), appscen);
        }
    }

    /**
     * getter for Application Scenario by UUID
     * @param uuid
     * @return
     */
    public ApplicationScenario getApplicationScenario(String uuid){
        return(m_appScenMap.get(uuid));
    }

    /**
     * getter for all application scenarios in GDTAF Scenario File
     * @return
     */
    public Collection<ApplicationScenario> getAllApplicationScenarios(){
        return m_appScenMap.values();
    }

    /**
     * getter for ApplicationScenario by uuid list
     * @param uuidList
     * @return List of ApplicationScenario objects
     */
    public List<ApplicationScenario> getApplicationScenariosByList(List<String> uuidList){
        List<ApplicationScenario> appScenarioList = new ArrayList<ApplicationScenario>();
        for(var uuid: uuidList){
            appScenarioList.add(getApplicationScenario(uuid));
        }
        return appScenarioList;
    }

}
