package com.wnowakcraft.logging;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class LogMessageParamsResolverTest {
    private static final Object NO_RETURN_VALUE = null;
    private static final TestPerson TEST_PERSON_1 = new TestPerson("testPerson1");
    private static final TestPerson TEST_PERSON_2 = new TestPerson("testPerson2");
    private static final int TEST_PERSON_2_INDEX = 1;
    private static final List<TestPerson> TEST_TWO_PERSONS = List.of(TEST_PERSON_1, TEST_PERSON_2);
    private static final int TEST_PERSON_1_INDEX = 0;

    @Test
    void correctlyResolvesParamsFromMessageTemplate_withNoReturnValue() {

        String messageTemplate = "This name: {p0.getName()} is same as {p0.name} but different than {p1.getSuggested(p2)}";
        var logMessageParamsResolver= new LogMessageParamsResolver(messageTemplate);

        Object[] resolvedParams = logMessageParamsResolver.getParamsReferredInTemplate(
                    new Object[] { TEST_PERSON_1, new TestSuggestions(TEST_TWO_PERSONS), TEST_PERSON_2_INDEX }, NO_RETURN_VALUE);

        assertThat(resolvedParams).hasSize(3);
        assertThat(resolvedParams[0]).isEqualTo(TEST_PERSON_1.getName());
        assertThat(resolvedParams[1]).isEqualTo(TEST_PERSON_1.getName());
        assertThat(resolvedParams[2]).isEqualTo(TEST_PERSON_2);
    }

    @Test
    void correctlyResolvesParamsFromMessageTemplate_withReturnValue_whenReferredInTemplate() {
        String messageTemplate = "This name: {p0.getName()} is different than {p1.getSuggested(p2)}, result = {r.getName()}";
        var logMessageParamsResolver= new LogMessageParamsResolver(messageTemplate);

        Object[] resolvedParams = logMessageParamsResolver.getParamsReferredInTemplate(
                new Object[] { TEST_PERSON_1, new TestSuggestions(TEST_TWO_PERSONS), TEST_PERSON_1_INDEX }, TEST_PERSON_2);

        assertThat(resolvedParams).hasSize(3);
        assertThat(resolvedParams[0]).isEqualTo(TEST_PERSON_1.getName());
        assertThat(resolvedParams[1]).isEqualTo(TEST_PERSON_1);
        assertThat(resolvedParams[2]).isEqualTo(TEST_PERSON_2.getName());
    }

    @Test
    void correctlyResolvesParamsFromMessageTemplate_withNoReturnValue_whenNotReferredInTemplate() {
        var returnValue = "resultValue";

        String messageTemplate = "This name: { p0.getName() } is different than {p1.getSuggested(p2)}";
        var logMessageParamsResolver= new LogMessageParamsResolver(messageTemplate);

        Object[] resolvedParams = logMessageParamsResolver.getParamsReferredInTemplate(
                new Object[] { TEST_PERSON_2, new TestSuggestions(TEST_TWO_PERSONS), TEST_PERSON_1_INDEX }, returnValue);

        assertThat(resolvedParams).hasSize(2);
        assertThat(resolvedParams[0]).isEqualTo(TEST_PERSON_2.getName());
        assertThat(resolvedParams[1]).isEqualTo(TEST_PERSON_1);
    }

    @Test
    void correctlyResolvesParamsFromMessageTemplate_withNullableReturnValue_whenReferredInTemplateButNull() {
        String messageTemplate = "Input: {p0}, Result = { r.getName() }";
        var logMessageParamsResolver= new LogMessageParamsResolver(messageTemplate);

        Object[] resolvedParams = logMessageParamsResolver.getParamsReferredInTemplate(
                new Object[] {TEST_PERSON_1 }, NO_RETURN_VALUE);

        assertThat(resolvedParams).hasSize(2);
        assertThat(resolvedParams[0]).isEqualTo(TEST_PERSON_1);
        assertThat(resolvedParams[1]).isEqualTo("<no_value>");
    }

    @Test
    void correctlyResolvesParamsFromMessageTemplate_withNullableReturnValue_whenReferredInTemplateButEmptyOptional() {
        String messageTemplate = "Input: {p1}, Result = { r.getName() }";
        var logMessageParamsResolver= new LogMessageParamsResolver(messageTemplate);

        Object[] resolvedParams = logMessageParamsResolver.getParamsReferredInTemplate(
                new Object[] {TEST_PERSON_1, TEST_PERSON_2_INDEX }, Optional.empty());

        assertThat(resolvedParams).hasSize(2);
        assertThat(resolvedParams[0]).isEqualTo(TEST_PERSON_2_INDEX);
        assertThat(resolvedParams[1]).isEqualTo("<no_value>");
    }

    @Test
    void correctlyResolvesParamsFromMessageTemplate_withOptionalParamsAndReturnValue_OptionalValueIsExtracted() {
        String messageTemplate = "Input: {p0.getName()}, {p1.getName()}, {p0}, Result = { r.getName() }";
        var logMessageParamsResolver= new LogMessageParamsResolver(messageTemplate);
        var optionalParam = Optional.of(TEST_PERSON_1);
        var optionalEmptyParam = Optional.empty();
        var optionalResult = Optional.of(TEST_PERSON_2);

        Object[] resolvedParams = logMessageParamsResolver.getParamsReferredInTemplate(
                new Object[] { optionalParam, optionalEmptyParam }, optionalResult);

        assertThat(resolvedParams).hasSize(4);
        assertThat(resolvedParams[0]).isEqualTo(TEST_PERSON_1.getName());
        assertThat(resolvedParams[1]).isEqualTo("<no_value>");
        assertThat(resolvedParams[2]).isEqualTo(TEST_PERSON_1);
        assertThat(resolvedParams[3]).isEqualTo(TEST_PERSON_2.getName());
    }

    @Test
    void doesntResolveAnyParamsIfMessageIsNotTemplate() {
        String messageTemplate = "This is just plain message with no params";
        var logMessageParamsResolver= new LogMessageParamsResolver(messageTemplate);

        Object[] resolvedParams = logMessageParamsResolver.getParamsReferredInTemplate(
                new Object[] { "some string",  2, new Object(), 3 }, NO_RETURN_VALUE);

        assertThat(resolvedParams).isEmpty();
    }

    @Test
    void doesntResolveAnyParamsIfMessageTemplateHasNoParameters() {
        String messageTemplate = "This is just plain template {} with no params {}";
        var logMessageParamsResolver= new LogMessageParamsResolver(messageTemplate);

        Object[] resolvedParams = logMessageParamsResolver.getParamsReferredInTemplate(
                new Object[] { "some string",  2, new Object() }, NO_RETURN_VALUE);

        assertThat(resolvedParams).isEmpty();
    }

    @Getter
    @RequiredArgsConstructor
    public static class TestPerson {
        private final String name;
    }

    @Getter
    @RequiredArgsConstructor
    public static class TestSuggestions {
        private final List<TestPerson> suggestions;

        public TestPerson getSuggested(short index) {
            return suggestions.get(index);
        }
    }

}