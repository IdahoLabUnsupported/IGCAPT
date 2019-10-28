package gov.inl.igcapt.components.DataModels;

import javax.persistence.*;
@Entity(name = "collapseIntoData")
public class SgCollapseInto implements BaseModel {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String guid;

    @Column
    private Long componentId;

    @Override
    public Long getId() { return id; }

    public Long getComponentId() { return this.componentId; }

    public void setComponentId(Long componentId) { this.componentId = componentId; }

    public String getGuid() {
        return this.guid;
    }

    public void setGuid(String newGuid) { this.guid = newGuid; }
}