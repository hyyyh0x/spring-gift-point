#!/bin/bash

GITHUB_ID = "hyyyh0x"
APP_NAME = "spring-gift"
PROJECT_NAME = "spring-gift-point"
PROJECT_VERSION = "0.0.1"
REPO_URL = "https://github.com/${GITHUB_ID}/${PROJECT_NAME}"
BRANCH = "step1_1"
DIR="/home/ubuntu"
JAR_FILE="${APP_NAME}-${PROJECT_VERSION}-SNAPSHOT.jar"

port=$(lsof -i :8080 -t)
if [ -n "$port" ]; then
    kill -9 $port
fi

cd ${DIR}

if [ ! -d "${PROJECT_NAME}" ]; then
    git clone -b ${BRANCH} --single-branch ${REPO_URL}
else
    cd ${PROJECT_NAME}
    git pull origin ${BRANCH}
fi

cd ${DIR}/${PROJECT_NAME}
./gradlew bootJar
cd build/libs
java -jar ${JAR_FILE} &
