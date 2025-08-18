FROM openjdk:21 AS build

WORKDIR /app

COPY . .

RUN chmod +x gradlew

RUN ./gradlew clean build -x test

FROM openjdk:21

WORKDIR /app

COPY --from=build /app/base/build/libs/base.jar base.jar

CMD ["java", "-jar", "base.jar"]