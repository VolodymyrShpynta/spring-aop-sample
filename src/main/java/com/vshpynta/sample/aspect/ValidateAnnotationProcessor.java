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
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static com.vshpynta.sample.utils.PredicateUtils.not;
import static java.util.stream.Collectors.toList;

/**
 * Created by vshpynta on 22/08/2017.
 */
@Component
@Aspect
public class ValidateAnnotationProcessor implements BeanFactoryPostProcessor {

    private Map<String, String> validatorClassBeanNameMap = new HashMap<>();

    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        for (String beanName : beanFactory.getBeanNamesForType(EntityValidator.class)) {
            validatorClassBeanNameMap.put(beanFactory.getBeanDefinition(beanName).getBeanClassName(),
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
    public void validateAnnotatedArguments(JoinPoint joinPoint) throws NoSuchMethodException {
        System.out.println("...Start validation annotated arguments...");
        final Annotation[][] allArgsAnnotations = getAllParametersAnnotations(joinPoint);
        IntStream.iterate(0, argPosition -> argPosition++).limit(allArgsAnnotations.length)
                .forEach(argPosition ->
                        validateArgument(joinPoint.getArgs()[argPosition],
                                getValidationAnnotations(allArgsAnnotations[argPosition])));
    }

    private void validateArgument(Object argument, List<Validate> validationAnnotations) {
        getValidators(validationAnnotations)
                .forEach(validator -> validator.validate(argument));
    }

    private List<EntityValidator> getValidators(List<Validate> validationAnnotations) {
        return validationAnnotations.stream()
                .flatMap(annotation -> Arrays.stream(annotation.value()))
                .map(Class::getName)
                .map(validatorClassBeanNameMap::get)
                .filter(not(StringUtils::isEmpty))
                .map(beanName -> beanFactory.getBean(beanName, EntityValidator.class))
                .collect(toList());
    }

    private List<Validate> getValidationAnnotations(Annotation[] annotations) {
        return Arrays.stream(annotations)
                .filter(annotation -> Validate.class.isAssignableFrom(annotation.annotationType()))
                .map(Validate.class::cast)
                .collect(toList());
    }

    private Annotation[][] getAllParametersAnnotations(JoinPoint methodJoinPoint) throws NoSuchMethodException {
        MethodSignature signature = (MethodSignature) methodJoinPoint.getSignature();
        String methodName = signature.getMethod().getName();
        Class<?>[] parameterTypes = signature.getMethod().getParameterTypes();
        return methodJoinPoint.getTarget().getClass().getMethod(methodName, parameterTypes).getParameterAnnotations();
    }
}
