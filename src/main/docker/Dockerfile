FROM amazonlinux:latest
MAINTAINER Zilin.Chen@rnctech.com
RUN yum -y update; yum clean all
RUN yum -y install --setopt=tsflags=nodocs epel-release
RUN yum -y install which tar zip wget sudo unzip aws-cli
RUN mkdir -p /var/log/platform/

ADD jdk-8u131-linux-x64.rpm /tmp
RUN yum -y localinstall /tmp/jdk-8u131-linux-x64.rpm

ENV JAVA_HOME=/usr/java/jdk1.8.0_131
ENV PATH=$PATH:$JAVA_HOME/bin
RUN alias ll='ls -l --color=auto'

ADD runjob.sh /tmp/
ADD simplejob.sh /tmp/
RUN chmod +x /tmp/*.sh
ADD rnctechentrypoint.sh /usr/local/bin/rnctechentrypoint.sh
RUN chmod +x /usr/local/bin/rnctechentrypoint.sh
WORKDIR /tmp

VOLUME /var/log/platform

ENTRYPOINT ["/usr/local/bin/rnctechentrypoint.sh"]
EXPOSE 8080
