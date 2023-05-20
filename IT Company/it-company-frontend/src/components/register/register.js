import React, {useEffect, useState} from 'react';
import {
    Box,
    Button,
    Dialog,
    DialogActions,
    DialogTitle,
    FormControl,
    InputLabel,
    MenuItem,
    Select,
    TextField
} from "@mui/material";
import LoginIcon from "@mui/icons-material/Login";
import HowToRegIcon from "@mui/icons-material/HowToReg";
import {useNavigate} from "react-router-dom";
import {Flex} from 'reflexbox'
import interceptor from "../../interceptor/interceptor";

function Register() {
    const navigate = useNavigate();
    const [user, setUser] = useState({
        password: '',
        profession: '',
        phoneNumber: '',
        passwordCheck: '',
        role: 'softwareEngineer',
        name: '',
        surname: '',
        email: '',
        address: {
            country: '',
            city: '',
            street: '',
            streetNumber: ''
        }
    });
    const [errorMessage, setErrorMessage] = useState("");

    const [isDisabled, setIsDisabled] = useState(true);
    const [usernameTakenDialogShow, setUsernameTakenDialogShow] = useState(false)
    const usernameTakenDialogClose = () => {
        setUsernameTakenDialogShow(false)
    };
    const handleInputChange = (event) => {

        const {name, value} = event.target;
        if (name.startsWith("address.")) {
            setUser((prevState) => {
                const address = {...prevState.address, [name.split(".")[1]]: value};
                return {...prevState, address};
            });
        } else {
            setUser((prevState) => ({...prevState, [name]: value}));
        }
    };

    useEffect(() => {
        const isValid =
            user.password.length >= 8 &&
            user.password === user.passwordCheck &&
            user.name !== "" &&
            user.phoneNumber !== "" &&
            user.profession !== "" &&
            user.surname !== "" &&
            user.email.match(/^([\w.%+-]+)@([\w-]+\.)+([\w]{2,})$/i) &&
            user.address.country !== "" &&
            user.address.city !== "" &&
            user.address.street !== "" &&
            user.address.streetNumber !== "" &&
            /[A-Z]/.test(user.password) && // At least one uppercase letter
            /[a-z]/.test(user.password) && // At least one lowercase letter
            /[!@#$%^&*(),.?":{}|<>]/.test(user.password); // At least one special character

        setIsDisabled(!isValid);
    }, [user]);

    const handleRegisterClick = () => {
        interceptor.post('/auth/register', user)
            .then((response) => {
                navigate('/login');
            })
            .catch((error) => {
                setErrorMessage(error.response.data)
                setUsernameTakenDialogShow(true)
            });
    };

    return (
        <div>
            <Dialog onClose={usernameTakenDialogClose} open={usernameTakenDialogShow}>
                <DialogTitle>{errorMessage}</DialogTitle>
                <DialogActions>
                    <Button onClick={usernameTakenDialogClose}
                            variant="contained"
                    >
                        Close
                    </Button>
                </DialogActions>
            </Dialog>
            <div className="wrapper">

                <Flex flexDirection="column">
                    <Flex>
                        <Box width={1 / 4} m={1}>
                            <TextField
                                fullWidth
                                variant="filled"
                                label="E-mail"
                                type="email"
                                name="email"
                                value={user.email}
                                onChange={handleInputChange}
                            />
                        </Box>

                        <Box width={1 / 4} m={1}>
                            <TextField
                                fullWidth
                                variant="filled"
                                label="Password"
                                type="password"
                                name="password"
                                value={user.password}
                                onChange={handleInputChange}
                            />
                        </Box>
                        <Box width={1 / 4} m={1}>
                            <TextField
                                fullWidth
                                variant="filled"
                                label="Password check"
                                type="password"
                                name="passwordCheck"
                                value={user.passwordCheck}
                                onChange={handleInputChange}
                            />
                        </Box>
                        <Box width={1 / 4} m={1}>
                            <FormControl variant="filled" fullWidth>
                                <InputLabel id="role">Role</InputLabel>
                                <Select
                                    fullWidth
                                    variant="filled"
                                    id="role"
                                    name="role"
                                    value={user.role}
                                    label="Age"
                                    onChange={handleInputChange}
                                >
                                    <MenuItem value="softwareEngineer">ENGINEER</MenuItem>
                                    <MenuItem value="projectManager">PROJECT MANAGER</MenuItem>
                                    <MenuItem value="hrManager">HR MANAGER</MenuItem>

                                </Select>
                            </FormControl>
                        </Box>
                    </Flex>
                    <Flex>
                        <Box width={1 / 4} m={1}>
                            <TextField
                                fullWidth
                                variant="filled"
                                label="Name"
                                name="name"
                                value={user.name}
                                onChange={handleInputChange}
                            />
                        </Box>
                        <Box width={1 / 4} m={1}>
                            <TextField
                                fullWidth
                                variant="filled"
                                label="Surname"
                                name="surname"
                                value={user.surname}
                                onChange={handleInputChange}
                            />
                        </Box>
                        <Box width={1 / 4} m={1}>
                            <TextField
                                fullWidth
                                variant="filled"
                                label="Phone number"
                                name="phoneNumber"
                                value={user.phoneNumber}
                                onChange={handleInputChange}
                            />
                        </Box>
                        <Box width={1 / 4} m={1}>
                            <TextField
                                fullWidth
                                variant="filled"
                                label="Profession"
                                name="profession"
                                value={user.profession}
                                onChange={handleInputChange}
                            />
                        </Box>
                    </Flex>
                    <Flex>
                        <Box width={1 / 4} m={1}>
                            <TextField
                                fullWidth
                                variant="filled"
                                label="Country"
                                name="address.country"
                                value={user.address.country}
                                onChange={handleInputChange}
                            />
                        </Box>
                        <Box width={1 / 4} m={1}>
                            <TextField
                                fullWidth
                                variant="filled"
                                label="City"
                                name="address.city"
                                value={user.address.city}
                                onChange={handleInputChange}
                            />
                        </Box>
                        <Box width={1 / 4} m={1}>
                            <TextField
                                fullWidth
                                variant="filled"
                                label="Street"
                                name="address.street"
                                value={user.address.street}
                                onChange={handleInputChange}
                            />
                        </Box>
                        <Box width={1 / 4} m={1}>
                            <TextField
                                fullWidth
                                variant="filled"
                                label="Street Number"
                                name="address.streetNumber"
                                value={user.address.streetNumber}
                                onChange={handleInputChange}
                            />
                        </Box>

                    </Flex>
                    <Flex flexDirection="column" justifyContent="center" alignItems="center">
                        <Box>
                            <p>All fields must be filled and email must be
                                in
                                valid
                                form</p>
                        </Box>
                        <Box>
                            <p>Password must contain one uppercase letter, one lowercase letter, one special character
                                and minimum 8 characters</p>

                        </Box>

                    </Flex>
                </Flex>
                <Flex flexDirection="row" justifyContent="space-between" alignItems="center">
                    <Box m={1}>
                        <Button variant="contained" color="warning" endIcon={<LoginIcon/>} onClick={() => {
                            navigate('/login')
                        }}>BACK TO LOGIN</Button>
                    </Box>
                    <Box m={1}>
                        <Button disabled={isDisabled} variant="contained" color="success" onClick={handleRegisterClick}
                                endIcon={<HowToRegIcon/>}>REGISTER</Button>
                    </Box>

                </Flex>

            </div>

        </div>
    );
}

export default Register;