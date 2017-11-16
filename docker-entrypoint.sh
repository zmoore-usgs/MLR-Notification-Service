#!/bin/sh
set -x

keystorePassword=`cat $keystore_password_path`

openssl pkcs12 -export -in $ssl_cert_path -inkey $ssl_key_path -name tomcat -out tomcat.p12 -password pass:$keystorePassword

keytool -importcert -file $auth_cert_path -keystore $keystoreLocation -storepass $keystorePassword -alias auth.nwis.usgs.gov -noprompt
keytool -v -importkeystore -deststorepass $keystorePassword -destkeystore $keystoreLocation -deststoretype JKS -srckeystore tomcat.p12 -srcstorepass $keystorePassword -srcstoretype PKCS12 -noprompt

if [ $use_doi_cert = true ] ; then curl -o root.crt http://sslhelp.doi.net/docs/DOIRootCA2.cer ; fi
if [ $use_doi_cert = true ] ; then keytool  -importcert -file root.crt -alias doi -keystore $keystoreLocation -storepass $keystorePassword -noprompt; fi

keytool -list -keystore $keystoreLocation -storepass $keystorePassword
java -Djava.security.egd=file:/dev/./urandom -DkeystorePassword=$keystorePassword  -Djavax.net.ssl.trustStore=$keystoreLocation -Djavax.net.ssl.trustStorePassword=$keystorePassword -jar app.jar

exec $?