# Task Manager (java-project-99)

### Hexlet tests and linter status:
[![Actions Status](https://github.com/irinakomarchenko/java-project-99/actions/workflows/hexlet-check.yml/badge.svg)](https://github.com/irinakomarchenko/java-project-99/actions)

![Java CI](https://github.com/irinakomarchenko/java-project-99/actions/workflows/ci.yml/badge.svg)

[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=irinakomarchenko_java-project-99&metric=coverage)](https://sonarcloud.io/summary/new_code?id=irinakomarchenko_java-project-99)


##  About the project
**Task Manager** is an educational project implemented with **Java 21** and **Spring Boot 3**.  
It allows users to register, create tasks, assign statuses and labels.

---

## Deployment
The application is deployed on Render:  
 [https://app-waos.onrender.com](https://app-waos.onrender.com)

---

## Tech stack
- Java 21
- Spring Boot 3 (MVC, Security, Data JPA)
- PostgreSQL + H2 (for tests)
- Hibernate ORM
- Gradle
- JUnit 5, Mockito
- Testcontainers
- SonarCloud (code quality analysis)
- Render (deployment)
- Sentry (error tracking)

---

## Features
- User registration and authentication
- CRUD for tasks:
- create, view, update, delete
- Manage task statuses (e.g., `New`, `In Progress`, `Done`)
- Add and manage labels to organize tasks
- Search and filter tasks by status, author, or executor
- Integrated with Sentry for error monitoring

---

## Run locally

### Clone the repository
```bash
git clone https://github.com/irinakomarchenko/java-project-99.git
cd java-project-99
```

## An example of the application's operation

[▶️ Watch a demo on YouTube](https://www.youtube.com/watch?v=U21lSgNgjQY)