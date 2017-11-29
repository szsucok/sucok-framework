/*
 * Copyright 2016 - 2017 suoke & Co., Ltd.
 */
package com.sucok.common.framework.data.expression;

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
public class GreaterThan<Y extends Comparable<? super Y>> extends AbstractExpression {

	public GreaterThan(String  field, Y value) {
		super(field,value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Predicate buildJpaPredicate(CriteriaBuilder cb, Root<?> root) {
		Expression<Y> path = QueryFormHelper.getPath(root,field);
		Y num = (Y) value;
		return cb.greaterThan(path, num);
	}

	@Override
	public Criteria buildMongoCriteria(Object expression) {
		String path = expression.toString();
		return Criteria.where(path).gt(value);
	}
}
