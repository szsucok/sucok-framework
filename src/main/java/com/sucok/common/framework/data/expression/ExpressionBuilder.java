package com.sucok.common.framework.data.expression;

import com.sucok.common.framework.data.AbstractExpression;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

/**
 *
 */
public class ExpressionBuilder {

    private Predicate predicate;
    private String orderBy;
    private Class<?> clazz;
    private  CriteriaBuilder cb;
    public static ExpressionBuilder create(Class<?> clazz){
         ExpressionBuilder eb=new ExpressionBuilder();
         eb.clazz=clazz;
         return  eb;
    }
    public   ExpressionBuilder and(AbstractExpression ... expressions){

        return this;
    }
    public   ExpressionBuilder or(AbstractExpression ... expressions){
        return this;
    }
    public   ExpressionBuilder or(String  ... order){
        return this;
    }
    public ExpressionBuilder asc(String field){
        return this;
    }
    public ExpressionBuilder desc(String field){
        return this;
    }
    public Predicate toPredicate(){
        return predicate;
    }
}
