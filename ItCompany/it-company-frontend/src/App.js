import ParticlesBg from 'particles-bg'
import "./particles.css"
import {AppBar, Box, Button, Snackbar, Toolbar, Tooltip} from "@mui/material";
import React, {useState} from "react";
import LoginIcon from '@mui/icons-material/Login';
import HowToRegIcon from '@mui/icons-material/HowToReg';
import ComputerIcon from '@mui/icons-material/Computer';
import {Navigate, Route, Routes, useNavigate} from "react-router-dom";
import LoginPage from "./pages/unauthenticated-pages/login-page";
import RegisterPage from "./pages/unauthenticated-pages/register-page";
import PersonOutlineOutlinedIcon from '@mui/icons-material/PersonOutlineOutlined';
import LogoutOutlinedIcon from '@mui/icons-material/LogoutOutlined';
import jwt_decode from "jwt-decode";
import LockIcon from '@mui/icons-material/Lock';
import ChecklistIcon from '@mui/icons-material/Checklist';
import PersonSearchIcon from '@mui/icons-material/PersonSearch';
import PersonAddIcon from '@mui/icons-material/PersonAdd';
import Diversity3Icon from '@mui/icons-material/Diversity3';
import PermissionsPage from "./pages/admin-pages/permissions-page";
import ProfilePage from "./pages/for-all-pages/profile-page";
import RegistrationApprovalPage from "./pages/admin-pages/registration-approval-page";
import WorkIcon from '@mui/icons-material/Work';
import EmojiEventsIcon from '@mui/icons-material/EmojiEvents';
import SkillsPage from "./pages/engineer-pages/skills-page";
import BarChartIcon from '@mui/icons-material/BarChart';
import ProjectsPage from "./pages/admin-pages/projects-page";
import EmployeesPage from "./pages/admin-pages/employees-page";
import RegisterAdminsPage from "./pages/admin-pages/register-admins-page";
import ManageMyProjectsPage from "./pages/project-manager-pages/manage-my-projects-page";
import WorkedOnProjectsPage from "./pages/engineer-pages/worked-on-projects-page";
import AdminPasswordChangePage from "./pages/admin-pages/admin-password-change-page";
import PasswordlessLoginPage from "./pages/unauthenticated-pages/passwordless-login-page";
import EmployeesInfoPage from './pages/hr-manager-pages/employees-info-page';
import SearchEngineersPage from "./pages/admin-pages/search-engineers-page";
import RecoverPasswordPage from "./pages/unauthenticated-pages/recover-password-page";
import FormatAlignJustifyIcon from '@mui/icons-material/FormatAlignJustify';
import ViewLogsPage from "./pages/admin-pages/view-logs-page";
import MuiAlert from '@mui/material/Alert';
import SockJS from "sockjs-client";
import {Stomp} from "@stomp/stompjs";
import {useKeycloak} from '@react-keycloak/web';

const Alert = React.forwardRef(function Alert(props, ref) {
    return <MuiAlert elevation={6} ref={ref} variant="filled" {...props} />;
});


