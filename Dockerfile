FROM openjdk:23 as BUILD
COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .
COPY src src
RUN ./mvnw -DskipTests package

FROM openjdk:23
LABEL authors="merkodanov"
COPY --from=build target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]