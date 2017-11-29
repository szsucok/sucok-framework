/*
 * Copyright 2016 - 2017 suoke & Co., Ltd.
 */
package com.sucok.common.framework.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author chendx
 * @version 1.0 created at 2017年4月22日 下午2:48:17
 *
 */
public class BeanViewUtils {

	public static <V extends BeanView<T>, T> V getView(T bean, Class<V> viewClass) {
		return  getView(bean,viewClass,null);
	}
	public static <V extends BeanView<T>, T> V getView(T bean, Class<V> viewClass,String... ignoreFields) {
		V view;
		try {
			view = viewClass.newInstance();
			if(ignoreFields!=null){
				view.setIgnoreFields(ignoreFields);
			}
		} catch (Exception e) {
			throw new RuntimeException("创建视图[" + viewClass.getName() + "]失败:" + e.getMessage());
		}
		view.transfer(bean);
		return view;
	}
	public static <V extends BeanView<T>, T> List<V> getList(Collection<T> collection, Class<V> viewClass) {
		List<V> viewList = new ArrayList<V>();
		for (T item : collection) {
			V view = getView(item, viewClass);
			viewList.add(view);
		}
		return viewList;
	}

	public static <V extends BeanView<T>, T> List<V> getList(Stream<T> collection, Class<V> viewClass) {
		return collection.map(item -> {
			return getView(item, viewClass);
		}).collect(Collectors.toList());
	}
}
