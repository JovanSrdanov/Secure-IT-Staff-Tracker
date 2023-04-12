import "./particles.css"
import {AppBar, Box, Button, Toolbar} from "@mui/material";
import {createTheme, ThemeProvider} from '@mui/material/styles';
import KeyIcon from '@mui/icons-material/Key';
import Typography from '@mui/material/Typography';
import {Navigate, Route, Routes, useNavigate} from "react-router-dom";
import LoginPage from "./pages/login-page/login-page";
import {useEffect, useState} from "react";
import ParticlesBg from 'particles-bg'
import ChangePasswordPage from "./pages/certificate-user-change-password-page/change-password-page";
import HackerHeaders from "./components/hacker-headers/hackerHeaders";
import AllCertificatesPage from "./pages/admin-pages/all-certificates-page";
import CreateCertificatePage from "./pages/admin-pages/create-certificate-page";
import MyCertificatesPage from "./pages/certificate-user-pages/my-certificates-page";
import IssueCertificatePage from "./pages/certificate-user-pages/issue-certificate-page";

const darkTheme = createTheme({
    palette: {
        mode: 'dark',
    },
});


function App() {
    HackerHeaders();
    const [jwt, setJwt] = useState(localStorage.getItem('jwt'));
    const [role, setRole] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        const checkJwt = () => {
            const token = localStorage.getItem('jwt');
            if (!token) {
                navigate('/login');
                setRole(null);
                return;
            }

            try {
                const decoded = JSON.parse(atob(token.split('.')[1]));
                const currentTime = new Date().getTime() / 1000;
                if (decoded.exp < currentTime) {
                    localStorage.removeItem('jwt');
                    navigate('/login');
                    setRole(null);
                    return;
                }

                setRole(decoded.role);
            } catch (error) {
                localStorage.removeItem('jwt');
                navigate('/login');
                setRole(null);

            }
        };

        checkJwt();
    }, [jwt, navigate]);

    const handleLogout = () => {
        localStorage.removeItem('jwt');
        setJwt(null);
        setRole(null);
        navigate('/login');
    };

    const IS_ROLE_PKI_ADMIN = role === 'ROLE_PKI_ADMIN';
    const IS_ROLE_CERTIFICATE_USER = role === 'ROLE_CERTIFICATE_USER';
    const IS_ROLE_CERTIFICATE_USER_CHANGE_PASSWORD = role === 'ROLE_CERTIFICATE_USER_CHANGE_PASSWORD';


    return (
        <ThemeProvider theme={darkTheme}>
            <ParticlesBg color="#008B8B" type="cobweb" num={200} bg={true}/>
            <div className="App">
                <Box>
                    <AppBar position="static">
                        <Toolbar>
                            <KeyIcon sx={{display: {xs: 'none', md: 'flex'}, mr: 1}}/>
                            <Typography
                                variant="h6"
                                noWrap
                                sx={{
                                    caretColor: "transparent",
                                    mr: 2,
                                    display: {xs: 'none', md: 'flex'},
                                    fontFamily: 'monospace',
                                    fontWeight: 700,
                                    letterSpacing: '.3rem',
                                    color: 'inherit',
                                    textDecoration: 'none',
                                }}
                            >
                                PKI
                            </Typography>
                            {IS_ROLE_PKI_ADMIN && (
                                <>
                                    <Button sx={{color: 'inherit'}} onClick={() => navigate('/all-certificates')}>
                                        All certificates
                                    </Button>
                                    <Button sx={{color: 'inherit'}} onClick={() => navigate('/create-certificate')}>
                                        Create certificate
                                    </Button>
                                </>
                            )}
                            {IS_ROLE_CERTIFICATE_USER && (
                                <>
                                    <Button sx={{color: 'inherit'}} onClick={() => navigate('/my-certificates')}>
                                        My certificates
                                    </Button>
                                    <Button sx={{color: 'inherit'}} onClick={() => navigate('/issue-certificate')}>
                                        Issue a certificate
                                    </Button>
                                </>
                            )}
                            {IS_ROLE_CERTIFICATE_USER_CHANGE_PASSWORD && (
                                <Button sx={{color: 'inherit'}} onClick={() => navigate('/change-password')}>
                                    Change password </Button>
                            )}
                            {role !== null && (
                                <Button sx={{color: 'inherit', marginLeft: 'auto'}} onClick={handleLogout}>
                                    Log out
                                </Button>
                            )}
                        </Toolbar>
                    </AppBar>
                    <Routes>
                        <Route path="/" element={<Navigate to="/all-certificates"/>}/>
                        <Route path="/login" element={<LoginPage/>}/>
                        {IS_ROLE_PKI_ADMIN && (
                            <>
                                <Route path="/all-certificates" element={<AllCertificatesPage/>}/>
                                <Route path="/create-certificate" element={<CreateCertificatePage/>}/>
                                <Route path="*" element={<Navigate to="/all-certificates"/>}/>
                            </>
                        )}
                        {IS_ROLE_CERTIFICATE_USER && (
                            <>
                                <Route path="/my-certificates" element={<MyCertificatesPage/>}/>
                                <Route path="/issue-certificate" element={<IssueCertificatePage/>}/>
                                <Route path="*" element={<Navigate to="/my-certificates"/>}/>
                            </>
                        )}
                        {IS_ROLE_CERTIFICATE_USER_CHANGE_PASSWORD && (
                            <>
                                <Route path="/change-password" element={<ChangePasswordPage/>}/>
                                <Route path="*" element={<Navigate to="/change-password"/>}/>
                            </>
                        )}
                        {role !== null && (
                            <Route path="*" element={<Navigate to="/login"/>}/>
                        )}
                    </Routes>
                </Box>
            </div>
        </ThemeProvider>
    );
}

export default App;
