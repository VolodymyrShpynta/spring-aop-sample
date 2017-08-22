package com.vshpynta.sample.aspect;

import com.vshpynta.sample.model.Lecturer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Created by vshpynta on 22/08/2017.
 */
@Component
@Aspect
public class CustomValidator {

    @Before("@annotation(com.vshpynta.sample.model.annotations.TestAnnotation)")
    public void withinAnnotationPointcut(JoinPoint jp) {
        System.out.println("...Running custom validator pointcut...");
        Arrays.stream(jp.getArgs())
                .filter(Lecturer.class::isInstance)
                .map(Lecturer.class::cast)
                .findAny()
                .ifPresent(lecturer -> System.out.println(lecturer.getFirstName()));

    }
}
