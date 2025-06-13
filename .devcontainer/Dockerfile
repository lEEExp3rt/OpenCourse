# Base image: JDK 21
FROM eclipse-temurin:21-jdk-jammy

# Create group and user
RUN groupadd -g 1000 opencourse && \
    useradd -u 1000 -g opencourse -d /home/opencourse -m opencourse && \
    usermod -aG sudo opencourse && \
    mkdir -p /etc/sudoers.d && \
    echo "opencourse ALL=(ALL) NOPASSWD:ALL" > /etc/sudoers.d/opencourse && \
    chmod 0440 /etc/sudoers.d/opencourse && \
    mkdir -p /home/opencourse/OpenCourse && \
    chown -R opencourse:opencourse /home/opencourse

# Replace the default apt source with Aliyun source
RUN echo "deb http://mirrors.aliyun.com/ubuntu/ jammy main restricted universe multiverse" > /etc/apt/sources.list && \
    echo "deb http://mirrors.aliyun.com/ubuntu/ jammy-updates main restricted universe multiverse" >> /etc/apt/sources.list && \
    echo "deb http://mirrors.aliyun.com/ubuntu/ jammy-backports main restricted universe multiverse" >> /etc/apt/sources.list && \
    echo "deb http://mirrors.aliyun.com/ubuntu/ jammy-security main restricted universe multiverse" >> /etc/apt/sources.list

# Install dependencies
RUN apt-get update && \
    apt-get install -y \
    sudo \
    maven \
    fish \
    mysql-client \
    redis-tools \
    curl \
    wget \
    git \
    vim \
    zip \
    unzip \
    lsof \
    && \
    rm -rf /var/lib/apt/lists/* && \
    chsh -s /usr/bin/fish opencourse

# Change source of maven
RUN mkdir -p /home/opencourse/.m2 && \
    echo '<?xml version="1.0" encoding="UTF-8"?>\
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">\
  <mirrors>\
    <mirror>\
      <id>aliyun</id>\
      <name>Aliyun Maven Repository</name>\
      <url>https://maven.aliyun.com/repository/public</url>\
      <mirrorOf>central</mirrorOf>\
    </mirror>\
  </mirrors>\
</settings>' > /home/opencourse/.m2/settings.xml && \
    chown -R opencourse:opencourse /home/opencourse/.m2

# Working directory
WORKDIR /home/opencourse/OpenCourse

# Default user
USER opencourse

# Default command
CMD ["sleep", "infinity"]