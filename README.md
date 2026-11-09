# Navnirman v0.3.0 — Admin Dashboard & Video Management

This release builds on v0.2.0 authentication. It adds admin-only category/video management, local video-file storage, a public published-video catalogue, and dashboard counts.

## Requirements
- Java 21
- Maven 3.9+
- Node.js 20+
- Docker Desktop

## 1. Database
From the project root:
```bash
docker compose up -d
```
Keep the existing MySQL host mapping `3307:3306` if your Windows MySQL already uses 3306.

## 2. Backend
```bash
cd backend
mvn clean test
mvn package
```
Set your admin credentials before starting. Use your own strong password (at least 12 characters); do not commit secrets:
```bash
export ADMIN_NAME="Navnirman Admin"
export ADMIN_EMAIL="admin@example.com"
export ADMIN_PASSWORD="replace-with-a-strong-private-password"
export JWT_SECRET="replace-with-a-random-secret-at-least-32-characters"
export VIDEO_STORAGE_DIR="../storage/videos"
mvn spring-boot:run
```
Windows PowerShell equivalent:
```powershell
$env:ADMIN_NAME="Navnirman Admin"
$env:ADMIN_EMAIL="admin@example.com"
$env:ADMIN_PASSWORD="replace-with-a-strong-private-password"
$env:JWT_SECRET="replace-with-a-random-secret-at-least-32-characters"
$env:VIDEO_STORAGE_DIR="../storage/videos"
mvn spring-boot:run
```
The bootstrap creates the admin only if the email is not already registered. Existing customer/admin records remain in MySQL.

## 3. Frontend
In a second terminal:
```bash
cd frontend
npm install
npm run dev
```
Open http://localhost:5173. Admin sign-in is at http://localhost:5173/admin/login. After admin login, open Dashboard.

## 4. v0.3.0 features
- Dashboard totals: videos, published, drafts, customers, categories.
- Create and delete categories.
- Upload video files to local disk; metadata is saved in MySQL.
- Marathi, Hindi and English titles/descriptions.
- Free/Premium flag and publish/unpublish controls.
- Public catalogue shows published videos only.
- Admin APIs require `ROLE_ADMIN`.
- Public playback for published Free videos through `GET /api/videos/{id}/stream`; Premium playback is blocked until subscription entitlement is implemented.
- Customer catalogue includes a Play button and in-page HTML5 video player.

## Important scope and safety notes
- Storage defaults to `./storage/videos` relative to the backend working directory. Set `VIDEO_STORAGE_DIR` to a durable path before production.
- Upload limit is 2 GB. Confirm adequate disk space and backup the storage directory.
- The v0.3.0 `Premium` flag is metadata only; subscription entitlement enforcement and payment verification are planned for v0.4/v0.5.
- Free published video playback is public in this release; Premium video playback returns 403 until subscription entitlement is implemented. The endpoint returns a file response; HTTP range/seek support, CDN, transcoding, watermarking, signed playback URLs, and stronger anti-download controls are future work.
- Thumbnail is currently an externally hosted URL. Local thumbnail upload is not included.
- Use HTTPS and secure secret management before public deployment. Do not use the development fallback JWT secret in production.
- `ddl-auto: update` is retained for local development. Use reviewed database migrations before production.
- No public admin registration endpoint exists.

## Git release workflow
From the repository root:
```bash
git status
git add .
git commit -m "Implement v0.3.0 admin dashboard and video management"
git tag -a v0.3.0 -m "Navnirman v0.3.0"
git push origin main
git push origin v0.3.0
```
Only tag after successful local builds and tests.
