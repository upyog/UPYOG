package org.egov.filemgmnt.archunit;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.tngtech.archunit.library.Architectures;
import com.tngtech.archunit.library.dependencies.SliceAssignment;
import com.tngtech.archunit.library.dependencies.SliceIdentifier;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;

@Disabled
@ExtendWith(JavaClassesResolver.class)
@SuppressWarnings({ "PMD.JUnitTestsShouldIncludeAssert", "PMD.AvoidDuplicateLiterals" })
class ArchitectureTests {

    @Test
    void packagesRule(JavaClasses javaClasses) {
        ArchRuleDefinition.classes()
                          .should()
                          .resideInAnyPackage("..filemgmnt",
                                              "..filemgmnt.config",
                                              "..filemgmnt.enrichment",
                                              "..filemgmnt.kafka",

                                              "..filemgmnt.repository",
                                              "..filemgmnt.repository.querybuilder",
                                              "..filemgmnt.repository.rowmapper",

                                              "..filemgmnt.service",
                                              "..filemgmnt.util",
                                              "..filemgmnt.validators",

                                              "..filemgmnt.web.controllers",
                                              "..filemgmnt.web.models")
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

                     .optionalLayer("Web")
                     .definedBy("..filemgmnt.web..")

                     .optionalLayer("Business")
                     .definedBy("..filemgmnt.service",
                                "..filemgmnt.enrichment",
                                "..filemgmnt.validators")

                     .optionalLayer("Store")
                     .definedBy("..filemgmnt.repository..", "..filemgmnt.kafka")

                     .layer("Common")
                     .definedBy("..filemgmnt.util")

                     // access rules
                     .whereLayer("App")
                     .mayNotAccessAnyLayer()

                     .whereLayer("Web")
                     .mayOnlyAccessLayers("Business", "Common", "App")

                     .whereLayer("Business")
                     .mayOnlyAccessLayers("Store", "Common", "App")

                     .whereLayer("Store")
                     .mayOnlyAccessLayers("Common", "App")

                     // accessed by rules
                     .whereLayer("Web")
                     .mayOnlyBeAccessedByLayers("Web", "Common", "Business", "Store")

                     .whereLayer("Business")
                     .mayOnlyBeAccessedByLayers("Business", "Web", "Common")

                     .whereLayer("Store")
                     .mayOnlyBeAccessedByLayers("Store", "Business", "Common")

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
                SliceIdentifier sliceIdentifier = SliceIdentifier.ignore();

                if (pckgName.matches(".*(filemgmnt.web).*")) {
                    sliceIdentifier = SliceIdentifier.of("Web");
                } else if (pckgName.matches(".*(filemgmnt.service).*")
                        || pckgName.matches(".*(filemgmnt.enrichment).*")
                        || pckgName.matches(".*(filemgmnt.validators).*")) {
                    sliceIdentifier = SliceIdentifier.of("Business");
                } else if (pckgName.matches(".*(filemgmnt.repository).*")
                        || pckgName.matches(".*(filemgmnt.kafka).*")) {
                    sliceIdentifier = SliceIdentifier.of("Store");
                }

                return sliceIdentifier;
            }
        }).should().beFreeOfCycles().check(javaClasses);

    }

//    @Test
//    void noDependencyRule(JavaClasses javaClasses) {
//        ArchRuleDefinition.noClasses()
//                          .that()
//                          .resideInAPackage("..filemgmnt..")
//                          .should()
//                          .dependOnClassesThat(new DescribedPredicate<JavaClass>(
//                                  "egov-filemgmnt should not depend on classes that resides...") {
//                              @Override
//                              public boolean test(JavaClass input) {
////                                  String pckgName = input.getPackageName();
////                                  String fullName = input.getFullName();
//
//                                  return false;
//                              }
//                          })
//                          .check(javaClasses);
//    }
}
