package gov.inl.igcapt.components.DataModels;

import gov.inl.igcapt.components.SgLayeredIcon;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import jakarta.persistence.*;

import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

@Entity(name = "component")
public class SgComponentData implements BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    protected String name;

    @Column(nullable = false)
    protected String iconPath;

    @Column
    protected String description;

    @Column(nullable = false, unique = true)
    protected String uuid;

    @Column
    protected boolean passthrough = true;

    @Column
    protected Boolean aggregate;

    @Column
    private Long componentGroupId;
    
    @Column(unique = true)
    private String formalIdentifier;
    
    @Column
    private String identifierType;

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinColumn(name = "componentId")
    private List<SgCollapseInto> sgCollapseIntos;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinTable(
            name="component_usecase",
            joinColumns = { @JoinColumn(name="componentId")},
            inverseJoinColumns = { @JoinColumn(name="usecaseId")}
    )
    private List<SgUseCase> usecases;

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinColumn(name = "componentId")
    private List<SgField> fields;
    
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinColumn(name = "componentId")
    private List<SgAttribute> attributes;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "componentDataId")
    private List<SgEndPoint> endpoints;

    public Long getId() {
        return id;
    }

    public String getDescription() { return description != null? description: ""; }
    public void setDescription(String description) { this.description = description; }

    public List<SgField> getFields() { return fields != null? fields: new ArrayList<>(); }
    public void setFields(List<SgField> fields) {
        this.fields = fields;
    }
    public void addField(SgField field) {
        fields = getFields();
        // Removes only if its already there to allow for updates
        Iterator<SgField> it = fields.iterator();
        while(it.hasNext()){
            if(it.getClass().getName().equals(field.getName())){
                it.remove();
            }
            it.next();
        }
        fields = getFields();
        fields.add(field);
    }
    
    public List<SgAttribute> getAttributes() { return attributes != null? attributes: new ArrayList<>(); }
    public void setAttributes(List<SgAttribute> attributes) {
        this.attributes = attributes;
    }
    public void addAttribute(SgAttribute attribute) {
        attributes = getAttributes();
        // Removes only if its already there to allow for updates
        Iterator<SgAttribute> it = attributes.iterator();
        while(it.hasNext()){
            if(it.getClass().getName().equals(attribute.getName())){
                it.remove();
            }
            it.next();
        }
        attributes = getAttributes();
        attributes.add(attribute);
    }

    public List<SgUseCase> getUsecases() { return usecases != null? usecases: new ArrayList<>(); }
    public void setUsecases(List<SgUseCase> usecases) { this.usecases = usecases; }
    public void addUsecase(SgUseCase usecase) {
        boolean found = false;
        usecases = getUsecases();
        if(usecases.contains(usecase)) { found = true; }
        for(var uc : usecases){
            if (uc.getName().equals(usecase.getName())) {
                found = true;
                break;
            }
        }
        if(found){
            return;
        }
        usecases.add(usecase);
        usecase.addComponent(this);
    }

    /**
     * this method updates a use_case by searching the SgComponent use_case list for
     * the use_case by name... if found it deletes what was found and adds what was passed in as
     * a parameter
     * @param usecase - the SgComponent Use Case to be updated.
     */
    public void updateUseCase(SgUseCase usecase){
        usecases = getUsecases();
        if(usecases != null) {
            for (var uc : usecases) {
                if (uc.getName().equals(usecase.getName())) {
                    removeUsecase(uc);
                    break;
                }
            }
            addUsecase(usecase);
        }
        else{
            setUsecases(new ArrayList<>());
            addUsecase(usecase);
        }
    }

    public void removeUsecase(SgUseCase usecase) {
        usecases.remove(usecase);
        if (usecase.getComponents().contains(this)) {
            usecase.removeComponent(this);
        }
    }

    public String getIconPath() { return iconPath; }
    public void setIconPath(String value) { this.iconPath = value; }
    
    @Transient
    private Icon icon = null;
    public Icon getIcon() {
        
        if (icon == null) {
            if (iconPath != null && !iconPath.isEmpty()) {
                BufferedImage img = null;
                try {
                    File iconFile = new File(iconPath);
                    img = ImageIO.read(new FileInputStream(iconFile));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(img != null) {
                    ImageIcon newicon = new ImageIcon(img);
                    icon = new SgLayeredIcon(newicon.getImage()); // LayeredIcon used for checking an icon
                }
            }
        }
        
        return icon;
    }

    public boolean isPassthrough() { return passthrough; }
    public void setPassthrough(boolean value) { this.passthrough = value; }

    public List<SgCollapseInto> getSgCollapseIntos() {
        return sgCollapseIntos;
    }
    
    public List<String> getSgCollapseIntoUuids() {
        List<String> returnval = new ArrayList<>();
        List<SgCollapseInto> collapseIntoList = getSgCollapseIntos();
        if (collapseIntoList != null) {
            for (SgCollapseInto collapseInto:collapseIntoList) {
                returnval.add(collapseInto.getGuid());
            }
        }
        
        return returnval;
    }

    public void setSgCollapseIntos(List<SgCollapseInto> value) {
        this.sgCollapseIntos = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getUuid() {
        if(uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
        return uuid;
    }

    public void setUuid(String value) {
        this.uuid = value;
    }

    public boolean isAggregate() {
        return aggregate;
    }
    public void setAggregate(Boolean value) {
        this.aggregate = value;
    }

    public void setComponentGroupId(Long componentGroupId) {
        this.componentGroupId = componentGroupId;
    }

    public Long getComponentGroupId(){
        return this.componentGroupId;
    }
    
    public String getFormalIdentifier() {
        return formalIdentifier;
    }
    
    public void setFormalIdentifier(String value) {
        formalIdentifier = value;
    }

   public String getIdentifierType() {
        return identifierType;
    }
    
    public void setIdentifierType(String value) {
        identifierType = value;
    }

    public List<SgEndPoint> getEndpoints() { return endpoints; }
    public void setEndpoints(List<SgEndPoint> endpoints) { this.endpoints = endpoints; }

    @Override
    public String toString() {
        return name;
    }
}