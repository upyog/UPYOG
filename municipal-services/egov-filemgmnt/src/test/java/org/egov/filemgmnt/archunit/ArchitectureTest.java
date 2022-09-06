package org.egov.filemgmnt.archunit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.tngtech.archunit.library.Architectures;
import com.tngtech.archunit.library.dependencies.SliceAssignment;
import com.tngtech.archunit.library.dependencies.SliceIdentifier;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;

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
                                              "..filemgmnt.common.exception",
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
                     .consideringOnlyDependenciesInLayers()
                     .ensureAllClassesAreContainedInArchitecture()
                     .withOptionalLayers(true)

                     // layers
                     .layer("App")
                     .definedBy("..filemgmnt", "..filemgmnt.config")

                     .layer("API")
                     .definedBy("..filemgmnt.api..")

                     .optionalLayer("Business")
                     .definedBy("..filemgmnt.business..")

                     .optionalLayer("Store")
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
    void cyclicCheckRule(JavaClasses javaClasses) {
        SlicesRuleDefinition.slices().assignedFrom(new SliceAssignment() {

            @Override
            public String getDescription() {
                return "org.egov.filemgmnt package";
            }

            @Override
            public SliceIdentifier getIdentifierOf(JavaClass javaClass) {
                String pckgName = javaClass.getPackageName();

                if (pckgName.matches(".*(filemgmnt.api).*")) {
                    return SliceIdentifier.of("Api");
                } else if (pckgName.matches(".*(filemgmnt.business).*")) {
                    return SliceIdentifier.of("Business");
                } else if (pckgName.matches(".*(filemgmnt.store).*")) {
                    return SliceIdentifier.of("Store");
                }

                return SliceIdentifier.ignore();
            }
        }).should().beFreeOfCycles().check(javaClasses);

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
