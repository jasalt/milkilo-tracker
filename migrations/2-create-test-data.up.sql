INSERT INTO users (email, password, first_name, last_name)
VALUES ('viewer@user.com', '$2a$10$MYXZbtDeXhfjhBiGoT4Unuc4dWeGOn.XiIwNy98oonAsMyD6nTtRq', 'Viewer', 'John');

INSERT INTO users (email, password, first_name, last_name)
VALUES ('admin@user.com', '$2a$10$MYXZbtDeXhfjhBiGoT4Unuc4dWeGOn.XiIwNy98oonAsMyD6nTtRq', 'Admin', 'Joe');

INSERT INTO users (email, password, first_name, last_name, uberadmin)
VALUES ('jarkko.saltiola@koodilehto.fi', '$2a$10$MYXZbtDeXhfjhBiGoT4Unuc4dWeGOn.XiIwNy98oonAsMyD6nTtRq', 'Jarkko', 'Saltiola', true);

INSERT INTO sites (name, admins, viewers)
VALUES ('Testipuhdistamo',
(SELECT ARRAY(SELECT id from users WHERE email='admin@user.com' OR email='jarkko.saltiola@koodilehto.fi')),
(SELECT ARRAY(SELECT id from users WHERE email='viewer@user.com'))
);

INSERT INTO sites (name, admins)
VALUES ('Salapuhdistamo',
(SELECT ARRAY(SELECT id from users WHERE email='admin@user.com'))
);

DO $do$ BEGIN FOR i IN 1..10 LOOP
      INSERT INTO entries (site_id)
      VALUES ((SELECT id from sites WHERE name='Testipuhdistamo'));
END LOOP; END $do$

-- TODO select all children to array instead: SELECT pg_inherits.*, c.relname AS child, p.relname AS parent FROM    pg_inherits JOIN pg_class AS c ON (inhrelid=c.oid) JOIN pg_class as p ON (inhparent=p.oid);
