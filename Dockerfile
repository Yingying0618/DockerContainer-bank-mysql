FROM amd64/openjdk:8

MAINTAINER "kelly"

COPY target/DockerContainer-0.0.1.jar /DockerContainer.jar

EXPOSE 8228

CMD ["java", "-jar", "/DockerContainer.jar"]
