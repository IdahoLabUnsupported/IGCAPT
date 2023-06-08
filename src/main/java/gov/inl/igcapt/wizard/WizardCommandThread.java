/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gov.inl.igcapt.wizard;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import gov.inl.igcapt.properties.WebServiceProperties;
import gov.inl.igcapt.properties.WebServiceProperties.WebServiceProperty;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author CHE
 */
/* Rather than multiple thread classes, this class is meant to be instantiated for each
   type of data it retrieves
*/
public class WizardCommandThread extends Thread {
    enum CommandType {
        GET_GUCS_LIST,   
        GET_CNRM_LIST,   
        CREATE_SCENARIO, 
        UPDATE_CNRM,
        UPDATE_GUCS,
        SAVE_FILE,
        VERIFY_CONNECTION
    }
    
    private CommandType m_command = null;
    private List<GucsInformation> m_gucsList = null;
    private List<CnrmInformation> m_cnrmList = null;
    private boolean m_connectionValid = false;
    private final String m_webServiceHost;
    private final String m_webServiceKey;
    private String m_errorMsg = null;
    private String m_cimRdfFile = null;
    private String m_name = null;
    private String m_desc = null;
    private String m_filename = null;
    private ScenarioInformation m_scenarioInfo = null;
    private String m_scenarioId = null;
    private int[] m_gucsSelectedIndices = null;
    private int[] m_cnrmSelectedIndices = null;

    public WizardCommandThread (CommandType command) {
        m_command = command;
        m_webServiceHost = WebServiceProperties.getInstance().getPropertyKeyValue(WebServiceProperty.WEB_SERVICE_HOST);
        m_webServiceKey = WebServiceProperties.getInstance().getPropertyKeyValue(WebServiceProperty.WEB_SERVICE_KEY);
    }
    
    // thread will execute based on m_command which is set in constructor
    public void run() {
        switch (m_command) {
            case GET_GUCS_LIST:
                gucsListFromSvc();
            break;
            case GET_CNRM_LIST:
                cnrmListFromSvc();
            break;
            case CREATE_SCENARIO:
                createScenario();
            break;
            case UPDATE_CNRM:
                updateCnrm();
            break;
            case UPDATE_GUCS:
                updateGucs();
            break;
            case SAVE_FILE:
                saveFile();
            break;
            case VERIFY_CONNECTION:
                verifyConnection();
        }    
    }
    
