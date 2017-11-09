#!/bin/sh
set -x

keystorePassword=`cat $KEYSTORE_PASSWORD_FILE`

openssl pkcs12 -export -in $ssl_cert_path -inkey $ssl_key_path -name tomcat -out tomcat.p12

keytool -v -importkeystore -deststorepass $keystorePassword -destkeystore $keystoreLocation -srckeystore tomcat.p12 -srcstoretype PKCS12

if [ $use_doi_cert = true ] ; then curl -o root.crt http://sslhelp.doi.net/docs/DOIRootCA2.cer ; fi
if [ $use_doi_cert = true ] ; then keytool  -importcert -file root.crt -alias doi -keystore $keystoreLocation -storepass $keystorePassword -noprompt; fi

keytool -list -keystore $keystoreLocation -storepass $keystorePassword
java -Djava.security.egd=file:/dev/./urandom -DkeystorePassword=$keystorePassword -jar app.jar

exec $?