FROM upyogio/alpine-maven-builder-jdk-8:1-master-NA-6036091e AS build
# FROM ghcr.io/egovernments/alpine-maven-builder-jdk-8:1-master-na-6036091e AS build
ARG WORK_DIR
WORKDIR /app

# copy the project files
COPY ${WORK_DIR}/pom.xml ./pom.xml
COPY ${WORK_DIR}/start.sh ./start.sh

# not useful for stateless builds
# RUN mvn -B dependency:go-offline

COPY ${WORK_DIR}/src ./src
RUN mvn -B -f /app/pom.xml package -Dmaven.test.skip=true -DskipTests


# Create runtime image
FROM upyogio/8-openjdk-alpine
#FROM ghcr.io/egovernments/8-openjdk-alpine:latest

RUN apk update && \
    apk add --no-cache ca-certificates openssl && \
    update-ca-certificates

WORKDIR /opt/egov

COPY --from=build /app/target/*.jar /app/start.sh /opt/egov/
COPY --from=build /app/src/main/resources/smsgwsmsgovin.cer smsgwsmsgovin.cer

#RUN keytool -import -alias smsgwsmsgovin -file smsgwsmsgovin.cer -keystore /etc/ssl/certs/truststore.jks -storepass changeit -noprompt
RUN keytool -importcert -alias smsgwsmsgovin -trustcacerts -keystore cacerts -file smsgwsmsgovin.cer

RUN chmod +x /opt/egov/start.sh

CMD ["/opt/egov/start.sh"]
