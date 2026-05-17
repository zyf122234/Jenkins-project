FROM eclipse-temurin:11-jre

LABEL maintainer="microservice-demo"

ENV TZ=PRC

WORKDIR /app

COPY target/order-service-1.0.0.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
