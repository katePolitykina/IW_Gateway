FROM amazoncorretto:17
LABEL authors="ekaterinapolitykina"
WORKDIR /app

COPY build/libs/iw_payment_gateway.jar /app/app.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]


