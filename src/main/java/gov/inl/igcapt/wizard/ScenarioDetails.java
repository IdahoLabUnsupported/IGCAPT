/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gov.inl.igcapt.wizard;

import java.util.Base64;

/**
 *
 * @author CHE
 */
public class ScenarioDetails {
    private String m_content;
    private ScenarioInformation m_scenarioInformation;
    
    public String getContent() {
        return m_content;
    }

    public void setContent(String content) {
        byte[] byteArray = Base64.getDecoder().decode(content);
        m_content = new String(byteArray);
    }

    public ScenarioInformation getScenarioInformation() {
        return m_scenarioInformation; 
    }
    
    public void setScenarioInformation(ScenarioInformation scenarioInformation) {
        m_scenarioInformation = scenarioInformation;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("***Scenario Details****\n");               
        sb.append("CONTENT:\n").append(m_content).append("\n");
        sb.append("Scenario Information:\n").append(getScenarioInformation()).append("\n");
        return sb.toString();
    }    
}
