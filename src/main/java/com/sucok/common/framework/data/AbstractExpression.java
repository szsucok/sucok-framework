/*
 * Copyright 2016 - 2017 suoke & Co., Ltd.
 */
package com.sucok.common.framework.data;

import org.springframework.data.mongodb.core.query.Criteria;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * 查询表达式，包括JPA和MongoDB
 * @author chendx
 * @version 1.0 created at 2017年5月26日 下午1:08:37
 *
 */
public abstract class AbstractExpression {

	protected Object value;
	protected String field;
	public AbstractExpression(String  field, Object value) {
		this.field=field;
		this.value = value;
	}

	public abstract Predicate buildJpaPredicate(CriteriaBuilder cb, Root<?> root);

	public abstract Criteria buildMongoCriteria(Object expression);

}
