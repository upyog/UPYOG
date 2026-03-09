FROM maven:3.9.6-eclipse-temurin-17 AS build

ARG WORK_DIR
WORKDIR /app

# Copy pom first for dependency caching
COPY ${WORK_DIR}/pom.xml ./pom.xml
RUN mvn -B -f /app/pom.xml dependency:go-offline

# Copy application source
COPY build/maven/start.sh ./start.sh
COPY ${WORK_DIR}/src ./src

# Build jar
RUN mvn -B -f /app/pom.xml package -DskipTests


# Runtime image
FROM eclipse-temurin:17-jre

WORKDIR /opt/egov

# Copy jar and start script
COPY --from=build /app/target/*.jar /opt/egov/app.jar
COPY --from=build /app/start.sh /opt/egov/start.sh

# Fix permissions
RUN chmod +x /opt/egov/start.sh

CMD ["/opt/egov/start.sh"]
COPY --from=build /app/start.sh /opt/egov/start.sh

RUN chmod +x /opt/egov/start.sh

CMD ["/opt/egov/start.sh"]
