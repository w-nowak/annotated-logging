package com.wnowakcraft.logging;

import lombok.RequiredArgsConstructor;
import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.MapContext;

import java.util.LinkedList;
import java.util.regex.Pattern;

@RequiredArgsConstructor
class LogMessageParamsResolver {
    private static final Pattern MESSAGE_PARAMS_PATTERN = Pattern.compile("\\{(p\\d+(\\.[\\w\\[\\]\\(\\)]+)*)}");
    private final String messageTemplate;

    static LogMessageParamsResolver forMessageTemplate(String messageTemplate) {
        return new LogMessageParamsResolver(messageTemplate);
    }

    Object[] getParamsReferredInTemplate(Object[] contextParams) {

        var expressionResolver = new ExpressionResolver(contextParams);
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
        private final MapContext contextParams = new MapContext();;

        ExpressionResolver(Object[] expressionContextParams) {
            for(var i = 0; i< expressionContextParams.length; i++) {
                contextParams.set("p" + i, expressionContextParams[i]);
            }
        }

        Object resolve(String expression) {
            var jexlExpression = new JexlBuilder().create().createExpression(expression);
            return jexlExpression.evaluate(contextParams);
        }
    }
}
