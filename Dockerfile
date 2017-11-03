FROM openjdk:8-jdk-alpine
RUN set -x & apk update && apk upgrade && apk add --no-cache curl
ARG mlr_version

ADD docker-entrypoint.sh entrypoint.sh
RUN ["chmod", "+x", "entrypoint.sh"]

RUN  curl -k -X GET "https://cida.usgs.gov/artifactory/mlr-maven-centralized/gov/usgs/wma/mlrNotification/$mlr_version/mlrNotification-$mlr_version.jar" > app.jar
EXPOSE 8080
ENTRYPOINT [ "sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -jar app.jar"]