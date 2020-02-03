package com.wnowakcraft.logging;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LogMessageParamsResolverTest {

    @Test
    void correctlyResolvesParamsFromMessageTemplate() {
        var testPerson1 = new TestPerson("testPerson1");
        var testPerson2 = new TestPerson("testPerson2");
        var testPerson2Index = 1;
        var testPersons = List.of(testPerson1, testPerson2);

        String messageTemplate = "This name: {p0.getName()} is same as {p0.name} but different than {p1.getSuggested(p2)}";
        var logMessageParamsResolver= new LogMessageParamsResolver(messageTemplate);

        Object[] resolvedParams = logMessageParamsResolver.getParamsReferredInTemplate(
                    new Object[] { testPerson1, new TestSuggestions(testPersons), testPerson2Index });

        assertThat(resolvedParams).hasSize(3);
        assertThat(resolvedParams[0]).isEqualTo(testPerson1.getName());
        assertThat(resolvedParams[1]).isEqualTo(testPerson1.getName());
        assertThat(resolvedParams[2]).isEqualTo(testPerson2);
    }

    @Test
    void doesntResolveAnyParamsIfMessageIsNotTemplate() {
        String messageTemplate = "This is just plain message with no params";
        var logMessageParamsResolver= new LogMessageParamsResolver(messageTemplate);

        Object[] resolvedParams = logMessageParamsResolver.getParamsReferredInTemplate(
                new Object[] { "some string",  2, new Object(), 3 });

        assertThat(resolvedParams).isEmpty();
    }

    @Test
    void doesntResolveAnyParamsIfMessageTemplateHasNoParameters() {
        String messageTemplate = "This is just plain template {} with no params {}";
        var logMessageParamsResolver= new LogMessageParamsResolver(messageTemplate);

        Object[] resolvedParams = logMessageParamsResolver.getParamsReferredInTemplate(
                new Object[] { "some string",  2, new Object() });

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