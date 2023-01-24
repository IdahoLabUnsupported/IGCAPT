/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gov.inl.igcapt.wizard;

/**
 *
 * @author CHE
 */
public class GucsInformation {
    private String id;
    private String sourceId;
    private String name;
    private String description;
    
    public String getId() {
        return id; 
    }
    
    public void setId(String id) {
        this.id = id;
    }

    public String getSourceId() {
        return sourceId; 
    }
    
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getName() {
        return name; 
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description; 
    }
    
    public void setDescription(String desc) {
        this.description = desc;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("***GUCS Information****\n");
        sb.append("ID=").append(getId()).append("\n");
        sb.append("Source ID=").append(getSourceId()).append("\n");
        sb.append("Name=").append(getName()).append("\n");
        sb.append("Description=").append(getDescription()).append("\n");
        return sb.toString();
    }    
}
