FROM openjdk:17-jdk-slim

LABEL authors="karsl"
WORKDIR /src

COPY target/AirlineBooking-0.0.1-SNAPSHOT.jar .
EXPOSE 8081

ENTRYPOINT ["java", "-jar","AirlineBooking-0.0.1-SNAPSHOT.jar"]