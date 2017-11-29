package com.sucok.common.framework.validation;

import com.sucok.common.framework.validation.validator.LengthValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = LengthValidator.class)
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
public @interface Length {
    int min() default 0;

    int max() default Integer.MAX_VALUE;

    String message() default "字符长度不正确";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}