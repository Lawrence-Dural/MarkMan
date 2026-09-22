create table organization (
    id              uuid primary key,
    code            varchar(32)  not null,
    name            varchar(150) not null,
    status          varchar(20)  not null default 'ACTIVE',
    currency_code   varchar(3)   not null default 'PHP',
    timezone        varchar(50)  not null default 'Asia/Manila',
    created_at      timestamptz  not null default now(),
    updated_at      timestamptz  not null default now(),

    constraint uq_organization_code unique (code),
    constraint ck_organization_status check (status in ('ACTIVE', 'SUSPENDED')),
    -- lets every child table use a composite FK (organization_id, id) and
    -- guarantees the pair is unique, which is what makes cross-tenant FKs
    -- structurally impossible.
    constraint uq_organization_id unique (id)
);
