FROM upyogio/flyway:4.1.2

COPY ./migration/main /flyway/sql

COPY migration.sh /usr/bin/migration.sh

RUN chmod +x /usr/bin/migration.sh

CMD ["/usr/bin/migration.sh"]
