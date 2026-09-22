# Architecture

Reference for decisions made in Phase 0–2 planning. Update this as later
phases add or revise anything here — it should stay the source of truth,
not a historical log.

## Scope (V1)

Core Platform, Shared Catalog, POS & Payments, Inventory, Employees &
Shifts, Reporting. Not in V1: Client Management, Scheduling, multi-branch,
loyalty, native mobile, offline sync, e-commerce, payroll/HRIS, full
accounting, AI features, KDS, microservices, Kubernetes, Kafka, payment
gateway integrations.

## Module layout

```
com.markman
├─ core        OPEN — tenant, organization, user, role, security, audit
├─ catalog     categories, catalog items (PRODUCT / SERVICE)
├─ inventory   stock movement ledger + balances          (depends on catalog)
├─ pos         shift, cart, sale, payment, refund         (depends on catalog, inventory)
├─ employee    staff management                           (depends on core)
└─ reporting   read-only cross-module reporting queries    (documented exception)
```

Boundaries are declared in each module's `package-info.java` and enforced
by `ModularityTests`. `core` is OPEN — every module may depend on it
without being listed. `reporting` is a deliberate, documented exception:
it reads other modules' tables directly rather than through their public
APIs, because a report typically joins across several of them.

## Tenant isolation — four layers

1. **App layer.** `CurrentTenant` reads the organization strictly from the
   authenticated `SecurityContext` — never from a request, URL, form or
   DTO. Repositories expose organization-scoped methods only
   (`findByOrganizationIdAndId`, etc.), never a bare `findById`.
2. **Hibernate `@TenantId`.** `TenantOwnedEntity` (in `core.tenant`) is the
   base class for entities from Phase 4 onward — Hibernate stamps and
   filters `organization_id` automatically via
   `HibernateTenantIdentifierResolver`. **Not** used by `User` or `Role`:
   login must look up a user *before* there is an authenticated tenant for
   the resolver to return, so those two are isolated via layer 1 and
   layer 3 only. See their Javadoc.
3. **Database.** `organization_id NOT NULL` plus `UNIQUE (organization_id,
   id)` on every tenant table, so children use composite foreign keys —
   a row can't reference a parent row from a different organization.
4. **Row-Level Security.** Schema is RLS-ready; switched on in the Phase
   10 hardening pass, not from day one.

## Identity

- One identity for user and employee — no separate `Employee` table.
- Login is **organization code + username + password**, not email.
  Spring Security's filter machinery expects one username string, so
  `OrgAwareAuthenticationFilter` combines the two into one identifier
  before handing off to `MarkManUserDetailsService`, which splits them
  back apart. See `LoginIdentifier`'s Javadoc.
- Primary keys are UUIDv7 (`com.markman.core.Uuid7`), generated in
  application code, not by the database — time-ordered, so indexes don't
  fragment the way fully-random UUIDs would.
- 5 consecutive bad-password attempts locks the account for 15 minutes
  (`User.recordFailedLogin`). Tracked via Spring Security's
  `AuthenticationFailureBadCredentialsEvent`/`AuthenticationSuccessEvent`,
  not in the filter itself.
- **Not yet implemented:** invalidating a user's live sessions on role
  change, disable, or org suspension. Needs a `SessionRegistry`; deferred
  to Phase 10 hardening rather than built speculatively now.

## Roles and permissions

Five system roles (`OWNER`, `ADMIN`, `MANAGER`, `CASHIER`, `STAFF`), seeded
by `V004__core_role.sql` with `organization_id = null` — shared by every
organization. A non-null `organization_id` is schema room for custom
per-organization roles later; nothing creates one in V1. Permission codes
are a global reference table (`V003__core_permission.sql`), enforced with
`@PreAuthorize` on service methods — never just hiding a button.

## Migrations

Sequential, forward-only, one folder (`db/migration`). `ddl-auto=validate`
always. A Testcontainers-backed smoke test builds an empty database from
`V001` to latest on every run — see `SmokeTest`.

## Implementation order

Phase 0 architecture (done) → **Phase 1 scaffold (done)** →
**Phase 2 core identity & tenant isolation (this)** → Phase 3 staff
management, module config, audit → Phase 4 catalog → Phase 5 inventory →
Phase 6 shifts → Phase 7 POS sale → Phase 8 void/refund → Phase 9
reporting → Phase 10 hardening (RLS, DB roles, deployment) → Phase 11
pilot hardening.
