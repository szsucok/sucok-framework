/*
 * Copyright 2016 - 2017 suoke & Co., Ltd.
 */
package com.sucok.common.framework.data.mongo;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.sucok.common.framework.data.AbstractExpression;
import com.sucok.common.framework.form.BaseQueryForm;
import com.sucok.common.framework.form.PaginationForm;
import com.sucok.common.framework.result.PaginationResult;
import com.sucok.common.framework.result.QueryResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.support.MappingMongoEntityInformation;
import org.springframework.util.Assert;

import lombok.AllArgsConstructor;

/**
 * 
 * @author chendx
 *
 * @param <T>
 * @param <ID>
 */
public abstract class MongoGenericDao<T, ID extends Serializable> {

	private MongoOperations mongoOperations;

	private MappingMongoEntityInformation<T, ID> entityInfo;

	private Class<T> entityClass;

	private Class<ID> idClass;

	private static final ConversionService conversionService = new DefaultConversionService();

	@SuppressWarnings("unchecked")
	public MongoGenericDao() {
		Type type = getClass().getGenericSuperclass();
		Type[] params = ((ParameterizedType) type).getActualTypeArguments();
		entityClass = (Class<T>) params[0];
		idClass = (Class<ID>) params[1];
	}

	public static ConversionService getConversionService() {
		return conversionService;
	}

	@Autowired
	@SuppressWarnings("unchecked")
	public void setMongoOperations(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
		MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext = mongoOperations
				.getConverter().getMappingContext();
		MongoPersistentEntity<T> entity = (MongoPersistentEntity<T>) mappingContext.getPersistentEntity(entityClass);
		this.entityInfo = new MappingMongoEntityInformation<T, ID>((MongoPersistentEntity<T>) entity, idClass);
	}

	/**
	 * 根据id 查询 返回对象
	 * 
	 * @param id
	 * @return T
	 */
	public T findById(ID id) {
		Assert.notNull(id, "The given id must not be null!");
		return mongoOperations.findById(id, entityClass);
	}

	/**
	 * 根据id查询对象是否存在
	 * 
	 * @param id
	 * @return 如果存在返回 true 否则返回false
	 */
	public boolean exists(ID id) {
		Assert.notNull(id, "The given id must not be null!");
		Criteria criteria = Criteria.where(entityInfo.getIdAttribute()).is(id);
		return mongoOperations.exists(new Query(criteria), entityClass);
	}

	/**
	 * 通过Ids列表查询对象列表
	 * 
	 * @param ids
	 * @return 返回查询出的所有列表
	 */
	public List<T> findByIds(ID[] ids) {
		List<T> list = new ArrayList<>(ids.length);
		for (ID id : ids) {
			T item = findById(id);
			if (item != null) {
				list.add(item);
			}
		}
		return list;
	}

	/**
	 * 通过以 {@link separator}分隔符 分割出字符串 然后转化成我们需要的idClass类型，然后查询
	 * 
	 * @param ids
	 * @param separator
	 * @return
	 */
	public List<T> findByStrIds(String ids, String separator) {
		// 使用分隔符分割出字符串数组
		String[] strs = ids.trim().split(separator);
		List<T> list = new ArrayList<>(strs.length);
		for (String str : strs) {
			if (str.length() == 0) {
				continue;
			}
			ID id = conversionService.convert(str, idClass);
			T item = this.findById(id);
			if (item != null) {
				list.add(item);
			}
		}
		return list;
	}

	public List<T> findAll() {
		return mongoOperations.find(new Query(), entityClass);
	}

	public List<T> findByProperty(String name, Object value) {
		return this.findByProperty(name, value, null);
	}

	public List<T> findByProperty(String name, Object value, String orderBy) {
		Criteria criteria = Criteria.where(name).is(value);
		Query query = new Query(criteria);
		if (!StringUtils.isEmpty(orderBy)) {
			query.with(MongoQueryFormHelper.getSort(orderBy));
		}
		return mongoOperations.find(query, entityClass);
	}

