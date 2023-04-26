/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package gov.inl.igcapt.wizard;

import java.util.List;
/**
 *
 * @author CHE
 */
// singleton to manage the threads that are used to drive the wizard
public class WizardDriver extends javax.swing.JDialog {
    
    private WizardCommandThread m_gucsThread = null;
    private WizardCommandThread m_cnrmThread = null;
    private WizardCommandThread m_verifyConnectionThread = null;
    private WizardCommandThread m_createScenarioThread = null;
    private WizardCommandThread m_gucsUpdateScenarioThread = null;
    private WizardCommandThread m_cnrmUpdateScenarioThread = null;
    private WizardCommandThread m_saveFileThread = null;
    private static WizardDriver m_wizardDriver = null;
    
    /**
     * Creates new form ScenarioWizard
     */
    public WizardDriver() {
        m_verifyConnectionThread = new WizardCommandThread(WizardCommandThread.CommandType.VERIFY_CONNECTION);
        m_gucsThread = new WizardCommandThread(WizardCommandThread.CommandType.GET_GUCS_LIST);
        m_cnrmThread = new WizardCommandThread(WizardCommandThread.CommandType.GET_CNRM_LIST);
        m_createScenarioThread = new WizardCommandThread(WizardCommandThread.CommandType.CREATE_SCENARIO);
        m_gucsUpdateScenarioThread = new WizardCommandThread(WizardCommandThread.CommandType.UPDATE_GUCS);
        m_cnrmUpdateScenarioThread = new WizardCommandThread(WizardCommandThread.CommandType.UPDATE_CNRM);
        m_saveFileThread = new WizardCommandThread(WizardCommandThread.CommandType.SAVE_FILE);        
    }
    
    // The WizardDriver should be created once per run through the wizard
    // Use the getHandle for access
    public static WizardDriver getHandle() {
        if (m_wizardDriver == null) {
            m_wizardDriver = new WizardDriver();
        }
        
        return m_wizardDriver;
    }
    
    // The WizardDriver should be created once per run through the wizard
    // use the refresh when instantiating the Scenario Wizard
    public static WizardDriver refresh() {
        m_wizardDriver = new WizardDriver();
        return m_wizardDriver;
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("New Scenario Wizard");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 664, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 69, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

        
    public void validateConnection() {
        m_verifyConnectionThread.start(); 
    }
    
    public boolean getValidConnection() {
        try {
            m_verifyConnectionThread.join();
        }
        catch (Exception e) {} 
                
        return m_verifyConnectionThread.getConnectionValid();
    }
    
    public void startGucsListThread() {
        m_gucsThread.start();
    }
    
    public List<GucsInformation> getGucs() {
        try {
            m_gucsThread.join();
        }
        catch (Exception e) { }
        return m_gucsThread.getGucsList();
    }
    
    public void startCnrmListThread() {
        m_cnrmThread.start();
    }

    public List<CnrmInformation> getCnrm() {
        try {
            m_cnrmThread.join();
        }
        catch (Exception e) { }
        return m_cnrmThread.getCnrmList();
    }

    public void startCreateScenarioThread(String cimRdfFile, String name, String desc) {
        m_createScenarioThread.setScenarioParams(cimRdfFile, name, desc);
        m_createScenarioThread.start();
    }
    
    public ScenarioInformation getScenarioInfo() {
        try {
            m_createScenarioThread.join();
        }
        catch (Exception e) {}
        
        return m_createScenarioThread.getScenario();
    }
    
    public void startGucsUpdateScenarioThread(int[] selectedIndices) {
        ScenarioInformation scenario = null;
        try {
            m_createScenarioThread.join();
            scenario = m_createScenarioThread.getScenario();
        }
        catch (Exception e) {}
             
        m_gucsUpdateScenarioThread.setGucsUpdateParams(selectedIndices, 
                scenario.getId(),
                m_gucsThread.getGucsList());
        m_gucsUpdateScenarioThread.start();
    }
    
    public void startCnrmUpdateScenarioThread(int[] selectedIndices) {
        try {
            m_createScenarioThread.join();
        }
        catch (Exception e) {}
        ScenarioInformation scenario = m_createScenarioThread.getScenario();
        m_cnrmUpdateScenarioThread.setCnrmUpdateParams(selectedIndices, 
                scenario.getId(),
                m_cnrmThread.getCnrmList());
        m_cnrmUpdateScenarioThread.start();
    }
    
    public void startSaveFileThread(String filename) {
        ScenarioInformation scenario = m_createScenarioThread.getScenario();
        m_saveFileThread.setSaveFileParams(scenario.getId(), 
                filename);             
        m_saveFileThread.saveFile();
    }
    
    public void joinSaveFileThread() {
        try {
            m_saveFileThread.join();
        }
        catch (Exception e) {}
        
    }
    
    public String getConnectionErrorMesg() {
        return m_verifyConnectionThread.getErrorMessage();
    }
    
    public String getGucsErrorMesg() {
        return m_gucsThread.getErrorMessage();
    }
    
    public String getCnrmErrorMesg() {
        return m_cnrmThread.getErrorMessage();
    }
    
    public String getScenarioErrorMesg() {
        return m_createScenarioThread.getErrorMessage();
    }
    
    public String getUpdateGucsErrorMesg() {
        return m_gucsUpdateScenarioThread.getErrorMessage();
    }
    
    public String getUpdateCnrmErrorMesg() {
        return m_cnrmUpdateScenarioThread.getErrorMessage();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}