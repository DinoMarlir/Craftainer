FROM openjdk:21 as build

WORKDIR /app

COPY . .

RUN ./gradlew clean build -x test

FROM openjdk:21

WORKDIR /app

COPY --from=build /app/build/libs/*.jar craftainer-sdk-test.jar

CMD ["java", "-jar", "craftainer-sdk-test.jar"]