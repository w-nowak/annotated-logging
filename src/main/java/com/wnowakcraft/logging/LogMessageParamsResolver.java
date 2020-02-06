package com.wnowakcraft.logging;

import lombok.RequiredArgsConstructor;
import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.MapContext;

import java.util.LinkedList;
import java.util.Optional;
import java.util.regex.Pattern;

@RequiredArgsConstructor
class LogMessageParamsResolver {
    private static final Pattern MESSAGE_PARAMS_PATTERN = Pattern.compile("\\{\\s*((?:p\\d+|r)(\\.[\\w\\[\\]\\(\\)]+)*)\\s*}");
    private final String messageTemplate;

    static LogMessageParamsResolver forMessageTemplate(String messageTemplate) {
        return new LogMessageParamsResolver(messageTemplate);
    }

    Object[] getParamsReferredInTemplate(Object[] contextParams, Object result) {

        var expressionResolver = new ExpressionResolver(contextParams, result);
        var matcher = MESSAGE_PARAMS_PATTERN.matcher(messageTemplate);
        var resolvedParams = new LinkedList<>();

        while(matcher.find()) {
            var expression = matcher.group(1);
            resolvedParams.add(expressionResolver.resolve(expression));
        }

        return resolvedParams.toArray();
    }

    String getCleanLogMessageTemplate() {
        return messageTemplate.replaceAll(MESSAGE_PARAMS_PATTERN.pattern(), "{}");
    }

    static class ExpressionResolver {
        private static final String PARAM = "p";
        private static final String RESULT = "r";
        private final MapContext contextParams = new MapContext();

        ExpressionResolver(Object[] expressionContextParams, Object result) {
            for(var i = 0; i< expressionContextParams.length; i++) {
                contextParams.set(PARAM + i, expressionContextParams[i]);
            }

            if(result != null) {
                contextParams.set(RESULT, result);
            }
        }

        Object resolve(String expression) {
            if(isReturnValueReferredIn(expression) && isResultValueMissing()) {
                return "<no_value>";
            }

            var jexlExpression = new JexlBuilder().create().createExpression(expression.trim());
            return jexlExpression.evaluate(contextParams);
        }

        private static boolean isReturnValueReferredIn(String expression) {
            return expression.trim().startsWith(RESULT + ".");
        }

        private boolean isResultValueMissing() {
            Object result = contextParams.get(RESULT);
            return result == null || (result instanceof Optional && ((Optional)result).isEmpty());
        }
    }
}
