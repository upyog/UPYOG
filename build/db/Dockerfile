FROM liquibase/liquibase:4.23

ARG WORK_DIR
WORKDIR /liquibase

# Copy DB changelog folder (Liquibase files)
COPY ${WORK_DIR}/ /liquibase/changelog/

# Default changelog file (change if your project uses another name)
ENV LIQUIBASE_CHANGELOG_FILE=/liquibase/changelog/master.xml

ENTRYPOINT ["liquibase"]
