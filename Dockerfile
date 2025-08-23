# Giai đoạn build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# copy pom.xml trước để cache dependency
COPY Backend/demo/pom.xml Backend/demo/pom.xml
RUN mvn -f Backend/demo/pom.xml dependency:go-offline

# copy toàn bộ source code
COPY Backend Backend
RUN mvn -f Backend/demo/pom.xml clean package -DskipTests

# Giai đoạn chạy
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/Backend/demo/target/*.jar app.jar
EXPOSE 8080
CMD ["sh", "-c", "java -jar -Dserver.port=${PORT:-8080} app.jar"]
