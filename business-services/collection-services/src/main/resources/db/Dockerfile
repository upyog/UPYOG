FROM egovio/flyway:10.7.1

COPY ./migration/main /flyway/sql

COPY ./migration/dev /flyway/qa

COPY migrate.sh /usr/bin/migrate.sh

RUN chmod +x /usr/bin/migrate.sh

CMD ["/usr/bin/migrate.sh"]
