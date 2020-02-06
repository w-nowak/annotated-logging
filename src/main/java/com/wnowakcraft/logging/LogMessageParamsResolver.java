package com.wnowakcraft.logging;

import lombok.RequiredArgsConstructor;
import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlException;
import org.apache.commons.jexl3.MapContext;

import java.util.LinkedList;
import java.util.Optional;
import java.util.regex.Pattern;

@RequiredArgsConstructor
class LogMessageParamsResolver {
    private static final String PARAM_START = "\\{";
    private static final String ANY_SPACES = "\\s*";
    private static final String PARAM_OR_RETURN_VALUE_MARKER = "((?:p\\d+|r)";
    private static final String ANY_MEMBER_EXPRESSION = "(\\.[\\w\\[\\]\\(\\)]+)*)";
    private static final String PARAM_END = "}";
    private static final Pattern MESSAGE_PARAMS_PATTERN = Pattern.compile(
            PARAM_START + ANY_SPACES + PARAM_OR_RETURN_VALUE_MARKER + ANY_MEMBER_EXPRESSION + ANY_SPACES + PARAM_END
    );
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
                contextParams.set(PARAM + i, getValueFrom(expressionContextParams[i]));
            }

            if(result != null) {
                contextParams.set(RESULT, getValueFrom(result));
            }
        }

        private static Object getValueFrom(Object result) {
            return result instanceof Optional ?
            ((Optional)result).orElse(null) : result;
        }

        Object resolve(String expression) {
            return evaluateWithPossibleNullValues(expression);
        }

        private Object evaluateWithPossibleNullValues(String expression) {
            var jexlExpression = new JexlBuilder().create().createExpression(expression.trim());

            try {
                return jexlExpression.evaluate(contextParams);
            } catch (JexlException.Variable ex) {
                if(ex.isUndefined() || ex.getMessage().contains("null value variable")) {
                    return "<no_value>";
                }

                throw ex;
            }
        }
    }
}
