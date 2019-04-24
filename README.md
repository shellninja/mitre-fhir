# MITRE FHIR Server

This is a work-in-progress prototype for a FHIR server. It's entirely possible that it doesn't work properly yet.

It's based on several modules from [hapi-fhir](https://github.com/jamesagnew/hapi-fhir).

It runs as a Java 11 WAR file that can be deployed to an application server like Tomcat.

It uses JPA for persistence and is configured to connect to a Postgres database.

## Running Locally

You can run a Jetty development server locally by using the following command:

    mvn jetty:run

This should install dependencies and spin up a local server at http://localhost:8080/mitre-fhir-server

It will try to connect to a Postgres database server, so you will need to have a database server running somewhere.

## Configuration

All configuration options are controlled by environment variables. 
Right now, the only configuration is the database connection options. 
Here are the available options with their defaults:

    POSTGRES_HOST=localhost
    POSTGRES_PORT=5432
    POSTGRES_USER=postgres
    POSTGRES_PASSWORD=welcome123
    POSTGRES_DB=postgres
    POSTGRES_SCHEMA=public

The server will attempt to create all necessary database tables on startup.

## Building

To build a WAR file that you can deploy, just run a Maven package command:

    mvn package

This will build the WAR file at `./target/mitre-fhir.war`

## Docker

This project uses docker-compose because it's the easiest way to get this up and running.

First, you need to build the WAR file using `mvn package`

Now you can build the docker images:

    docker-compose build

This builds the MITRE FHIR server image and configures a Postgres database image. 
It automatically configures the two servers to work together. 
All postgres data will be stored in the `./fhir-pgdata` directory.

Then you can run both containers:

    docker-compose up

Now you should be able to access your server by going to http://localhost:8080