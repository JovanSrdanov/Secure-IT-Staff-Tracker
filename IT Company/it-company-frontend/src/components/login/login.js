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
        interceptor.post('api-1/account-credentials/login', {
            username: email,
            password: password
        }).then(res => {
            const accessToken = res.data.accessToken;
            const refreshToken = res.data.refreshToken;
            localStorage.setItem('accessToken', accessToken);
            localStorage.setItem('refreshToken', refreshToken);
            navigate("/")
        }).catch(err => {
            setShowAlert(true);
        })

    };
    const handleAlertClose = () => {
        setShowAlert(false);
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
                        value={email}
                        onChange={handleEmailChange}
                    />
                    <Button
                        variant="contained" color="primary" endIcon={<LoginIcon/>}
                        onClick={handleLogin}
                    >Passwordless Login
                    </Button>
                    <Flex flexDirection="row" justifyContent="center">
                        Email will be sent to you
                    </Flex>
                </div>
            </Flex>
            {showAlert && (
                <Alert sx={{width: "fit-content", margin: "10px auto"}} severity="error" onClose={handleAlertClose}>
                    Invalid username or password, please try again.
                </Alert>
            )}
        </>
    );
}

export default Login;