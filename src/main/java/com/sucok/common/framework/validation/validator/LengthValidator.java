package com.sucok.common.framework.validation.validator;

import com.sucok.common.framework.validation.Length;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author chendx
 */
public class LengthValidator  implements ConstraintValidator<Length,String> {

    private  Length annotation;
    @Override
    public void initialize(Length annotation) {
        this.annotation=annotation;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value==null){
            return true;
        }
        if(value.length()>=annotation.min()&&value.length()>=annotation.max()){
            return true;
        }
        return false;
    }
}
