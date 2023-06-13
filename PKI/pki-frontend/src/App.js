import "./particles.css"
import {AppBar, Box, Button, Toolbar} from "@mui/material";
import {createTheme, ThemeProvider} from '@mui/material/styles';
import KeyIcon from '@mui/icons-material/Key';
import Typography from '@mui/material/Typography';
import {Navigate, Route, Routes, useNavigate} from "react-router-dom";
import LoginPage from "./pages/login-page/login-page";
import {useState} from "react";
import ParticlesBg from 'particles-bg'
import ChangePasswordPage from "./pages/certificate-user-change-password-page/change-password-page";
import HackerHeaders from "./components/hacker-headers/hackerHeaders";
import AllCertificatesPage from "./pages/admin-pages/all-certificates-page";
import CreateCertificatePage from "./pages/admin-pages/create-certificate-page";
import MyCertificatesPage from "./pages/certificate-user-pages/my-certificates-page";
import IssueCertificatePage from "./pages/certificate-user-pages/issue-certificate-page";
import {useKeycloak} from "@react-keycloak/web";

const darkTheme = createTheme({
    palette: {
        mode: 'dark',
    },
});


function App() {
    HackerHeaders();
    const [jwt, setJwt] = useState(localStorage.getItem('jwt'));
    //const [role, setRole] = useState(null);
    const navigate = useNavigate();

    // Keycloak
    const {keycloak, initialized} = useKeycloak();

    function isAuthenticatedUsingKeycloak() {
        return keycloak && keycloak?.token;
    }

    keycloak.onTokenExpired = () => {
        localStorage.removeItem("jwt");
        setJwt(null);
        window.location.href = "/login";
    };

    // useEffect(() => {
    //   if (keycloak) {

    //   } else {
    //     const checkJwt = () => {
    //       const token = localStorage.getItem("jwt");
    //       if (!token) {
    //         navigate("/login");
    //         setRole(null);
    //         return;
    //       }

    //       try {
    //         const decoded = JSON.parse(atob(token.split(".")[1]));
    //         const currentTime = new Date().getTime() / 1000;
    //         if (decoded.exp < currentTime) {
    //           localStorage.removeItem("jwt");
    //           navigate("/login");
    //           setRole(null);
    //           return;
    //         }

    //         setRole(decoded.role);
    //       } catch (error) {
    //         localStorage.removeItem("jwt");
    //         navigate("/login");
    //         setRole(null);
    //       }
    //     };

    //     checkJwt();
    //   }
    // }, [jwt, navigate, initialized]);

    function getRoleFromToken() {
        if (isAuthenticatedUsingKeycloak()) {
            const decoded = JSON.parse(
                atob(keycloak?.token.split(".")[1])
            );
            const role = decoded.realm_access.roles.filter((item) =>
                item.startsWith("ROLE")
            )[0];
            //setRole(role);
            console.log("DEKODIRAN KEYCLOAK TOKEN: " + decoded)
            console.log("KEYCLOAK ROLA: " + role);
            //setRole("ROLE_PKI_ADMIN");
            localStorage.setItem("jwt", keycloak?.token);
            return "ROLE_PKI_ADMIN";
        } else {
            console.log("Nije preko keycloak-a")
            const token = localStorage.getItem("jwt");
            console.log("OBICAN TOKEN: " + token);
            if (!token) {
                //navigate("/login");
                //setRole(null);
                return null;
            }

            try {
                const decoded = JSON.parse(atob(token.split(".")[1]));
                //console.log(decoded);
                const currentTime = new Date().getTime() / 1000;
                if (decoded.exp < currentTime) {
                    localStorage.removeItem("jwt");
                    //navigate("/login");
                    //setRole(null);
                    return null;
                }
                //setRole(decoded.role);
                console.log("OBICNA ROLA: " + decoded.role)
                // ovo se desi ako se izlogujem preko keycloaka sa druge aplikacije, ostane keycloak jwt u local
                // storage-u
                if (decoded.role === undefined) {
                    localStorage.removeItem("jwt");
                    setJwt(null);
                    return null;
                }
                return decoded.role;
            } catch (error) {
                localStorage.removeItem("jwt");
                //navigate("/login");
                //setRole(null);
                return null;
            }
        }
    }

    const handleLogout = () => {
        localStorage.removeItem('jwt');
        setJwt(null);
        //setRole(null);
        if (isAuthenticatedUsingKeycloak()) {
            keycloak.logout();
        } else {
            navigate("/login");
        }
    };

    const ROLE = getRoleFromToken();

    // const IS_ROLE_PKI_ADMIN = role === "ROLE_PKI_ADMIN";
    // const IS_ROLE_CERTIFICATE_USER = role === 'ROLE_CERTIFICATE_USER';
    // const IS_ROLE_CERTIFICATE_USER_CHANGE_PASSWORD = role === 'ROLE_CERTIFICATE_USER_CHANGE_PASSWORD';


    return (
        <ThemeProvider theme={darkTheme}>
            <ParticlesBg color="#008B8B" type="cobweb" num={200} bg={true}/>
            <div className="App">
                <Box>
                    <AppBar position="static">
                        <Toolbar>
                            <KeyIcon sx={{display: {xs: "none", md: "flex"}, mr: 1}}/>
                            <Typography
                                variant="h6"
                                noWrap
                                sx={{
                                    caretColor: "transparent",
                                    mr: 2,
                                    display: {xs: "none", md: "flex"},
                                    fontFamily: "monospace",
                                    fontWeight: 700,
                                    letterSpacing: ".3rem",
                                    color: "inherit",
                                    textDecoration: "none",
                                }}
                            >
                                PKI
                            </Typography>
                            {ROLE === "ROLE_PKI_ADMIN" && (
                                <>
                                    <Button
                                        sx={{color: "inherit"}}
                                        onClick={() => navigate("/all-certificates")}
                                    >
                                        All certificates
                                    </Button>
                                    <Button
                                        sx={{color: "inherit"}}
                                        onClick={() => navigate("/create-certificate")}
                                    >
                                        Create certificate
                                    </Button>
                                </>
                            )}
                            {ROLE === "ROLE_CERTIFICATE_USER" && (
                                <>
                                    <Button
                                        sx={{color: "inherit"}}
                                        onClick={() => navigate("/my-certificates")}
                                    >
                                        My certificates
                                    </Button>
                                    <Button
                                        sx={{color: "inherit"}}
                                        onClick={() => navigate("/issue-certificate")}
                                    >
                                        Issue a certificate
                                    </Button>
                                </>
                            )}
                            {ROLE === "ROLE_CERTIFICATE_USER_CHANGE_PASSWORD" && (
                                <Button
                                    sx={{color: "inherit"}}
                                    onClick={() => navigate("/change-password")}
                                >
                                    Change password{" "}
                                </Button>
                            )}
                            {ROLE !== null && (
                                <Button
                                    sx={{color: "inherit", marginLeft: "auto"}}
                                    onClick={handleLogout}
                                >
                                    Log out
                                </Button>
                            )}
                        </Toolbar>
                    </AppBar>
                    <Routes>
                        {/* <Route path="/" element={<Navigate to="/all-certificates"/>}/>
                        <Route path="/login" element={<LoginPage/>}/> */}
                        {ROLE === "ROLE_PKI_ADMIN" && (
                            <>
                                <Route
                                    path="/all-certificates"
                                    element={<AllCertificatesPage/>}
                                />
                                <Route
                                    path="/create-certificate"
                                    element={<CreateCertificatePage/>}
                                />
                                <Route
                                    path="*"
                                    element={<Navigate to="/all-certificates"/>}
                                />
                            </>
                        )}
                        {ROLE === "ROLE_CERTIFICATE_USER" && (
                            <>
                                <Route
                                    path="/my-certificates"
                                    element={<MyCertificatesPage/>}
                                />
                                <Route
                                    path="/issue-certificate"
                                    element={<IssueCertificatePage/>}
                                />
                                <Route
                                    path="*"
                                    element={<Navigate to="/my-certificates"/>}
                                />
                            </>
                        )}
                        {ROLE === "ROLE_CERTIFICATE_USER_CHANGE_PASSWORD" && (
                            <>
                                <Route
                                    path="/change-password"
                                    element={<ChangePasswordPage/>}
                                />
                                <Route
                                    path="*"
                                    element={<Navigate to="/change-password"/>}
                                />
                            </>
                        )}
                        {ROLE === null && (
                            <>
                                <Route path="/login" element={<LoginPage/>}/>
                                <Route path="/*" element={<Navigate to="/login"/>}/>
                            </>
                        )}
                    </Routes>
                </Box>
            </div>
        </ThemeProvider>
    );
}

export default App;
