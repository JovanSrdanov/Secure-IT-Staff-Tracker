import ParticlesBg from 'particles-bg'
import "./particles.css"
import {AppBar, Box, Button, Toolbar, Tooltip} from "@mui/material";
import React from "react";
import LoginIcon from '@mui/icons-material/Login';
import HowToRegIcon from '@mui/icons-material/HowToReg';
import ComputerIcon from '@mui/icons-material/Computer';
import {Route, Routes, useNavigate} from "react-router-dom";
import LoginPage from "./pages/unauthenticated-pages/login-page";
import RegisterPage from "./pages/unauthenticated-pages/register-page";
import PersonOutlineOutlinedIcon from '@mui/icons-material/PersonOutlineOutlined';
import LogoutOutlinedIcon from '@mui/icons-material/LogoutOutlined';

function App() {
    const navigate = useNavigate()
    const handleLogout = () => {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        navigate('/login');
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

                </Routes>
            </Box>
        </div>
    );
}

export default App;
