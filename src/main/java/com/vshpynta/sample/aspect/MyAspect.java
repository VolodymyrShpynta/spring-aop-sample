package com.vshpynta.sample.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * Created by vshpynta on 22/08/2017.
 */
@Component
@Aspect
public class MyAspect {

    @Before("execution(public * *(..))")
    public void anyPublicMethod() {
        System.out.println("...Running Before aspect...");
    }

    @Before("within(com.vshpynta.sample.service.*)")
    public void withinPointcut() {
        System.out.println("...Running Before aspect with within pointcut...");
    }

    @Before("@annotation(com.vshpynta.sample.model.annotations.TestAnnotation)")
    public void withinAnnotationPointcut() {
        System.out.println("...Running Before aspect with within annotation pointcut...");
    }
}
