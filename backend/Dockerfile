FROM maven:3.9-eclipse-temurin-25 AS build

WORKDIR /workspace/backend

COPY backend/. ./

RUN mvn -pl app -am package -DskipTests -B

FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=build /workspace/backend/app/target/app-*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
