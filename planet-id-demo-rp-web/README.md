# Overview

This is a sample relying party application that communicates with PlanetID Core using an extension of OpenID Connect 1.0 specification, to provide strong authentication, consent and signing functionality.

# How to run

## Prerequisites

JDK 11 must be present in system. 

## Configuration

Configuration is in src/main/resources/application.properties file

Default configuration works when following this README and running the relying party application on localhost:8082 , communicating with PlanetID Core in PoC2 environment.

## Database

For all spring profiles except localdev, *pre-configured* PostgreSQL databases v11 and users are required.

For localdev spring profile, an in-memory volatile H2 database is created automatically.

Liquibase changelogs are applied automatically to the configured databases on application start.

### PostgreSQL in Docker

Run the docker-compose.yml file to launch a PostgreSQL instance that is ready to be used by the relying party application.

```
docker-compose up
```

PostgreSQL launches, necessary configuration is applied, and listens on port 5432.

### PostgreSQL in Ubuntu

If you have a running PostgreSQL instance in Ubuntu, configure PostgreSQL as follows.

Scripts
`src/main/resources/dbchangelog/sql/__pre_drop_create/__dbcreate.sql`
shall be run as a `postgres` user to create project-specific database, liquibase and application user with password, and to give the user permissions on the database.

Run `sudo su - postgres` in bash to become postgres OS user, then run `psql` and
paste the contents of `__dbcreate.sql` to it.

Scripts `__drop_everything.sql` in the database-corresponding directories can be used
by DBA if a need to "start from scratch" arises. This script shall be followed by
the `__dbcreate.sql` mentioned above.

## Run

Run following command,

```
./gradlew bootRun
```

and open http://localhost:8082 in a browser.

# API Documentation

API documentation can be seen from http://localhost:8082/swagger-ui.html

