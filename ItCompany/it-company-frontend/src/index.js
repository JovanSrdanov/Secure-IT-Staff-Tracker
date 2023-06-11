import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import {BrowserRouter} from 'react-router-dom';
import {createTheme, ThemeProvider} from '@mui/material/styles';
import { ReactKeycloakProvider } from '@react-keycloak/web';
import keycloakConfing from './Config/keycloakConfing';

const darkTheme = createTheme({
    palette: {
        mode: 'dark',
    },
});
//<KeycloakProvider></KeycloakProvider>
const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <ThemeProvider theme={darkTheme}>
    <ReactKeycloakProvider authClient={keycloakConfing}>
      <BrowserRouter>
        <App></App>
      </BrowserRouter>
    </ReactKeycloakProvider>
  </ThemeProvider>
);

//KeycloakService.initKeycloak(root);