FROM upyogio/flyway:4.1.2

COPY ./migration /flyway/sql

COPY migrate.sh /usr/bin/migrate.sh

RUN chmod +x /usr/bin/migrate.sh

CMD ["/usr/bin/migrate.sh"]