	public List<T> findByMap(Map<Object, Object> map) {
		return this.findByMap(map, null);
	}

	public List<T> findByMap(Map<Object, Object> map, String orderBy) {
		return createQueryByMap(map, entityClass, orderBy).list();
	}

	public T findOneByProperty(String name, Object value) {
		Criteria criteria = Criteria.where(name).is(value);
		return mongoOperations.findOne(new Query(criteria), entityClass);
	}

	public T findOneByMap(Map<Object, Object> map, String orderBy) {
		return createQueryByMap(map, entityClass, orderBy).single();
	}

	public <S extends T> S save(S entity) {
		Assert.notNull(entity, "Entity must not be null!");
		if (entityInfo.isNew(entity)) {
			mongoOperations.insert(entity);
		} else {
			mongoOperations.save(entity);
		}
		return entity;
	}

	public void delete(T entity) {
		Assert.notNull(entity, "The given entity must not be null!");
		deleteById(entityInfo.getId(entity));
	}

	public boolean deleteById(ID id) {
		Assert.notNull(id, "The entity must not be null!");
		Criteria criteria = Criteria.where(entityInfo.getIdAttribute()).is(id);
		mongoOperations.remove(new Query(criteria), entityClass);
		return true;
	}

	public long countByProperty(String name, Object value) {
		Map<Object, Object> map = new HashMap<>();
		map.put(name, value);
		return countByMap(map);
	}

	public long countByMap(Map<Object, Object> map) {
		return createQueryByMap(map, Long.class, null).count();
	}

	public QueryResult<T> query(BaseQueryForm form) {
		String orderBy = form.getOrderBy();
		Query query = MongoQueryFormHelper.createQuery(form);
		if (form instanceof PaginationForm) {
			PaginationForm pagination = (PaginationForm) form;
			int currentPage = pagination.getCurrentPage();
			int pageSize = pagination.getPageSize();
			int offset = (currentPage - 1) * pageSize;
			query.skip(offset);
			query.limit(pageSize);
			List<T> list = mongoOperations.find(query, entityClass);
			long total = mongoOperations.count(query, entityClass);
			return new PaginationResult<T>(total, pageSize, currentPage, list, orderBy);
		} else {
			List<T> list = mongoOperations.find(query, entityClass);
			return new QueryResult<T>(list, orderBy);
		}
	}

	public long count(BaseQueryForm form) {
		Query query = MongoQueryFormHelper.createQuery(form);
		return mongoOperations.count(query, entityClass);
	}

	/**************************** 私有方法 *******************************/

	@SuppressWarnings("unchecked")
	private <R> QueryWraper<R> createQueryByMap(Map<Object, Object> map, Class<R> resultType, String orderBy) {
		Criteria criteriaLink = new Criteria();
		for (Entry<Object, Object> entry : map.entrySet()) {
			Object key = entry.getKey();
			Object value = entry.getValue();
			if (value instanceof AbstractExpression) {
				Criteria criteria = ((AbstractExpression) value).buildMongoCriteria(key);
				criteriaLink.andOperator(criteria);
			} else {
				String name = key.toString();
				criteriaLink.and(name).is(value);
			}
		}
		Query query = new Query(criteriaLink);
		if (!StringUtils.isEmpty(orderBy)) {
			query.with(MongoQueryFormHelper.getSort(orderBy));
		}
		return new QueryWraper<R>(query, (Class<R>) entityClass);
	}

	@AllArgsConstructor
	private class QueryWraper<R> {

		private Query query;

		private Class<R> resultClass;

		public long count() {
			String collectionName = entityInfo.getCollectionName();
			return mongoOperations.count(query, resultClass, collectionName);
		}

		public List<R> list() {
			String collectionName = entityInfo.getCollectionName();
			return mongoOperations.find(query, resultClass, collectionName);
		}

		public R single() {
			String collectionName = entityInfo.getCollectionName();
			return mongoOperations.findOne(query, resultClass, collectionName);
		}
	}

}
