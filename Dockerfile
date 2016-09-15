# Copyright (c) 2012-2016 Codenvy, S.A., Red Hat, Inc
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
#
# Contributors:
#   Mario Loriedo
#   Codenvy S.A
#
# To build it, run in the repository root:
#  `docker build -t codenvy/che-server .`
#
# To run it:
#  docker run --net=host \
#             --name che \
#             -v /var/run/docker.sock:/var/run/docker.sock \
#             -v /home/user/che/lib:/home/user/che/lib-copy \
#             -v /home/user/che/workspaces:/home/user/che/workspaces \
#             -v /home/user/che/storage:/home/user/che/storage \
#             codenvy/che-server
#           
FROM alpine:3.4
#FROM maven:alpine  used to migrate to pod

ENV LANG=C.UTF-8 \
    JAVA_HOME=/usr/lib/jvm/default-jvm/jre \
    PATH=${PATH}:${JAVA_HOME}/bin \
    CHE_HOME=/home/user/che \
    DOCKER_VERSION=1.6.0 \
    DOCKER_BUCKET=get.docker.com

RUN echo "http://dl-4.alpinelinux.org/alpine/edge/community" >> /etc/apk/repositories && \
    apk upgrade --update && \
    apk add --update ca-certificates curl openssl openjdk8 sudo bash && \
    curl -sSL "https://${DOCKER_BUCKET}/builds/Linux/x86_64/docker-${DOCKER_VERSION}" -o /usr/bin/docker && \
    chmod +x /usr/bin/docker && \
    addgroup -S user -g 1000 && \
    adduser -S user -h /home/user -s /bin/bash -G root -u 1000 -D && \
    addgroup -S docker -g 101 && \
    adduser user docker && \
    adduser user user && \
    adduser user users && \
    addgroup -g 50 -S docker4mac && \
    adduser user docker4mac && \
    echo "%root ALL=(ALL) NOPASSWD: ALL" >> /etc/sudoers && \
    rm -rf /tmp/* /var/cache/apk/* 
    

EXPOSE 8000 8080

USER user

# must change that to build the fat jar before build the image
# ADD assembly/assembly-main/target/eclipse-che-*/eclipse-che-* /home/user/che/
RUN curl https://dl.dropboxusercontent.com/u/7034677/eclipse-che.tar.gz -o /tmp/eclipse-che.tar.gz
RUN tar -xzvf /tmp/eclipse-che.tar.gz -C /home/user/che
RUN rm /tmp/eclipse-che.tar.gz

#ADD https://dl.dropboxusercontent.com/u/7034677/eclipse-che.tar.gz /home/user/che

ENV CHE_HOME /home/user/che

ENTRYPOINT [ "/home/user/che/bin/che.sh start", "-c" ]

CMD [ "run" ]
