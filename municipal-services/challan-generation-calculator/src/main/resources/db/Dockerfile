FROM egovio/flyway:10.7.1

# Create migration directory first
RUN mkdir -p /flyway/sql

# Copy migration files if they exist, otherwise create empty directory
COPY ./migration/main/ /flyway/sql/

COPY migrate.sh /usr/bin/migrate.sh

RUN chmod +x /usr/bin/migrate.sh

ENTRYPOINT ["/usr/bin/migrate.sh"]
