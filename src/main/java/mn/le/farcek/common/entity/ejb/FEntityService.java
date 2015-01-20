/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mn.le.farcek.common.entity.ejb;

import java.io.Serializable;
import java.sql.DatabaseMetaData;
import java.util.List;
import javax.ejb.EJBException;
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
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.Transactional;
import javax.transaction.UserTransaction;

import mn.le.farcek.common.entity.FEntity;
import mn.le.farcek.common.entity.criteria.FCriteriaBuilder;
import mn.le.farcek.common.entity.criteria.FilterItem;
import mn.le.farcek.common.entity.criteria.OrderByItem;
import mn.le.farcek.common.objects.FSortedObject;
import mn.le.farcek.common.utils.FCollectionUtils;
//import org.eclipse.persistence.sessions.Session;

/**
 *
 * @author Farcek
 */
public abstract class FEntityService {//implements FEntityServiceInterface {

    private boolean debug = false;

    public void setDebugMode(boolean flag) {
        debug = flag;
    }

    public boolean isDebug() {
        return debug;
    }

    public abstract EntityManager getEntityManager();

    public void ServiceRun(FServiceRunner serviceRunner) throws FServiceException {
        serviceRunner.run(this);
    }

    public <T extends FEntity> void doCreate(Class<T> entityClass, T enity) throws FServiceException {
        if (entityClass == null || enity == null)
            throw new NullPointerException();
        try {
            getEntityManager().persist(enity);
        } catch (Exception e) {
            throw new FServiceException(e);
        }

    }

    public <T extends FEntity> void doUpdate(Class<T> entityClass, T enity) throws FServiceException {
        if (entityClass == null || enity == null)
            throw new NullPointerException();
        Serializable pk = enity.getId();
        if (pk == null)
            throw new FServiceException("can not update. id = `null`");
        FEntity model = getEntityManager().find(entityClass, pk);
        if (model == null)
            throw new FServiceException("can not update. not exists entity. entity=" + enity);

        getEntityManager().merge(enity);
    }

    public <T extends FEntity> void doSave(Class<T> entityClass, T entity) throws FServiceException {
        if (entityClass == null || entity == null)
            throw new NullPointerException();
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

        getEntityManager().merge(entity);
    }

    public <T extends FEntity> void doDelete(Class<T> entityClass, T enity) throws FServiceException {
        if (entityClass == null || enity == null)
            throw new NullPointerException();
        Serializable pk = enity.getId();
        if (pk == null)
            throw new FServiceException("can not delete. id = `null`");
        FEntity model = getEntityManager().find(entityClass, pk);
        if (model == null)
            throw new FServiceException("can not delete. not exists entity. entity=" + enity);

        getEntityManager().remove(getEntityManager().merge(model));
    }

    public <T extends FEntity> String getEntityName(Class<T> entityClass) {
        EntityType<T> meta = getEntityManager().getMetamodel().entity(entityClass);
        if (meta == null)
            throw new RuntimeException("not registered entityClass=" + entityClass);

        return meta.getName();
    }

    public <T extends FEntity> Type<?> getPkType(Class<T> entityClass) {
        EntityType<T> meta = getEntityManager().getMetamodel().entity(entityClass);
        if (meta == null)
            throw new RuntimeException("not registered entityClass=" + entityClass);
        return meta.getIdType();
    }

    //-- entityBy
    public <T extends FEntity> T entityById(Class<T> entityClass, Serializable id) {
        if (id == null)
            return null;
        return getEntityManager().find(entityClass, id);
    }

    public <T extends FEntity> T entityBy(Class<T> entityClass, FilterItem... filters) {
        return entityBy(entityClass, filters, null);
    }

