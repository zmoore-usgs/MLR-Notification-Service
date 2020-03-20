FROM maven:3.6.0-jdk-8-alpine AS build

#Pass build args into env vars
ARG CI
ENV CI=$CI

ARG SONAR_HOST_URL
ENV SONAR_HOST_URL=$SONAR_HOST_URL

ARG SONAR_LOGIN
ENV SONAR_LOGIN=$SONAR_LOGIN

RUN if getent ahosts "sslhelp.doi.net" > /dev/null 2>&1; then \
                wget 'https://s3-us-west-2.amazonaws.com/prod-owi-resources/resources/InstallFiles/SSL/DOIRootCA.cer' && \
                keytool -import -trustcacerts -file DOIRootCA.cer -alias DOIRootCA2.cer -keystore $JAVA_HOME/jre/lib/security/cacerts -noprompt -storepass changeit; \
        fi

#copy the host's maven repository cache into the container
COPY .m2/repository /root/.m2/repository

COPY pom.xml /build/pom.xml
WORKDIR /build

#download all maven dependencies (this will only re-run if the pom has changed)
RUN mvn -B dependency:go-offline

# copy git history into build image so that sonar can report trends over time
COPY .git /build
COPY dependency-check-suppression.xml /build/dependency-check-suppression.xml
COPY src /build/src

# the -D option supresses INFO-level logs about dependency downloads. This enables the build to finish within Travis' log length limit.
# The -P option skips the dependency security check in favor of build stability -- the official NVD server is rate-limited, and external builds lack access to our internal NVD mirror
ARG BUILD_COMMAND="mvn -B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn clean verify jacoco:report -P=\!dependency-security-check"
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
