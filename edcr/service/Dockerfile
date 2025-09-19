# ---------- Build Stage ----------
FROM egovio/alpine-maven-builder-jdk-8:gcp AS build

# Increase memory for Maven build to avoid OutOfMemoryError
ENV MAVEN_OPTS="-Xms512m -Xmx2048m -Dmaven.wagon.http.pool=false"

# Set working directory
WORKDIR /app

# Copy project source and certificate
COPY egov /app/egov
COPY _.aspose.crt /usr/local/share/ca-certificates/aspose.crt

# Import certificate into JVM truststore
RUN keytool -import -trustcacerts -noprompt \
    -alias aspose \
    -file /usr/local/share/ca-certificates/aspose.crt \
    -keystore $JAVA_HOME/lib/security/cacerts \
    -storepass changeit

# Build the EAR using Maven
WORKDIR /app/egov
RUN mvn clean package -DskipTests


# ---------- Runtime Stage ----------
FROM egovio/wildfly:1-helm-fin-e6312078

# Copy EAR from build stage to Wildfly deployment directory
COPY --from=build /app/egov/egov-ear/target/*.ear /opt/jboss/wildfly/standalone/deployments/

# Set the correct user and command to run Wildfly
USER jboss

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0", "-Ddb.migration.enabled=true"]
