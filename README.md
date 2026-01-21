## 1. 프로젝트 소개
- 프로젝트 명 : Corevia(협력을 통하여)
- 프로젝트 기간 : 2025.09.15 ~ 2026.01.10 (5개월)
- 구성원: 고명환,김현희
- 서비스 URL : http://13.124.195.150/login


## 2. 서비스 대상
- 전사 직원 → 일반 사용자, 공지 확인, 문서 열람/협업<br>
- 부서/팀 → 부서별 자료 관리, 팀 협업, 일정 관리<br>
- 관리자 → 사용자 권한 관리, 승인, 보고서 확인<br>


## 3. 기획배경
- Corevia 인트라넷은 사내 인트라넷은 외부에서는 접근할 수 없고, 실제 조직 구성원만 접속할 수 있도록 설계되어야 합니다.
- 이는 민감한 업무 자료, 전자결재, 내부 정책 등 중요한 정보가 외부로 유출되는 것을 방지하고, 조직 내부의 신뢰성을 확보하기 위함입니다.
- 또한 사내 인트라넷은 분산된 정보와 문서, 협업 도구를 단일 플랫폼에서 통합하여 업무 효율성을 높이고, 필요한 자료와 소통을 빠르게 관리할 수 있도록 돕습니다.
- 결과적으로, 보안을 기반으로 안전하게 운영되는 사내 인트라넷은 업무 생산성을 높이는 동시에 조직 구성원 간 협업과 소통을 원활하게 만드는 핵심 입니다.

## 4. 기술스택
- ## Language
   # <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white"/> 
- ## Framework
   # <img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"/> 
- ## Data Access
   # <img src="https://img.shields.io/badge/JPA-59666C?style=for-the-badge"/>  <img src="https://img.shields.io/badge/JdbcTemplate-6DB33F?style=for-the-badge&logo=spring&logoColor=white"/>
- ## API 
   # <img src="https://img.shields.io/badge/RESTful_API-02569B?style=for-the-badge"/> <img src="https://img.shields.io/badge/Open_API-0A66C2?style=for-the-badge"/> 
- ## Security
   # <img src="https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white"/>  <img src="https://img.shields.io/badge/Session-4A90E2?style=for-the-badge"/>  <img src="https://img.shields.io/badge/CSRF-000000?style=for-the-badge"/>
- ## Real-Time
   # <img src="https://img.shields.io/badge/WebSocket-010101?style=for-the-badge&logo=socketdotio&logoColor=white"/> 
- ## Database
   # <img src="https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=mariadb&logoColor=white"/> 
- ## Infra / Deploy
   # <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white"/>  <img src="https://img.shields.io/badge/Nginx-009639?style=for-the-badge&logo=nginx&logoColor=white"/> <img src="https://img.shields.io/badge/AWS-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white"/> 
- ## Collaboration & Environment
   # <img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white"/> <img src="https://img.shields.io/badge/Slack-4A154B?style=for-the-badge&logo=slack&logoColor=white"/> <img src="https://img.shields.io/badge/IntelliJ_IDEA-000000?style=for-the-badge&logo=intellijidea&logoColor=white"/> <img src="https://img.shields.io/badge/VS_Code-007ACC?style=for-the-badge&logo=visualstudiocode&logoColor=white"/> 


## 5. ERD
<img width="7644" height="5826" alt="corevia_last_db" src="https://github.com/user-attachments/assets/642d149a-35b7-4049-9a74-96e224bf63a2" />


