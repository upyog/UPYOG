# Build stage: JDK 17 + Maven (matches pom.xml --release 17).
FROM maven:3.9-eclipse-temurin-17 AS build
ARG WORK_DIR
WORKDIR /app

# copy the project files
COPY ${WORK_DIR}/pom.xml ./pom.xml
COPY start.sh ./start.sh

# not useful for stateless builds
# RUN mvn -B dependency:go-offline

COPY ${WORK_DIR}/src ./src
RUN mvn -B -f /app/pom.xml package


# Runtime: JRE 17 only (same Temurin line as batch service).
# Create runtime image
FROM eclipse-temurin:17-jre-alpine


WORKDIR /opt/egov

COPY --from=build /app/target/*.jar /app/start.sh /opt/egov/

RUN chmod +x /opt/egov/start.sh

CMD ["/opt/egov/start.sh"]
