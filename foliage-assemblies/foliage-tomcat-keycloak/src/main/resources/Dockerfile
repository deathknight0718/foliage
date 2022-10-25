# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements. See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership. The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License. You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied. See the License for the
# specific language governing permissions and limitations
# under the License.
#

FROM openjdk:8-jre AS artifactbase
LABEL maintainer="Capelon IoT Tomcat <deathknight@qq.com>"

ARG TOMCAT_WEBAPP
ARG TOMCAT_VERSION

ENV TOMCAT_BINARY ${TOMCAT_WEBAPP}-${TOMCAT_VERSION}.tar.gz
ENV TOMCAT_BASE_DIR /opt
ENV TOMCAT_HOME ${TOMCAT_BASE_DIR}/${TOMCAT_WEBAPP}
ENV TOMCAT_PID_DIR=${TOMCAT_HOME}/run
ENV TOMCAT_LOG_DIR=${TOMCAT_HOME}/logs

COPY $TOMCAT_BINARY $TOMCAT_BASE_DIR
RUN tar -zxvf ${TOMCAT_BASE_DIR}/${TOMCAT_BINARY} -C ${TOMCAT_BASE_DIR}/ \
    && rm ${TOMCAT_BASE_DIR}/${TOMCAT_BINARY} \
    && ln -s ${TOMCAT_BASE_DIR}/${TOMCAT_WEBAPP}-${TOMCAT_VERSION} ${TOMCAT_HOME}

VOLUME ${TOMCAT_LOG_DIR} ${TOMCAT_HOME}/conf ${TOMCAT_HOME}/webapps

# Web HTTP(s) & Socket Site-to-Site Ports
EXPOSE 7080 7443

WORKDIR ${TOMCAT_HOME}

# Apply configuration and start Tomcat
#
# We need to use the exec form to avoid running our command in a subshell and omitting signals,
# thus being unable to shut down gracefully:
# https://docs.docker.com/engine/reference/builder/#entrypoint
#
# Also we need to use relative path, because the exec form does not invoke a command shell,
# thus normal shell processing does not happen:
# https://docs.docker.com/engine/reference/builder/#exec-form-entrypoint-example
ENTRYPOINT ["./bin/catalina.sh", "run"]
