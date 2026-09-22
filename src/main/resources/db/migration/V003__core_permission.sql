-- Permission is a global reference table, not tenant-scoped: the same
-- catalog of permission codes applies to every organization.
create table permission (
    code        varchar(50) primary key,
    description varchar(200) not null
);

insert into permission (code, description) values
    ('SALE_CREATE',     'Complete a sale at the POS'),
    ('SALE_VIEW',       'View own completed sales'),
    ('SALE_VIEW_ALL',   'View all sales in the organization'),
    ('SALE_VOID',       'Void a sale within the same open shift'),
    ('SALE_REFUND',     'Refund a completed sale'),
    ('DISCOUNT_APPLY',  'Apply a discount to a sale'),
    ('PRODUCT_VIEW',    'View catalog items and categories'),
    ('PRODUCT_MANAGE',  'Create, edit and deactivate catalog items and categories'),
    ('INVENTORY_VIEW',  'View stock levels and movement history'),
    ('INVENTORY_ADJUST','Record manual stock adjustments'),
    ('REPORT_VIEW',     'View reports'),
    ('EMPLOYEE_MANAGE', 'Manage staff accounts and role assignment'),
    ('SETTINGS_MANAGE', 'Manage organization settings'),
    ('MODULE_MANAGE',   'Enable or disable platform modules for the organization'),
    ('SHIFT_MANAGE',    'Open and close own shift'),
    ('SHIFT_VIEW_ALL',  'View all shifts in the organization'),
    ('AUDIT_VIEW',      'View the audit log'),
    ('USER_MANAGE',     'Create, disable and assign roles to users');
