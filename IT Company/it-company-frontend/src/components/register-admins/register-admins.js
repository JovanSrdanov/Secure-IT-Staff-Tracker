import React, {useEffect, useState} from 'react';
import {Box, Button, Dialog, DialogActions, DialogTitle, TextField} from "@mui/material";
import HowToRegIcon from "@mui/icons-material/HowToReg";
import {useNavigate} from "react-router-dom";
import {Flex} from 'reflexbox'
import interceptor from "../../interceptor/interceptor";

function RegisterAdmins() {
    const navigate = useNavigate();
    const [user, setUser] = useState({
        profession: '',
        phoneNumber: '',
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
            user.name.length <= 255 &&
            user.name !== "" &&

            user.surname.length <= 255 &&
            user.surname !== "" &&

            user.profession.length <= 255 &&
            user.profession !== "" &&

            user.phoneNumber !== "" &&
            /^[+]?[\d\s.-](?:\/?[\d\s.-]){0,}$/.test(user.phoneNumber) &&
            user.phoneNumber.length <= 255 &&

            user.email !== "" &&
            user.email.match(/^([\w.%+-]+)@([\w-]+\.)+([\w]{2,})$/i) &&
            user.email.length <= 255 &&

            user.address.country.length <= 255 &&
            user.address.country !== "" &&

            user.address.city.length <= 255 &&
            user.address.city !== "" &&

            user.address.street.length <= 255 &&
            user.address.street !== "" &&

            user.address.streetNumber.length <= 255 &&
            user.address.streetNumber !== "";

        setIsDisabled(!isValid);
    }, [user]);


    const handleRegisterClick = () => {
        console.log(user)
        interceptor.post('/auth/register-admin', user)
            .then((response) => {
                navigate('/profile');
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
                    <Flex flexDirection="row" justifyContent="center" alignItems="center">
                        <Box m={1}>
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
                            <p>All fields must be filled and email and phone number must be
                                in
                                valid
                                form</p>
                        </Box>


                    </Flex>
                </Flex>
                <Flex flexDirection="row" justifyContent="center" alignItems="end">

                    <Box m={1}>
                        <Button disabled={isDisabled} variant="contained" color="success" onClick={handleRegisterClick}
                                endIcon={<HowToRegIcon/>}>REGISTER admin</Button>
                    </Box>

                </Flex>

            </div>

        </div>
    );
}

export default RegisterAdmins;