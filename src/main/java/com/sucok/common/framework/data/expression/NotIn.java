/*
 * Copyright 2016 - 2017 suoke & Co., Ltd.
 */
package com.sucok.common.framework.data.expression;

import java.lang.reflect.Array;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.sucok.common.framework.data.AbstractExpression;
import org.springframework.data.mongodb.core.query.Criteria;

import com.sucok.common.framework.data.QueryFormHelper;

/**
 * @author chendx
 * @version 1.0 created at 2017年5月26日 下午1:08:21
 *
 */
public class NotIn extends AbstractExpression {

	public <T> NotIn(String  field, T[] value) {
		super(field,value);
	}

	@Override
	public Predicate buildJpaPredicate(CriteriaBuilder builder, Root<?> root) {
		Expression<Object> path = QueryFormHelper.getPath(root, field);
		CriteriaBuilder.In<Object> predicate = builder.in(path);
		int length = Array.getLength(value);
		for (int i = 0; i < length; i++) {
			predicate.value(Array.get(value, i));
		}
		return builder.not(predicate);
	}

	@Override
	public Criteria buildMongoCriteria(Object expression) {
		String path = expression.toString();
		Object[] values = (Object[]) value;
		return Criteria.where(path).nin(values);
	}
}
