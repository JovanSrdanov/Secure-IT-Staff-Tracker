import ParticlesBg from 'particles-bg'
import "./particles.css"
import {AppBar, Box, Button, Toolbar, Tooltip} from "@mui/material";
import React from "react";
import LoginIcon from '@mui/icons-material/Login';
import HowToRegIcon from '@mui/icons-material/HowToReg';
import ComputerIcon from '@mui/icons-material/Computer';
import {Navigate, Route, Routes, useNavigate} from "react-router-dom";
import LoginPage from "./pages/unauthenticated-pages/login-page";
import RegisterPage from "./pages/unauthenticated-pages/register-page";
import PersonOutlineOutlinedIcon from '@mui/icons-material/PersonOutlineOutlined';
import LogoutOutlinedIcon from '@mui/icons-material/LogoutOutlined';
import jwt_decode from "jwt-decode";
import interceptor from "./interceptor/interceptor";

function App() {
    const navigate = useNavigate()

    function getCookieValue(name) {
        const cookies = document.cookie.split(';');
        for (let i = 0; i < cookies.length; i++) {
            const cookie = cookies[i].trim();
            if (cookie.startsWith(`${name}=`)) {
                return cookie.substring(name.length + 1);
            }
        }
        return null;
    }

    function getRoleFromToken() {
        const token = getCookieValue('accessToken');
        if (!token) {
            removeTokens();
            return null;
        }
        const decodedToken = jwt_decode(token);

        const currentTime = Date.now() / 1000;
        if (decodedToken.exp < currentTime) {
            removeTokens();
            navigate("/login");
            return null;
        }
        return decodedToken.role;
    }

    function removeTokens() {
        document.cookie = 'accessToken=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
        document.cookie = 'refreshToken=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
    }

    const ROLE = getRoleFromToken();
    const handleLogout = () => {
        removeTokens()
        navigate('/login');
    };
    const handleTest = () => {
        interceptor.get('/account/test', {}).then(res => {
            console.log("res")
            console.log(res)
        }).catch(err => {
            console.log("err")
            console.log(err)
        });
    };
    return (
        <div>
            <ParticlesBg color="#000000" type="cobweb" num={200} bg={true}/>
            <Box>
                <AppBar position="static">
                    <Toolbar>
                        <Tooltip title="IT Company" arrow>
                            <Button sx={{color: "white"}}
                                    startIcon={<ComputerIcon/>}>
                                It Company
                            </Button>
                        </Tooltip>
                        <>
                            <Tooltip title="Log in to your account" arrow>
                                <Button color="primary" sx={{marginLeft: 'auto'}} startIcon={<LoginIcon/>}>
                                    Log in
                                </Button>
                            </Tooltip>
                            <Tooltip title="Register a new account" arrow>
                                <Button color="success" startIcon={<HowToRegIcon/>}
                                >
                                    Register
                                </Button>
                            </Tooltip>
                            <>
                                <Tooltip title="Your informations" arrow>
                                    <Button color="info"
                                            startIcon={<PersonOutlineOutlinedIcon/>}
                                            onClick={() => {
                                                navigate('/profile');
                                            }}>

                                        My profile
                                    </Button>
                                </Tooltip>
                                <Tooltip title="Log out of the system" arrow>
                                    <Button color="error" onClick={handleLogout} startIcon={<LogoutOutlinedIcon/>}>

                                        Log out
                                    </Button>
                                </Tooltip>
                            </>
                        </>
                    </Toolbar>
                </AppBar>
                <Routes>

                    <Route path="/login" element={<LoginPage/>}/>
                    <Route path="/register" element={<RegisterPage/>}/>
                    <Route path="/*" element={<Navigate to="/login"/>}/>
                    <Route path="/button"
                           element={<Button variant="contained" onClick={handleTest}>Klikni</Button>}/>


                </Routes>
            </Box>
        </div>
    );
}

export default App;
