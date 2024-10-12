#!/bin/bash

# 프로젝트 클린 및 빌드
./gradlew clean bootJar -PexcludeSecrets=true

# Docker 이미지 빌드
docker build -t ekgns33/gdsc-spring:latest .

# Docker 이미지 푸시
docker push ekgns33/gdsc-spring:latest

# 완료 메시지
echo "Docker image pushed to ekgns33/gdsc-spring:latest successfully."
