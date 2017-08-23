package com.vshpynta.sample.model.annotations;

import com.vshpynta.sample.validation.BaseEntityValidator;
import com.vshpynta.sample.validation.EntityValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by vshpynta on 17/04/2015.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface Validate {

    Class<? extends EntityValidator>[] value() default {BaseEntityValidator.class};
}
