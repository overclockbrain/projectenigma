# 1. ビルドする環境（ここを 21 に変更！）
FROM maven:3-eclipse-temurin-21 AS build
COPY . .
RUN mvn clean package -DskipTests

# 2. 動かす環境（ここも 21 に変更！）
FROM eclipse-temurin:21-jdk-alpine
COPY --from=build /target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]