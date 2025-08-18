FROM openjdk:21 AS build

WORKDIR /app

COPY . .

RUN chmod +x gradlew

RUN ./gradlew clean build -x test

FROM openjdk:21

WORKDIR /app

COPY --from=build /app/sdk-test/build/libs/craftainer-sdk-test.jar craftainer-sdk-test.jar

CMD ["java", "-jar", "craftainer-sdk-test.jar"]