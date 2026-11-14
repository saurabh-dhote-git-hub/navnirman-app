# Navnirman v0.5.0 — Razorpay Payment Integration

This release adds Razorpay Standard Checkout for the single Monthly Premium plan: **₹50 / 30 days**.

## Requirements
- Java 21, Maven 3.9+
- Node.js 20+
- Docker Desktop
- Razorpay account with Test Mode API keys

## Configure Razorpay Test Mode
Generate Test Mode API keys in the Razorpay Dashboard. Never put the Key Secret or Webhook Secret in frontend code or commit them to Git.

Git Bash (from project root):
```bash
export RAZORPAY_KEY_ID="rzp_test_your_key_id"
export RAZORPAY_KEY_SECRET="your_test_key_secret"
export RAZORPAY_WEBHOOK_SECRET="your_webhook_secret"
```
Keep these variables set in the same terminal session used to start Spring Boot. On Windows PowerShell, use `$env:RAZORPAY_KEY_ID="..."` etc.

## Start database and backend
```bash
docker compose up -d
cd backend
mvn clean test
mvn spring-boot:run
```

## Start frontend
In another terminal:
```bash
cd frontend
npm install
npm run dev
```
Open http://localhost:5173, sign in as a customer, open Premium and click **Pay ₹50 securely**.

## Payment flow
1. Authenticated customer asks backend to create an order.
2. Backend sends the fixed ₹50 / INR amount to Razorpay Orders API. The browser cannot choose the amount.
3. Razorpay Checkout collects payment details.
4. Browser sends order ID, payment ID and checkout signature to backend.
5. Backend verifies HMAC signature, fetches payment from Razorpay API using server credentials, checks order ID, amount, currency and `captured` status.
6. Only then does the backend mark the order paid and grant the 30-day subscription.
7. A signed `payment.captured` webhook can also activate the subscription. Repeated callbacks/webhooks are idempotent for an already-paid order.

## Webhook setup
For local testing, Razorpay cannot reach localhost. Deploy the backend to a public HTTPS URL or use a secure tunnel for development. In Razorpay Dashboard configure:
- Webhook URL: `https://YOUR_PUBLIC_HOST/api/payments/webhook`
- Secret: same value as `RAZORPAY_WEBHOOK_SECRET`
- Event: `payment.captured`
- Optional event: `payment.failed`

Do not use a production webhook secret in source control. Ensure Razorpay Dashboard auto-capture is enabled so payment status reaches `captured`.

## Security notes
- Order creation and verification require a signed-in customer.
- Webhook endpoint is public but validates `X-Razorpay-Signature` against the raw request body.
- Amount, currency and plan duration are fixed and validated on the backend.
- The Key Secret and Webhook Secret are backend-only.
- Admin manual grant remains available for support/testing; it is not proof of payment.
- Configure HTTPS, production keys, durable backups, logging/monitoring, and rate limiting before launch.
- Do not accept real payments until the full test-mode flow and webhook are validated.

## Troubleshooting
- `Razorpay is not configured`: check `RAZORPAY_KEY_ID` and `RAZORPAY_KEY_SECRET` in the backend process environment.
- Order creation error: confirm Test Mode keys belong to the same Razorpay account and account is enabled for payment gateway.
- Payment verification error: enable auto-capture and verify the payment is `captured` in the Razorpay Dashboard.
- Webhook signature error: ensure webhook secret exactly matches the secret configured for this webhook.

## Git release
After successful local builds and test-mode payment:
```bash
git status
git add .
git commit -m "Implement v0.5.0 Razorpay payments"
git tag -a v0.5.0 -m "Navnirman v0.5.0"
git push origin main
git push origin v0.5.0
```
