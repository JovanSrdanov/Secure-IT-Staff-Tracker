import React, { useEffect, useState } from 'react';
import Keycloak from 'keycloak-js';
import keycloakConfing from '../Config/keycloakConfing';

export const KeycloakContext = React.createContext();

const KeycloakProvider = ({ children }) => {
  const [keycloak, setKeycloak] = useState(null);

  useEffect(() => {
    const initKeycloak = async () => {
      const keycloakInstance = Keycloak(keycloakConfing);
      try {
        await keycloakInstance.init({
        onLoad: "check-sso",
        silentCheckSsoRedirectUri:
          window.location.origin + '/silent-check-sso.html',
      });
      setKeycloak(keycloakInstance);
      } catch (error) {
        console.log("Keycloak initialization error:", error);
      }
    };

    initKeycloak();
  }, []);

  return (
    <KeycloakContext.Provider value={keycloak}>
      {keycloak ? children : children}
    </KeycloakContext.Provider>
  );
};

export default KeycloakProvider;