function App() {
    const navigate = useNavigate()
    const [open, setOpen] = useState(false);
    const [message, setMessage] = useState('');
    const handleClose = (event, reason) => {
        if (reason === 'clickaway') {
            return;
        }
        setOpen(false);
    };

    // Keycloak
    const {keycloak, initialized} = useKeycloak();
    // console.log("Access token: " + keycloak.token + "\nRefresh token: " + keycloak.refreshToken + 
    // "\nIs authenticated: " + keycloak.authenticated + "\nIs initialized: " + initialized)
    // if (isAuthenticatedUsingKeycloak()) {
    //   console.log(jwt_decode(keycloak.token).realm_access.roles);
    // }

    function isAuthenticatedUsingKeycloak() {
        return keycloak && keycloak?.token;
    }

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

    keycloak.onTokenExpired = () => {
        removeTokens();
        window.location.href = "/login";
    };

    const handleLogout = () => {
        if (isAuthenticatedUsingKeycloak()) {
            removeTokens();
            keycloak.logout();
        } else {
            removeTokens();
            window.location.href = "/login";
        }
    };

    function removeTokens() {
        document.cookie = 'accessToken=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
        document.cookie = 'refreshToken=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
    }

    const openWebSocket = (role) => {
        if (role === "ROLE_ADMIN") {
            const socket = new SockJS('https://localhost:4430/socket');
            const stompClient = Stomp.over(socket);
            stompClient.debug = () => {
            };
            stompClient.connect({}, () => {
                stompClient.subscribe('/socket-publisher', (message) => {
                    const receivedMessage = message.body;
                    console.log(message.body)
                    setMessage(receivedMessage);
                    setOpen(true);
                    setTimeout(() => {
                        setOpen(false);
                    }, 5000);
                });
            });
            return () => {
                stompClient.disconnect();
            };
        }
    };

    function getRoleFromToken() {
        // console.log(
        //     "Is initialized: " +
        //     initialized +
        //     "\nAccess token: " +
        //     keycloak?.token
        // );
        if (isAuthenticatedUsingKeycloak()) {
            // console.log("ACCESS KOLACIC: " + getCookieValue("accessToken"))
            // console.log("REFRESH KOLACIC" + getCookieValue("refreshToken"))
            // console.log("ACCESS TOKEN: " + keycloak?.token);
            // console.log("REFRESH TOKEN: " + keycloak?.refreshToken)
            document.cookie = `accessToken=${encodeURIComponent(
              keycloak?.token
            )}; Secure; SameSite=Strict;`;
            document.cookie = `refreshToken=${encodeURIComponent(
              keycloak?.refreshToken
            )}; Secure; SameSite=Strict;`;

            const decodedKeycloakAccessToken = jwt_decode(keycloak?.token);
            console.log("DECODED ACCESS TOKEN: " + decodedKeycloakAccessToken);
            const currentTime = Date.now() / 1000;

            const refreshToken = jwt_decode(keycloak?.refreshToken);
            if (refreshToken.exp < currentTime) {
                removeTokens();
                return null;
            }

            const role = decodedKeycloakAccessToken.realm_access.roles.filter(
                (item) => item.startsWith("ROLE")
            )[0];
            
            console.log("ROLE: " + role) 

            openWebSocket(role);
            return role;
        } else {
            const token = getCookieValue("accessToken");
            if (!token) {
                removeTokens();
                return null;
            }
            const currentTime = Date.now() / 1000;
            const refreshToken = getCookieValue("refreshToken");
            if (!refreshToken) {
                removeTokens();
                return null;
            }
            if (jwt_decode(refreshToken).exp < currentTime) {
                removeTokens();
                return null;
            }
            const decodedToken = jwt_decode(token);
            console.log("OBICNA ROLA: " + decodedToken.role);
            // ovo se desi ako se izlogujem preko keycloaka sa druge aplikacije, ostane keycloak jwt u local
            // storage-u
            if (decodedToken.role === undefined) {
                removeTokens();
                return null;
            }

            openWebSocket(decodedToken.role);
            return decodedToken.role;
        }
    }

    const ROLE = getRoleFromToken();
    //console.log(ROLE)
    return (
        <div>
            <Snackbar
                anchorOrigin={{
                    vertical: 'bottom',
                    horizontal: 'right',
                }}
                open={open}
                autoHideDuration={5000}
                onClose={() => setOpen(false)}
                message={message}
            >
                <Alert onClose={handleClose} severity="error" sx={{width: '100%'}}>
                    {message}
                </Alert>
            </Snackbar>
            <ParticlesBg color="#000000" type="cobweb" num={250} bg={true}/>
            <Box>
                <AppBar position="static">
                    <Toolbar>
                        <Tooltip title="IT Company" arrow>
                            <Button
                                sx={{color: "white", marginRight: 5}}
                                startIcon={<ComputerIcon/>}
                            >
                                It Company
                            </Button>
                        </Tooltip>
                        {ROLE === "ROLE_ADMIN" && (
                            <>
                                <Tooltip title="Set or remove permissions for roles" arrow>
                                    <Button
                                        startIcon={<LockIcon/>}
                                        sx={{color: "inherit"}}
                                        onClick={() => {
                                            navigate("/permissions");
                                        }}
                                    >
                                        Permissions
                                    </Button>
                                </Tooltip>
                                <Tooltip title="Registration approval of new employees" arrow>
                                    <Button
                                        startIcon={<ChecklistIcon/>}
                                        sx={{color: "inherit"}}
                                        onClick={() => {
                                            navigate("/registration-approval");
                                        }}
                                    >
                                        Registration approval
                                    </Button>
                                </Tooltip>
                                <Tooltip title="Search engineers" arrow>
                                    <Button
                                        startIcon={<PersonSearchIcon/>}
                                        sx={{color: "inherit"}}
                                        onClick={() => {
                                            navigate("/search-engineers");
                                        }}
                                    >
                                        Search engineers
                                    </Button>
                                </Tooltip>
                                <Tooltip title="View all employees" arrow>
                                    <Button
                                        startIcon={<Diversity3Icon/>}
                                        sx={{color: "inherit"}}
                                        onClick={() => {
                                            navigate("/employees");
                                        }}
                                    >
                                        Employees
                                    </Button>
                                </Tooltip>
                                <Tooltip title="Create and view projects" arrow>
                                    <Button
                                        startIcon={<BarChartIcon/>}
                                        sx={{color: "inherit"}}
                                        onClick={() => {
                                            navigate("/all-projects");
                                        }}
                                    >
                                        Projects
                                    </Button>
                                </Tooltip>
                                <Tooltip title="Register admins" arrow>
                                    <Button
                                        startIcon={<PersonAddIcon/>}
                                        sx={{color: "inherit"}}
                                        onClick={() => {
                                            navigate("/register-admins");
                                        }}
                                    >
                                        Register admins
                                    </Button>
                                </Tooltip>
                                <Tooltip title="View logs" arrow>
                                    <Button
                                        startIcon={<FormatAlignJustifyIcon/>}
                                        sx={{color: "inherit"}}
                                        onClick={() => {
                                            navigate("/view-logs");
                                        }}
                                    >
                                        View logs
                                    </Button>
                                </Tooltip>
                            </>
                        )}
                        {ROLE === "ROLE_ENGINEER" && (
                            <>
                                <Tooltip title="My skills and seniority" arrow>
                                    <Button
                                        startIcon={<EmojiEventsIcon/>}
                                        sx={{color: "inherit"}}
                                        onClick={() => {
                                            navigate("/skills");
                                        }}
                                    >
                                        Skills
                                    </Button>
                                </Tooltip>

                                <Tooltip title="My projects" arrow>
                                    <Button
                                        startIcon={<WorkIcon/>}
                                        sx={{color: "inherit"}}
                                        onClick={() => {
                                            navigate("/worked-on-projects-page");
                                        }}
                                    >
                                        Projects
                                    </Button>
                                </Tooltip>
                            </>
                        )}
                        {ROLE === "ROLE_PROJECT_MANAGER" && (
                            <>
                                <Tooltip title="My projects" arrow>
                                    <Button
                                        startIcon={<WorkIcon/>}
                                        sx={{color: "inherit"}}
                                        onClick={() => {
                                            navigate("/manage-my-projects");
                                        }}
                                    >
                                        Manage my Projects
                                    </Button>
                                </Tooltip>
                            </>
                        )}
                        {ROLE === "ROLE_HR_MANAGER" && <>   <Tooltip title="View all employees" arrow>
                            <Button
                                startIcon={<Diversity3Icon/>}
                                sx={{color: "inherit"}}
                                onClick={() => {
                                    navigate("/employees-info");
                                }}
                            >
                                Employees info
                            </Button>
                        </Tooltip></>}
                        {ROLE === null && (
                            <>
                                <Tooltip title="Log in to your account" arrow>
                                    <Button
                                        color="primary"
                                        sx={{marginLeft: "auto"}}
                                        startIcon={<LoginIcon/>}
                                        onClick={() => {
                                            navigate("/login");
                                        }}
                                    >
                                        Log in
                                    </Button>
                                </Tooltip>
                                <Tooltip title="Register a new account" arrow>
                                    <Button
                                        color="success"
                                        startIcon={<HowToRegIcon/>}
                                        onClick={() => {
                                            navigate("/register");
                                        }}
                                    >
                                        Register
                                    </Button>
                                </Tooltip>
                            </>
                        )}

                        {ROLE !== null && (
                            <>
                                <Tooltip title="Log out of the system" arrow>
                                    <Button
                                        sx={{marginLeft: "auto"}}
                                        color="error"
                                        onClick={handleLogout}
                                        startIcon={<LogoutOutlinedIcon/>}
                                    >
                                        Log out
                                    </Button>
                                </Tooltip>
                            </>
                        )}
                        {ROLE !== null && ROLE !== "ROLE_ADMIN_PASSWORD_CHANGE" && (
                            <>
                                <Tooltip title="Your information" arrow>
                                    <Button
                                        color="info"
                                        startIcon={<PersonOutlineOutlinedIcon/>}
                                        onClick={() => {
                                            navigate("/profile");
                                        }}
                                    >
                                        My profile
                                    </Button>
                                </Tooltip>
                            </>
                        )}
                    </Toolbar>
                </AppBar>
                <Routes>
                    {ROLE === "ROLE_ADMIN" && (
                        <>
                            <Route path="/permissions" element={<PermissionsPage/>}/>
                            <Route
                                path="/registration-approval"
                                element={<RegistrationApprovalPage/>}
                            />
                            <Route path="/search-engineers" element={<SearchEngineersPage/>}/>
                            <Route path="/view-logs" element={<ViewLogsPage/>}/>
                            <Route path="/employees" element={<EmployeesPage/>}/>
                            <Route path="/all-projects" element={<ProjectsPage/>}/>
                            <Route
                                path="/register-admins"
                                element={<RegisterAdminsPage/>}
                            />
                            <Route path="/profile" element={<ProfilePage/>}/>
                            <Route path="/*" element={<Navigate to="/profile"/>}/>
                        </>
                    )}
                    {ROLE === "ROLE_ENGINEER" && (
                        <>
                            <Route path="/profile" element={<ProfilePage/>}/>
                            <Route path="/skills" element={<SkillsPage/>}/>
                            <Route
                                path="/worked-on-projects-page"
                                element={<WorkedOnProjectsPage/>}
                            />
                            <Route path="/*" element={<Navigate to="/profile"/>}/>
                        </>
                    )}

                    {ROLE === "ROLE_PROJECT_MANAGER" && (
                        <>
                            <Route path="/profile" element={<ProfilePage/>}/>
                            <Route
                                path="/manage-my-projects"
                                element={<ManageMyProjectsPage/>}
                            />
                            <Route path="/*" element={<Navigate to="/profile"/>}/>
                        </>
                    )}

                    {ROLE === "ROLE_ADMIN_PASSWORD_CHANGE" && (
                        <>
                            <Route
                                path="/admin-password-change"
                                element={<AdminPasswordChangePage/>}
                            />
                            <Route
                                path="/*"
                                element={<Navigate to="/admin-password-change"/>}
                            />
                        </>
                    )}

                    {ROLE === "ROLE_HR_MANAGER" && (
                        <>
                            <Route path="/profile" element={<ProfilePage/>}/>
                            <Route path="/employees-info" element={<EmployeesInfoPage/>}/>
                            <Route path="/*" element={<Navigate to="/profile"/>}/>
                        </>
                    )}
                    {ROLE === null && (
                        <>
                            <Route path="/login" element={<LoginPage/>}/>
                            <Route path="/register" element={<RegisterPage/>}/>
                            <Route
                                path="/error-page"
                                element={
                                    <h1>Account not activated, there has been an error</h1>
                                }
                            />

                            <Route
                                path="/passwordless-login"
                                element={<PasswordlessLoginPage/>}
                            />

                            <Route
                                path="/recover"
                                element={<RecoverPasswordPage/>}
                            />

                            <Route path="/*" element={<Navigate to="/login"/>}/>
                        </>
                    )}
                </Routes>
            </Box>
        </div>
    );
}

export default App;
