/**
 * Point of sale: shifts, cart, sale completion, payments and refunds.
 * <p>
 * Sale completion is the one place that spans catalog (pricing) and
 * inventory (stock movements) inside a single atomic transaction — it does
 * so through those modules' public APIs, never their internals.
 */
@org.springframework.modulith.ApplicationModule(
        allowedDependencies = {"catalog", "inventory"}
)
package com.markman.pos;
