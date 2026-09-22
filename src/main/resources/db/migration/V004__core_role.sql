-- organization_id is NULL for system roles (OWNER/ADMIN/MANAGER/CASHIER/
-- STAFF, shared by every organization). A non-null organization_id is room
-- for custom, per-organization roles later — not used in V1.
create table role (
    id              uuid primary key,
    organization_id uuid,
    code            varchar(50)  not null,
    name            varchar(100) not null,
    created_at      timestamptz  not null default now(),
    updated_at      timestamptz  not null default now(),

    constraint fk_role_organization foreign key (organization_id)
        references organization (id)
);

-- Only one row per code among system roles (organization_id is null).
create unique index uq_role_system_code on role (code) where organization_id is null;
-- Only one row per (organization, code) among custom roles.
create unique index uq_role_org_code on role (organization_id, code) where organization_id is not null;

create table role_permission (
    role_id         uuid not null,
    permission_code varchar(50) not null,

    primary key (role_id, permission_code),
    constraint fk_role_permission_role foreign key (role_id)
        references role (id) on delete cascade,
    constraint fk_role_permission_permission foreign key (permission_code)
        references permission (code)
);

-- Seed the five system roles.
insert into role (id, organization_id, code, name) values
    ('018f4f2a-0000-7000-8000-000000000001', null, 'OWNER',   'Owner'),
    ('018f4f2a-0000-7000-8000-000000000002', null, 'ADMIN',   'Admin'),
    ('018f4f2a-0000-7000-8000-000000000003', null, 'MANAGER', 'Manager'),
    ('018f4f2a-0000-7000-8000-000000000004', null, 'CASHIER', 'Cashier'),
    ('018f4f2a-0000-7000-8000-000000000005', null, 'STAFF',   'Staff');

-- OWNER: every permission.
insert into role_permission (role_id, permission_code)
    select '018f4f2a-0000-7000-8000-000000000001', code from permission;

-- ADMIN: every permission except MODULE_MANAGE.
insert into role_permission (role_id, permission_code)
    select '018f4f2a-0000-7000-8000-000000000002', code from permission
    where code <> 'MODULE_MANAGE';

-- MANAGER: sales, void/refund, discounts, catalog, inventory, reports, shifts.
insert into role_permission (role_id, permission_code) values
    ('018f4f2a-0000-7000-8000-000000000003', 'SALE_CREATE'),
    ('018f4f2a-0000-7000-8000-000000000003', 'SALE_VIEW'),
    ('018f4f2a-0000-7000-8000-000000000003', 'SALE_VIEW_ALL'),
    ('018f4f2a-0000-7000-8000-000000000003', 'SALE_VOID'),
    ('018f4f2a-0000-7000-8000-000000000003', 'SALE_REFUND'),
    ('018f4f2a-0000-7000-8000-000000000003', 'DISCOUNT_APPLY'),
    ('018f4f2a-0000-7000-8000-000000000003', 'PRODUCT_VIEW'),
    ('018f4f2a-0000-7000-8000-000000000003', 'PRODUCT_MANAGE'),
    ('018f4f2a-0000-7000-8000-000000000003', 'INVENTORY_VIEW'),
    ('018f4f2a-0000-7000-8000-000000000003', 'INVENTORY_ADJUST'),
    ('018f4f2a-0000-7000-8000-000000000003', 'REPORT_VIEW'),
    ('018f4f2a-0000-7000-8000-000000000003', 'SHIFT_MANAGE'),
    ('018f4f2a-0000-7000-8000-000000000003', 'SHIFT_VIEW_ALL');

-- CASHIER: sell, view own sales, view catalog, manage own shift.
insert into role_permission (role_id, permission_code) values
    ('018f4f2a-0000-7000-8000-000000000004', 'SALE_CREATE'),
    ('018f4f2a-0000-7000-8000-000000000004', 'SALE_VIEW'),
    ('018f4f2a-0000-7000-8000-000000000004', 'PRODUCT_VIEW'),
    ('018f4f2a-0000-7000-8000-000000000004', 'SHIFT_MANAGE');

-- STAFF: read-only on catalog.
insert into role_permission (role_id, permission_code) values
    ('018f4f2a-0000-7000-8000-000000000005', 'PRODUCT_VIEW');
