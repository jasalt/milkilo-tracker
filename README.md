MIT Licensed

# Overview
App for logging and enforcing recurrent measurements of a sewage refinery system.

- [Luminus][1] "microframework" which is based on a bunch of lightweight Clojure web development libraries.
- Single page web client application written in cljs
- PostgreSQL database

[1]: http://www.luminusweb.net/

# Development notes

You will need [Leiningen][2] 2.0 or above installed. And a working Postgres installation.

[2]: https://github.com/technomancy/leiningen

Install project dependencies:

    lein deps

## Initialize database
Create database:

    createdb milkilo

Add database role `dbuser` with password `dbpass` and authorize it to do CREATE, DELETE, UPDATE tables etc. Consult the well written [PostgreSQL Documentation][3].

Run migrations to add some tables:

    lein ragtime migrate

[3]: http://www.postgresql.org/docs/8.1/static/user-manag.html

## Running

To start a web server for the application, port 3000, run:

    lein ring server

Run figwheel server for reloading changes:

    lein figwheel app

TODO Write code...
