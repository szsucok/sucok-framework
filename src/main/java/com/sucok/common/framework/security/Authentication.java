/*
 * Copyright 2016 - 2017 suoke & Co., Ltd.
 */
package com.sucok.common.framework.security;

import java.io.Serializable;
import java.security.Principal;

import com.sucok.common.framework.constant.AuthenticationType;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * 
 * @author chendx
 *
 */
@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class Authentication implements Principal, Serializable {

	private static final long serialVersionUID = 3142842461499197779L;

	@NonNull
	@Setter(AccessLevel.PRIVATE)
	private Object userId;

	@NonNull
	@Setter(AccessLevel.PRIVATE)
	private String name;

	@NonNull
	@Setter(AccessLevel.PRIVATE)
	private String client;

	@NonNull
	@Setter(AccessLevel.PRIVATE)
	private String type;

	@NonNull
	@Setter(AccessLevel.PRIVATE)
	private String roles;



	@Override
	public String getName() {
		return name;
	}

	public boolean isOfType(AuthenticationType type) {
		return type.is(this.type);
	}

	@SuppressWarnings("unchecked")
	public <T> T convertUserId(Class<T> type) {
		if (type == userId.getClass()) {
			return (T) userId;
		}
		if (type == Integer.class) {
			return (T) Integer.valueOf(this.userId.toString());
		} else if (type == Long.class) {
			return (T) Long.valueOf(this.userId.toString());
		} else {
			return (T) userId;
		}
	}
}
