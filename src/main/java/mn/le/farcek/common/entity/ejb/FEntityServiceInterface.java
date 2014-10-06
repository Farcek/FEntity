/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mn.le.farcek.common.entity.ejb;

import java.io.Serializable;
import java.util.List;
import mn.le.farcek.common.entity.FEntity;
import mn.le.farcek.common.entity.criteria.FilterItem;
import mn.le.farcek.common.entity.criteria.OrderByItem;

/**
 *
 * @author Farcek
 */
interface FEntityServiceInterface {

    <T extends FEntity> Boolean doCreate(Class<T> entityClass, FEntity enity);

    <T extends FEntity> Boolean doUpdate(Class<T> entityClass, FEntity enity);

    <T extends FEntity> Boolean doSave(Class<T> entityClass, FEntity enity);

    <T extends FEntity> Boolean doDelete(Class<T> entityClass, FEntity enity);
    // -- entityBy
    <T extends FEntity> T entityById(Class<T> entityClass, Serializable id);

    <T extends FEntity> T entityBy(Class<T> entityClass, FilterItem... filters);

    <T extends FEntity> T entityBy(Class<T> entityClass, FilterItem[] filters, OrderByItem[] orders);

    // -- entityByQuery
    <T extends FEntity> T entityByQuery(Class<T> entityClass, String jpql, FParamItem... params);

    // -- entityByNamedQuery
    <T extends FEntity> T entityByNamedQuery(Class<T> entityClass, String queryName, FParamItem... params);

    // -- entitysBy
    <T extends FEntity> List<T> entitysBy(Class<T> entityClass, FilterItem... filters);

    <T extends FEntity> List<T> entitysBy(Class<T> entityClass, FilterItem[] filters, int maxResult, int firstResult);

    <T extends FEntity> List<T> entitysBy(Class<T> entityClass, FilterItem[] filters, OrderByItem[] orders);

    <T extends FEntity> List<T> entitysBy(Class<T> entityClass, FilterItem[] filters, OrderByItem[] orders, int maxResult, int firstResult);

    // -- entitysByQuery
    <T extends FEntity> List<T> entitysByQuery(Class<T> entityClass, String jpql, FParamItem... params);

    <T extends FEntity> List<T> entitysByQuery(Class<T> entityClass, String jpql, FParamItem[] params, int maxResult, int firstResult);

    //-- entitysByNamedQuery
    <T extends FEntity> List<T> entitysByNamedQuery(Class<T> entityClass, String queryName, FParamItem... params);

    <T extends FEntity> List<T> entitysByNamedQuery(Class<T> entityClass, String queryName, FParamItem[] params, int maxResult, int firstResult);

    //-- Object
    public <T> T byObject(Class<T> objectType, String jpql, FParamItem... params);

    public <T> List<T> byObjectList(Class<T> objectType, String jpql, FParamItem... params);

    public <T> List<T> byObjectList(Class<T> objectType, String jpql, FParamItem[] params, int maxResult, int firstResult);

}
