/**
 * Shared kernel: tenancy, identity, security, module configuration and audit.
 * <p>
 * This module is OPEN — every other module may depend on it without being
 * listed as an allowed dependency. It must never depend on any other module.
 */
@org.springframework.modulith.ApplicationModule(
        type = org.springframework.modulith.ApplicationModule.Type.OPEN
)
package com.markman.core;
