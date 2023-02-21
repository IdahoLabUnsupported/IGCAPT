/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gov.inl.igcapt.wizard;

/**
 *
 * @author CHE
 */
public class RestSvcVersion {
    private String version;
    private String buildTime;
    
    
    public String getVersion() {
        return version; 
    }
    
    public void setId(String version) {
        this.version = version;
    }
    
    public String getBuildTime() {
        return buildTime;
    }
    
    public void setBuildTime(String buildTime) {
        this.buildTime = buildTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("***Web Service Version Information****\n");
        sb.append("Version=").append(getVersion()).append("\n");
        sb.append("Build Time==").append(getBuildTime()).append("\n");
        return sb.toString();
    }    
}
