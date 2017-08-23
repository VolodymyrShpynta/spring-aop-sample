package com.vshpynta.sample.validation;

/**
 * Created by vshpynta on 23.08.17.
 */
public interface EntityValidator<T> {
    void validate(T entity);
}
