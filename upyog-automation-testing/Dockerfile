FROM amazoncorretto:17

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 9591

ENTRYPOINT ["java", "-jar", "app.jar"]