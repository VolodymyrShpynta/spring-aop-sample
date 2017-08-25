package com.vshpynta.sample.aspect;

import com.vshpynta.sample.model.annotations.Validate;
import com.vshpynta.sample.validation.EntityValidator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.vshpynta.sample.utils.PredicateUtils.not;

/**
 * Created by vshpynta on 22/08/2017.
 */
@Component
public class ValidateAnnotationBeanPostProcessor implements BeanFactoryPostProcessor, BeanPostProcessor {

    private Map<String, Class> beanNameClassMap = new HashMap<>();
    private Map<String, String> validatorsClassNameMap = new HashMap<>();

    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        for (String beanName : beanFactory.getBeanNamesForType(EntityValidator.class)) {
            validatorsClassNameMap.put(beanFactory.getBean(beanName).getClass().getName(),
                    beanName);
        }
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        if (isValidateParamAnnotationPresent(beanClass)) {
            beanNameClassMap.put(beanName, beanClass);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return Optional.ofNullable(beanNameClassMap.get(beanName))
                .map(beanClass -> proxyTargetBean(bean, beanClass))
                .orElse(bean);
    }

    private Object proxyTargetBean(Object bean, Class beanClass) {
        return Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(),
                beanClass.getInterfaces(),
                (proxy, method, args) -> { //Method InvocationHandler
                    validateAnnotatedArguments(beanClass, method, args);
                    return method.invoke(bean, args);
                });
    }

    private void validateAnnotatedArguments(Class beanClass, Method method, Object[] args) {
        final Map<Integer, Validate> annotations = getPositionAnnotationMap(beanClass, method, Validate.class);
        for (Map.Entry<Integer, Validate> entry : annotations.entrySet()) {
            for (Class<? extends EntityValidator> validatorClass : entry.getValue().value()) {
                final String beanName = validatorsClassNameMap.get(validatorClass.getName());
                System.out.println("Invoke argument validator: " + beanName);
                beanFactory.getBean(beanName, EntityValidator.class)
                        .validate(args[entry.getKey()]);
            }
        }
    }

    private boolean isValidateParamAnnotationPresent(Class<?> beanClass) {
        return Arrays.stream(beanClass.getMethods())
                .map(method -> getPositionAnnotationMap(beanClass, method, Validate.class))
                .filter(not(Map::isEmpty))
                .findAny()
                .isPresent();
    }

    private <T extends Annotation> Map<Integer, T> getPositionAnnotationMap(Class beanClass, Method method, Class<T> annotationClass) {
        final Annotation[][] parametersAnnotations = getParameterAnnotations(beanClass, method);
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

    private Annotation[][] getParameterAnnotations(Class beanClass, Method method) {
        try {
            String methodName = method.getName();
            Class<?>[] parameterTypes = method.getParameterTypes();
            return beanClass.getMethod(methodName, parameterTypes).getParameterAnnotations();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
