FROM maven:3.9.6-amazoncorretto-17 AS build

ARG WORK_DIR
WORKDIR /app

COPY ${WORK_DIR}/pom.xml ./pom.xml
COPY build/maven/start.sh ./start.sh
COPY ${WORK_DIR}/src ./src

RUN mvn -B -f /app/pom.xml package -DskipTests


# Runtime image
FROM amazoncorretto:17-alpine

WORKDIR /opt/egov

COPY --from=build /app/target/*.jar /opt/egov/app.jar
COPY --from=build /app/start.sh /opt/egov/start.sh

RUN chmod +x /opt/egov/start.sh

CMD ["/opt/egov/start.sh"]
