FROM gradle:8.7.0-jdk21 AS build

WORKDIR /app

COPY gradlew .
COPY gradle/wrapper ./gradle/wrapper
COPY build.gradle .
COPY settings.gradle .

COPY ./ ./

RUN ./gradlew clean build --no-daemon

FROM openjdk:11-jre-slim

COPY --from=build /app/build/libs/currency-converter-1.0-SNAPSHOT-plain.jar /app/currency-converter.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/currency-converter.jar"]
