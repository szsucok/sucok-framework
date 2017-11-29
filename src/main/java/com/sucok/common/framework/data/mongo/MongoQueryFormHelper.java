/*
 * Copyright 2016 - 2017 suoke & Co., Ltd.
 */
package com.sucok.common.framework.data.mongo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.sucok.common.framework.data.annotation.Condition;
import com.sucok.common.framework.data.annotation.MatchMode;
import com.sucok.common.framework.form.BaseQueryForm;
import com.sucok.common.framework.form.PaginationForm;
import com.sucok.common.framework.util.DateUtils;
import org.assertj.core.util.Arrays;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * 查询表单辅助类
 * 
 * @author chendx
 *
 */
public class MongoQueryFormHelper {

	public static Query createQuery(BaseQueryForm form) {
		List<Criteria> criteriaList = new ArrayList<>();
		addFields(form, criteriaList);
		addSearch(form, criteriaList);
		Query query = null;
		if (criteriaList.isEmpty()) {
			query = new Query();
		} else {
			Criteria[] criterias = new Criteria[criteriaList.size()];
			criteriaList.toArray(criterias);
			query = new Query(new Criteria().andOperator(criterias));
		}
		String orderBy = form.getOrderBy();
		if (!StringUtils.isEmpty(orderBy)) {
			query.with(MongoQueryFormHelper.getSort(orderBy));
		}
		return query;
	}

	private static <T> void addFields(final BaseQueryForm form, List<Criteria> criterias) {
		ReflectionUtils.doWithLocalFields(form.getClass(), field ->{
				Condition condition = field.getAnnotation(Condition.class);
				if (condition == null) {
					return;
				}
				String pathName = condition.path();
				if (StringUtils.isEmpty(pathName)) {
					pathName = field.getName();
				}
				ReflectionUtils.makeAccessible(field);
				Object value = field.get(form);
				if (StringUtils.isEmpty(value)) {
					return;
				}
				if (value instanceof Date) {
					if (condition.minTime()) {
						value = DateUtils.getBeginTimeOfDay((Date) value);
					}
					if (condition.maxTime()) {
						value = DateUtils.getEndTimeOfDay((Date) value);
					}
				}
				MatchMode mode = condition.match();
				if (mode == MatchMode.CONTAINS) {
					criterias.add(Criteria.where(pathName).regex(value.toString()));
				} else if (mode == MatchMode.START) {
					criterias.add(Criteria.where(pathName).regex(value + ".*$"));
				} else if (mode == MatchMode.END) {
					criterias.add(Criteria.where(pathName).regex("^.*" + value + "$"));
				} else if (mode == MatchMode.BETWEEN) {
					Object[] values = null;
					if (Arrays.isArray(value)) {
						values = (Object[]) values;
					} else {
						values = value.toString().split(",");
					}
					criterias.add(Criteria.where(pathName).gte(values[0]).and(pathName).lte(values[1]));
				} else if (mode == MatchMode.IN) {
					Object array;
					if (Arrays.isArray(value)) {
						array = value;
					} else {
						array = value.toString().split(",");
					}
					criterias.add(Criteria.where(pathName).in(array));
				} else if (mode == MatchMode.NOTIN) {
					Object array;
					if (Arrays.isArray(value)) {
						array = value;
					} else {
						array = value.toString().split(",");
					}
					criterias.add(Criteria.where(pathName).nin(array));
				} else if (mode == MatchMode.GE) {
					criterias.add(Criteria.where(pathName).gte(value));
				} else if (mode == MatchMode.LE) {
					criterias.add(Criteria.where(pathName).lte(value));
				} else if (mode == MatchMode.GT) {
					criterias.add(Criteria.where(pathName).gt(value));
				} else if (mode == MatchMode.LT) {
					criterias.add(Criteria.where(pathName).lt(value));
				} else if (mode == MatchMode.NE) {
					criterias.add(Criteria.where(pathName).ne(value));
				} else if (mode == MatchMode.ISNULL) {
					criterias.add(Criteria.where(pathName).is(null));
				} else {
					criterias.add(Criteria.where(pathName).is(value));
				}
			}
		);
	}

	/**
	 * 加入搜索条件
	 * 
	 * @param criterias 条件组合
	 */
	private static void addSearch(BaseQueryForm form, List<Criteria> criterias) {
		if (form.getSearchFields() == null) {
			return;
		}
		String searchText = form.getSearchText();
		if (StringUtils.isEmpty(searchText)) {
			return;
		}
		String[] searchFields = form.getSearchFields();
		if (searchFields.length == 0) {
			return;
		}
		int length = searchFields.length;
		Criteria[] criteriaGroup = new Criteria[length];
		for (int i = 0; i < length; i++) {
			String field = searchFields[i];
			criteriaGroup[i] = Criteria.where(field).regex(searchText);
		}
		criterias.add(new Criteria().orOperator(criteriaGroup));
	}

	public static Pageable createPageable(PaginationForm form) {
		int currentPage = form.getCurrentPage() - 1;
		int pageSize = form.getPageSize();
		PageRequest pageable = new PageRequest(currentPage, pageSize);
		return pageable;
	}

	public static <T> List<Order> getOrdes(String orderBy) {
		if (StringUtils.isEmpty(orderBy)) {
			return Collections.emptyList();
		}
		String[] groups = orderBy.trim().split(",");
		List<Order> orders = new ArrayList<Order>(groups.length);
		for (String group : groups) {
			boolean ascending = true;
			String[] array = group.split("\\s", 2);
			String property = array[0];
			if (array.length > 1) {
				ascending ="asc".equalsIgnoreCase(array[0]);;
			}
			Order order = new Order(ascending ? Direction.ASC : Direction.DESC, property);
			orders.add(order);
		}
		return orders;
	}

	public static <T> Sort getSort(String orderBy) {
		return new Sort(getOrdes(orderBy));
	}
}
