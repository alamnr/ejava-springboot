package info.ejava.examples.app.testing.testbasics.mockito;

import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Tag("mocks")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("map")
public class ExampleMockitoTest {
    
    @Mock  // creating a mock to configure for use in each test
    private Map<String,String> mapMock;

    @Captor
    private ArgumentCaptor<String> stringArgCaptor;

    @Test
    void listMap() {

        // when()/then() define custom conditions and responses for mock within scope of test
        // define behavior of mock during test
        when(mapMock.get(stringArgCaptor.capture()))  // capture String argument
                .thenReturn("springBoot","testing");

        // alternate syntax
        //doReturn("springBoot","testing").when(mapMock.get(stringArgCaptor.capture()));

        // conduct test
        int size = mapMock.size();
        String secret1 = mapMock.get("happiness");
        String secret2 = mapMock.get("joy");


        // evaluate the result
        verify(mapMock).size(); // verify  called once
        verify(mapMock, times(2)).get(anyString()); // verify called twice

        // verify what was given to mock
        // getValue()/getAllValues() can be called on the captor to obtain value(s) passed to the mock
        Assertions.assertThat(stringArgCaptor.getAllValues().get(0)).isEqualTo("happiness");
        Assertions.assertThat(stringArgCaptor.getAllValues().get(1)).isEqualTo("joy");
        
        // verify what was returned by mock
        // verify() can be called to verify what was called of the mock
        Assertions.assertThat(size).as("unexpected size").isZero();
        Assertions.assertThat(secret1).as("unexpected first result").isEqualTo("springBoot");
        Assertions.assertThat(secret2).as("unexpected second result").isEqualTo("testing");

        // mapMock.size() returned 0 while mapMock.get() returned values. We defined behavior
        // for mapMock.get() but left other interface methods in their default, "nice mock" state.
    }

    @Test
    void listMap_no_capture() {
        // define behavior of mock during test
        when(mapMock.get(anyString())) // not capturing input
            .thenReturn("springBoot", "testing");
        
        // conduct test
        int size = mapMock.size();
        String secret1 = mapMock.get("happiness");
        String secret2 = mapMock.get("joy");

        // evaluate result
        verify(mapMock).size(); // verify that mapMock.size() called once
        verify(mapMock, times(2)).get(anyString()); // verify called twice

        // verify what was returned by mock
        Assertions.assertThat(size).as("unexpected size").isZero();
        Assertions.assertThat(secret1).as("unexpected first result").isEqualTo("springBoot");
        Assertions.assertThat(secret2).as("unexpected 2nd result").isEqualTo("testing");

    }

    static class Caller {
        void businessMethod(Map<String,String> answers) {
            // ... 
            answers.get("hapiness");
            answers.get("joy");
            // ...
        }
    }

    @Test
    void listMap_used_in_context() {
        // stub / define behavior of mock during test
        when(mapMock.get(anyString())) // not capturing input
            .thenReturn("springboot","testing");
        
        Caller subject = new Caller();
        
        // conduct test
        subject.businessMethod(mapMock);

        // evaluate result
        verify(mapMock,times(0)).size(); // verify mapMock.size() not called
        verify(mapMock, times(2)).get(anyString()); // verify called twice
    }

    @Nested 
    @Tag("bdd")
    public class when_has_key {
        @Test 
        public void returns_value() {
            // given
            BDDMockito.given(mapMock.get(stringArgCaptor.capture()))  // capturing input
                .willReturn("springboot","testing");
            
            // when
            int size = mapMock.size();
            String secret1 = mapMock.get("happiness");
            String secret2 = mapMock.get("joy");

             //then - can use static import for BDDMockito or BDDAssertions, not both
             BDDMockito.then(mapMock).should().size(); // verify called once
             BDDMockito.then(mapMock).should(times(2)).get(anyString());  // verify called twice

             //and.then requires aspectj-core 3.14.0
             BDDAssertions.and.then(stringArgCaptor.getAllValues().get(0)).isEqualTo("happiness");
             BDDAssertions.and.then(stringArgCaptor.getAllValues().get(1)).isEqualTo("joy");
             BDDAssertions.and.then(size).as("unexpevted size").isZero();
             BDDAssertions.and.then(secret1).as("unexpected first result").isEqualTo("springboot");
             BDDAssertions.and.then(secret2).as("unexpected second result").isEqualTo("testing");
        }
    }
}
