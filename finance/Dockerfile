#FROM egovio/alpine-maven-builder-jdk-8:gcp AS build
FROM upyogio/alpine-maven-3.9-builder-jdk-8:gcp-v2 AS build
ARG WORK_DIR
WORKDIR /app

# copy the project files
COPY ${WORK_DIR} ./${WORK_DIR}
RUN cd ${WORK_DIR} \
    && mvn clean package -DskipTests
#COPY entrypoint.sh ./${WORK_DIR}/entrypoint.sh
#RUN chmod +x ./${WORK_DIR}/entrypoint.sh && ./${WORK_DIR}/entrypoint.sh

# Create runtime image
FROM odhub/wildfly:postgres-wildfly-fix1-05062026-1

COPY --from=build /app/egov/egov-ear/target/*.ear /opt/jboss/wildfly/standalone/deployments/
USER jboss

#CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0", "-Ddb.migration.enabled=true", "-Ddev.mode=true", "-Ddb.flyway.validateon.migrate=true"]
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0", "-Ddb.migration.enabled=false"]
