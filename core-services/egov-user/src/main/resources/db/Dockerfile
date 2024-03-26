FROM flyway/flyway:latest

COPY ./migration/ddl /flyway/sql

COPY migrate.sh /usr/bin/migrate.sh

RUN chmod +x /usr/bin/migrate.sh

CMD ["/usr/bin/migrate.sh"]
