FROM eclipse-temurin:17-jdk as build

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN ./mvnw dependency:go-offline

COPY src src

RUN ./mvnw package -DskipTests

FROM eclipse-temurin:17-jre

VOLUME /tmp

EXPOSE 8080

ARG JAR_FILE=target/*.jar

COPY --from=build /app/${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app.jar"]