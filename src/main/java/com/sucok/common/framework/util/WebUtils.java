/*
 * Copyright 2016 - 2017 suoke & Co., Ltd.
 */
package com.sucok.common.framework.util;

import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author chendx
 * @version 1.0 created at 2017年5月9日 下午5:17:32
 *
 */
public class WebUtils {

	public static HttpServletRequest getCurrentRequest() {
		ServletRequestAttributes reqAttrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (reqAttrs == null) {
			return null;
		}
		HttpServletRequest request = reqAttrs.getRequest();
		return request;
	}

	public static HttpServletResponse getCurrentRespost() {
		ServletRequestAttributes reqAttrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletResponse response = reqAttrs.getResponse();
		return response;
	}

	/**
	 * 在很多应用下都可能有需要将用户的真实IP记录下来，这时就要获得用户的真实IP地址，在JSP里，获取客户端的IP地
	 * 址的方法是：request.getRemoteAddr()，这种方法在大部分情况下都是有效的。但是在通过了Apache,Squid等 反向代理软件就不能获取到客户端的真实IP地址了。
	 * 但是在转发请求的HTTP头信息中，增加了X－FORWARDED－FOR信息。用以跟踪原有的客户端IP地址和原来客户端请求的服务器地址。
	 * 
	 * @param request
	 * @return
	 */
	public static String getRemoteIp(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Real-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if (!StringUtils.isEmpty(ip)) {
			ip = ip.split(",")[0];
		}
		return ip;
	}

	public static boolean isMulitpart(HttpServletRequest request) {
		String contentType = request.getHeader("Content-Type");
		return contentType != null && contentType.contains("multipart");
	}

	public static String getRequestParametersString(HttpServletRequest request) {
		StringBuffer sb = new StringBuffer();
		for (Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
			sb.append("&").append(entry.getKey()).append("=");
			for (String value : entry.getValue()) {
				sb.append(value).append(",");
			}
			if (!ArrayUtils.isEmpty(entry.getValue())) {
				sb.deleteCharAt(sb.length() - 1);
			}
		}
		if (sb.length() > 0) {
			sb.delete(0, 1);
		}
		return sb.toString();
	}

	public static String getSessionId() {
		return getCurrentRequest().getHeader("X-SessionID");
	}

	public static String getClientId() {
		HttpServletRequest request = getCurrentRequest();
		if (request == null) {
			return null;
		}
		return request.getHeader("X-ClientID");
	}

	public static String getTraceId() {
		HttpServletRequest request = getCurrentRequest();
		if (request == null) {
			return null;
		}
		return getTraceId(request);
	}

	public static String getTraceId(HttpServletRequest request) {
		String traceId=request.getHeader("X-RequestID");
		if(traceId==null) {
			return request.getHeader("X-TraceID");
		}
		return traceId;
	}

	public static String getToken() {
		HttpServletRequest request = getCurrentRequest();
		if (request == null) {
			return null;
		}
		return request.getHeader("X-Token");
	}

	public static String getClientVer() {
		HttpServletRequest request = getCurrentRequest();
		if (request == null) {
			return null;
		}
		return request.getHeader("X-ClientVer");
	}

	public static String getUA() {
		HttpServletRequest request = getCurrentRequest();
		if (request == null) {
			return null;
		}
		return request.getHeader("X-UA");
	}

	private static String getUsetAgent() {
		HttpServletRequest request = getCurrentRequest();
		if (request == null) {
			return null;
		}
		return request.getHeader("user-agent");
	}

	public static String getReferer() {
		HttpServletRequest request = getCurrentRequest();
		if (request == null) {
			return null;
		}
		return request.getHeader("referer");
	}

}
