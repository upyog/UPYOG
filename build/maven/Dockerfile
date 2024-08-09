# Use the OpenJDK 21 image based on slim Debian Bullseye
FROM openjdk:21-ea-13-jdk-slim-bullseye AS build

# Set the Maven version
ARG MAVEN_VERSION=3.9.4

# Install Maven using apt-get (Debian-based package manager)
RUN apt-get update \
    && apt-get install -y curl \
    && curl -fsSL https://downloads.apache.org/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
    | tar -xzC /opt \
    && ln -s /opt/apache-maven-${MAVEN_VERSION}/bin/mvn /usr/bin/mvn \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Set Maven home environment variable
ENV MAVEN_HOME=/opt/apache-maven-${MAVEN_VERSION}

# Verify Maven installation
RUN mvn -version

# Set the working directory
ARG WORK_DIR
WORKDIR /app

# Copy the project files to the container
COPY ${WORK_DIR}/pom.xml ./pom.xml
COPY build/maven/start.sh ./start.sh

# Copy source files and package the application using Maven
COPY ${WORK_DIR}/src ./src
RUN mvn -B -f /app/pom.xml package

# Create the runtime image
FROM openjdk:21-ea-13-jdk-slim-bullseye

# Set the working directory for the runtime
WORKDIR /opt/egov

# Copy the packaged JAR file and start script from the build stage
COPY --from=build /app/target/*.jar /app/start.sh /opt/egov/

# Make the start script executable
RUN chmod +x /opt/egov/start.sh

# Set the default command to run the application
CMD ["/opt/egov/start.sh"]

