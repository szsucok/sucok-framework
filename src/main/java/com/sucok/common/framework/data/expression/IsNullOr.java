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
public class IsNullOr extends AbstractExpression {

	public IsNullOr(AbstractExpression expression) {
		super(null,expression);
	}

	@Override
	public Predicate buildJpaPredicate(CriteriaBuilder cb, Root<?> root) {
		Expression<?> path = QueryFormHelper.getPath(root, field);
		AbstractExpression expr = (AbstractExpression) value;
		return cb.or(cb.isNull(path), expr.buildJpaPredicate(cb, root));
	}

	@Override
	public Criteria buildMongoCriteria(Object expression) {
		String path = expression.toString();
		AbstractExpression expr = (AbstractExpression) value;
		return Criteria.where(path).is(null).orOperator(expr.buildMongoCriteria(expression));
	}
}
