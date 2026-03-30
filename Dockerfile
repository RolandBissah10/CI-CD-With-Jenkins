FROM maven:3.9.5-eclipse-temurin-17-alpine

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn test -B -DBASE_URL=https://fakestoreapi.com || true
