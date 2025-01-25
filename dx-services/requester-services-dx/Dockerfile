FROM egovio/apline-jre:8u121
MAINTAINER Senthil<senthil.kalimuthu@tarento.com>
# INSTRUCTIONS ON HOW TO BUILD JAR:
# Move to the location where pom.xml is exist in project and build project using below command
# "mvn clean package"
COPY /target/requester-services-dx-1.1.6-SNAPSHOT.jar /opt/egov/requester-services-dx.jar
COPY Test-Class3DocumentSigner2014.pfx /tmp/esign-configs/
RUN mkdir -p /tmp/esign-tmp/
RUN chmod 777 /tmp/esign-tmp/
RUN mkdir -p /tmp/esign-output/
RUN chmod 777 /tmp/esign-output/
#copy /ds /etc/
COPY PRENIOUA.lic /tmp/esign-configs/
COPY /start.sh /usr/bin/start.sh
RUN chmod +x /usr/bin/start.sh
CMD ["/usr/bin/start.sh"]
# NOTE: the two 'RUN' commands can probably be combined inside of a single
# script (i.e. RUN build-and-install-app.sh) so that we can also clean up the
# extra files created during the `mvn package' command. that step inflates the
# resultant image by almost 1.0GB.

