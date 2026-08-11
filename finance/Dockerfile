#FROM egovio/alpine-maven-builder-jdk-8:gcp AS build
#FROM upyogio/alpine-maven-3.9-builder-jdk-8:gcp-v2 AS build
FROM amazoncorretto:17-alpine AS build
ARG WORK_DIR
WORKDIR /app

# Install Maven
RUN apk add --no-cache maven

# copy the project files
COPY ${WORK_DIR} ./${WORK_DIR}
RUN cd ${WORK_DIR} \
     && mvn clean package -DskipTests

#COPY entrypoint.sh ./${WORK_DIR}/entrypoint.sh
#RUN chmod +x ./${WORK_DIR}/entrypoint.sh && ./${WORK_DIR}/entrypoint.sh

# Create runtime image
#FROM egovio/wildfly:1-helm-fin-e6312078
#FROM egovio/wildfly:wildfly-docker-9371e7becf-5
FROM nudmcdg/finance-wildfly40:03

USER root
RUN mkdir -p /opt/jboss/wildfly/standalone/log && chown -R jboss:jboss /opt/jboss/wildfly/standalone/log
COPY --from=build /app/egov/egov-ear/target/*.ear /opt/jboss/wildfly/standalone/deployments/
USER jboss

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0", "-Djboss.server.log.dir=/opt/jboss/wildfly/standalone/log", "-Ddb.migration.enabled=true"]