    // (Not currently used but) Verifies web service connection
    private void verifyConnection() {
        try {
            URL url = new URL("https://" + m_webServiceHost + "/framework" +
            "?subscription-key=" + m_webServiceKey);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) {
                m_errorMsg = "Web Service connection failed -- Error code : " +
                    conn.getResponseCode();
                m_connectionValid = false;
            }
            m_connectionValid = true;
            conn.disconnect();
            }
        catch (Exception e3) {
            m_errorMsg = "Web Service failure -- " + e3.getMessage();
        }
    }
    
    // set params to create scenario
    public void setScenarioParams(String cimRdfFile, String name, String desc) {
        m_cimRdfFile = cimRdfFile;
        m_name = name;
        m_desc = desc;
    }
    
    // encode the Cim/Rdf file for scenario creation
    private String encodeCimrdf(String filename) {
        FileInputStream fileInputStream = null;
        String cimRdfEncodedFile = null;
        try {
            fileInputStream = new FileInputStream(filename);
            byte[] bytes = new byte[(int)fileInputStream.available()];
            fileInputStream.read(bytes);
            cimRdfEncodedFile = Base64.getEncoder().encodeToString(bytes);
            fileInputStream.close();
        }
        catch (Exception e) {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                }
                catch (Exception e2) {
                    m_errorMsg = "Encoding failure -- " + e2.getMessage();
                }
            }
        }
        return cimRdfEncodedFile;
    }
    
    // Web Service call to create scenario
    private void createScenario() {
        String cimrdfEncoded = encodeCimrdf(m_cimRdfFile);
        // Return if valid file was NOT selected
        if (cimrdfEncoded== null) {
            return;
        }
        
        // Call Rest service to create scenario
        JSONObject json = new JSONObject();
        json.put("name", m_name);
        json.put("topology", cimrdfEncoded);
        json.put("description", m_desc);
        
        ScenarioInformation scenInfo;
        try {
           
            URL url = new URL("https://" + m_webServiceHost + "/scenarios?subscription-key="+m_webServiceKey);
            
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            byte[] input = json.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP Error code : "+ conn.getResponseCode());
            }       
                        
            InputStreamReader isr = new InputStreamReader(conn.getInputStream(), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            ObjectMapper objMapper = new ObjectMapper();
            
            String response = br.readLine();
            m_scenarioInfo = objMapper.readValue(response, ScenarioInformation.class);
                
            conn.disconnect();
        }
        catch (Exception e2) {
            m_errorMsg = "Web Service failure -- " + e2.getMessage();;
        }
    }
    
    // set params to save the scenario file
    public void setSaveFileParams(String scenarioId, String filename) {
        m_scenarioId = scenarioId;
        m_filename = filename;
    }
    
    // Name the file scenarioInformation.getName()
    // Write the file to the selected path
    private String writeScenarioFile(ScenarioDetails scenarioDetails) {
        String path = m_filename;
        String scenarioFile = path + File.separator + 
            scenarioDetails.getScenarioInformation().getName() + ".xml";
        try {
            
            PrintWriter writer = new PrintWriter(scenarioFile, "UTF-8");
            writer.println(scenarioDetails.getContent());
            writer.close();
        }
        catch (Exception e) {
            m_errorMsg = "Exception: "+e.getMessage();
            return null;
        }
        return scenarioFile;
    }
    
    // Retrieve scenario and save file to local machine
    public void saveFile() {
        String output;
        String fileName = null;
        ScenarioDetails scenarioDetails = null;
        try {
            URL url = new URL("https://" + m_webServiceHost + "/scenarios/" +
                    m_scenarioId + "?subscription-key=" + 
                    m_webServiceKey);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) {
                m_errorMsg = "Web Service exception -- HTTP Error code : " 
                    + conn.getResponseCode();
                return;
            }
            
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            BufferedReader br = new BufferedReader(in);
            ObjectMapper objMapper = new ObjectMapper();
            while ((output = br.readLine()) != null) {
                scenarioDetails = objMapper.readValue(output, ScenarioDetails.class);
            }
            if (scenarioDetails != null) {
                fileName = writeScenarioFile(scenarioDetails);
            }
            conn.disconnect();
        }
        catch (Exception e2) {
            m_errorMsg =  "Exception: " + e2.getMessage();
        }
    }
    
    // set params to update scenario with gucs
    public void setGucsUpdateParams(int[] selectedIndices, String scenarioId, 
            List<GucsInformation>gucsList) {
        m_gucsSelectedIndices = selectedIndices;
        m_scenarioId = scenarioId;
        m_gucsList = gucsList;
    }
    
    // set params to update scenario with cnrm
    public void setCnrmUpdateParams(int[] selectedIndices, String scenarioId,
            List<CnrmInformation>cnrmList) {
        m_cnrmSelectedIndices = selectedIndices;
        m_scenarioId = scenarioId;
        m_cnrmList = cnrmList;
    }
    
    // Call the web service to apply the GUCS list to the scenario 
    private void updateGucs() {
        JSONArray jsonArray = new JSONArray();
        GucsInformation selectedGucs;
        for (int i : m_gucsSelectedIndices) {
            selectedGucs = m_gucsList.get(i);
            jsonArray.put(selectedGucs.getId());
        }       
        
        try {
            String urlString = "https://" + m_webServiceHost + 
                    "/scenarios/" + m_scenarioId + "/gucs?subscription-key=" +
                    m_webServiceKey; 
            URL url = new URL(urlString);
            
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            
            OutputStream os = conn.getOutputStream();
            byte[] input = jsonArray.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
            if (conn.getResponseCode() != 200) {
                m_errorMsg = "Web Service exception -- HTTP Error code : " + 
                        conn.getResponseCode();
                return;                
            }       
                        
            InputStreamReader isr = new InputStreamReader(conn.getInputStream(), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            ObjectMapper objMapper = new ObjectMapper();
            
            String response = br.readLine();
            if (conn.getResponseCode() != 200) { 
                ProblemDetails problemDetails = objMapper.readValue(response, ProblemDetails.class);
                m_errorMsg = problemDetails.toString();
            }
                
            conn.disconnect();
        }
        catch (Exception e2) {
            m_errorMsg = "Web Service exception -- HTTP Error code : " +
                e2.getMessage();
        }
    }
    
    // Call the web service to update the scenario with the CNRM list
    private void updateCnrm() {
        JSONArray jsonArray = new JSONArray();
        CnrmInformation selectedCnrm;
        for (int i : m_cnrmSelectedIndices) {
            selectedCnrm = m_cnrmList.get(i);
            jsonArray.put(selectedCnrm.getId());
        }       
        
        try {
            String urlString = "https://" + m_webServiceHost + 
                    "/scenarios/" + m_scenarioId + "/cnrm?subscription-key=" + 
                    m_webServiceKey;
            URL url = new URL(urlString);
            
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            
            OutputStream os = conn.getOutputStream();
            byte[] input = jsonArray.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
            if (conn.getResponseCode() != 200) {
                m_errorMsg = "Web Service exception -- HTTP Error code : " + 
                    conn.getResponseCode();
                return;
            }       
                        
            InputStreamReader isr = new InputStreamReader(conn.getInputStream(), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            ObjectMapper objMapper = new ObjectMapper();
            
            String response = br.readLine();
            if (conn.getResponseCode() != 200) { 
                ProblemDetails problemDetails = objMapper.readValue(response, ProblemDetails.class);
                m_errorMsg = problemDetails.toString();
            }
                
            conn.disconnect();
        }
        catch (Exception e2) {
            m_errorMsg = "Web Service exception -- HTTP Error code : "+ e2.getMessage();
        }
    }
    
    // get the flag that indicates validity of connection
    public boolean getConnectionValid() {
        return m_connectionValid;
    }
    
    public String getErrorMessage() {
        return m_errorMsg;
    }
    
    // Call the web service to retrieve the list of GUCS
    private void gucsListFromSvc() {
        String output;
        m_errorMsg = null;
             
        try {
            URL url = new URL("https://" + m_webServiceHost + 
                    "/gucs?subscription-key=" + m_webServiceKey);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) {
                m_errorMsg = "Web Service exception -- HTTP Error code : "+ conn.getResponseCode();
                return;
            }
            
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            BufferedReader br = new BufferedReader(in);
            ObjectMapper objMapper = new ObjectMapper();
            TypeFactory typeFactory = objMapper.getTypeFactory();
            CollectionType collectionType = typeFactory.constructCollectionType(List.class, GucsInformation.class);
            while ((output = br.readLine()) != null) {
                m_gucsList = objMapper.readValue(output, collectionType);
            }
            conn.disconnect();
            // at this point need to parse the results and add to the combo
        }
        catch (Exception e2) {
            m_errorMsg = "Web Service failure -- " + e2.getMessage();
        }
    }
    
    // Return the list of GUCS retrieved from web service
    public List<GucsInformation> getGucsList() {

        if (m_errorMsg != null) {
            return null;
        }
        return m_gucsList;
    }
    
    // Return the list of CNRM retrieved from web service
    public List<CnrmInformation> getCnrmList() {

        if (m_errorMsg != null) {
            return null;
        }
        return m_cnrmList;
    }
    
    // Return the Scenario retrieved from web service
    public ScenarioInformation getScenario() {
        return m_scenarioInfo;
    }
    
    // Call the web service to retrieve the list of CNRM
    private void cnrmListFromSvc() {
        String output;
        
        try {
            URL url = new URL("https://" + m_webServiceHost + 
                    "/cnrm?subscription-key=" + m_webServiceKey);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) {
                m_errorMsg = "Web Service exception -- HTTP Error code : "+ conn.getResponseCode();
                return;
            }
            
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            BufferedReader br = new BufferedReader(in);
            ObjectMapper objMapper = new ObjectMapper();
            TypeFactory typeFactory = objMapper.getTypeFactory();
            CollectionType collectionType = typeFactory.constructCollectionType(List.class, CnrmInformation.class);
            while ((output = br.readLine()) != null) {
                m_cnrmList = objMapper.readValue(output, collectionType);
            }
            conn.disconnect();
            // at this point need to parse the results and add to the combo
        }
        catch (Exception e2) {
            m_errorMsg = "Web Service failure -- " + e2.getMessage();
        }
    }  // cnrmListFromSvc
}  // WizardCommandThread
