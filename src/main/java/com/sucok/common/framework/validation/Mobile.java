package com.sucok.common.framework.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Pattern;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy ={})
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@ReportAsSingleViolation
@Pattern(regexp = "^((13[0-9])|(15[0-9])|(16[0-9])|(18[0-9])|(17[0-9])|(146)|(145)|(147))\\d{8}$")
public @interface Mobile {
    String message() default "手机不正确";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
