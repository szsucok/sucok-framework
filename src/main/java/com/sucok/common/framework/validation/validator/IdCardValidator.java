package com.sucok.common.framework.validation.validator;



import com.sucok.common.framework.validation.IdCard;
import com.sucok.common.framework.util.IDCardUtils;
import javax.validation.*;


public class IdCardValidator implements ConstraintValidator<IdCard,String> {



    private IdCard constraintAnnotation;




    @Override
    public void initialize(IdCard constraintAnnotation) {
           this.constraintAnnotation=constraintAnnotation;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {


            return IDCardUtils.idCardValidate(value);

    }



}
