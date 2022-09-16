package org.egov.filemgmnt.archunit;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.core.importer.Location;

class JavaClassesResolver implements ParameterResolver {

    private static JavaClasses javaClasses;

    static {
        javaClasses = new ClassFileImporter()//
                                             .withImportOption(new ImportOption() {
                                                 @Override
                                                 public boolean includes(Location location) {
                                                     return !location.contains("/test-classes/");
                                                 }
                                             })
                                             .importPackages("org.egov.filemgmnt");
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
            ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType() == JavaClasses.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext,
            ExtensionContext extensionContext) {
        return javaClasses;
    }
}
