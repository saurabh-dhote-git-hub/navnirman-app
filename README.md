# Navnirman v0.2.0
Authentication release: customer registration (name, email, mobile, password), customer login by email/mobile, separate admin login, BCrypt password hashing, JWT bearer tokens and role-based authorization.

## Prerequisites
Java 21, Maven 3.9+, Node.js 20+, npm, Docker Desktop.

## Database
From repository root: `docker compose up -d mysql` (host port 3307).

## Backend (Git Bash)
```bash
cd backend
export JWT_SECRET='replace-with-a-random-secret-at-least-32-characters-long'
# Optional first admin bootstrap; no admin is created unless all are provided.
export ADMIN_NAME='Navnirman Admin'
export ADMIN_EMAIL='admin@example.com'
export ADMIN_PASSWORD='use-a-strong-password-at-least-12-chars'
mvn spring-boot:run
```
Health: http://localhost:8080/api/health

## Frontend
In another terminal: `cd frontend && npm install && npm run dev`, then open http://localhost:5173.

## API
- POST `/api/auth/register`: `{ "fullName":"Example User", "email":"user@example.com", "mobileNumber":"9876543210", "password":"strong-password" }`
- POST `/api/auth/login`: `{ "identifier":"email-or-mobile", "password":"strong-password" }`
- POST `/api/auth/admin/login`: same login shape, but only ADMIN accounts authenticate here.
- GET `/api/auth/me`: authenticated bearer token required.

## Security / scope
Public registration always creates CUSTOMER; there is no public admin registration. Admin bootstrap is environment-configured. Email/mobile ownership verification, password reset, refresh-token rotation, rate limiting, audit logging and production HTTPS are not included yet. Demo frontend stores JWT in sessionStorage; production should use an HttpOnly Secure SameSite cookie and appropriate CSRF protection. Never commit credentials or JWT secrets. This is a development release, not a production security certification.

## Separate admin portal (v0.2.0)
- Customer-facing UI has no Administrator access link or button.
- Admin login is available by directly navigating to `/admin/login` (local: http://localhost:5173/admin/login).
- Admin login uses `POST /api/auth/admin/login`; it accepts only existing ADMIN accounts.
- Admin APIs under `/api/admin/**` require `ROLE_ADMIN` on the backend. Hiding the route is not the security boundary; backend authorization is.
- First admin is provisioned only with server environment variables `ADMIN_NAME`, `ADMIN_EMAIL`, and `ADMIN_PASSWORD`; there is no public admin registration.
- MFA and production-grade rate limiting are not configured in this local release. Add an MFA provider and edge/API rate limiting before production exposure.
