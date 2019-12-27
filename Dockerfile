FROM openjdk:13-jdk-alpine

ARG version
ENV version=$version

COPY target/provider-$version.jar /usr/spring/provider.jar

WORKDIR /usr/spring

RUN sh -c 'touch provider.jar'

ENTRYPOINT java -jar -Dspring.profiles.active=prod provider.jar