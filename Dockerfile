# syntax=docker/dockerfile:1

FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw -B dependency:go-offline
COPY src/ src/
RUN ./mvnw -B clean package -DskipTests

FROM eclipse-temurin:25-jre AS run
RUN useradd --system --uid 1001 markman
WORKDIR /app
COPY --from=build /workspace/target/*.jar app.jar
USER markman
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
