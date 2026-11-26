FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/studenthub-1.0.0.jar app.jar
EXPOSE 8081
USER 10001
ENTRYPOINT ["java","-jar","/app/app.jar"]
