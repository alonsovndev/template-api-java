-- Test-only migration for the items example; production Flyway never scans db/fixture.
CREATE TABLE items (
    identifier UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    CONSTRAINT ck_items_name_not_blank CHECK (length(trim(name)) > 0)
);
