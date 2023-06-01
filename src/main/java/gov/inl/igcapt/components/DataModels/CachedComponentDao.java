package gov.inl.igcapt.components.DataModels;

import java.util.*;

public class CachedComponentDao implements IComponentDao {
    private ComponentDao componentDao;
    private List<SgComponentListData> componentLists;
    private List<SgComponentGroupData> componentGroups;
    private List<SgComponentData> components;
    private List<SgCollapseInto> collapseIntos;
    private List<SgUseCase> useCases;
    private List<SgField> fields;

    private Map<Long, SgComponentGroupData> groupsMap;
    private Map<Long, SgComponentData> componentsMap;
    private Map<Long, SgField> fieldsMap;


    public CachedComponentDao() {
        componentDao = new ComponentDao();

    }

    @Override
    public SgComponentListData getFirstComponentList() {
        return getComponentLists().get(0);
    }

    @Override
    public void saveComponentGroup(SgComponentGroupData group) {
        componentDao.saveComponentGroup(group);
    }

    @Override
    public void saveComponent(SgComponentData component) {
        componentDao.saveComponent(component);
    }

    @Override
    public void saveCollapseInto(SgCollapseInto collapseInto) {
        componentDao.saveCollapseInto(collapseInto);
    }

    @Override
    public void saveUseCase(SgUseCase useCase) {
        componentDao.saveUseCase(useCase);
    }

    @Override
    public void saveField(SgField field) {
        componentDao.saveField(field);
    }

    @Override
    public SgUseCase getUseCaseByName(String name){
        for(var uc : getUseCases()){
            if(uc.getName().equals(name)){
                return uc;
            }
        }
        return null;
    }

    @Override
    public SgField getFieldByName(String name){
        for(var field: getFields()){
            if(field.getName().equals(name)){
                return field;
            }
        }
        return null;
    }

    public void stageComponentGroup(SgComponentGroupData componentGroup) {
        if(!componentGroups.contains(componentGroup)) {
            componentGroups.add(componentGroup);
        }
    }
    public void stageComponent(SgComponentData component) {
        if(!components.contains(component)) {
            components.add(component);
        }
    }
    public void stageCollapseInto(SgCollapseInto collapseInto) {
        if(!collapseIntos.contains(collapseInto)) {
            collapseIntos.add(collapseInto);
        }
    }

    public void stageUseCase(SgUseCase useCase) {
        if (!useCases.contains(useCase)) {
            useCases.add(useCase);
        }
    }

    public void stageField(SgField field) {
        if(!fields.contains(field)) {
            fields.add(field);
        }
    }

    @Override
    public List<SgComponentListData> getComponentLists() {
        if (componentLists == null) {
            componentLists = componentDao.getComponentLists();

            componentGroups = getComponentGroups();
            groupsMap = new HashMap<>();
            for (SgComponentGroupData group : componentGroups){
                groupsMap.put(group.getId(), group);
            }

            for(SgComponentListData list : componentLists) {
                List<SgComponentGroupData> listGroups = new ArrayList<>();
                for (SgComponentGroupData group : list.getSgComponentGroupData()) {
                    listGroups.add(groupsMap.get(group.getId()));
                }

                list.setSgComponentGroupData(listGroups);
            }
        }

        return componentLists;
    }

    @Override
    public List<SgComponentGroupData> getComponentGroups() {
        if (componentGroups == null) {
            componentGroups = componentDao.getComponentGroups();

            components = getComponents();
            componentsMap = new HashMap<>();
            for (SgComponentData component : components){
                componentsMap.put(component.getId(), component);
            }

            for(SgComponentGroupData group : componentGroups) {
                List<SgComponentData> groupComponents = new ArrayList<>();
                for (SgComponentData component : group.getComponents()) {
                    groupComponents.add(componentsMap.get(component.getId()));
                }

                group.setComponents(groupComponents);
            }
        }

        return componentGroups;
    }

    @Override
    public List<SgComponentData> getComponents() {
        if (components == null) {
            components = componentDao.getComponents();

            useCases = getUseCases();
            Map<Long, SgUseCase> useCaseMap = new HashMap<>();
            for (SgUseCase usecase : useCases) {
                useCaseMap.put(usecase.getId(), usecase);
            }

            fieldsMap = new HashMap<>();
            for (SgField field : fields){
                fieldsMap.put(field.getId(), field);
            }

            for (SgComponentData component : components) {
                List<SgUseCase> componentUseCases = new ArrayList<>();
                List<SgField> componentFields = new ArrayList<>();

                for (SgUseCase useCase : component.getUsecases()) {
                    componentUseCases.add(useCaseMap.get(useCase.getId()));
                }
                component.setUsecases(componentUseCases);

                for (SgField field : component.getFields()) {
                    componentFields.add(fieldsMap.get(field.getId()));
                }
                component.setFields(componentFields);
            }

            if (componentsMap == null) {
                componentsMap = new HashMap<>();
                for (SgComponentData component : components) {
                    componentsMap.put(component.getId(), component);
                }
            }

            for (SgUseCase useCase : useCases) {
                List<SgComponentData> useCaseComponents = new ArrayList<>();
                for (SgComponentData component : useCase.getComponents()) {
                    useCaseComponents.add(componentsMap.get(component.getId()));
                }
                useCase.setComponents(useCaseComponents);
            }
        }

        return components;
    }

    @Override
    public List<SgCollapseInto> getCollapseInto() {
        if (collapseIntos == null) {
            collapseIntos = componentDao.getCollapseInto();
        }

        return collapseIntos;
    }

    @Override
    public List<SgUseCase> getUseCases() {
        if (useCases == null) {
            useCases = componentDao.getUseCases();
            fields = getFields();

            fieldsMap = new HashMap<>();
            for (SgField field : fields){
                fieldsMap.put(field.getId(), field);
            }

            for (SgUseCase useCase : useCases) {
                List<SgField> useCaseFields = new ArrayList<>();
                for (SgField field : useCase.getFields()) {
                    useCaseFields.add(fieldsMap.get(field.getId()));
                }
                useCase.setFields(useCaseFields);
            }
        }

        return useCases;
    }

    @Override
    public List<SgField> getFields() {
        if (fields == null) {
            fields = componentDao.getFields();
        }

        return fields;
    }

    @Override
    public SgComponentData getComponentByUUID(String uuid) {
        for (SgComponentData component : getComponents()) {
            if (component.getUuid().equalsIgnoreCase(uuid)) {
                return component;
            }
        }

        return null;
    }

    @Override
    public SgComponentData getComponentByUUID(UUID uuid) {
        return getComponentByUUID(uuid.toString());
    }

    @Override
    public void delete(Object obj) {
        componentDao.delete(obj);
    }
}
