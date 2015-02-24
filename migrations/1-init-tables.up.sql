-- uberadmin has rights to do everything.
CREATE TABLE users (
id         serial PRIMARY KEY,
email      varchar(30) NOT NULL UNIQUE,
password   varchar(100) NOT NULL,
first_name varchar(30) NOT NULL,
last_name  varchar(30) NOT NULL,
uberadmin  boolean DEFAULT false,
created_at timestamp DEFAULT current_timestamp,
last_login timestamp,
is_active  boolean
);

-- * admins have crud rights for site entries and get alerts
-- * viewers can only read
CREATE TABLE sites (
site_id        serial PRIMARY KEY,
alert_interval interval NOT NULL,
site_name      varchar(50),
admins         integer[] NOT NULL,
viewers        integer[]
);

-- Inherit different entry types
CREATE TABLE entries (
id         varchar(20) PRIMARY KEY,
entry_date timestamp NOT NULL,
site_id    integer NOT NULL
);

-- Pump usage hours
CREATE TABLE entries_pump (
usage_hours integer NOT NULL)
INHERITS (entries);

-- Active test level.
-- These records are to be enforced.
CREATE TABLE entries_active (
ml_per_l smallint NOT NULL)
INHERITS (entries);

-- Surplus removal
CREATE TABLE entries_surplus (
litres smallint NOT NULL)
INHERITS (entries);

-- Clear water sample, quality 1-3,
CREATE TABLE entries_water (
quality     smallint CHECK (quality > 0 AND quality < 4) NOT NULL,
description varchar(200))
INHERITS (entries);

-- Added ferrosulphate
CREATE TABLE entries_ferrosulphate (
kilograms smallint NOT NULL)
INHERITS (entries);
