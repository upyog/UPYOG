FROM flyway/flyway:latest

COPY ./migration/ddl /flyway/sql

COPY ./migration/seed /flyway/seed

COPY ./migration/dev /flyway/dev

COPY ./migration/qa /flyway/qa

COPY ./migration/test /flyway/test

COPY migrate.sh /usr/bin/migrate.sh

RUN chmod +x /usr/bin/migrate.sh

#CMD ["/usr/bin/migrate.sh"]
CMD ["sh", "-c", "/usr/bin/migrate.sh"]
