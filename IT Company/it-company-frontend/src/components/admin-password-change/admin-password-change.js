import React, {useState} from 'react';
import {Box, Button, Dialog, DialogActions, DialogTitle, TextField} from "@mui/material";
import {useNavigate} from "react-router-dom";
import interceptor from "../../interceptor/interceptor";

function AdminPasswordChange(props) {
    const [oldPassword, setOldPassword] = useState("")
    const [newPassword, setNewPassword] = useState("")
    const [successDialogShow, setSuccessDialogShow] = useState(false)
    const [errorDialogShow, setErrorDialogShow] = useState(false)
    const handleOldPasswordChange = (event) => {
        setOldPassword(event.target.value);
    };

    const handleNewPasswordChange = (event) => {
        setNewPassword(event.target.value);
    };

    const handleErrorClose = () => {
        setErrorDialogShow(false)
    };

    const handleClose = () => {
        setSuccessDialogShow(false)
        handleLogout();
    };


    function removeTokens() {
        document.cookie = 'accessToken=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
        document.cookie = 'refreshToken=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
    }

    const navigate = useNavigate()

    const handleLogout = () => {
        removeTokens()
        navigate('/login');
    };


    const handleChangePassword = () => {
        console.log({oldPassword, newPassword})
        interceptor.patch("/auth/admin-change-password", {
            oldPassword: oldPassword,
            newPassword: newPassword
        }).then((res) => {
            console.log(res.data)
            setSuccessDialogShow(true)
        }).catch((err) => {
            setErrorDialogShow(true)
        })

    };


    return (
        <>

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
                <DialogTitle>Error: Please enter the correct old password</DialogTitle>
                <DialogActions>
                    <Button onClick={handleErrorClose}
                            variant="contained"
                    >
                        Close
                    </Button>
                </DialogActions>
            </Dialog>

            <div className="wrapper">
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

                <Button
                    onClick={handleChangePassword}
                    disabled={
                        oldPassword.length < 8 ||
                        newPassword.length < 8 ||
                        !/[A-Z]/.test(oldPassword) ||  // At least one uppercase letter in old password
                        !/[a-z]/.test(oldPassword) ||  // At least one lowercase letter in old password
                        !/\d/.test(oldPassword) ||     // At least one number in old password
                        !/[_!@#$%^&*]/.test(oldPassword)   // At least one special character in old password
                        ||
                        !/[A-Z]/.test(newPassword) ||  // At least one uppercase letter in new password
                        !/[a-z]/.test(newPassword) ||  // At least one lowercase letter in new password
                        !/\d/.test(newPassword) ||     // At least one number in new password
                        !/[_!@#$%^&*]/.test(newPassword)   // At least one special character in new password
                    }
                    color="warning"
                    variant="contained"
                >
                    Change password
                </Button>
            </div>

        </>
    );
}

export default AdminPasswordChange;