/*
 * Copyright 2016 - 2017 suoke & Co., Ltd.
 */
package com.sucok.common.framework.data.aggregate;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * @author chendx
 * @version 1.0 created at 2017年6月23日 下午5:57:14
 *
 */
@Data
public class AggregateExpression {

	@Setter(AccessLevel.PRIVATE)
	private String expression;

	@Setter(AccessLevel.PRIVATE)
	private String alias;

	private AggregateType type;

	public AggregateExpression(AggregateType type, String expression, String alias) {
		this.type = type;
		this.expression = expression;
		this.alias = alias;
	}
}
