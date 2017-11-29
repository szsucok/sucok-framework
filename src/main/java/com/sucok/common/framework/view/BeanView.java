/*
 * Copyright 2016 - 2017 suoke & Co., Ltd.
 */
package com.sucok.common.framework.view;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.sucok.common.framework.util.ReflectionUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author chendx
 * @version 1.0 created at 2017年4月22日 下午2:46:35
 *
 */
public class BeanView<T> {

	private String[] ignoreFields=new String[0];
	@JsonIgnore
	public String[] ignoreFields() {
		return ignoreFields;
	}

	public void setIgnoreFields(String... ignoreFields){
             this.ignoreFields=ignoreFields;
	}
	@SuppressWarnings("unchecked")
	public void transfer(T bean) {
		List<String> ignores = Arrays.asList(ignoreFields());
		Class<?> viewClass = this.getClass();
		Class<?> beanClass = bean.getClass();
		Field[] viewFields = ReflectionUtils.getAllDeclaredFields(viewClass);
		for (Field viewField : viewFields) {
			try {
				String name = viewField.getName();
				if (ignores.contains(name)) {
					continue;
				}
				viewField.setAccessible(true);
				PropertyDescriptor beanProperty = ReflectionUtils.findProperty(beanClass, name);
				if (beanProperty == null) {
					continue;
				}
				if (beanProperty.getReadMethod() == null) {
					throw new RuntimeException("实体[" + beanClass.getName() + "],字段[" + name + "]没有getter");
				}
				Class<?> fieldType = viewField.getType();
				Object value = beanProperty.getReadMethod().invoke(bean);
				if (value == null) {
					continue;
				}
				if (BeanView.class.isAssignableFrom(fieldType)) {
					BeanView<Object> view = (BeanView<Object>) fieldType.newInstance();
					view.transfer(value);
					value = view;
				} else if (Collection.class.isAssignableFrom(fieldType)) {
					ParameterizedType genericType = (ParameterizedType) viewField.getGenericType();
					Class<?> cildViewClass = (Class<?>) genericType.getActualTypeArguments()[0];
					if (BeanView.class.isAssignableFrom(cildViewClass)) {
						Collection<BeanView<?>> viewList = new ArrayList<>();
						for (Object item : (Collection<?>) value) {
							BeanView<Object> view = (BeanView<Object>) cildViewClass.newInstance();
							view.transfer(item);
							viewList.add(view);
						}
						value = viewList;
					}
				}
				viewField.set(this, value);
			} catch (Exception e) {

				e.printStackTrace();
				continue;

				// throw new RuntimeException(
				// "视图[" + viewClass.getName() + "],字段[" + viewField.getName() + "]赋值失败:" +
				// e.getMessage());
			}
		}
	}



}