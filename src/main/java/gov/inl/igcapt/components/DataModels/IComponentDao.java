package gov.inl.igcapt.components.DataModels;

import java.util.List;
import java.util.UUID;

public interface IComponentDao {

    SgComponentListData getFirstComponentList();

    void saveComponentGroup(SgComponentGroupData group);

    void saveComponent(SgComponentData component);

    void saveCollapseInto(SgCollapseInto collapseInto);

    void saveUseCase(SgUseCase useCase);

    void saveField(SgField field);
    
    void saveAttribute(SgAttribute attribute);

    List<SgComponentListData> getComponentLists();

    List<SgComponentGroupData> getComponentGroups();

    List<SgComponentData> getComponents();

    List<SgCollapseInto> getCollapseInto();

    List<SgUseCase> getUseCases();

    SgUseCase getUseCaseByName(String name);

    List<SgField> getFields();
    
    List<SgAttribute> getAttributes();

    SgField getFieldByName(String name);
    
    SgAttribute getAttributeByName(String name);

    SgComponentData getComponentByUUID(String uuid);

    SgComponentData getComponentByUUID(UUID uuid);

    void delete(Object obj);
}
