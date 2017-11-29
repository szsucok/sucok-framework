/*
 * Copyright 2016 - 2017 suoke & Co., Ltd.
 */
package com.sucok.common.framework.form;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 分页查询表单
 * 
 * @author chendx
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PaginationForm extends BaseQueryForm {

	/**
	 * 当前页
	 */
	private int currentPage = 1;

	/**
	 * 分页大小
	 */
	private int pageSize = 10;

	public Pageable buildPageRequest() {
		return new PageRequest(currentPage - 1, pageSize);
	}

}
