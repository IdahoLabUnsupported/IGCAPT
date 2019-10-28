package gov.inl.igcapt.components.DataModels;

import gov.inl.igcapt.components.SgLayeredIcon;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

@Entity(name = "component")
public class SgComponentData implements BaseModel {

    @Id
    @GeneratedValue
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

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    @JoinColumn(name = "componentId")
    private List<SgCollapseInto> sgCollapseIntos;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    @JoinTable(
            name="component_usecase",
            joinColumns = { @JoinColumn(name="componentId")},
            inverseJoinColumns = { @JoinColumn(name="usecaseId")}
    )
    private List<SgUseCase> usecases;

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    @JoinColumn(name = "componentId")
    private List<SgField> fields;

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
        fields.remove(field); // Removes only if its already there to allow for updates
        fields.add(field);
    }

    public List<SgUseCase> getUsecases() { return usecases != null? usecases: new ArrayList<>(); }
    public void setUsecases(List<SgUseCase> usecases) { this.usecases = usecases; }
    public void addUsecase(SgUseCase usecase) {
        usecases = getUsecases();
        if(usecases.contains(usecase)) { return; }

        usecases.add(usecase);
        usecase.addComponent(this);
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

                ImageIcon newicon = new ImageIcon(img);
                icon = new SgLayeredIcon(newicon.getImage()); // LayeredIcon used for checking an icon
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
        if (aggregate == null) {
            return false;
        } else {
            return aggregate;
        }
    }
    public void setAggregate(Boolean value) {
        this.aggregate = value;
    }

    public void setComponentGroupId(Long componentGroupId) {
        this.componentGroupId = componentGroupId;
    }

    @Override
    public String toString() {
        return name;
    }
}