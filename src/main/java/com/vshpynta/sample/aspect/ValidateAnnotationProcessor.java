package com.vshpynta.sample.aspect;

import com.vshpynta.sample.model.Lecturer;
import com.vshpynta.sample.model.annotations.Validate;
import com.vshpynta.sample.validation.EntityValidator;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
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
public class ValidateAnnotationProcessor implements BeanFactoryPostProcessor {

    private Map<String, String> validatorsClassNameMap = new HashMap<>();

    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        for (String beanName : beanFactory.getBeanNamesForType(EntityValidator.class)) {
            validatorsClassNameMap.put(beanFactory.getBeanDefinition(beanName).getBeanClassName(),
                    beanName);
        }
    }

    @Before("@annotation(com.vshpynta.sample.model.annotations.TestAnnotation)")
    public void withinAnnotationPointcut(JoinPoint joinPoint) {
        System.out.println("...Running custom validator pointcut...");
        Arrays.stream(joinPoint.getArgs())
                .filter(Lecturer.class::isInstance)
                .map(Lecturer.class::cast)
                .findAny()
                .ifPresent(lecturer -> System.out.println(lecturer.getFirstName()));
    }

    @Before("execution(* *(.., @com.vshpynta.sample.model.annotations.Validate (*), ..))")
    public void beforeAnnotatedParameter(JoinPoint joinPoint) throws NoSuchMethodException {
        System.out.println("...Running custom validator for parameters...");
        final Map<Integer, Validate> annotations = getPositionAnnotationMap(joinPoint, Validate.class);
        for (Map.Entry<Integer, Validate> entry : annotations.entrySet()) {
            for (Class<? extends EntityValidator> validatorClass : entry.getValue().value()) {
                final String beanName = validatorsClassNameMap.get(validatorClass.getName());
                beanFactory.getBean(beanName, EntityValidator.class)
                        .validate(joinPoint.getArgs()[entry.getKey()]);
            }
        }
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
