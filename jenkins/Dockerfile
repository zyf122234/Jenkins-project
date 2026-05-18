FROM jenkins/jenkins:lts-jdk17

USER root

# 安装 Docker CLI（用于构建镜像）
RUN apt-get update && \
    apt-get install -y apt-transport-https ca-certificates curl gnupg lsb-release && \
    curl -fsSL https://download.docker.com/linux/debian/gpg | gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg && \
    echo "deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/debian $(lsb_release -cs) stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null && \
    apt-get update && \
    apt-get install -y docker-ce-cli && \
    rm -rf /var/lib/apt/lists/*

# 安装 Maven
ARG MAVEN_VERSION=3.9.6
RUN curl -fsSL https://dlcdn.apache.org/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz | tar -xz -C /opt && \
    ln -s /opt/apache-maven-${MAVEN_VERSION}/bin/mvn /usr/bin/mvn

# 安装 Chinese font support (for Chinese characters in logs)
RUN apt-get update && \
    apt-get install -y fonts-wqy-zenhei fonts-wqy-microhei && \
    rm -rf /var/lib/apt/lists/*

ENV MAVEN_HOME=/opt/apache-maven-${MAVEN_VERSION}

USER jenkins
