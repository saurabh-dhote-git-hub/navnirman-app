# Navnirman v0.6.0 — Optimized Video Delivery

This release builds on v0.5.0 and improves local video delivery while retaining the existing React, Spring Boot, MySQL, authentication, subscription and Razorpay flows.

## What's changed
- Video files are streamed from disk using `StreamingResponseBody` and a 64 KB server-side buffer.
- Supports full-file `200 OK` responses and single-range `206 Partial Content` responses, including suffix ranges.
- Preserves published-video checks and Premium subscription entitlement checks on every stream request.
- Keeps local disk storage as the default; no cloud-storage account or recurring storage cost is required.
- Repairs literal `\\n` sequences embedded in Java source files and fixes the over-escaped Content-Range regex in the frontend.

## Requirements
- Java 21, Maven 3.9+
- Node.js 20+
- Docker Desktop

## Start locally
From the project root, start MySQL:
```bash
docker compose up -d
```
Backend:
```bash
cd backend
mvn clean test
mvn spring-boot:run
```
Frontend, in another terminal:
```bash
cd frontend
npm install
npm run dev
```
Open http://localhost:5173.

## Video delivery notes
- Free published videos remain publicly playable.
- Premium videos still require a signed-in user with an active subscription.
- The frontend's existing Premium playback flow still collects chunks into a Blob. This release reduces backend response buffering, but it does not yet provide fully memory-efficient browser playback for very large Premium videos.
- Local storage remains at the configured `VIDEO_STORAGE_DIR` (default `./storage/videos`). Back up this directory together with the MySQL database.

## Validation
Run `mvn clean test` and `npm run build` on your machine before tagging or deploying. The backend build has not been executed in the packaging environment.
