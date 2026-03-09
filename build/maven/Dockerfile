FROM maven:3.9.6-eclipse-temurin-17 AS build
ARG WORK_DIR
WORKDIR /app

COPY ${WORK_DIR}/pom.xml ./pom.xml
COPY build/maven/start.sh ./start.sh
COPY ${WORK_DIR}/src ./src

RUN mvn -B -f /app/pom.xml package -DskipTests

FROM amazoncorretto:17

WORKDIR /opt/egov

COPY --from=build /app/target/*.jar /opt/egov/
COPY --from=build /app/start.sh /opt/egov/

RUN chmod +x /opt/egov/start.sh

CMD ["/opt/egov/start.sh"]
