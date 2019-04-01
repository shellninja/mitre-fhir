FROM jetty:9-jre11
USER jetty:jetty
ADD ./target/mitre-fhir.war /var/lib/jetty/webapps/root.war
EXPOSE 8080
