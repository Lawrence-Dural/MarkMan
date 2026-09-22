/**
 * Inventory: append-only stock movement ledger and current stock balances
 * for inventory-tracked catalog items.
 */
@org.springframework.modulith.ApplicationModule(
        allowedDependencies = {"catalog"}
)
package com.markman.inventory;
