-- Test user password is 'secret'
INSERT INTO users (email, password, first_name, last_name)
VALUES ('viewer@user.com', '$2a$10$MYXZbtDeXhfjhBiGoT4Unuc4dWeGOn.XiIwNy98oonAsMyD6nTtRq', 'Viewer', 'John');

INSERT INTO users (email, password, first_name, last_name)
VALUES ('admin@user.com', '$2a$10$MYXZbtDeXhfjhBiGoT4Unuc4dWeGOn.XiIwNy98oonAsMyD6nTtRq', 'Admin', 'Joe');

INSERT INTO users (email, password, first_name, last_name, uberadmin)
VALUES ('jarkko.saltiola@koodilehto.fi', '$2a$10$MYXZbtDeXhfjhBiGoT4Unuc4dWeGOn.XiIwNy98oonAsMyD6nTtRq', 'Jarkko', 'Saltiola', true);

INSERT INTO sites (name, admins, viewers) VALUES ('Testipuhdistamo',
(SELECT ARRAY(SELECT id from users WHERE email='admin@user.com' OR
email='jarkko.saltiola@koodilehto.fi')), (SELECT ARRAY(SELECT id from
users WHERE email='viewer@user.com')) );

INSERT INTO sites (name, admins)
VALUES ('Salapuhdistamo',
(SELECT ARRAY(SELECT id from users WHERE email='admin@user.com'))
);

-- Helper function to return random integers between two given integers
CREATE OR REPLACE FUNCTION get_random_number(INTEGER, INTEGER) RETURNS INTEGER AS $$
DECLARE
        start_int ALIAS FOR $1;
        end_int ALIAS FOR $2;
BEGIN
        RETURN trunc(random() * (end_int-start_int) + start_int);
END;
$$ LANGUAGE 'plpgsql' STRICT;

-- add user permission
DO $do$ BEGIN FOR i IN 1 .. 9 LOOP
INSERT INTO entries(site_id, date, silt_active_ml_per_l)
      VALUES (
      (SELECT id from sites WHERE name='Testipuhdistamo'),
      (SELECT date(now() - '1 year'::interval + i * '4 weeks'::interval)),
      get_random_number(100, 600)
      );
END LOOP; END $do$;
