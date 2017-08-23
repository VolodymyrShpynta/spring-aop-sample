package com.vshpynta.sample.aspect;

import com.vshpynta.sample.model.Lecturer;
import com.vshpynta.sample.model.annotations.ValidatedBy;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vshpynta on 22/08/2017.
 */
@Component
@Aspect
public class CustomValidator {

    @Before("@annotation(com.vshpynta.sample.model.annotations.TestAnnotation)")
    public void withinAnnotationPointcut(JoinPoint joinPoint) {
        System.out.println("...Running custom validator pointcut...");
        Arrays.stream(joinPoint.getArgs())
                .filter(Lecturer.class::isInstance)
                .map(Lecturer.class::cast)
                .findAny()
                .ifPresent(lecturer -> System.out.println(lecturer.getFirstName()));
    }

    @Before("execution(* *(.., @com.vshpynta.sample.model.annotations.ValidatedBy (*), ..))")
    public void beforeAnnotatedParameter(JoinPoint joinPoint) throws NoSuchMethodException {
        System.out.println("...Running custom validator for parameters...");
        final Map<Integer, ValidatedBy> annotations = getPositionAnnotationMap(joinPoint, ValidatedBy.class);
        System.out.println(annotations);
    }

    private <T extends Annotation> Map<Integer, T> getPositionAnnotationMap(JoinPoint joinPoint, Class<T> annotationClass) throws NoSuchMethodException {
        final Annotation[][] parametersAnnotations = getParametersAnnotations(joinPoint);
        Map<Integer, T> annotations = new HashMap<>();
        for (int i = 0; i < parametersAnnotations.length; i++) {
            for (int j = 0; j < parametersAnnotations[i].length; j++) {
                final Annotation annotation = parametersAnnotations[i][j];
                if (annotationClass.isAssignableFrom(annotation.annotationType())) {
                    annotations.put(i, annotationClass.cast(annotation));
                }
            }
        }
        return annotations;
    }

    private Annotation[][] getParametersAnnotations(JoinPoint methodJoinPoint) throws NoSuchMethodException {
        MethodSignature signature = (MethodSignature) methodJoinPoint.getSignature();
        String methodName = signature.getMethod().getName();
        Class<?>[] parameterTypes = signature.getMethod().getParameterTypes();
        return methodJoinPoint.getTarget().getClass().getMethod(methodName, parameterTypes).getParameterAnnotations();
    }
}
