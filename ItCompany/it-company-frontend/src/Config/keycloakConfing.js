import Keycloak from "keycloak-js";

const keycloakConfing = new Keycloak({
  url: "http://localhost:8080/",
  realm: "it-company-realm",
  clientId: "it-company",
  enableLogging: true,
});

export default keycloakConfing;