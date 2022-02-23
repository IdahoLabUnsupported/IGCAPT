package gov.inl.igcapt.components.DataModels;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;


@Entity(name = "usecase")
public class SgUseCase implements BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    protected String name;

    @Column
    protected String description;

    @Column
    private int latency;

    @ManyToMany(mappedBy = "usecases", fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private List<SgComponentData> components;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(
            name="usecase_field",
            joinColumns = { @JoinColumn(name="usecaseId")},
            inverseJoinColumns = { @JoinColumn(name="fieldId")}
    )
    private List<SgField> fields;

    public Long getId() { return id; }

    public String getDescription() { return description != null? description: ""; }
    public void setDescription(String description) { this.description = description; }

    public List<SgComponentData> getComponents() { return components != null? components: new ArrayList<>(); }
    public void setComponents(List<SgComponentData> components) { this.components = components; }
    public void addComponent(SgComponentData component) {
        components = getComponents();
        if (components.contains(component)) { return; }

        components.add(component);
        component.addUsecase(this);
    }

    public void removeComponent(SgComponentData component) {
        components.remove(component);
        if (component.getUsecases().contains(this)) {
            component.removeUsecase(this);
        }

        for (SgField componentField : component.getFields()) {
            for(SgField usecaseField : getFields()) {
                if (componentField.getId().equals(usecaseField.getId())) {
                    removeField(usecaseField);
                    break;
                }
            }
        }
    }

    public List<SgField> getFields() { return fields != null? fields: new ArrayList<>(); }
    public void setFields(List<SgField> fields) { this.fields = fields; }
    public void addField(SgField field) {
        fields = getFields();

        boolean found = false;

        for(SgComponentData component: components){
            if(component.getFields().contains(field)) {
                found = true;
                break;
            }
        }

        if(!found) {
            ComponentDao dao = new ComponentDao();
            for(SgComponentData component: dao.getComponents()){
                if(component.getFields().contains(field)){
                    components.add(component);
                    break;
                }
            }
        }
        if(!found){
            System.out.println("Field not Found");
        }
        fields.add(field);
    }
    public void removeField(SgField fieldToRemove) {
        fields = getFields();
        fields.remove(fieldToRemove);
    }

    public int getLatency() { return latency; }
    public void setLatency(int value) {this.latency = value; }

    public String getName() { return name; }

    public void setName(String value) { this.name = value; }

    @Override
    public String toString() {
        return name;
    }
}
