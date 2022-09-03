package org.egov.filemgmnt.archunit;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.tngtech.archunit.library.Architectures;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(JavaClassesResolver.class)
class ArchitectureTest {

    @Test
    void packagesRule(JavaClasses javaClasses) {
        ArchRuleDefinition.classes()
                          .should()
                          .resideInAnyPackage("..filemgmnt",

                                              "..filemgmnt.api.controllers",
                                              "..filemgmnt.api.models",

                                              "..filemgmnt.business.data",
                                              "..filemgmnt.business.service",

                                              "..filemgmnt.common.enums",
                                              "..filemgmnt.common.mappers",
                                              "..filemgmnt.common.utils",

                                              "..filemgmnt.config",

                                              "..filemgmnt.messaging.consumer",
                                              "..filemgmnt.messaging.models",
                                              "..filemgmnt.messaging.producer",

                                              "..filemgmnt.store.entities",
                                              "..filemgmnt.store.repo",
                                              "..filemgmnt.store.views")
                          .check(javaClasses);
    }

    @Test
    void layerAccessRule(JavaClasses javaClasses) {
        Architectures.layeredArchitecture()
                     .consideringAllDependencies()
                     .ensureAllClassesAreContainedInArchitecture()
                     .withOptionalLayers(true)

                     // layers
                     .layer("App")
                     .definedBy("..filemgmnt")

                     .layer("API")
                     .definedBy("..filemgmnt.api..")

                     .layer("Business")
                     .definedBy("..filemgmnt.business..")

                     .layer("Store")
                     .definedBy("..filemgmnt.store..")

                     .layer("Common")
                     .definedBy("..filemgmnt.common..")

                     // access rules
                     .whereLayer("API")
                     .mayOnlyAccessLayers("Business", "Common")

                     .whereLayer("Business")
                     .mayOnlyAccessLayers("Store", "Common")

                     // accessed by rules
                     .whereLayer("API")
                     .mayNotBeAccessedByAnyLayer()

                     .whereLayer("Business")
                     .mayOnlyBeAccessedByLayers("API", "Common")

                     .whereLayer("Store")
                     .mayOnlyBeAccessedByLayers("Business", "Common")

                     .check(javaClasses);
    }

    @Test
    void noDependencyRule(JavaClasses javaClasses) {
        ArchRuleDefinition.noClasses()
                          .that()
                          .resideInAPackage("..filemgmnt..")
                          .should()
                          .dependOnClassesThat(new DescribedPredicate<JavaClass>(
                                  "egov-filemgmnt should not depend on classes that resides...") {
                              @Override
                              public boolean test(JavaClass input) {
//                                  String pckgName = input.getPackageName();
//                                  String fullName = input.getFullName();

                                  return false;
                              }
                          })
                          .check(javaClasses);
    }
}
