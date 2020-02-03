package com.wnowakcraft.logging;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(LogAfterEntries.class)
public @interface LogAfter {
    String value();
    Level level() default Level.INFO;
}
