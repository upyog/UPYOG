package org.egov.filemgmnt.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import lombok.extern.slf4j.Slf4j;

@Disabled
@Slf4j
@TestMethodOrder(OrderAnnotation.class)
class LearningTest {

    @Test
    @Order(1)
    void firstTest() {
        LOG.info("firstTest()");
    }

    @ParameterizedTest
    @Order(2)
    @MethodSource("org.egov.filemgmnt.archunit.JavaClassesResolver#javaClassFiles")
    void parameterizedTest(JavaClasses javaClasses) {
        javaClasses.forEach(clazz -> LOG.info("Parameter: {}", clazz.getFullName()));
    }

    @Test
    @Order(3)
    @ExtendWith(JavaClassesResolver.class) // can be at class level
    void parameterResolverTest(JavaClasses javaClasses) {
        javaClasses.forEach(clazz -> LOG.info("Resolver: {}", clazz.getFullName()));
    }

}
