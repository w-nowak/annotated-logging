package com.wnowakcraft.logging;

import java.lang.annotation.*;

/**
 * Annotation being used to log after the execution of the annotated method.
 * It can be only put at method level, and it's repeatable.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(LogAfterEntries.class)
public @interface LogAfter {
    /**
     * Defines a message to be logged by the log statement.
     * Message can also contain expression(s) referencing either input parameters or return value
     * enclosed by curly brackets: <pre>{expression_here}</pre>.
     *
     * @return the log message
     * */
    String value();

    /**
     * Defines a severity level of the log statement.
     *
     * @return the severity level
     * */
    Level level() default Level.DEBUG;
}
