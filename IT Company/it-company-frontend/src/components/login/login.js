import React from 'react';

import {Alert, Button, TextField} from "@mui/material";
import LoginIcon from '@mui/icons-material/Login';
import {useNavigate} from "react-router-dom";
import interceptor from "../../interceptor/interceptor";
import {Flex} from "reflexbox";


function Login() {
    const navigate = useNavigate();
    const [email, setEmail] = React.useState("");
    const [resetPasswordEmail, setResetPasswordEmail] = React.useState("");
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
    const handleresetPasswordEmailChange = (event) => {
        setResetPasswordEmail(event.target.value);
    };

    function handleresetPassword() {
        console.log(resetPasswordEmail)
    }

    return (
        <>
            <Flex flexDirection="row" justifyContent="center" alignItems="center">
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
                    <hr
                        style={{
                            width: "100%",
                            border: "1px solid grey",
                        }}
                    />
                    <div className="wrapper">
                        <Flex flexDirection="row" justifyContent="center">
                            Forgot your password?
                        </Flex>
                        <Flex flexDirection="row" justifyContent="center">
                            Enter your email and we will reset it for you!
                        </Flex>

                        <TextField
                            fullWidth
                            variant="filled"
                            label="Email"
                            type={"email"}
                            value={resetPasswordEmail}
                            onChange={handleresetPasswordEmailChange}
                        />
                        <Button
                            variant="contained" color="warning" endIcon={<LoginIcon/>}
                            onClick={handleresetPassword}
                        >Reset password
                        </Button>
                    </div>
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