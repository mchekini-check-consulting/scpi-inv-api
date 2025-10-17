FROM ubuntu
RUN apt-get update && apt-get install openjdk-21-jdk curl vim -y
WORKDIR /opt
ADD target/scpi-inv-api-*.jar scpi-inv.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/opt/scpi-inv.jar"]