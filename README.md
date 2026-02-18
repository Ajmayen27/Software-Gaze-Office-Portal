# Software Gaze — Office Portal (Version 2)

> Professional office portal for managing expenses, reports, and administrative tasks. Clean architecture, Docker-ready, and built for production.

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Key Features](#key-features)
3. [Architecture & Tech Stack](#architecture--tech-stack)
4. [Prerequisites](#prerequisites)
5. [Quick Start — Local](#quick-start--local)
6. [Docker / Production](#docker--production)
7. [Configuration (.env / application.yml)](#configuration-env--applicationyml)
8. [Testing](#testing)
9. [Deployment Notes](#deployment-notes)
10. [Contributing](#contributing)
11. [Roadmap](#roadmap)
12. [License](#license)
13. [Contact](#contact)

---

## Project Overview

Software Gaze — Office Portal is a web application focused on simplifying office expense management and administrative workflows. Version 2 delivers major improvements across the frontend and backend, emphasizing reliability, exportable reporting, and a cleaner user experience.

This repository contains the backend and API services powering the portal and is designed to be containerized for reproducible deployments.

---

## Key Features

* Admin user flows for expense management.
* Upload expense transaction slips as screenshots (image attachments).
* Export monthly and yearly expense reports with flexible tag-wise filtering (e.g. `paid`, `unpaid`, `all`).
* Role-based access control scaffolding (Admin, User — extendable).
* Dockerized build and run scripts for consistent environments.
* Configurable via environment variables for DB, storage, and JWT settings.

---

## Architecture & Tech Stack

**Architecture**

* Layered architecture (Controller → Service → Repository) for separation of concerns.
* DTOs used for request/response shapes to keep API contracts stable.
* Persistent storage for transactional data and optional object storage for images.

**Core Tech**

* Java + Spring Boot (REST API, configuration, security)
* JPA / Hibernate (database access)
* PostgreSQL / MySQL (configurable via environment variable)
* Docker (containerization)
* (Optional) S3-compatible object storage for storing uploaded slips

---

## Prerequisites

Make sure you have the following installed locally:

* Java 11+ or compatible JDK
* Maven or Gradle (depending on your build setup)
* Docker & Docker Compose (for containerized runs)
* Git (to clone this repo)

---

## Quick Start — Local

1. Clone the repository:

```bash
git clone https://github.com/Ajmayen27/Software-Gaze-Office-Portal.git
cd Software-Gaze-Office-Portal
```

2. Create a `.env` file in the project root (see the Configuration section below for recommended variables).

3. Build the application (Maven example):

```bash
./mvnw clean package -DskipTests
# or
mvn clean package -DskipTests
```

4. Run the JAR locally:

```bash
java -jar target/app.jar
```

5. The API should be reachable at `http://localhost:8080/` (or the value of `SERVER_PORT`).

**Notes**: If you use a different build system, replace the build commands accordingly.

---

## Docker / Production

A production-friendly Docker image is included in the repository (Dockerfile). Example workflow:

1. Build the Docker image:

```bash
docker build -t softwaregaze-backend .
```

2. Run the container (example):

```bash
docker run -d \
  --name softwaregaze \
  --env-file .env \
  -p 8080:8080 \
  softwaregaze-backend
```

3. To view logs / troubleshoot:

```bash
docker logs -f softwaregaze
```

4. When updating the image, stop & remove the old container, rebuild, then recreate:

```bash
docker stop softwaregaze && docker rm softwaregaze
docker build -t softwaregaze-backend .
docker run -d --name softwaregaze --env-file .env -p 8080:8080 softwaregaze-backend
```

---

## Configuration (.env / application.yml)

Create a `.env` file (or pass env vars via your orchestration tool). Example variables used by the app:

```
# Database
DATASOURCE_URL=jdbc:postgresql://db:5432/softwaregaze
DATASOURCE_USERNAME=your_db_user
DATASOURCE_PASSWORD=your_db_password

# Spring
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080

# JWT / Auth
JWT_SECRET=replace_with_a_strong_secret
JWT_EXPIRATION_MS=86400000

# Object storage (optional)
STORAGE_PROVIDER=s3
S3_BUCKET=my-bucket-name
S3_REGION=ap-south-1
S3_ACCESS_KEY=your_access_key
S3_SECRET_KEY=your_secret_key

# App-specific
APP_ADMIN_EMAIL=admin@example.com
```

> Keep secrets out of version control. Use a secret manager or environment variable injection for production.

---

## Testing

* Unit tests (service and repository layers) should be run via your build tool:

```bash
./mvnw test
```

* Add integration tests that spin up a test container (Testcontainers) for the database to increase confidence before deployments.

---

## Deployment Notes & Best Practices

* Use a managed DB (RDS, Cloud SQL) for production resilience.
* Serve static frontend separately (CDN or static hosting). The backend should be stateless where possible.
* Store uploaded images in an object storage and keep only references in the DB.
* Add HTTPS termination (reverse proxy, load balancer) for secure transport.
* Use health checks and readiness/liveness endpoints for orchestrators (Kubernetes / Docker Swarm).
* Use CI/CD pipelines to build, test, and push images to a container registry.

---

## Contributing

Contributions are welcome. Please follow these steps:

1. Fork the repository.
2. Create a new branch (`feature/your-feature` or `fix/issue-number`).
3. Add tests for new behavior.
4. Open a pull request with a clear description of changes.

Please follow the repository coding style and include meaningful commit messages.

---

## Roadmap

Planned features for upcoming versions:

* Enhanced reporting (export to Excel/CSV/PDF with richer filters)
* Multi-tenant support
* Improved role & permission management
* Mobile-friendly UI improvements for quick expense capture
* Audit logs and activity stream

(Work on additional features is in progress for upcoming releases.)

---

## License

This project is available under the MIT License. Update or change to your desired license as appropriate.

---

## Contact

For questions or collaboration, open an issue or contact the maintainer.

**Maintainer:** Ajmayen Fayek

---

### Appendix — Helpful Commands

```bash
# Build image
docker build -t softwaregaze-backend .

# Run container with env file
docker run -d --name softwaregaze --env-file .env -p 8080:8080 softwaregaze-backend

# Tail logs
docker logs -f softwaregaze
```

---

*Generated README — customize screenshots, API docs, and exact environment variable names to match your repo structure.*
