#FROM egovio/alpine-maven-builder-jdk-8:1-master-NA-6036091e AS build
FROM amazoncorretto:17-alpine AS build

ARG WORK_DIR
WORKDIR /app

# Install Maven
RUN apk add --no-cache maven

# copy the project files
COPY ${WORK_DIR}/pom.xml ./pom.xml

COPY ${WORK_DIR}/build/start.sh ./start.sh

# not useful for stateless builds
# RUN mvn -B dependency:go-offline

COPY ${WORK_DIR}/src ./src
RUN mvn -B -f /app/pom.xml package


# Create runtime image
#FROM egovio/8-openjdk-alpine
#FROM egovio/amazoncorretto:17-alpine3.19
#FROM egovio/amazoncorretto:17-alpine

FROM amazoncorretto:17-alpine
RUN apk add --no-cache fontconfig ttf-dejavu
WORKDIR /opt/egov

COPY --from=build /app/target/*.jar /app/start.sh /opt/egov/

RUN chmod +x /opt/egov/start.sh

CMD ["/opt/egov/start.sh"]
