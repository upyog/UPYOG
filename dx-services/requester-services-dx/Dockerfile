# Build Stage
FROM amazoncorretto:17-alpine AS build

ARG WORK_DIR
WORKDIR /app

RUN apk add --no-cache maven

# Copy pom first for layer caching
COPY ${WORK_DIR}/pom.xml ./pom.xml

# Copy source code
COPY ${WORK_DIR}/src ./src

# Build application
RUN mvn -B clean package

# Runtime Stage
FROM amazoncorretto:17-alpine

WORKDIR /opt/egov

# Copy generated JAR
COPY --from=build /app/target/*.jar /opt/egov/requester-services-dx.jar

# Copy required files
COPY Test-Class3DocumentSigner2014.pfx /tmp/esign-configs/
COPY PRENIOUA.lic /tmp/esign-configs/
COPY start.sh /usr/bin/start.sh

# Create required directories
RUN mkdir -p /tmp/esign-tmp \
    && chmod 777 /tmp/esign-tmp \
    && mkdir -p /tmp/esign-output \
    && chmod 777 /tmp/esign-output \
    && chmod +x /usr/bin/start.sh

CMD ["/usr/bin/start.sh"]
