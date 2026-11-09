# Navnirman v0.4.0 — Subscriptions & Protected Playback

Builds on v0.3.0. Adds subscription plan management, customer subscription history/cancellation, admin manual activation, Premium entitlement checks, and HTTP byte-range video responses.

## Premium plan in v0.4.0
- Only one plan is offered: **Monthly — ₹50 for 30 days**.
- No yearly, quarterly, or other plan is seeded or shown.
- The ₹50 amount is stored as 5,000 paise.

## Requirements
- Java 21, Maven 3.9+
- Node.js 20+
- Docker Desktop

## Start database
```bash
docker compose up -d
```
MySQL remains on host port 3307.

## Backend
Set the existing admin and secret environment variables as in v0.3.0, then:
```bash
cd backend
mvn clean test
mvn spring-boot:run
```
Health endpoint: `http://localhost:8080/api/health` (version 0.4.0).

## Frontend
```bash
cd frontend
npm install
npm run dev
```
Open `http://localhost:5173`.

## v0.4.0 features
- Monthly Premium plan: ₹50 / 30 days.
- Customer can view subscription history and cancel an active subscription.
- Admin can manually grant/cancel subscriptions.
- Premium video stream checks the signed-in user's active subscription.
- Free published videos remain publicly playable.
- Single byte-range responses (up to 1 MiB per request) for seeking/chunked playback.

## Important upgrade note
If you already ran an earlier v0.4.0 build, its old YEARLY plan may remain in your database. This version removes all non-MONTHLY plans at startup and creates the ₹50 Monthly plan if missing. Existing subscription records remain in the database, but they may reference the old plan records; back up your database before applying schema/data changes in any non-test environment.

## Important boundaries
- **No payment gateway is connected in v0.4.0.** Admin activation is manual/test activation only; it is not payment proof. Actual checkout, signed payment webhooks, reconciliation, refunds, tax invoices and renewal automation are v0.5.0 scope.
- Premium playback currently buffers the complete video into browser memory via Blob assembly. Suitable only for small local test videos; not production-grade large-video streaming.
- HTTP range support is single-range only, capped at 1 MiB per response. CDN, transcoding, HLS/DASH, signed short-lived playback URLs, watermarking, and download deterrence remain future work.
- Local video files must be backed up. Configure a durable `VIDEO_STORAGE_DIR`.
- Use HTTPS and secure secret management before public deployment. Do not use development fallback JWT secret in production.
- MySQL schema updates are managed locally with Hibernate `ddl-auto:update`; use reviewed migrations before production.

## Git release
After successful backend/frontend builds and manual tests:
```bash
git status
git add .
git commit -m "Set Navnirman Premium plan to ₹50 monthly"
git tag -a v0.4.0 -m "Navnirman v0.4.0"
git push origin main
git push origin v0.4.0
```
