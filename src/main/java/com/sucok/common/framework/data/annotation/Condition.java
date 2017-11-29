/*
 * Copyright 2016 - 2017 suoke & Co., Ltd.
 */
package com.sucok.common.framework.data.annotation;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 条件注解
 * @author chendx
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { FIELD })
public @interface Condition {

	/**
	 * 查询路径 如 name  或 parent.name
	 * @return
	 */
	String path() default "";

	/**
	 * 匹配模式
	 * @see MatchMode
	 * @return
	 */
	MatchMode match() default MatchMode.EQ;

	/**
	 * 数据类型
	 * @return
	 */
	Class<?> type() default Object.class;
	/**
	 * 操作方式 默认 AND
	 * @return
	 */
	Operation operation() default Operation.AND;

	/**
	 * Or条件组 主要用于or查询
	 * @return
	 */
	String orGroup() default  "";
	/**
	 * 用于时间处理。表示此日期最早时间 如2017-11-09 00:00:00
	 * @return
	 */
	boolean minTime() default false;
	/**
	 * 用于时间处理。表示此日期最晚时间 如2017-11-09 23:59:59 999
	 * @return
	 */
	boolean maxTime() default false;



}
