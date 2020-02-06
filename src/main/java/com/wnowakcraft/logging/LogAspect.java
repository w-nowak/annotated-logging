package com.wnowakcraft.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

import static java.lang.reflect.Modifier.isStatic;

@Aspect
public class LogAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogAspect.class);
    private static final String LOG_FIELD_NAME = "log";

    @Pointcut("execution(* *(..))")
    public static void anyMethod() { }

    @Pointcut("execution(!void *(..))")
    public static void anyMethodReturningSomeResult() { }

    @Pointcut("execution(void *(..))")
    public static void anyMethodReturningNoResult() { }

    @Before("anyMethod() && @annotation(logBefore)")
    public void logBefore(LogBefore logBefore, JoinPoint joinPoint) {
        doLog(logBefore.value(), logBefore.level(), joinPoint);
    }

    @After("anyMethodReturningNoResult() && @annotation(logAfter)")
    public void logAfter(LogAfter logAfter, JoinPoint joinPoint) {
        doLog(logAfter.value(), logAfter.level(), joinPoint);
    }

    @AfterReturning(value = "anyMethodReturningSomeResult() && @annotation(logAfter)", returning = "result")
    public void logAfterResult(LogAfter logAfter, JoinPoint joinPoint, Object result) {
        doLogWithResult(logAfter.value(), logAfter.level(), joinPoint, result);
    }

    @Before("anyMethod() && @annotation(logBeforeEntries)")
    public void logBeforeEntries(LogBeforeEntries logBeforeEntries, JoinPoint joinPoint) {
        for(LogBefore logBefore : logBeforeEntries.value()) {
            doLog(logBefore.value(), logBefore.level(), joinPoint);
        }
    }

    @After("anyMethodReturningNoResult() && @annotation(logAfterEntries)")
    public void logAfterEntries(LogAfterEntries logAfterEntries, JoinPoint joinPoint) {
        for(LogAfter logAfter : logAfterEntries.value()) {
            doLog(logAfter.value(), logAfter.level(), joinPoint);
        }
    }

    @AfterReturning(value = "anyMethodReturningSomeResult() && @annotation(logAfterEntries)", returning = "result")
    public void logAfterEntriesResult(LogAfterEntries logAfterEntries, JoinPoint joinPoint, Object result) {
        for(LogAfter logAfter : logAfterEntries.value()) {
            doLogWithResult(logAfter.value(), logAfter.level(), joinPoint, result);
        }
    }

    private void doLog(String logMessageTemplate, Level level, JoinPoint joinPoint) {
        Object noResult = null;
        doLogWithResult(logMessageTemplate, level, joinPoint, noResult);
    }

    private void doLogWithResult(String logMessageTemplate, Level level, JoinPoint joinPoint, Object result) {
        try {

            var logger = getLogger(joinPoint.getTarget());

            var logMessageParamsResolver = LogMessageParamsResolver.forMessageTemplate(logMessageTemplate);
            Object[] logMessageParams = logMessageParamsResolver.getParamsReferredInTemplate(joinPoint.getArgs(), result);
            String cleanLogMessagePattern = logMessageParamsResolver.getCleanLogMessageTemplate();

            level.log(logger, cleanLogMessagePattern, logMessageParams);
        } catch (Exception ex) {
            LOGGER.warn("Couldn't logBefore annotate logBefore message. Reason: " + ex.getMessage(), ex);
        }
    }

    private static Logger getLogger(Object target) throws Exception {
        Field logField = target.getClass().getDeclaredField(LOG_FIELD_NAME);

        Logger logger = FieldAccessor.getStaticField(logField);

        if(logger == null) {
            logger = FieldAccessor.getInstanceField(logField, target);
        }

        return logger;
    }

    private static class FieldAccessor {
        static <T> T getStaticField(Field field) throws Exception {
            if(!isStatic(field.getModifiers())){
                return null;
            }

            if(!field.canAccess(null)) {
                field.setAccessible(true);
            }

            Object fromStaticContext = null;
            return getField(field, fromStaticContext);
        }

        static <T> T getInstanceField(Field field, Object target) throws Exception {
            if(!field.canAccess(null)) {
                field.setAccessible(true);
            }
            return getField(field, target);
        }

        @SuppressWarnings("unchecked")
        private static <T> T getField(Field field, Object target) throws Exception {
            return (T)field.get(target);
        }
    }
}