## 6. 프로젝트 폴더 구조
```
📁 프로젝트 루트
├─ ⚙️ .env
├─ ⚙️ .gitattributes
├─ ⚙️ .gitignore
├─ 🐘 build.gradle
├─ 🐳 docker-compose.yml
├─ 🐳 Dockerfile
├─ ⚙️ gradlew
├─ ⚙️ gradlew.bat
├─ 📄 HELP.md
├─ 🐘 settings.gradle
└─ 📁 src
   ├─ 📁 main
   │  ├─ 📁 java
   │  │  └─ 📁 com
   │  │     └─ 📁 example
   │  │        └─ 📁 corenet
   │  │           ├─ 📁 admin
   │  │           ├─ 📁 client
   │  │           ├─ 📁 common
   │  │           ├─ 📁 entity
   │  │           ├─ 📁 showpagecont
   │  │           ├─ 📄 CorenetApplication.java
   │  │           ├─ 📄 CustomAuthenticationFailureHandler.java
   │  │           ├─ 📄 CustomAuthenticationSuccessHandler.java
   │  │           ├─ 📄 CustomUserDetailsService.java
   │  │           ├─ 📄 IpUtil.java
   │  │           ├─ 📄 PasswordEncodeRunner.java
   │  │           ├─ 📄 SecurityConfig.java
   │  │           └─ 📄 WebSocketConfig.java
   │  └─ 📁 resources
   │     ├─ 📁 static
   │     └─ 📁 templates
   │        ├─ 📁 admin
   │        ├─ 📁 fragments
   └─       └─ 📁 user
 

```


## 7. 시연 영상 
[사용자]
- 메일  
  ![메일_자막](https://github.com/user-attachments/assets/c794b08f-7706-40c2-a33c-1b2627eeb630)


- 메신저  
  ![1대1 통신 자막](https://github.com/user-attachments/assets/1111fead-eb23-46ac-aa42-cc1fb80e5246)
  ![그룹채팅 3명](https://github.com/user-attachments/assets/c84ab392-1761-4690-9ca6-d8f4c44b8726)

- 게시판  
  ![게시판_자막_part1](https://github.com/user-attachments/assets/6d867daf-64c9-4422-93df-2c0ba56a38ce)
  ![게시판_자막_part2](https://github.com/user-attachments/assets/9e5599f1-1c7c-4d13-ae44-7269b0929f11)


- 전자결재  
  ![전자결재_자막_part1](https://github.com/user-attachments/assets/bf711f08-e471-41ae-bc42-5517aece8705)
  ![전자결재_자막_part2](https://github.com/user-attachments/assets/9d1c0b4d-d7d8-45d4-b6bb-994826e6ce83)


- 개인정보 변경  
  ![마이페이지 비버 변경_자막](https://github.com/user-attachments/assets/318e543e-51c0-4b6d-adbb-6e528665ce80)


[관리자]
- 대시보드
  ![관리자 -  사용자가_ 자막](https://github.com/user-attachments/assets/096f247a-3bd6-4e6e-b6c9-0d87087d4d19)
  ![관리자 대시보드_자막](https://github.com/user-attachments/assets/40351050-aa37-4575-8400-c26743125972)

- 권한 관리  
  ![관리자 인사관리_자막](https://github.com/user-attachments/assets/67c7ee28-4f39-4f84-bb54-ef668cb8a56b)  
  ![관리자 부서관리_자막](https://github.com/user-attachments/assets/2bcee773-efdc-4073-ae49-711a4abbf81d)

- 전자결재 관리  
  ![전자결재_자막](https://github.com/user-attachments/assets/a961370d-ca9f-48a4-b53c-628334b8d225)

  ![전자결재 문서함   삭제 자막](https://github.com/user-attachments/assets/09a5f44b-12dc-4f34-aaee-7b22ad86ffc2)

- 게시판 관리  
  ![게시판추가_자막](https://github.com/user-attachments/assets/6c16cc81-42c7-487b-a492-7fe4d6c42d1d)

  ![게시판 삭제_ 자막](https://github.com/user-attachments/assets/1422332a-298c-4f2b-8745-690383c68b89)

  ![관리자 페이지 게시글_자막](https://github.com/user-attachments/assets/0a457de6-ddb6-44cc-922f-9628ac8fd8fd)

- 보고서 확인 & 승인  
  ![게시판_자막](https://github.com/user-attachments/assets/d8d8b60a-0d9e-418b-b16e-978669924182)

- 보안 확인  
  ![systemadminlogin_part1](https://github.com/user-attachments/assets/d6e68c83-0e6a-4ea2-8966-d943133465b8)




## 8. 팀구성 
| 팀명 | 팀장 | 팀원 |
| - | - | - |
| 9k9k | 고명환 | 김현희 |  

