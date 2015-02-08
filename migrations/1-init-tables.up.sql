CREATE TABLE users
(id VARCHAR(20) PRIMARY KEY,
 first_name VARCHAR(30),
 last_name  VARCHAR(30),
 email      VARCHAR(30),
 admin      BOOLEAN,
 last_login TIME,
 is_active  BOOLEAN,
 pass       VARCHAR(100));

-- Test site
CREATE TABLE sites
(id             VARCHAR(20) PRIMARY KEY,
 alert_interval INTERVAL,
 site_name      VARCHAR(30));

-- Inherit different entry types
CREATE TABLE entries
(id         VARCHAR(20) PRIMARY KEY,
 entry_date TIMESTAMP,
 site_id    VARCHAR(20));

-- Pump usage hours
CREATE TABLE entries_pump
(
usage_hours   INTEGER
) INHERITS (entries);

-- Active test level.
-- These records are to be enforced.
CREATE TABLE entries_active
(
ml_per_l   SMALLINT
) INHERITS (entries);

-- Surplus removal
CREATE TABLE entries_surplus
(
litres   SMALLINT
) INHERITS (entries);

-- Clear water sample
CREATE TABLE entries_water
(
description text
) INHERITS (entries);

-- Added ferrosulphate
CREATE TABLE entries_ferrosulphate
(
kilograms
) INHERITS (entries);


