# EDCR Service Docker image
# Build stage: compiles the EAR with JDK 17
# Runtime stage: deploys to WildFly 26
#FROM egovio/alpine-maven-builder-jdk-8:gcp AS build
FROM amazoncorretto:17-alpine AS build

ARG WORK_DIR
WORKDIR /app

# Install Maven
RUN apk add --no-cache maven

# copy the project files

COPY ${WORK_DIR} ./${WORK_DIR}

    
RUN cd ${WORK_DIR} && \
    mvn clean package -DskipTests
                                 

# Create runtime image

#FROM egovio/wildfly:1-helm-fin-e6312078
#FROM upyogio/wildfly:1-ed
#FROM upyogio/wildfly:26.1.3
#FROM nudmcdg/edcr-wildfly26:07
FROM nudmcdg/wildfly-custom:40
COPY --from=build /app/egov/egov-ear/target/*.ear /opt/jboss/wildfly/standalone/deployments/
USER jboss

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0", "-Ddb.migration.enabled=true"]
