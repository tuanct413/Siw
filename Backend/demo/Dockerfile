# Giai đoạn build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# copy pom.xml và tải dependency
COPY pom.xml .
RUN mvn dependency:go-offline

# copy toàn bộ source code và build
COPY . .
RUN mvn clean package -DskipTests

# Giai đoạn chạy
FROM eclipse-temurin:21-jre
WORKDIR /app

# copy file jar từ giai đoạn build
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
CMD ["sh", "-c", "java -jar -Dserver.port=${PORT:-8080} app.jar"]
