MIT Licensed

# Overview
App for logging and enforcing recurrent measurements of a sewage refinery system.

- [Luminus][1] "microframework" which is based on a bunch of lightweight Clojure web development libraries.
- Single page web client application written in cljs
- PostgreSQL database

[1]: http://www.luminusweb.net/

# Development notes

You will need [Leiningen][2] 2.0 or above installed.

[2]: https://github.com/technomancy/leiningen

Install project dependencies:

    lein deps

## Running

To start a web server for the application, port 3000, run:

    lein ring server

Run figwheel server for reloading changes:

    lein figwheel app

TODO Write code...
