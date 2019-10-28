package gov.inl.igcapt.components.DataModels;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ComponentDao implements IComponentDao {
    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY
            = Persistence.createEntityManagerFactory("IGCAPT");

    @Override
    public SgComponentListData getFirstComponentList() {
        List<SgComponentListData> componentGroups = getComponentLists();

        if(componentGroups.isEmpty()) {
            return new SgComponentListData();
        }

        return getComponentLists().get(0);
    }

    private void save(BaseModel obj) {
        if (obj == null) { return; }

        EntityManager manager = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction transaction;

        try {
            transaction = manager.getTransaction();
            transaction.begin();

            if (obj.getId() != null) {
                manager.merge(obj);
            } else {
                manager.persist(obj);
            }

            transaction.commit();
        } catch (Exception ex) {
            Logger.getLogger(ComponentDao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            manager.close();
        }
    }

    @Override
    public void saveComponentGroup(SgComponentGroupData group) { save(group); }
    @Override
    public void saveComponent(SgComponentData component) { save(component); }
    @Override
    public void saveCollapseInto(SgCollapseInto collapseInto) { save(collapseInto); }
    @Override
    public void saveUseCase(SgUseCase useCase) { save(useCase); }
    @Override
    public void saveField(SgField field) { save(field); }


    @Override
    public List<SgComponentListData> getComponentLists() { return getAll("componentList", SgComponentListData.class); }
    @Override
    public List<SgComponentGroupData> getComponentGroups() { return getAll("componentGroup", SgComponentGroupData.class); }
    @Override
    public List<SgComponentData> getComponents() { return getAll("component", SgComponentData.class); }
    @Override
    public List<SgCollapseInto> getCollapseInto() { return getAll("collapseIntoData", SgCollapseInto.class); }
    @Override
    public List<SgUseCase> getUseCases() { return getAll("usecase", SgUseCase.class); }
    @Override
    public List<SgField> getFields() { return getAll("field", SgField.class); }

    private <T> List<T> getAll(String tableName, Class cls) {
        EntityManager manager = ComponentDao.ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction transaction = null;

        List<T> groups = null;

        try {
            transaction = manager.getTransaction();
            transaction.begin();

            String query = String.format("SELECT c FROM %s c", tableName);

            groups = manager.createQuery(query, cls).getResultList();

            transaction.commit();
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }

            Logger.getLogger(ComponentDao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            manager.close();
        }

        return groups;
    }

    @Override
    public SgComponentData getComponentByUUID(String uuid) {

        SgComponentData returnval = null;

        List<SgComponentData> components = getComponents();

        for(SgComponentData component:components) {
            if (component.getUuid().equals(uuid)) {
                returnval = component;
                break;
            }
        }

        return returnval;
    }
    
    @Override
    public SgComponentData getComponentByUUID(UUID uuid) {
        return getComponentByUUID(uuid.toString());
    }

    @Override
    public void delete(Object obj) {
        EntityManager manager = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = manager.getTransaction();
            transaction.begin();

            manager.remove(manager.contains(obj) ? obj : manager.merge(obj));

            transaction.commit();
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }

            Logger.getLogger(ComponentDao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            manager.close();
        }
    }
}
