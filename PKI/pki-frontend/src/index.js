import React from 'react';
import ReactDOM from 'react-dom/client';
import {BrowserRouter, Route, Routes} from 'react-router-dom';
import App from './App';
import { ReactKeycloakProvider } from '@react-keycloak/web';
import keycloakConfing from './Config/keycloakConfig';

const root = ReactDOM.createRoot(document.getElementById('root'));
//<ReactKeycloakProvider authClient={keycloakConfing}>
//</ReactKeycloakProvider>
//<React.StrictMode>
root.render(
    <ReactKeycloakProvider authClient={keycloakConfing}>
      <BrowserRouter>
        <Routes>
          <Route path="/*" element={<App />} />
        </Routes>
      </BrowserRouter>
    </ReactKeycloakProvider>
);

