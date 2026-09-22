/**
 * Reporting: read-only queries over authoritative transaction records from
 * every other module (sales, payments, inventory movements, shifts, catalog).
 * <p>
 * This module is a deliberate, documented exception to normal module
 * boundaries: it reads other modules' tables directly via its own read-only
 * SQL rather than going through their public APIs, because a reporting
 * query typically joins across several of them. It must never write to
 * another module's tables.
 */
@org.springframework.modulith.ApplicationModule(
        allowedDependencies = {"catalog", "inventory", "pos", "employee"}
)
package com.markman.reporting;
