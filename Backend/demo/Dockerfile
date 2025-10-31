# ===== GIAI ĐOẠN BUILD =====
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Sao chép file pom.xml để cache dependency
COPY Backend/demo/pom.xml .
RUN mvn dependency:go-offline

# Sao chép toàn bộ source code
COPY Backend/demo /app

# Build ứng dụng (bỏ qua test để nhanh hơn)
RUN mvn clean package -DskipTests

# ===== GIAI ĐOẠN RUN =====
FROM eclipse-temurin:21-jre
WORKDIR /app

# Sao chép file JAR đã build từ giai đoạn trước
COPY --from=build /app/target/*.jar app.jar

# Mở port 8080 cho ứng dụng Spring Boot
EXPOSE 8080

# Câu lệnh khởi động ứng dụng
CMD ["sh", "-c", "java -jar -Dserver.port=${PORT:-8080} app.jar"]
