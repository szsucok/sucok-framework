/*
 * Copyright 2016 - 2017 suoke & Co., Ltd.
 */
package com.sucok.common.framework.exception;

import com.sucok.common.framework.result.ActionResult;

/**
 * 应用程序错误
 * 
 * @author chendx
 *
 */
public class ApplicationException extends RuntimeException {

	private static final long serialVersionUID = 2156274765383407719L;

	private ActionResult<?> result;

	public ApplicationException() {
		result = ActionResult.error();
	}

	public ApplicationException(int errcode) {
		result = ActionResult.error(errcode);
	}

	public ApplicationException(String errmsg) {
		result = ActionResult.error(errmsg);
	}

	public ApplicationException(int errcode, String errmsg) {
		result = ActionResult.error(errcode, errmsg);
	}

	public ApplicationException(ActionResult<?> result) {
		this.result = result;
	}

	@Override
	public String getMessage() {
		return result.getMsg();
	}

	public ActionResult<?> getResult() {
		return result;
	}
}
