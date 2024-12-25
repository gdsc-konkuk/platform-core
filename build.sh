#!/bin/bash

# 프로젝트 클린 및 빌드
./gradlew clean bootJar -PexcludeSecrets=true

# Docker 이미지 빌드 & 푸시
docker buildx build --platform linux/amd64,linux/arm64 -t goldentrash/gdsc-internal:latest . --push

# 완료 메시지
echo "Docker image pushed to goldentrash/gdsc-internal:latest successfully."
