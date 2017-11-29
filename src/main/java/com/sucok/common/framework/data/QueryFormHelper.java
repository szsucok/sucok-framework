/*
 * Copyright 2016 - 2017 suoke & Co., Ltd.
 */
package com.sucok.common.framework.data;

import java.lang.reflect.Array;
import java.util.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.sucok.common.framework.data.annotation.Condition;
import com.sucok.common.framework.data.annotation.MatchMode;
import com.sucok.common.framework.data.annotation.Operation;
import com.sucok.common.framework.form.BaseQueryForm;
import com.sucok.common.framework.form.PaginationForm;
import com.sucok.common.framework.util.DateUtils;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import org.assertj.core.util.Arrays;

/**
 * 查询表单辅助类
 * 
 * @author chendx
 *
 */
public class QueryFormHelper {

	private static final ConversionService conversionService = BaseDao.getConversionService();

	public static <T> QueryWrapper createQueryWrapper(final BaseQueryForm form) {
		return  (Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb, boolean sort)-> {
				List<Predicate> predicates = new ArrayList<>();
				addSearch(form, predicates, root, cb);
				addAndFields(form, predicates, root, cb);
				//加入或条件
				addOrFields(form, predicates, root, cb);
				Predicate[] array = new Predicate[predicates.size()];
				predicates.toArray(array);
				query.where(array);
				// 加入排序
				if (sort) {
					List<Order> orders = getOrdes(form, root);
					query.orderBy(orders);
				}

		};
	}
	public static <T> QueryWrapper createQueryWrapper(List<AbstractExpression> expressions) {
		return  (Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb, boolean sort)-> {
			List<Predicate> predicates = new ArrayList<>();
			expressions.forEach(expression->
				predicates.add(expression.buildJpaPredicate(cb,root))
			);
			Predicate[] array = new Predicate[predicates.size()];
			predicates.toArray(array);
			query.where(array);
		};
	}
	public static <T> Specification<T> createSpecification(final PaginationForm form) {
		return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb)-> {
				List<Predicate> predicates = new ArrayList<>();
				//加入搜索条件
				addSearch(form, predicates, root, cb);

				//加入与条件

				addAndFields(form, predicates, root, cb);
				//加入或条件
				addOrFields(form, predicates, root, cb);
				Predicate[] array = new Predicate[predicates.size()];
				predicates.toArray(array);
				query.where(array);
				// 加入排序
				List<Order> orders = getOrdes(form, root);
				query.orderBy(orders);
				return null;

		};
	}

	public static Pageable createPageable(PaginationForm form) {
		int currentPage = form.getCurrentPage() - 1;
		int pageSize = form.getPageSize();
		PageRequest pageable = new PageRequest(currentPage, pageSize);
		return pageable;
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static <T> void addOrFields(final BaseQueryForm form, final List<Predicate> predicates, final Root<T> root,  final CriteriaBuilder cb) {

		  //Google Guava Multimap 键映射到多个值
		  Multimap<String,Object[]> multimap=HashMultimap.create();
		  ReflectionUtils.doWithLocalFields(form.getClass(), (field)->{
			  Condition condition = field.getAnnotation(Condition.class);
			  if (condition == null||condition.operation()== Operation.AND) {
				  return;
			  }
              String groupName=condition.orGroup();
			  String pathName = condition.path();
			  if (StringUtils.isEmpty(pathName)) {
				  pathName = field.getName();
			  }
			  ReflectionUtils.makeAccessible(field);
			  Object value = field.get(form);
			  if (value instanceof Date) {
				  if (condition.minTime()) {
					  value = DateUtils.getBeginTimeOfDay((Date) value);
				  }
				  if (condition.maxTime()) {
					  value = DateUtils.getEndTimeOfDay((Date) value);
				  }
			  }
			  multimap.put(groupName,new Object[]{pathName,value,condition.type(),condition.match()});
		  });
		  if(multimap.isEmpty()){
		  	   return;
		  }
		  for(String groupName:multimap.keySet()){
			  Collection<Object[]> values=multimap.get(groupName);
			  int length=values.size();
			  Predicate[] predicateGroup = new Predicate[length];
			  int i=0;
			  for (Object [] value:values) {
				  String field = (String) value[0];

				  Object v=value[1];
				  Class valueType=(Class)value[2];
				  MatchMode mode=(MatchMode)value[3];

				  Predicate predicate =buildPredicate(root,cb,valueType,mode,field,v);
				  predicateGroup[i] = predicate;
				  i++;
			  }
			  predicates.add(cb.or(predicateGroup));

		  }
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static <T> void addAndFields(final BaseQueryForm form, final List<Predicate> predicates, final Root<T> root,final CriteriaBuilder cb) {

		ReflectionUtils.doWithLocalFields(form.getClass(), (field)->{
				Condition condition = field.getAnnotation(Condition.class);
				if (condition == null) {
					return;
				}
				//或条件不参与
				if(condition.operation()== Operation.OR){
					return;
				}
				String pathName = condition.path();
				if (StringUtils.isEmpty(pathName)) {
					pathName = field.getName();
				}
				ReflectionUtils.makeAccessible(field);
				Object value = field.get(form);
				if (StringUtils.isEmpty(value)) {
					return;
				}
				if (value instanceof Date) {
					if (condition.minTime()) {
						value = DateUtils.getBeginTimeOfDay((Date) value);
					}
					if (condition.maxTime()) {
						value = DateUtils.getEndTimeOfDay((Date) value);
					}
				}
				if (value instanceof String) {
					value = ((String) value).trim();
				}
				Class<?> valueType = condition.type();
				MatchMode mode = condition.match();
			    Predicate predicate =buildPredicate(root,cb,valueType,mode,pathName,value);
			    predicates.add(predicate);

			//}
		});
	}

	private static  Predicate buildPredicate(final Root root,final CriteriaBuilder cb,Class<?> valueType ,MatchMode mode ,String pathName,Object value){

		Predicate predicate=null;
		if (mode == MatchMode.CONTAINS) {
			Expression<String> path = getPath(root, pathName);
			 predicate = cb.like(path, "%" + value + "%");

		} else if (mode == MatchMode.START) {
			Expression<String> path = getPath(root, pathName);
			predicate = cb.like(path, "%" + value);

		} else if (mode == MatchMode.END) {
			Expression<String> path = getPath(root, pathName);
			predicate = cb.like(path, value + "%");

		} else if (mode == MatchMode.BETWEEN) {
			Object[] arrayValues = null;
			if (Arrays.isArray(value)) {
				arrayValues = (Object[]) value;
			} else {
				arrayValues = value.toString().split(",");
			}
			if (arrayValues.length > 1) {
				Expression<Comparable> path = getPath(root, pathName);
				Comparable value1 = (Comparable) conversionService.convert(arrayValues[0], valueType);
				Comparable value2 = (Comparable) conversionService.convert(arrayValues[1], valueType);
				predicate = cb.between(path, value1, value2);

			}
		} else if (mode == MatchMode.IN) {
			Object array;
			if (Arrays.isArray(value)) {
				array = value;
			} else {
				array = value.toString().split(",");
			}
			int length = Array.getLength(array);
			if (length > 0) {
				In<Object> inPredicate = cb.in(getPath(root, pathName));
				for (int i = 0; i < length; i++) {
					Object val = Array.get(array, i);
					val = conversionService.convert(val, valueType);
					inPredicate.value(val);
				}
				predicate=inPredicate;
			} else {
				//predicates.add(cb.isTrue(cb.or()));
			}
		} else if (mode == MatchMode.NOTIN) {
			Object array;
			if (Arrays.isArray(value)) {
				array = value;
			} else {
				array = value.toString().split(",");
			}
			int length = Array.getLength(array);
			if (length > 0) {
				In<Object> inPredicate = cb.in(getPath(root, pathName));
				for (int i = 0; i < length; i++) {
					Object val = Array.get(array, i);
					val = conversionService.convert(val, valueType);
					inPredicate.value(val);
				}
				return cb.not(inPredicate);
			}
		} else if (mode == MatchMode.GE) {
			Expression<Comparable> path = getPath(root, pathName);
			Comparable num = (Comparable) conversionService.convert(value, valueType);
			predicate = cb.greaterThanOrEqualTo(path, num);

		} else if (mode == MatchMode.LE) {
			Expression<Comparable> path = getPath(root, pathName);
			Comparable num = (Comparable) conversionService.convert(value, valueType);
			predicate = cb.lessThanOrEqualTo(path, num);

		} else if (mode == MatchMode.GT) {
			Expression<Comparable> path = getPath(root, pathName);
			Comparable num = (Comparable) conversionService.convert(value, valueType);
			 predicate = cb.greaterThan(path, num);

		} else if (mode == MatchMode.LT) {
			Expression<Comparable> path = getPath(root, pathName);
			Comparable num = (Comparable) conversionService.convert(value, valueType);
			 predicate = cb.lessThan(path, num);

		} else if (mode == MatchMode.NE) {
			Number num = (Number) conversionService.convert(value, valueType);
			predicate = cb.notEqual(getPath(root, pathName), num);

		} else if (mode == MatchMode.ISNULL) {
			if ((boolean) value) {
				predicate = cb.isNull(getPath(root, pathName));

			}
		} else {
			value = conversionService.convert(value, valueType);
			predicate = cb.equal(getPath(root, pathName), value);

		}
		return predicate;
	}
	/**
	 * 加入搜索条件
	 * 
	 * @param predicates 条件组合
	 */
	private static <T> void addSearch(BaseQueryForm form, List<Predicate> predicates, final Root<T> root,
			final CriteriaBuilder cb) {
		if (form.getSearchFields() == null) {
			return;
		}
		String searchText = form.getSearchText();
		if (StringUtils.isEmpty(searchText)) {
			return;
		}
		String[] searchFields = form.getSearchFields();
		if (searchFields.length == 0) {
			return;
		}
		int length = searchFields.length;
		Predicate[] predicateGroup = new Predicate[length];
		for (int i = 0; i < length; i++) {
			String field = searchFields[i];
			Expression<String> path = getPath(root, field);
			predicateGroup[i] = cb.like(path, "%" + searchText + "%");
		}
		predicates.add(cb.or(predicateGroup));
	}

	/**
	 * 加入排序依据
	 */
	private static <T> List<Order> getOrdes(BaseQueryForm form, Root<T> root) {
		String orderBy = form.getOrderMapping();
		if (StringUtils.isEmpty(orderBy)) {
			return Collections.emptyList();
		}
		String[] groups = orderBy.trim().split(",");
		List<Order> orders = new ArrayList<>(groups.length);
		for (String group : groups) {
			boolean ascending = true;
			String[] array = group.split("\\s", 2);
			String field = array[0];
			if (array.length > 1) {
				ascending = "asc".equals(array[1].toLowerCase());
			}
			Order order = new OrderImpl(getPath(root, field), ascending);
			orders.add(order);
		}
		return orders;
	}

	public static <T> List<Order> getOrdes(String orderBy, Root<T> root) {
		if (StringUtils.isEmpty(orderBy)) {
			return Collections.emptyList();
		}
		String[] groups = orderBy.trim().split(",");
		List<Order> orders = new ArrayList<Order>(groups.length);
		for (String group : groups) {
			boolean ascending = true;
			String[] array = group.split("\\s", 2);
			String field = array[0];
			if (array.length > 1) {
				ascending = "asc".equals(array[0].toLowerCase());
			}
			Order order = new OrderImpl(getPath(root, field), ascending);
			orders.add(order);
		}
		return orders;
	}

	public static <X, T> Expression<X> getPath(Root<T> root, String name) {
		String[] array = name.split("[.]");
		Expression<X> expr = root.get(array[0]);
		for (int i = 1; i < array.length; i++) {
			expr = ((Path<X>) expr).get(array[i]);
		}
		return expr;
	}

	@SuppressWarnings("unchecked")
	public static <T, N extends Number> Expression<N> getExpression(CriteriaBuilder cb, Root<T> root, String input) {
		StringTokenizer tokenizer = new StringTokenizer(input, "+-*/", true);
		Expression<N> expr = getPath(root, tokenizer.nextToken());
		if (tokenizer.hasMoreTokens()) {
			String op = tokenizer.nextToken();
			String name = tokenizer.nextToken();
			Expression<N> expr2 = getPath(root, name);
			if ("+".equals(op)) {
				expr = cb.sum(expr, expr2);
			} else if ("-".equals(op)) {
				expr = cb.diff(expr, expr2);
			} else if ("*".equals(op)) {
				expr = cb.prod(expr, expr2);
			} else if ("/".equals(op)) {
				expr = (Expression<N>) cb.quot(expr, expr2);
			}
		}
		return expr;
	}
}
