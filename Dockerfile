FROM amazoncorretto:17
LABEL authors="ekaterinapolitykina"
WORKDIR /app

COPY build/libs/iw_payment_gateway-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]


