package com.markman;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Project-specific architecture rules beyond what Spring Modulith checks.
 * These enforce the tenant-isolation and security guarantees from
 * ARCHITECTURE.md, not just package structure.
 * <p>
 * Deliberately empty of concrete rules in Phase 1 — there are no
 * controllers, DTOs or entities yet. Phase 2 adds the two rules this class
 * is here for:
 * <ul>
 *   <li>no request DTO may declare an {@code organizationId} field</li>
 *   <li>no service method that mutates tenant-owned data may be public
 *       without an authorization annotation</li>
 * </ul>
 */
class ArchitectureTests {

    private static final ClassFileImporter importer =
            new ClassFileImporter().withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS);

    @Test
    void placeholderUntilPhase2AddsDomainClasses() {
        ArchRule noOp = noClasses().should().dependOnClassesThat().resideInAPackage("this.package.does.not.exist");
        noOp.check(importer.importPackages("com.markman"));
    }
}
