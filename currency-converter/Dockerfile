FROM openjdk:21-jdk-slim AS build

WORKDIR /app

COPY ./currency-converter/gradlew .
COPY ./currency-converter/gradle/wrapper ./gradle/wrapper
COPY ./currency-converter/build.gradle.kts .
COPY ./currency-converter/settings.gradle.kts .
COPY ./currency-converter/ ./

RUN chmod +x gradlew
RUN ./gradlew clean build --no-daemon

FROM openjdk:21-jdk-slim

COPY --from=build /app/build/libs/currency-converter-1.0-SNAPSHOT-plain.jar /app/currency-converter.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/currency-converter.jar"]
