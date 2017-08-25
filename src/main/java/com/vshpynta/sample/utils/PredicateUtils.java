package com.vshpynta.sample.utils;

import lombok.experimental.UtilityClass;

import java.util.function.Predicate;

@UtilityClass
public class PredicateUtils {

    /**
     * This method allows to filter items if predicate returns {@code false}
     * Ex: {@code nodes.stream().filter(not(Node::isDeleted))...}
     */
    public static <T> Predicate<T> not(final Predicate<T> p) {
        return p.negate();
    }
}