    public <T extends FEntity> T entityBy(Class<T> entityClass, FilterItem[] filters, OrderByItem[] orders) {
        StringBuilder cr = new StringBuilder("SELECT o FROM ").append(getEntityName(entityClass)).append(" o");
        generateFilter(cr, filters);

        if (FCollectionUtils.isEmpty(orders) && FSortedObject.class.isAssignableFrom(entityClass))
            orders = new OrderByItem[]{new OrderByItem("sortIndex")};

        if (FCollectionUtils.notEmpty(orders)) {
            cr.append(" ORDER BY ");

            int i = 0;
            for (OrderByItem it : orders) {
                if (i++ > 0)
                    cr.append(", ");
                cr.append("o.").append(it.getFieldName()).append(" ").append(it.getType().name());
            }
        }

        if (debug)
            System.out.println(String.format("findByAll >> jpql = %s;", cr));

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
        if (FCollectionUtils.notEmpty(params))
            for (FParamItem p : params)
                p.pushParam(query);

        query = query.setMaxResults(1);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public <T extends FEntity> T entityByNamedQuery(Class<T> entityClass, String queryName, FParamItem... params) {
        TypedQuery<T> query = getEntityManager().createNamedQuery(queryName, entityClass);

        if (FCollectionUtils.notEmpty(params))
            for (FParamItem entry : params)
                entry.pushParam(query);

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

    public <T extends FEntity> Long countBy(Class<T> entityClass, FilterItem... filters) {
        StringBuilder cr = new StringBuilder("SELECT count(o) FROM ").append(getEntityName(entityClass)).append(" o");
        generateFilter(cr, filters);

        TypedQuery<Long> q = getEntityManager().createQuery(cr.toString(), Long.class);
        pushFilterParam(q, filters);

        try {
            return q.getSingleResult();
        } catch (NoResultException e) {
            return 0l;
        }

    }

    public <T extends FEntity> FCriteriaBuilder<T> factoryCriteriaBuilder(Class<T> cls) {
        return new FCriteriaBuilder<>(cls);
    }

    public <T extends FEntity> FListResult<T> entitysBy(final FCriteriaBuilder<T> criteriaBuilder) {
        List<T> list = entitysBy(criteriaBuilder.getEntityClass(),
                criteriaBuilder.getFilters().toArray(new FilterItem[]{}),
                criteriaBuilder.getOrders().toArray(new OrderByItem[]{}),
                criteriaBuilder.getLimit(), criteriaBuilder.getOffset());

        FCountRunner countRunner = new FCountRunner() {

            @Override
            public long getCount() {
                return countBy(criteriaBuilder.getEntityClass(), criteriaBuilder.getFilters().toArray(new FilterItem[]{}));
            }
        };

        return new FListResult<>(list, countRunner);
    }

    public <T extends FEntity> List<T> entitysBy(Class<T> entityClass, FilterItem[] filters, OrderByItem[] orders, int maxResult, int firstResult) {
        StringBuilder cr = new StringBuilder("SELECT o FROM ").append(getEntityName(entityClass)).append(" o");
        generateFilter(cr, filters);

        if (FCollectionUtils.isEmpty(orders) && FSortedObject.class.isAssignableFrom(entityClass))
            orders = new OrderByItem[]{new OrderByItem("sortIndex")};

        if (FCollectionUtils.notEmpty(orders)) {
            cr.append(" ORDER BY ");

            int i = 0;
            for (OrderByItem it : orders) {
                if (i++ > 0)
                    cr.append(", ");
                cr.append("o.").append(it.getFieldName()).append(" ").append(it.getType().name());
            }
        }

        if (debug)
            System.out.println(String.format("entitysBy >> jpql = %s; max=%d; first=%d", cr, maxResult, firstResult));

        TypedQuery<T> q = getEntityManager().createQuery(cr.toString(), entityClass);
        pushFilterParam(q, filters);

        if (maxResult > 0)
            q.setMaxResults(maxResult);

        if (firstResult > 0)
            q.setFirstResult(firstResult);

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
        if (FCollectionUtils.notEmpty(params))
            for (FParamItem p : params)
                p.pushParam(query);

        if (maxResult > 0)
            query.setMaxResults(maxResult);

        if (firstResult > 0)
            query.setFirstResult(firstResult);

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

        if (FCollectionUtils.notEmpty(params))
            for (FParamItem entry : params)
                entry.pushParam(query);

        if (maxResult > 0)
            query.setMaxResults(maxResult);

        if (firstResult > 0)
            query.setFirstResult(firstResult);

        return query.getResultList();
    }

    // -- object 
    public <T> T byObject(Class<T> objectType, String jpql, FParamItem... params) {
        if (debug) {
            System.out.println("byObject >> " + jpql);
            printParam(params);
        }
        TypedQuery<T> query = getEntityManager().createQuery(jpql, objectType);
        if (FCollectionUtils.notEmpty(params))
            for (FParamItem p : params)
                p.pushParam(query);

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
        if (FCollectionUtils.notEmpty(params))
            for (FParamItem p : params)
                p.pushParam(query);

        if (maxResult > 0)
            query.setMaxResults(maxResult);

        if (firstResult > 0)
            query.setFirstResult(firstResult);

        return query.getResultList();
    }

    // --- executed 
    public int executeUpdateOrDelete(String jpql, FParamItem... params) {
        if (debug) {
            System.out.println("executeUpdateOrDelete >> " + jpql);
            printParam(params);
        }

        Query q = getEntityManager().createQuery(jpql);
        if (FCollectionUtils.notEmpty(params))
            for (FParamItem p : params)
                p.pushParam(q);
        return q.executeUpdate();
    }

    // --- cache
    public void cacheClearAll() {
        getEntityManager().getEntityManagerFactory().getCache().evictAll();
    }

    public void cacheClear(Class cls) {
        getEntityManager().getEntityManagerFactory().getCache().evict(cls);
    }

    public void cacheClear(Class cls, Object id) {
        getEntityManager().getEntityManagerFactory().getCache().evict(cls, id);
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

    // -- extra
    private void printParam(FParamItem[] params) {
        if (debug)
            if (params != null && params.length > 0) {
                int i = 1;
                for (FParamItem p : params)
                    System.out.println(String.format("[%d] %s", i++, p));
            }
    }

    private void generateFilter(StringBuilder cr, FilterItem[] filters) {
        if (FCollectionUtils.notEmpty(filters)) {
            cr.append(" WHERE ");
            FilterItem.Indexer i = new FilterItem.Indexer(1);
            for (FilterItem it : filters) {
                if (i.getIndex() > 1)
                    cr.append(" AND ");
                cr.append(it.genereteCriteria("o", i));
            }
        }
    }

    private void pushFilterParam(Query q, FilterItem[] filters) {
        if (FCollectionUtils.notEmpty(filters)) {
            int i = 1;
            for (FilterItem it : filters) {
                if (debug)
                    System.out.println("[" + (i++) + "] " + it);
                it.pushParam(q);
            }
        }
    }

}
