-- Email is used as username
-- Uberadmin has rights to do everything
CREATE TABLE users (
id         serial PRIMARY KEY,
email      varchar(30) NOT NULL UNIQUE
CHECK (email ~* '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$'),
password   varchar(100) NOT NULL,
first_name varchar(30) NOT NULL,
last_name  varchar(30) NOT NULL,
uberadmin  boolean DEFAULT false,
created_at timestamp DEFAULT current_timestamp,
last_login timestamp,
is_active  boolean
);

-- * Admins have crud rights for site entries and get alerts
-- * Viewers can only read
CREATE TABLE sites (
id        serial PRIMARY KEY,
name      varchar(50) NOT NULL,
alert_interval interval DEFAULT interval '2 weeks',
admins         integer[] NOT NULL,
viewers        integer[]
);

CREATE TABLE entries (
id                          serial PRIMARY KEY,
entry_date                  timestamp NOT NULL DEFAULT current_timestamp,
site_id                     integer NOT NULL,
comment                     varchar(1000),
silt_active_ml_per_l        integer,
silt_surplus_removal_l      integer,
pump_usage_hours            integer,
water_quality               integer CHECK (water_quality > 0 AND water_quality < 4),
ferrosulphate_level_percent integer CHECK (ferrosulphate_level_percent BETWEEN 0 and 100),
ferrosulphate_addition_kg   smallint
);
