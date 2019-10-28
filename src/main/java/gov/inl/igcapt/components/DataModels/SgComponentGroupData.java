package gov.inl.igcapt.components.DataModels;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

@Entity(name = "componentGroup")
public class SgComponentGroupData implements BaseModel {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "componentGroupId")
    private List<SgComponentData> components;
    @Column(unique = true)
    private String groupName;
    @Column
    private Boolean display;
    @Column
    private Long componentListId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public void setComponents(List<SgComponentData> value) {components = value;}
    public List<SgComponentData> getComponents() { return components != null? components: new ArrayList<>(); }
    public void addComponent(SgComponentData component) {
        components = getComponents();

        components.add(component);
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String value) {
        this.groupName = value;
    }

    public boolean isDisplay() {
        if (display == null) {
            return true;
        } else {
            return display;
        }
    }

    public void setDisplay(Boolean value) {
        this.display = value;
    }

    public String toString() {
        return this.groupName;
    }
}
