# StudentHub — Jenkins + Docker classroom project

Java 17 / Spring Boot / Maven / Thymeleaf / PostgreSQL. Student CRUD dashboard with server-side validation.

## 1. Prepare Docker network, volume and PostgreSQL

```bash
docker network create studenthub-net
docker volume create studenthub-pgdata
```

Choose a strong password and store it locally (do not commit it). For a quick interactive lab:

```bash
read -rsp 'PostgreSQL password: ' DB_PASSWORD; echo
export DB_PASSWORD
docker run -d --name studenthub-db --network studenthub-net \
  --restart unless-stopped \
  -e POSTGRES_DB=studenthub -e POSTGRES_USER=studenthub \
  -e POSTGRES_PASSWORD="$DB_PASSWORD" \
  -v studenthub-pgdata:/var/lib/postgresql/data \
  postgres:16
```

Note: the password is visible in container metadata to users with Docker access. Use a dedicated training host and a secrets solution for production.

Check database readiness:

```bash
docker exec studenthub-db pg_isready -U studenthub -d studenthub
```

## 2. Build the Java application

Install JDK 17 and Maven 3.8+ on the build machine, then:

```bash
mvn clean package
```

This creates `target/studenthub-1.0.0.jar`. Maven dependencies require network access on the first build.

## 3. Build the image and run the application

```bash
docker build -t studenthub:1.0.0 .
docker run -d --name studenthub-app --network studenthub-net \
  --restart unless-stopped -p 8081:8081 \
  -e DB_URL=jdbc:postgresql://studenthub-db:5432/studenthub \
  -e DB_USERNAME=studenthub -e DB_PASSWORD="$DB_PASSWORD" \
  studenthub:1.0.0
```

Browse to http://localhost:8081/ (on the Docker host). For a remote EC2 host, use SSH port forwarding instead of opening the app to the internet:

```bash
ssh -i jenkins-key.pem -L 8081:localhost:8081 ec2-user@YOUR_EC2_PUBLIC_IP
```

Inspect logs:

```bash
docker logs --tail 100 studenthub-app
```

## 4. Verify database persistence

Create a student through the UI. Then inspect the PostgreSQL table:

```bash
docker exec -it studenthub-db psql -U studenthub -d studenthub -c 'SELECT id,name,email,course FROM students;'
```

Recreate only the application container and check that records remain. Do **not** delete `studenthub-pgdata` if you want to keep the database.

## Jenkins training flow

1. Maven project `01-student-compile`: `clean compile`
2. Maven project `02-student-test`: `clean test`
3. Maven project `03-student-package`: `clean package`; archive `target/*.jar`
4. Freestyle `04-student-docker-deploy`: copy archived JAR from the correct upstream build, check out the matching Dockerfile revision, build image, and replace application container.

Each Maven job checks out the same pinned commit. A downstream job must not blindly build the latest `master`/`main` revision. The fourth job requires the JAR and Dockerfile in the same Docker build context. Docker access is highly privileged; use an isolated deployment agent.

## Classroom limitations

- Authentication/authorization and CSRF protection for forms are **not included**. Do not expose the application publicly with real student information.
- `ddl-auto=update` is for training; use migrations for production.
- Unit test is intentionally minimal; expand it with controller and repository tests.
- Pin Docker image digests and update dependencies for production.
