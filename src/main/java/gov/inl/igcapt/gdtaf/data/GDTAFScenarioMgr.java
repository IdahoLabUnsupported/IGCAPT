package gov.inl.igcapt.gdtaf.data;

import gov.inl.igcapt.gdtaf.model.GDTAF;
import gov.inl.igcapt.gdtaf.model.Scenario;
import gov.inl.igcapt.gdtaf.model.Solution;
import gov.inl.igcapt.gdtaf.model.SolutionOption;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class GDTAFScenarioMgr {
    private static final GDTAFScenarioMgr instance = new GDTAFScenarioMgr();
    private Map<String, Scenario> m_scenarioMap = new HashMap<String, Scenario>();

    private Scenario m_activeScenario = null;
    private Solution m_activeSolution = null;
    private SolutionOption m_activeOption = null;

    private GDTAFScenarioMgr(){}

    private void warningMessage(String message){
        JOptionPane.showMessageDialog(null,
                message,
                "Attention",
                JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Singleton Pattern hook to object
     * @return ScenarioRepoMgr
     */
    public static GDTAFScenarioMgr getInstance(){
        return instance;
    }

    /**
     * Initialize the Repo MGR with data.  Must be done once the
     * GDTAF scenario File is read...   and before use of this class
     * @param gdtaf
     */
    public void initRepo(GDTAF gdtaf){
        var scenarioList = gdtaf.getScenarioRepo().getScenario();
        for (var scenario:scenarioList) {
            m_scenarioMap.put(scenario.getUUID(), scenario);
        }
    }

    /**
     * getter... get Scenario by UUID
     * @param uuid
     * @return Scenario
     */
    public Scenario getScenario(String uuid){
        return m_scenarioMap.get(uuid);
    }

    /**
     * getter get Scenario by Name
     * If not found returns null
     * @param name
     * @return Scenario
     */
    public Scenario getByName(String name){
        for (var scenario: m_scenarioMap.values()) {
            if (scenario.getName().equals(name)){
                return scenario;
            }
        }
        return null;
    }

    /**
     * getter for active Scenario
     * @return Scenario
     */
    public Scenario getActiveScenario() {
        return m_activeScenario;
    }

    /**
     * getter for active Solution
     * @return Solution
     */
    public Solution getActiveSolution() {
        return m_activeSolution;
    }

    /**
     * getter for active solution option
     * @return SolutionOption
     */
    public SolutionOption getActiveSolutionOption(){
        return m_activeOption;
    }

    /**
     * setter  for activeScenario by name
     * @param name
     */
    public void setActiveScenarioByName(String name){
        m_activeScenario = getByName(name);
        if  (m_activeScenario == null){
            warningMessage("ActiveScenario not set: " + name + " not found");
        }
    }

    /**
     * setter for activeScenario by uuid.
     * @param uuid
     */
    public void setActiveScenario(String uuid){
        m_activeScenario = m_scenarioMap.get(uuid);
        if  (m_activeScenario == null){
            warningMessage("ActiveScenario not set: " + uuid + " not found");
        }

    }

    /**
     * setter for active GDTAF Solution. An Active Scenario must be set prior
     * to setting the Solution, as Solutions are apart of the Scenario Definition
     * @param name
     */
    public void setActiveSolutionByName(String name){
        if(m_activeScenario == null){
            warningMessage("ActiveScenario not set.  Can not set Solution without an active Scenario");
        }
        else{
            var solutionList = m_activeScenario.getSolution();
            for (var soln:solutionList) {
                if(soln.getName().equals(name)){
                    m_activeSolution = soln;
                }
            }
            if(m_activeSolution == null){
                warningMessage("Active Solution not set: " + name + " not found");
            }
        }
    }

    /**
     * setter for active solution.  Active Scenario must be set before setting
     * active solution as the solution is apart of the scenario
     * @param uuid
     */
    public void setActiveSolution(String uuid){
        if(m_activeScenario == null){
            warningMessage("ActiveScenario not set.  Can not set Solution without an active Scenario");
        }
        else{
            var solutionList = m_activeScenario.getSolution();
            for (var soln:solutionList) {
                if(soln.getUUID().equals(uuid)){
                    m_activeSolution = soln;
                }
            }
            if(m_activeSolution == null){
                warningMessage("Active Solution not set: " + uuid + " not found");
            }
        }
    }

    /**
     * setter for Solution Option by Name.  active Solution must be set prior to setting
     * Solution Option as the Solution Option is a property of a Solution
     * @param name
     */
    public void setSolutionOptionByName(String name){
        if(m_activeSolution == null){
            warningMessage("ActiveSolution not set.  Can not set Solution Option without an active Solution");
        }
        else{
            var optionList = m_activeSolution.getOption();
            for (var option:optionList) {
                if(option.getName().equals(name)){
                    m_activeOption = option;
                }
            }
            if(m_activeSolution == null){
                warningMessage("Active Solution not set: " + name + " not found");
            }
        }
    }

    /**
     * setter for Solution Option by UUID.  Active Solution must be set prior to setting
     * Solution Option as Solution Option is a propery of Solution
     * @param uuid
     */
    public void setSolutionOption(String uuid){
        if(m_activeSolution == null){
            warningMessage("ActiveSolution not set.  Can not set Solution Option without an active Solution");
        }
        else{
            var optionList = m_activeSolution.getOption();
            for (var option:optionList) {
                if(option.getUUID().equals(uuid)){
                    m_activeOption = option;
                }
            }
            if(m_activeSolution == null){
                warningMessage("Active Solution not set: " + uuid + " not found");
            }
        }
    }


}
