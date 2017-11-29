package com.sucok.common.framework.validation.validator;

import com.sucok.common.framework.validation.Money;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class MoneyValidator implements ConstraintValidator<Money,Object> {

    private String moneyReg = "^\\d+(\\.\\d{1,2})?$";//表示金额的正则表达式
    private Pattern moneyPattern = Pattern.compile(moneyReg);
    @Override
    public void initialize(Money constraintAnnotation) {

    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {

        if (value == null) {
            return true;
        }
        return moneyPattern.matcher(value.toString()).matches();

    }
}
