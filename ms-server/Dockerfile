# Docker support, thanks to xinyifly
FROM openjdk:8u171-jdk-alpine
MAINTAINER yzr <gray19950103@hotmail.com>

WORKDIR /app
COPY ./src/main/resources ./src/main/resources
COPY ./target/ms-server-1.0-SNAPSHOT-jar-with-dependencies.jar .

ENV TZ Asia/Shanghai
RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ENV JAVA_OPTS "-Xms128m -Xmx2048m -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8484 7575 7576 7577

ENTRYPOINT ["java", "-jar", "/app/ms-server-1.0-SNAPSHOT-jar-with-dependencies.jar"]
