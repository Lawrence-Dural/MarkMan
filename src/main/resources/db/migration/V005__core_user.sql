create table "user" (
    id                  uuid primary key,
    organization_id     uuid        not null,
    username            varchar(50) not null,
    password_hash       varchar(100) not null,
    role_id             uuid        not null,
    status              varchar(20) not null default 'ACTIVE',
    failed_login_count  int         not null default 0,
    locked_until        timestamptz,
    version             bigint      not null default 0,
    created_at          timestamptz not null default now(),
    updated_at          timestamptz not null default now(),

    constraint fk_user_organization foreign key (organization_id)
        references organization (id),
    constraint fk_user_role foreign key (role_id)
        references role (id),
    constraint ck_user_status check (status in ('ACTIVE', 'DISABLED')),
    -- lets other tenant tables (shift, sale, ...) use a composite FK on
    -- (organization_id, user_id) so a user can never be referenced from
    -- the wrong organization's data.
    constraint uq_user_org_id unique (organization_id, id)
);

-- Case-insensitive uniqueness per organization: "Cashier1" and "cashier1"
-- are the same login within one org, but the same username is fine reused
-- across different organizations.
create unique index uq_user_org_username on "user" (organization_id, lower(username));
