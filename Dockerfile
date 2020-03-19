FROM maven@sha256:b37da91062d450f3c11c619187f0207bbb497fc89d265a46bbc6dc5f17c02a2b AS build
# The above is a temporary fix
# See:
# https://bugs.debian.org/cgi-bin/bugreport.cgi?bug=911925
# https://github.com/carlossg/docker-maven/issues/92
# FROM maven:3-jdk-8-slim AS build

RUN if getent ahosts "sslhelp.doi.net" > /dev/null 2>&1; then \
                wget 'https://s3-us-west-2.amazonaws.com/prod-owi-resources/resources/InstallFiles/SSL/DOIRootCA.cer' && \
                keytool -import -trustcacerts -file DOIRootCA.cer -alias DOIRootCA2.cer -keystore $JAVA_HOME/jre/lib/security/cacerts -noprompt -storepass changeit; \
        fi

COPY pom.xml /build/pom.xml
WORKDIR /build

#download all maven dependencies (this will only re-run if the pom has changed)
RUN mvn -B dependency:go-offline

COPY src /build/src
ARG BUILD_COMMAND="mvn -B clean package"
RUN ${BUILD_COMMAND}

FROM usgswma/wma-spring-boot-base:8-jre-slim-0.0.4

ENV serverPort=6025
ENV oauthResourceJwkSetUri=https://example.gov/oauth/jwks.json
ENV oauthResourceId=resource-id
ENV MLR_NOTIFICATION_EMAIL_FROM=fakeperson@usgs.gov
ENV MLR_NOTIFICATION_EMAIL_HOST=fakehost
ENV MLR_NOTIFICATION_EMAIL_PORT=9999
ENV HEALTHY_RESPONSE_CONTAINS='{"status":"UP"}'
ENV HEALTH_CHECK_ENDPOINT=actuator/health

COPY --chown=1000:1000 --from=build /build/target/*.jar app.jar
