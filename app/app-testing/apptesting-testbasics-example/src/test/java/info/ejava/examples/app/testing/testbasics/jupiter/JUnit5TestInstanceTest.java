package info.ejava.examples.app.testing.testbasics.jupiter;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JUnit5TestInstanceTest {
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_METHOD)
    class StaticShared {
        private static final Logger log = LoggerFactory.getLogger("Static shared");
        private  static int staticState; // shared state <<< STATIC
        public StaticShared(){
            log.info("new instance");
        }

        @BeforeAll
        static void init(){
            log.info("state = {}", staticState++);
        }
        @Test
        void testA() {
            log.info("state = {}", staticState);
        }

        @Test
        void testB() {
            log.info("state = {}", staticState);
        }

    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class InstanceShared {
        private static final Logger log = LoggerFactory.getLogger("Instance Shared");
        private int instanceState; // shared instance <<< INSTANCE
        public InstanceShared() { log.info("new instance"); }
        @BeforeAll
        void init() {
            log.info("state={}", instanceState++);
        }
        @Test
        void testA() {
            log.info("state={}", instanceState);
        }
        @Test
        void testB() {
            log.info("state={}", instanceState);
        }


    }
}
