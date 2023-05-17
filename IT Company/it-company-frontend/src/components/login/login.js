import React from 'react';

import {Alert, Button, TextField} from "@mui/material";
import LoginIcon from '@mui/icons-material/Login';
import {useNavigate} from "react-router-dom";
import interceptor from "../../interceptor/interceptor";
import {Flex} from "reflexbox";


function Login() {
    const navigate = useNavigate();
    const [email, setEmail] = React.useState("");
    const [emailPasswordLess, setEmailPasswordLess] = React.useState("");
    const [password, setPassword] = React.useState("");
    const [showAlert, setShowAlert] = React.useState(false);

    const handleEmailChange = (event) => {
        setEmail(event.target.value);
    };

    const handlePasswordChange = (event) => {
        setPassword(event.target.value);
    };
    const handleLogin = async () => {
        interceptor.post('auth/login', {
            email: email,
            password: password
        }).then(res => {
            console.log(res.data);
            // Set HTTP-only cookies

            document.cookie = `accessToken=${encodeURIComponent(res.data.accessToken)}; Secure; SameSite=Strict;`;
            document.cookie = `refreshToken=${encodeURIComponent(res.data.refreshToken)}; Secure; SameSite=Strict;`;
            navigate("/");
        }).catch(err => {
            setShowAlert(true);
        });
    };
    const handleAlertClose = () => {
        setShowAlert(false);
    };

    const handlePasswordlessEmailChange = (event) => {
        setEmailPasswordLess(event.target.value);
    };
    const handlePasswordlessLogin = () => {
        interceptor.post('auth/login-GASCINA', {
            email: emailPasswordLess,
        }).then(res => {
            console.log(res.data)

        }).catch(err => {
            setShowAlert(true);
        })
    };
    return (
        <>
            <Flex flexDirection="row">
                <div className="wrapper">

                    <TextField
                        fullWidth
                        variant="filled"
                        label="Email"
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
                        variant="contained" color="primary" endIcon={<LoginIcon/>}
                        onClick={handleLogin}
                    >Regular LOGIN
                    </Button>

                </div>
                <div className="wrapper">

                    <TextField
                        fullWidth
                        variant="filled"
                        label="Email"
                        type={"email"}
                        value={emailPasswordLess}
                        onChange={handlePasswordlessEmailChange}
                    />
                    <Button
                        variant="contained" color="primary" endIcon={<LoginIcon/>}
                        onClick={handlePasswordlessLogin}
                    >Passwordless Login
                    </Button>
                    <Flex flexDirection="row" justifyContent="center">
                        Email will be sent to you
                    </Flex>
                </div>
            </Flex>
            {showAlert && (
                <Alert sx={{width: "fit-content", margin: "10px auto"}} severity="error" onClose={handleAlertClose}>
                    Invalid credentials, please try again.
                </Alert>
            )}
        </>
    );
}

export default Login;