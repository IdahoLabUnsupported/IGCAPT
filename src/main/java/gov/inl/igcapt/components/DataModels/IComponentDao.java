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

    List<SgComponentListData> getComponentLists();

    List<SgComponentGroupData> getComponentGroups();

    List<SgComponentData> getComponents();

    List<SgCollapseInto> getCollapseInto();

    List<SgUseCase> getUseCases();

    List<SgField> getFields();

    SgComponentData getComponentByUUID(String uuid);

    SgComponentData getComponentByUUID(UUID uuid);

    void delete(Object obj);
}
