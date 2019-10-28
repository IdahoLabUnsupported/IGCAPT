package gov.inl.igcapt.components.DataModels;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;


@Entity(name="componentList")
public class SgComponentListData implements BaseModel {
    @Id
    @GeneratedValue
    private Long id;
    @Column
    private Date date;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name="componentListId")
    private List<SgComponentGroupData> sgComponentGroupData;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Date getDate() {
        return date;
    }

    public void setDate(Date value) {
        this.date = value;
    }

    public void setSgComponentGroupData(List<SgComponentGroupData> value) {
        sgComponentGroupData = value;
    }
    public List<SgComponentGroupData> getSgComponentGroupData() {
        return this.sgComponentGroupData != null? sgComponentGroupData: new ArrayList<>();
    }
}
