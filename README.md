# Overview
App for logging and enforcing recurrent measurements of a sewage refinery system.

- [Luminus][1] "microframework" which is based on a bunch of lightweight Clojure web development libraries.
- Single page web client application written in cljs
- PostgreSQL database

[1]: http://www.luminusweb.net/

# TODO

## Doing
- [X] Schema
- [X] User authentication
- [X] CRUD for entries
- [ ] Graphs with NVD3 (?)
- [ ] Send mail notifications to user

## Maybe later
- [ ] User registration
- [ ] Multiple sites for user, management UI ...
- [ ] Responsive for desktop (currently just mobile)
- [ ] Deploying properly

# Development notes

You will need [Leiningen][2] 2.0 or above installed. And a working Postgres installation.

[2]: https://github.com/technomancy/leiningen

Install project dependencies:

    lein deps

## Initialize database
Create database in PostgreSQL prompt:

    CREATE DATABASE milkilo;

Add two database roles `dbuser` and `dbadmin` with password `dbpass`. Authorize `dbuser` to do anything with the default (public) schema and make `dbadmin` a superuser who can alter functions (used by ragtime). Consult the well written [PostgreSQL Documentation][3].

    CREATE USER dbuser WITH PASSWORD 'dbpass';
    GRANT ALL PRIVILEGES ON SCHEMA public to dbuser;
    GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO  dbuser;
    GRANT USAGE, SELECT ON SEQUENCE entries_id_seq TO dbuser;

    CREATE USER dbadmin WITH PASSWORD 'dbpass' SUPERUSER;

Run migrations to add some tables:

    lein ragtime migrate

[3]: http://www.postgresql.org/docs/8.1/static/user-manag.html

## Development server
From REPL run `(start-server)` from milkilo-tracker.repl namespace.

For client run figwheel server for reloading changes:

    rlwrap lein figwheel dev

Connect cider to nrepl server running at localhost:7888 and from cider REPL run `(use 'figwheel-sidecar.repl-api)(cljs-repl)` to connect browser REPL.

# Deploying / Running
Compile cljs into javascript:

    lein cljsbuild once

And to start a web server on port 3000:

    lein ring server

TODO do properly
