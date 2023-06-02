import React, {useEffect, useState} from 'react';
import {Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField} from "@mui/material";
import {Flex} from 'reflexbox'
import PublishedWithChangesIcon from '@mui/icons-material/PublishedWithChanges';
import interceptor from "../../interceptor/interceptor";

function Profile() {
    const [user, setUser] = useState({
        profession: '',
        phoneNumber: '',
        name: '',
        surname: '',
        address: {
            country: '',
            city: '',
            street: '',
            streetNumber: ''
        }
    });


    const [isDisabled, setIsDisabled] = useState(true);

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
            user.surname.length <= 255 &&
            user.profession.length <= 255 &&
            user.phoneNumber.length <= 255 &&


            user.address.country.length <= 255 &&
            user.address.city.length <= 255 &&
            user.address.street.length <= 255 &&
            user.address.streetNumber.length <= 255 &&


            user.name !== "" &&
            user.phoneNumber !== "" &&
            /^[+]?[\d\s.-](?:\/?[\d\s.-]){0,}$/.test(user.phoneNumber) &&
            user.profession !== "" &&
            user.surname !== "" &&
            user.address.country !== "" &&
            user.address.city !== "" &&
            user.address.street !== "" &&
            user.address.streetNumber !== "";
        setIsDisabled(!isValid);
    }, [user]);


    const [oldPassword, setOldPassword] = useState("")
    const [newPassword, setNewPassword] = useState("")
    const [passwordDialogShow, setPasswordDialogShow] = useState(false)
    const [successDialogShow, setSuccessDialogShow] = useState(false)
    const handleOldPasswordChange = (event) => {
        setOldPassword(event.target.value);
    };

    const handleNewPasswordChange = (event) => {
        setNewPassword(event.target.value);
    };
    const [errorDialogShow, setErrorDialogShow] = useState(false)

    const handleChangePassword = () => {

        interceptor.post('account/change-password', {
            oldPassword: oldPassword,
            newPassword: newPassword
        })
            .then((response) => {
                setOldPassword("");
                setNewPassword("");
                setPasswordDialogShow(false);
                setSuccessDialogShow(true)
            })
            .catch((error) => {
                setErrorDialogShow(true)
                setPasswordDialogShow(false);
            });

    };
    const handleErrorClose = () => {
        setErrorDialogShow(false)
    };

    const handleUpdate = () => {
        interceptor.put("employee/logged-in-info", user).then((res) => {
            setSuccessDialogShow(true)
        }).catch((err) => {
            console.log(err)
        })


    };

    const getMyInfo = () => {
        interceptor.get("employee/logged-in-info").then((res) => {
            setUser(res.data)

        }).catch((err) => {
            console.log(err)
        })

    }

    useEffect(() => {
        getMyInfo()
    }, []);

    const handleClose = () => {
        setSuccessDialogShow(false)
    };

    return (
        <>
            <Dialog open={passwordDialogShow} onClose={() => setPasswordDialogShow(false)}>
                <DialogTitle>Change password</DialogTitle>
                <DialogContent>
                    <Box m={1}>
                        <TextField
                            fullWidth
                            variant="filled"
                            label="Old password"
                            type="password"
                            name="oldPassword"
                            value={oldPassword}
                            onChange={handleOldPasswordChange}
                        />
                    </Box>

                    <Box m={1}>
                        <TextField m={1}
                                   fullWidth
                                   variant="filled"
                                   label="New password"
                                   type="password"
                                   name="newPassword"
                                   value={newPassword}
                                   onChange={handleNewPasswordChange}
                        />
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => {
                        setOldPassword("");
                        setNewPassword("");
                        setPasswordDialogShow(false);
                    }} color="error"
                            variant="outlined">
                        Close
                    </Button>
                    <Button
                        onClick={handleChangePassword}
                        disabled={
                            oldPassword.length < 8 ||
                            newPassword.length < 8 ||
                            !/[A-Z]/.test(oldPassword) ||  // At least one uppercase letter in old password
                            !/[a-z]/.test(oldPassword) ||  // At least one lowercase letter in old password
                            !/\d/.test(oldPassword) ||     // At least one number in old password
                            !/[!@#$%^&*]/.test(oldPassword)  // At least one special character in old password
                            ||
                            !/[A-Z]/.test(newPassword) ||  // At least one uppercase letter in new password
                            !/[a-z]/.test(newPassword) ||  // At least one lowercase letter in new password
                            !/\d/.test(newPassword) ||     // At least one number in new password
                            !/[!@#$%^&*]/.test(newPassword)  // At least one special character in new password
                        }
                        color="warning"
                        variant="contained"
                    >
                        Change password
                    </Button>
                </DialogActions>
            </Dialog>

            <Dialog onClose={handleClose} open={successDialogShow}>
                <DialogTitle>Update Successful!</DialogTitle>
                <DialogActions>
                    <Button onClick={handleClose}
                            variant="contained"
                    >
                        Close
                    </Button>
                </DialogActions>
            </Dialog>

            <Dialog onClose={handleErrorClose} open={errorDialogShow}>
                <DialogTitle>Error</DialogTitle>
                <DialogActions>
                    <Button onClick={handleErrorClose}
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
                            <p>All fields must be filled for update and phone number must be valid</p>
                        </Box>
                        <Box m={1}>
                            <Button variant="contained" color="success" endIcon={<PublishedWithChangesIcon/>}
                                    disabled={isDisabled}
                                    onClick={handleUpdate}
                            >
                                Update</Button>
                        </Box>
                    </Flex>
                    <hr
                        style={{
                            width: "100%",
                            border: "1px solid grey",
                        }}
                    />
                    <Flex flexDirection="column" justifyContent="center" alignItems="center">
                        <Box m={1}>
                            Old password must be re-entered so that it can be changed
                        </Box>
                        <Box>
                            <p>Password must contain one uppercase letter, one lowercase letter, one special character
                                and minimum 8 characters</p>
                        </Box>
                        <Box m={1}>
                            <Button

                                color="warning"
                                variant="contained"
                                onClick={() => {
                                    setPasswordDialogShow(true)
                                }}
                            >
                                Change Password TBA

                            </Button>
                        </Box>
                    </Flex>

                </Flex>


            </div>

        </>
    );
}

export default Profile;