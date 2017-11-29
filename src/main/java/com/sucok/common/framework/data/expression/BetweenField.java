/*
 * Copyright 2016 - 2017 suoke & Co., Ltd.
 */
package com.sucok.common.framework.data.expression;

import java.lang.reflect.Array;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.mongodb.core.query.Criteria;

import com.sucok.common.framework.data.AbstractExpression;
import com.sucok.common.framework.data.QueryFormHelper;

/**
 * @author chendx
 * @version 1.0 created at 2017年5月26日 下午1:08:21
 *
 */
public class BetweenField extends AbstractExpression {

	public BetweenField(String field, Object value) {
		super(field, value);
	}

	private String field1,field2;
	public BetweenField(String field1, String field2,Object value) {
		super(field1,value);
		this.field1=field1;
		this.field2=field2;
		this.value=value;
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Predicate buildJpaPredicate(CriteriaBuilder cb, Root root) {
		String field1 = (String) Array.get(value, 0);
		String field2 = (String) Array.get(value, 1);
		Expression<Comparable> path1 = QueryFormHelper.getPath(root, field1);
		Expression<Comparable> path2 = QueryFormHelper.getPath(root, field2);
		Expression<Comparable> parameter = cb.literal((Comparable) value);
		return cb.between(parameter, path1, path2);
	}

	@Override
	public Criteria buildMongoCriteria(Object expression) {
		String field1 = (String) Array.get(value, 0);
		String field2 = (String) Array.get(value, 1);
		Object value = expression;
		return Criteria.where(field1).lte(value).and(field2).gte(value);
	}
}
