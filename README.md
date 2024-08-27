# GDSC Konkuk Internal Platform

## 소개

본 프로그램은 GDSC Konkuk 운영진이 사용할 동아리 관리 시스템입니다.
이 프로그램을 통해 운영진은 회원 정보를 관리하고 공지 사항을 전달할 수 있습니다.
동아리를 운영하며 기획 및 진행할 다양한 이벤트 정보를 기록하고 회고를 작성할 수 있습니다.

### 동기

GDSC Konkuk이 매 기수 성장하며 건국대학교의 활발한 학생 개발 커뮤니티가 되기를 기원합니다.
이를 위해서 운영진은 회원과 유기적으로 소통하며 회원이 동아리 활동에 적극적으로 참여하며 성장할 수 있도록 지원해야 합니다.
하지만 동아리 운영은 그리 간단하지만은 않아서 이 모든 일들은 많은 시간과 노력이 필요합니다.
우리는 GDSC Konkuk의 개발팀으로서 관리 시스템을 개발하여 이러한 문제를 해결하고자 합니다.

### 기능

- 회원 관리
- QR을 통한 이벤트 출결
- E-mail 예약 발송을 통한 공지 발송
- 이벤트 및 회고 Archiving

## 구현

### Architecture

![image](https://github.com/user-attachments/assets/2fab3b13-1aec-4c2f-ad23-3892be3d617d)

### 기술 스택

> Web Frontend는 다음 [repo](https://github.com/gdsc-konkuk/platform-core-front)를 참고하세요!

- Java 17 / Gradle
- Spring Boot 3.3.1
- Spring Data JPA / MySQL 8 (H2 for test)
- Spring Security (Traditional Session & Google OIDC)
- Spring Rest Docs / JUnit 5
- Spring Mail & S3

## ETC

### Developers

- [goldentrash](https://github.com/goldentrash)
    - 출석
    - 이벤트 CRUD
- [ekgns33](https://github.com/ekgns33)
    - 메일 예약 발송
    - 회원 CRUD

### In the future...

- Study Archiving
- SSO 개발, General Member와 Guest에게 제한된 인가 제공
