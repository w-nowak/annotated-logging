package com.wnowakcraft.logging;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(LogBeforeEntries.class)
public @interface LogBefore {
    String value();
    Level level() default Level.DEBUG;
}
