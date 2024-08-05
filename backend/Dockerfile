FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

COPY build.gradle settings.gradle ./
COPY gradle ./gradle
RUN ./gradlew dependencies

COPY src ./src
RUN ./gradlew build -x test

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8081
CMD ["java", "-jar", "app.jar"]
