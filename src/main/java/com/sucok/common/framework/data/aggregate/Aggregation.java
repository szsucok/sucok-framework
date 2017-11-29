/*
 * Copyright 2016 - 2017 suoke & Co., Ltd.
 */
package com.sucok.common.framework.data.aggregate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import com.sucok.common.framework.data.AbstractExpression;
import com.sucok.common.framework.data.QueryFormHelper;
import org.springframework.util.StringUtils;

/**
 * @author chendx
 * @version 1.0 created at 2017年6月23日 下午6:09:38
 *
 */
public class Aggregation<R> {

	private Class<R> resultType;

	public List<AggregateExpression> expressions;

	public Map<Object, Object> conditions;

	private List<String> groups;

	private String orderBy;

	private Aggregation(Class<R> resultType) {
		this.resultType = resultType;
		this.expressions = new ArrayList<>(3);
		this.groups = new ArrayList<>(1);
	}

	public static <R> Aggregation<R> create(Class<R> resultType) {
		return new Aggregation<>(resultType);
	}

	public Aggregation<R> groupBy(String... expressions) {
		for (String expression : expressions) {
			this.groups.add(expression);
		}
		return this;
	}

	public Aggregation<R> select(String... expressions) {
		for (String expression : expressions) {
			select(expression, expression);
		}
		return this;
	}

	public Aggregation<R> select(String expression, String alias) {
		expressions.add(new AggregateExpression(AggregateType.NONE, expression, alias));
		return this;
	}

	public Aggregation<R> sum(String expression, String alias) {
		expressions.add(new AggregateExpression(AggregateType.SUM, expression, alias));
		return this;
	}

	public Aggregation<R> count(String expression, String alias) {
		expressions.add(new AggregateExpression(AggregateType.COUNT, expression, alias));
		return this;
	}

	public Aggregation<R> max(String expression, String alias) {
		expressions.add(new AggregateExpression(AggregateType.MAX, expression, alias));
		return this;
	}

	public Aggregation<R> min(String expression, String alias) {
		expressions.add(new AggregateExpression(AggregateType.MIN, expression, alias));
		return this;
	}

	public Aggregation<R> avg(String expression, String alias) {
		expressions.add(new AggregateExpression(AggregateType.AVG, expression, alias));
		return this;
	}

	public Aggregation<R> where(Map<Object, Object> conditions) {
		if (this.conditions == null) {
			this.conditions = conditions;
		} else {
			this.conditions.putAll(conditions);
		}
		return this;
	}

	public Aggregation<R> where(Object name, Object value) {
		if (this.conditions == null) {
			this.conditions = new HashMap<>();
		}
		this.conditions.put(name, value);
		return this;
	}

	public Aggregation<R> orderBy(String orderBy) {
		this.orderBy = orderBy;
		return this;
	}

	@SuppressWarnings("unchecked")
	public <T> CriteriaQuery<R> createQuery(EntityManager em, Class<T> domainClass) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<R> query = cb.createQuery(resultType);
		Root<T> root = query.from(domainClass);
		if (conditions != null) {
			int i = 0;
			Predicate[] predicates = new Predicate[conditions.size()];
			for (Entry<Object, Object> entry : conditions.entrySet()) {
				Object key = entry.getKey();
				Object value = entry.getValue();
				if (value instanceof AbstractExpression) {
					Predicate predicate = ((AbstractExpression) value).buildJpaPredicate(cb, root);
					predicates[i++] = predicate;
				} else {
					String name = key.toString();
					Expression<?> path = QueryFormHelper.getPath(root, name);
					Predicate predicate = cb.equal(path, value);
					predicates[i++] = predicate;
				}
			}
			query.where(predicates);
		}
		List<Selection<?>> selections = new ArrayList<>(expressions.size());
		for (AggregateExpression expr : expressions) {
			Expression<?> expression = QueryFormHelper.getExpression(cb, root, expr.getExpression());
			if (expr.getType() == AggregateType.COUNT) {
				expression = cb.count(expression);
			} else if (expr.getType() == AggregateType.SUM) {
				expression = cb.sum((Expression<? extends Number>) expression);
			} else if (expr.getType() == AggregateType.AVG) {
				expression = cb.avg((Expression<? extends Number>) expression);
			} else if (expr.getType() == AggregateType.MAX) {
				expression = cb.max((Expression<? extends Number>) expression);
			} else if (expr.getType() == AggregateType.MIN) {
				expression = cb.min((Expression<? extends Number>) expression);
			}
			selections.add(expression.alias(expr.getAlias()));
		}
		query.multiselect(selections);
		if (!groups.isEmpty()) {
			List<Expression<?>> grouping = new ArrayList<>(groups.size());
			for (String groupBy : groups) {
				Expression<?> expression = QueryFormHelper.getExpression(cb, root, groupBy);
				grouping.add(expression);
			}
			query.groupBy(grouping);
		}
		if (!StringUtils.isEmpty(orderBy)) {
			List<Order> orders = QueryFormHelper.getOrdes(orderBy, root);
			query.orderBy(orders);
		}
		return query;
	}
}
