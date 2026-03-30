import React, { createContext, useContext, useEffect, useState } from "react";
import keycloak, { keycloakConfig } from "@/lib/keycloak";

interface KeycloakContextType {
  isAuthenticated: boolean;
  isLoading: boolean;
  user: any;
  login: () => void;
  logout: () => void;
  keycloak: any;
}

const KeycloakContext = createContext<KeycloakContextType | undefined>(
  undefined,
);

export const KeycloakProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [user, setUser] = useState<any>(null);

  useEffect(() => {
    const initKeycloak = async () => {
      try {
        const authenticated = await keycloak.init({
          onLoad: "check-sso",
          silentCheckSsoRedirectUri: `${window.location.origin}/silent-check-sso.html`,
          pkceMethod: "S256",
        });

        setIsAuthenticated(authenticated);
        setUser(keycloak.tokenParsed);
        setIsLoading(false);

        if (authenticated) {
          keycloak.onTokenExpired = () => {
            keycloak
              .updateToken(70)
              .then((refreshed) => {
                if (refreshed) {
                  setUser(keycloak.tokenParsed);
                }
              })
              .catch(() => {
                logout();
              });
          };
        }
      } catch (error) {
        console.error("Keycloak initialization error:", error);
        setIsLoading(false);
      }
    };

    initKeycloak();
  }, []);

  const login = () => {
    keycloak.login({
      redirectUri: window.location.origin,
    });
  };

  const logout = () => {
    keycloak.logout({
      redirectUri: window.location.origin,
    });
  };

  return (
    <KeycloakContext.Provider
      value={{
        isAuthenticated,
        isLoading,
        user,
        login,
        logout,
        keycloak,
      }}
    >
      {children}
    </KeycloakContext.Provider>
  );
};

export const useKeycloak = () => {
  const context = useContext(KeycloakContext);
  if (context === undefined) {
    throw new Error("useKeycloak must be used within a KeycloakProvider");
  }
  return context;
};
