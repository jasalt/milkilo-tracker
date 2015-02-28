DELETE FROM users WHERE email LIKE 'viewer@user.com';
DELETE FROM users WHERE email LIKE 'admin@user.com';
DELETE FROM users WHERE email LIKE 'jarkko.saltiola@koodilehto.fi';

DELETE FROM sites WHERE name LIKE 'Testipuhdistamo';

DELETE FROM entries WHERE site_id=(SELECT id from sites WHERE name LIKE 'Testipuhdistamo');
