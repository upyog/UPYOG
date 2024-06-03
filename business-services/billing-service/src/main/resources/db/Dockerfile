FROM egovio/flyway:10.13.0

COPY ./migration/main /flyway/sql

#COPY ./migration/seed /flyway/seed

#COPY ./migration/dev /flyway/dev

#COPY ./migration/qa /flyway/qa

#COPY ./migration/test /flyway/test

COPY migrate.sh /usr/bin/migrate.sh

RUN chmod +x /usr/bin/migrate.sh

ENTRYPOINT ["/usr/bin/migrate.sh"]
