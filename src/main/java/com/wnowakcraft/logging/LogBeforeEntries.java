package com.wnowakcraft.logging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation being used to define multiple {@link LogBefore} annotations on a single method.
 * This annotation can be used explicitly and implicitly, as Java will use it by default when it finds more than one
 * {@link LogBefore} annotation on a specific method.
 * For the sake of code readability, the recommended way is to use it implicitly wherever possible.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LogBeforeEntries {
    /**
     * Defines an array of {@link LogBefore} annotations.
     *
     * @return array of {@link LogBefore} annotations
     * */
    LogBefore[] value();
}
