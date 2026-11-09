# Navnirman

Premium multilingual video-learning platform for History, Technology, Science and other knowledge categories.

## v0.1.0 – Foundation

Initial repository foundation:
- React web client
- Java 21 / Spring Boot backend
- MySQL database via Docker Compose
- Health endpoint
- Environment-based configuration

## Prerequisites
- Git
- Java 21
- Maven 3.9+
- Node.js 20+
- Docker Desktop (for MySQL)

## Start the database
```bash
docker compose up -d mysql
```

## Start the backend
```bash
cd backend
mvn spring-boot:run
```
Backend health check: http://localhost:8080/api/health

## Start the frontend
```bash
cd frontend
npm install
npm run dev
```
Frontend: http://localhost:5173

## Configuration
Backend defaults are for local development. Override with environment variables:
`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`.

Never commit production credentials, payment secrets, JWT secrets, or uploaded videos.

## Versioning
Use semantic version tags, for example:
```bash
git tag -a v0.1.0 -m "Navnirman foundation"
git push origin v0.1.0
```
