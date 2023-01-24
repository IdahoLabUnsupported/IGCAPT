/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gov.inl.igcapt.wizard;

/**
 *
 * @author CHE
 */
public class CnrmInformation {
    private String m_id;
    private String m_sourceId;
    private String m_name;
    private String m_description;
    
    public String getId() {
        return m_id; 
    }
    
    public void setId(String id) {
        m_id = id;
    }

    public String getSourceId() {
        return m_sourceId; 
    }
    
    public void setSourceId(String sourceId) {
        m_sourceId = sourceId;
    }

    public String getName() {
        return m_name; 
    }
    
    public void setName(String name) {
        m_name = name;
    }

    public String getDescription() {
        return m_description; 
    }
    
    public void setDescription(String desc) {
        m_description = desc;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("***CNRM Information****\n");
        sb.append("ID=").append(getId()).append("\n");
        sb.append("Source ID=").append(getSourceId()).append("\n");
        sb.append("Name=").append(getName()).append("\n");
        sb.append("Description=").append(getDescription()).append("\n");
        return sb.toString();
    }
}
