import React from 'react';
import "../../pages/wrapper.css"
import {Alert, Button, TextField} from "@mui/material";
import LoginIcon from '@mui/icons-material/Login';
import interceptor from "../../interceptor/interceptor";
import {useNavigate} from "react-router-dom";
import { useKeycloak } from "@react-keycloak/web";


function Login() {
  const navigate = useNavigate();
  const [email, setEmail] = React.useState("");
  const [password, setPassword] = React.useState("");
  const [showAlert, setShowAlert] = React.useState(false);

  // Keycloak
  const { keycloak, initialized } = useKeycloak();

  keycloak.onAuthSuccess = () => {
    const accessToken = keycloak.token;
    localStorage.setItem("jwt", accessToken);

    console.log("USPESNO DODAO KEYCLOAK TOKEN");
  };
  
  const handleKeycloakLogin = () => {
    keycloak.login();
  };

  const handleEmailChange = (event) => {
    setEmail(event.target.value);
  };

  const handlePasswordChange = (event) => {
    setPassword(event.target.value);
  };
  const handleLogin = async () => {
    interceptor
      .post("auth/login", {
        email: email,
        password: password,
      })
      .then((res) => {
        localStorage.setItem("jwt", res.data.jwt);
        const decoded = JSON.parse(atob(res.data.jwt.split(".")[1]));
        const role = decoded.role;
        if (role === "ROLE_PKI_ADMIN") {
          navigate("/all-certificates");
          return;
        }
        if (role === "ROLE_CERTIFICATE_USER") {
          navigate("/my-certificates");
          return;
        }
        if (role === "ROLE_CERTIFICATE_USER_CHANGE_PASSWORD") {
          navigate("/change-password");
          return;
        }
        navigate("/login");
      })
      .catch((err) => {
        setShowAlert(true);
      });
  };
  const handleAlertClose = () => {
    setShowAlert(false);
  };

  return (
    <div>
      <div className="wrapper">
        <TextField
          fullWidth
          variant="filled"
          label="E-mail"
          type={"email"}
          value={email}
          onChange={handleEmailChange}
        />
        <TextField
          fullWidth
          variant="filled"
          label="Password"
          type="password"
          value={password}
          onChange={handlePasswordChange}
        />
        <Button
          variant="contained"
          endIcon={<LoginIcon />}
          onClick={handleLogin}
        >
          LOGIN
        </Button>
        {!keycloak.authenticated && (
          <Button
            variant="contained"
            endIcon={<LoginIcon />}
            onClick={handleKeycloakLogin}
          >
            Login with Keycloak
          </Button>
        )}
      </div>
      {showAlert && (
        <Alert
          sx={{ width: "fit-content", margin: "10px auto" }}
          severity="info"
          onClose={handleAlertClose}
        >
          Invalid email or password, please try again.
        </Alert>
      )}
    </div>
  );
}

export default Login;