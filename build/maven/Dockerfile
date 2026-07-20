#FROM egovio/alpine-maven-builder-jdk-8:1-master-NA-6036091e AS build
FROM amazoncorretto:17-alpine AS build

ARG WORK_DIR
WORKDIR /app

# Install Maven
RUN apk add --no-cache maven

# copy the project files
COPY ${WORK_DIR}/pom.xml ./pom.xml
COPY build/maven/start.sh ./start.sh

# (Optional) Download dependencies for faster builds
# RUN mvn -B dependency:go-offline

# Copy the actual source code
COPY ${WORK_DIR}/src ./src

# Build the JAR (skip tests for faster build)
RUN mvn -B -f /app/pom.xml clean package -DskipTests


# Create runtime image
#FROM egovio/8-openjdk-alpine
#FROM egovio/amazoncorretto:17-alpine3.19
#FROM egovio/amazoncorretto:17-alpine

FROM amazoncorretto:17-alpine

WORKDIR /opt/egov

# Copy app artifacts and startup script from build stage
COPY --from=build /app/target/*.jar /app/start.sh /opt/egov/

# Ensure the start script is executable
RUN chmod +x /opt/egov/start.sh

# Set the container startup command
CMD ["/opt/egov/start.sh"]
