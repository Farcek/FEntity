/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mn.le.farcek.common.entity.ejb;

import java.io.Serializable;
import java.util.List;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.Transient;
import javax.persistence.TypedQuery;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Type;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.Transactional;
import javax.transaction.UserTransaction;

import mn.le.farcek.common.entity.FEntity;
import mn.le.farcek.common.entity.criteria.FilterItem;
import mn.le.farcek.common.entity.criteria.OrderByItem;
import mn.le.farcek.common.utils.FCollectionUtils;
//import org.eclipse.persistence.sessions.Session;

/**
 *
 * @author Farcek
 */
public abstract class FEntityService {//implements FEntityServiceInterface {

    protected boolean debug = false;

    public Mode getMode() {
        return Mode.JTA;
    }

    public static enum Mode {

        JTA, RESOURCE_LOCAL;
    }

    public void setDebugMode(boolean flag) {
        debug = flag;
    }

    public abstract EntityManager getEntityManager();

    public abstract UserTransaction getUserTransaction();

    public boolean transactionBegin() throws Exception {
        if (getMode() == Mode.JTA) {
            UserTransaction utx = getUserTransaction();
            if (utx != null) {
                if (Status.STATUS_NO_TRANSACTION == utx.getStatus()) {
                    utx.begin();
                    return true;
                }
            }
        } else {
            EntityTransaction t = getEntityManager().getTransaction();
            if (t != null) {
                if (t.isActive() == false) {
                    t.begin();
                    return true;
                }
            }
        }
        return false;
    }

