package com.markman;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

/**
 * Verifies that the module boundaries declared via {@code package-info.java}
 * (core, catalog, inventory, pos, employee, reporting) are respected —
 * fails the build on any illegal cross-module dependency.
 */
class ModularityTests {

    static final ApplicationModules modules = ApplicationModules.of(MarkManApplication.class);

    @Test
    void verifiesModularStructure() {
        modules.verify();
    }

    // Module diagram/doc generation (spring-modulith-docs) is intentionally
    // left out of Phase 1 — add it once there's enough module content
    // (beyond package-info.java) for a diagram to be worth generating.
}
