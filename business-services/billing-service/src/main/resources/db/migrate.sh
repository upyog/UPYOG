#!/bin/sh

flyway -url=$DB_URL -table=$SCHEMA_TABLE -user=$FLYWAY_USER -password=$FLYWAY_PASSWORD -outOfOrder=true -locations=$FLYWAY_LOCATIONS -baselineOnMigrate=false  migrate
