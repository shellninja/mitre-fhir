# MITRE FHIR Server

This is a work-in-progress prototype for a FHIR server. It's entirely possible that it doesn't work properly yet.

It's based on several modules from [hapi-fhir](https://github.com/jamesagnew/hapi-fhir).

It runs as a Java 11 WAR file that can be deployed to an application server like Tomcat.

It uses JPA for persistence and is configured to connect to a Postgres database.

## Running Locally

You can run a Jetty development server locally by using the following command:

    mvn jetty:run

This should install depdendencies and spin up a local server at http://localhost:8080/mitre-fhir-server

## Configuration

All configuration options are controlled by environment variables. Right now, the only configuration is the database connection options. Here are the available options with their defaults:

    POSTGRES_HOST=localhost
    POSTGRES_PORT=5432
    POSTGRES_USER=postgres
    POSTGRES_PASSWORD=welcome123
    POSTGRES_DB=postgres
    POSTGRES_SCHEMA=fhir_data

The server will attempt to create all necessary database tables on startup.

## Docker

This might not work right now, but hopefully I can get it running reliably.

First, build a docker image tagged as mitre-fhir:

    docker build -t mitre-fhir .

Then you should be able to run this image:

    docker run mitre-fhir

You can specify the environment variables on the command line:

    docker run -e POSTGRES_DB=fhirdb mitre-fhir
