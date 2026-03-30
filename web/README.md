<div align="center">
<img width="1200" height="475" alt="GHBanner" src="https://github.com/user-attachments/assets/0aa67016-6eaf-458a-adb2-6e31a0763ed6" />
</div>

# Run and deploy your AI Studio app

This contains everything you need to run your app locally.

View your app in AI Studio: https://ai.studio/apps/d1e3477e-8511-4a10-bebd-18e232e7905b

## Run Locally

**Prerequisites:** Node.js, Keycloak server

1. Install dependencies:
   `npm install`

2. Set the `GEMINI_API_KEY` in [.env.local](.env.local) to your Gemini API key

3. Configure Keycloak:
   - Set the following environment variables in [.env.local](.env.local):
     ```
     KEYCLOAK_URL=http://localhost:8080
     KEYCLOAK_REALM=master
     KEYCLOAK_CLIENT_ID=doc-request-app
     ```
   - Create a Keycloak client with the following settings:
     - Client ID: `doc-request-app`
     - Client Protocol: `openid-connect`
     - Valid Redirect URIs: `http://localhost:3000/*`
     - Valid Post Logout Redirect URIs: `http://localhost:3000/*`
     - Access Type: `confidential` (or `public` for development)

4. Run the app:
   `npm run dev`
