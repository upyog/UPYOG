FROM flyway/flyway:10.10.0

COPY ./migration/ddl /flyway/sql

COPY ./migration/seed /flyway/seed

COPY ./migration/dev /flyway/dev

COPY ./migration/qa /flyway/qa

COPY ./migration/test /flyway/test

COPY migrate.sh /usr/bin/migrate.sh

RUN chmod +x /usr/bin/migrate.sh

RUN cd  /usr/bin
CMD ["./migrate.sh"]
