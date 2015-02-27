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

