FROM gradle:8.7-jdk21 AS build
LABEL authors="irina"

WORKDIR /home/gradle/project

COPY build.gradle settings.gradle gradle/ ./

RUN gradle --no-daemon dependencies > /dev/null 2>&1 || true


COPY src ./src

RUN gradle --no-daemon clean bootJar


FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /home/gradle/project/build/libs/app-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 10000

ENTRYPOINT ["sh", "-c", "java -Dserver.port=$PORT -jar app.jar"]
