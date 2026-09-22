-- Phase 1 baseline.
-- Domain tables (organization, user, role, permission, ...) land in Phase 2
-- as their own dedicated migrations, per module.

create extension if not exists pgcrypto;