    public void transactionCommit() throws SystemException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
        if (getMode() == Mode.JTA) {
            UserTransaction utx = getUserTransaction();
            if (utx != null) {
                if (Status.STATUS_ACTIVE == utx.getStatus()) {
                    utx.commit();
                }
            }
        } else {
            EntityTransaction t = getEntityManager().getTransaction();
            if (t != null) {
                if (t.isActive()) {
                    t.commit();
                }
            }
        }
    }

    public void transactionRollbak() throws SystemException {
        if (getMode() == Mode.JTA) {
            UserTransaction utx = getUserTransaction();
            if (utx != null) {
                if (Status.STATUS_ACTIVE == utx.getStatus()) {
                    utx.rollback();
                }
            }
        } else {
            EntityTransaction t = getEntityManager().getTransaction();
            if (t != null) {
                if (t.isActive()) {
                    t.rollback();
                }
            }
        }
    }

    
    public <T extends FEntity> void doCreate(Class<T> entityClass, T enity) throws Exception {
        if (entityClass == null || enity == null) {
            throw new NullPointerException();
        }
        transactionBegin();
        try {
            getEntityManager().persist(enity);
            transactionCommit();

        } catch (RuntimeException ex) {
            transactionRollbak();
            throw ex;
        }
    }

    public <T extends FEntity> void doUpdate(Class<T> entityClass, T enity) throws Exception {
        if (entityClass == null || enity == null) {
            throw new NullPointerException();
        }
        Serializable pk = enity.getId();
        if (pk == null) {
            throw new RuntimeException("can not update. id = `null`");
        }
        FEntity model = getEntityManager().find(entityClass, pk);
        if (model == null) {
            throw new RuntimeException("can not update. not exists entity. entity=" + enity);
        }
        
        
        transactionBegin();
        try {
            
            getEntityManager().merge(enity);
            transactionCommit();
        } catch (RuntimeException ex) {
            transactionRollbak();
            throw ex;
        }
    }

    public <T extends FEntity> void doSave(Class<T> entityClass, T entity) throws Exception {
        if (entityClass == null || entity == null) {
            throw new NullPointerException();
        }
        Serializable pk = entity.getId();

        if (pk == null) {
            doCreate(entityClass, entity);
            return;
        }

        FEntity model = getEntityManager().find(entityClass, pk);
        if (model == null) {
            doCreate(entityClass, entity);
            return;
        }

        UserTransaction utx = getUserTransaction();
        if (utx != null) {
            utx.begin();
        }
        try {
            getEntityManager().merge(entity);
            if (utx != null) {
                utx.commit();
            }
        } catch (RuntimeException ex) {
            if (utx != null) {
                utx.rollback();
            }
            throw ex;
        }
    }

    public <T extends FEntity> void doDelete(Class<T> entityClass, T enity) throws Exception {
        if (entityClass == null || enity == null) {
            throw new NullPointerException();
        }
        Serializable pk = enity.getId();
        if (pk == null) {
            throw new RuntimeException("can not delete. id = `null`");
        }
        FEntity model = getEntityManager().find(entityClass, pk);
        if (model == null) {
            throw new RuntimeException("can not delete. not exists entity. entity=" + enity);
        }
        UserTransaction utx = getUserTransaction();
        if (utx != null) {
            utx.begin();
        }
        try {
            getEntityManager().remove(getEntityManager().merge(model));
            if (utx != null) {
                utx.commit();
            }
        } catch (RuntimeException ex) {
            if (utx != null) {
                utx.rollback();
            }
            throw ex;
        }
    }

    public <T extends FEntity> String getEntityName(Class<T> entityClass) {
        EntityType<T> meta = getEntityManager().getMetamodel().entity(entityClass);
        if (meta == null) {
            throw new RuntimeException("not registered entityClass=" + entityClass);
        }

        return meta.getName();
    }

    //-- entityBy
    public <T extends FEntity> T entityById(Class<T> entityClass, Serializable id) {
        return getEntityManager().find(entityClass, id);
    }

    public <T extends FEntity> T entityBy(Class<T> entityClass, FilterItem... filters) {
        return entityBy(entityClass, filters, null);
    }

    public <T extends FEntity> T entityBy(Class<T> entityClass, FilterItem[] filters, OrderByItem[] orders) {
        StringBuilder cr = new StringBuilder("SELECT o FROM ").append(getEntityName(entityClass)).append(" o");
        generateFilter(cr, filters);

        if (FCollectionUtils.notEmpty(orders)) {
            cr.append(" ORDER BY ");

            int i = 0;
            for (OrderByItem it : orders) {
                if (i > 0) {
                    cr.append(", ");
                }
                cr.append("o.").append(it.getFieldName()).append(" ").append(it.isAscending() ? "ASC" : "DESC");
            }
        }

        if (debug) {
            System.out.println(String.format("findByAll >> jpql = %s;", cr));
        }

        TypedQuery<T> query = getEntityManager().createQuery(cr.toString(), entityClass);
        pushFilterParam(query, filters);

        query.setMaxResults(1);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public <T extends FEntity> T entityByQuery(Class<T> entityClass, String jpql, FParamItem... params) {
        if (debug) {
            System.out.println("entityByQuery >> " + jpql);
            printParam(params);
        }
        TypedQuery<T> query = getEntityManager().createQuery(jpql, entityClass);
        if (FCollectionUtils.notEmpty(params)) {
            for (FParamItem p : params) {
                p.pushParam(query);
            }
        }

        query = query.setMaxResults(1);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public <T extends FEntity> T entityByNamedQuery(Class<T> entityClass, String queryName, FParamItem... params) {
        TypedQuery<T> query = getEntityManager().createNamedQuery(queryName, entityClass);

        if (FCollectionUtils.notEmpty(params)) {
            for (FParamItem entry : params) {
                entry.pushParam(query);
            }
        }

        query = query.setMaxResults(1);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List entitysBy(String jpql) {
        Query q = getEntityManager().createQuery(jpql);
        return q.getResultList();
    }

    public List entitysBy(String jpql, int limit, int offset) {
        Query q = getEntityManager().createQuery(jpql);
        q.setMaxResults(limit);
        q.setFirstResult(offset);
        return q.getResultList();
    }

    public <T extends FEntity> List<T> entitysBy(Class<T> entityClass, FilterItem... filters) {
        return entitysBy(entityClass, filters, null, 0, 0);
    }

    public <T extends FEntity> List<T> entitysBy(Class<T> entityClass, FilterItem[] filters, int maxResult, int firstResult) {
        return entitysBy(entityClass, filters, null, maxResult, firstResult);
    }

    public <T extends FEntity> List<T> entitysBy(Class<T> entityClass, FilterItem[] filters, OrderByItem[] orders) {
        return entitysBy(entityClass, filters, orders, 0, 0);
    }

    public <T extends FEntity> List<T> entitysBy(Class<T> entityClass, FilterItem[] filters, OrderByItem[] orders, int maxResult, int firstResult) {
        StringBuilder cr = new StringBuilder("SELECT o FROM ").append(getEntityName(entityClass)).append(" o");
        generateFilter(cr, filters);

        if (FCollectionUtils.notEmpty(orders)) {
            cr.append(" ORDER BY ");

            int i = 0;
            for (OrderByItem it : orders) {
                if (i > 0) {
                    cr.append(", ");
                }
                cr.append("o.").append(it.getFieldName()).append(" ").append(it.isAscending() ? "ASC" : "DESC");
            }
        }

        if (debug) {
            System.out.println(String.format("entitysBy >> jpql = %s; max=%d; first=%d", cr, maxResult, firstResult));
        }

        TypedQuery<T> q = getEntityManager().createQuery(cr.toString(), entityClass);
        pushFilterParam(q, filters);

        if (maxResult > 0) {
            q.setMaxResults(maxResult);
        }

        if (firstResult > 0) {
            q.setFirstResult(firstResult);
        }

        return q.getResultList();
    }

    public <T extends FEntity> List<T> entitysByQuery(Class<T> entityClass, String jpql, FParamItem... params) {
        return entitysByQuery(entityClass, jpql, params, 0, 0);
    }

    public <T extends FEntity> List<T> entitysByQuery(Class<T> entityClass, String jpql, FParamItem[] params, int maxResult, int firstResult) {
        if (debug) {
            System.out.println("entitysByQuery >> " + jpql);
            printParam(params);
        }
        TypedQuery<T> query = getEntityManager().createQuery(jpql, entityClass);
        if (FCollectionUtils.notEmpty(params)) {
            for (FParamItem p : params) {
                p.pushParam(query);
            }
        }

        if (maxResult > 0) {
            query.setMaxResults(maxResult);
        }

        if (firstResult > 0) {
            query.setFirstResult(firstResult);
        }

        return query.getResultList();
    }

    public <T extends FEntity> List<T> entitysByNamedQuery(Class<T> entityClass, String queryName, FParamItem... params) {
        return entitysByNamedQuery(entityClass, queryName, params, 0, 0);
    }

    public <T extends FEntity> List<T> entitysByNamedQuery(Class<T> entityClass, String queryName, FParamItem[] params, int maxResult, int firstResult) {
        if (debug) {
            System.out.println("entitysByNamedQuery >> queryName=" + queryName);
            printParam(params);
        }
        TypedQuery<T> query = getEntityManager().createNamedQuery(queryName, entityClass);

        if (FCollectionUtils.notEmpty(params)) {
            for (FParamItem entry : params) {
                entry.pushParam(query);
            }
        }

        if (maxResult > 0) {
            query.setMaxResults(maxResult);
        }

        if (firstResult > 0) {
            query.setFirstResult(firstResult);
        }

        return query.getResultList();
    }

    // -- object 
    public <T> T byObject(Class<T> objectType, String jpql, FParamItem... params) {
        if (debug) {
            System.out.println("byObject >> " + jpql);
            printParam(params);
        }
        TypedQuery<T> query = getEntityManager().createQuery(jpql, objectType);
        if (FCollectionUtils.notEmpty(params)) {
            for (FParamItem p : params) {
                p.pushParam(query);
            }
        }

        query = query.setMaxResults(1);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public <T> List<T> byObjectList(Class<T> objectType, String jpql, FParamItem... params) {
        return byObjectList(objectType, jpql, params, 0, 0);
    }

    public <T> List<T> byObjectList(Class<T> objectType, String jpql, FParamItem[] params, int maxResult, int firstResult) {
        if (debug) {
            System.out.println("byObjectList >> " + jpql);
            printParam(params);
        }
        TypedQuery<T> query = getEntityManager().createQuery(jpql, objectType);
        if (FCollectionUtils.notEmpty(params)) {
            for (FParamItem p : params) {
                p.pushParam(query);
            }
        }

        if (maxResult > 0) {
            query.setMaxResults(maxResult);
        }

        if (firstResult > 0) {
            query.setFirstResult(firstResult);
        }

        return query.getResultList();
    }

    public Class<?> getIdType(Class<? extends FEntity> entityClass) {
        EntityType<?> meta = getEntityManager().getMetamodel().entity(entityClass);
        Type<?> t = meta.getIdType();
        return t.getJavaType();
    }

    public Number NextId(Class<? extends FEntity> entityClass) {
        return 0;
        //return getEntityManager().unwrap(Session.class).getNextSequenceNumberValue(entityClass);
    }

    // -- entra
    private void printParam(FParamItem[] params) {
        if (debug) {
            if (params != null && params.length > 0) {
                int i = 1;
                for (FParamItem p : params) {
                    System.out.println(String.format("[%d] %s", i++, p));
                }
            }
        }
    }

    private void generateFilter(StringBuilder cr, FilterItem[] filters) {
        if (FCollectionUtils.notEmpty(filters)) {
            cr.append(" WHERE ");
            FilterItem.Indexer i = new FilterItem.Indexer(1);
            for (FilterItem it : filters) {
                if (i.getIndex() > 1) {
                    cr.append(" AND ");
                }
                cr.append(it.genereteCriteria("o", i));
            }
        }
    }

    private void pushFilterParam(Query q, FilterItem[] filters) {
        if (FCollectionUtils.notEmpty(filters)) {
            int i = 1;
            for (FilterItem it : filters) {
                if (debug) {
                    System.out.println("[" + (i++) + "] " + it);
                }
                it.pushParam(q);
            }
        }
    }

}
