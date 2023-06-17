import Keycloak from "keycloak-js";

const keycloakConfing = new Keycloak({
  url: "http://localhost:8080/",
  realm: "it-company-realm",
  clientId: "PKIClient",
  enableLogging: true,
});

export default keycloakConfing;