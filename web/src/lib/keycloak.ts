import Keycloak from "keycloak-js";

const keycloakConfig = {
  url: import.meta.env.VITE_KEYCLOAK_URL || "http://localhost:8080",
  realm: import.meta.env.VITE_KEYCLOAK_REALM || "doc-request",
  clientId: import.meta.env.VITE_KEYCLOAK_CLIENT_ID || "doc-request-api",
};

console.log("Keycloak Config:", keycloakConfig);

const keycloak = new Keycloak(keycloakConfig);

// Track if Keycloak has been initialized
let isInitialized = false;

export const initializeKeycloak = async () => {
  if (!isInitialized) {
    try {
      await keycloak.init({
        onLoad: "check-sso",
        checkLoginIframe: false,
        pkceMethod: "S256",
      });
      isInitialized = true;
    } catch (error) {
      // isInitialized = true;
      console.error("Keycloak initialization failed:", error);
    }
  }
  return keycloak;
};

export { keycloakConfig };
export default keycloak;
export { isInitialized };
