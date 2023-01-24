/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gov.inl.igcapt.wizard;

/**
 *
 * @author CHE
 */
public class ProblemDetails {
    private String m_type;
    private String m_title;
    private int m_status;
    private String m_detail;
    private String m_instance;
    
    public String getType() {
        return m_type;
    }
    
    public void setType(String type) {
        m_type = type;
    }
    
    public String getTitle() {
        return m_title;
    }
    
    public void setTitle(String title) {
        m_title = title;
    }
    
    public int getStatus() {
        return m_status;
    }
    
    public void setStatus(int status) {
        m_status = status;
    }
    
    public String getDetail() {
        return m_detail;
    }
    
    public void setDetail(String detail) {
        m_detail = detail;
    }
    
    public String getInstance() {
        return m_instance;
    }
    
    public void setInstance(String instance) {
        m_instance = instance;
    }
    
@Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("***Problem Details****\n");
        sb.append("Type=").append(getType()).append("\n");
        sb.append("Title=").append(getTitle()).append("\n");
        sb.append("Status=").append(getStatus()).append("\n");
        sb.append("Detail=").append(getDetail()).append("\n");
        sb.append("Instance=").append(getInstance()).append("\n");
        return sb.toString();
    }    
}
