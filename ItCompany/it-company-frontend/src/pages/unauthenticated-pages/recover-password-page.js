import React, {useState} from 'react';
import {useLocation, useNavigate} from "react-router-dom";
import {Button, Dialog, DialogActions, DialogTitle, TextField} from "@mui/material";
import LoginIcon from "@mui/icons-material/Login";
import interceptor from "../../interceptor/interceptor";

function RecoverPasswordPage(props) {

    const location = useLocation();
    const searchParams = new URLSearchParams(location.search);
    const token = searchParams.get('token');
    const navigate = useNavigate();


    const updatePasswordClick = async () => {
        interceptor.post("account/recover/" + token, {newPassword: newPassword}).then((res) => {
            setSuccessShow(true)
        }).catch((err) => {
            console.log(err)
            setErrorDialogShow(true)
        })

    };


    const [newPassword, setNewPassword] = React.useState("");

    const handlePasswordChange = (event) => {
        setNewPassword(event.target.value);
    };
    const [errorDialogShow, setErrorDialogShow] = useState(false)
    const [successShow, setSuccessShow] = useState(false)

    const handleErrorClose = () => {
        setErrorDialogShow(false)
    };

    const handleCloseSuccess = () => {
        navigate("/login");
    };

    return (
        <>
            <Dialog onClose={handleErrorClose} open={errorDialogShow}>
                <DialogTitle>Error updating the password</DialogTitle>
                <DialogActions>
                    <Button onClick={handleErrorClose}
                            variant="contained"
                    >
                        Close
                    </Button>
                </DialogActions>
            </Dialog>


            <Dialog onClose={handleCloseSuccess} open={successShow}>
                <DialogTitle>Password changed!</DialogTitle>
                <DialogActions>
                    <Button onClick={handleCloseSuccess}
                            variant="contained"
                    >
                        Close
                    </Button>
                </DialogActions>
            </Dialog>

            <div>
                <h1>Recover password</h1>

                <div className="wrapper">

                    <TextField
                        fullWidth
                        variant="filled"
                        label="New password"
                        type="password"
                        value={newPassword}
                        onChange={handlePasswordChange}
                    />
                    <Button
                        disabled={!(newPassword.length >= 8 &&
                            newPassword.length <= 255 &&
                            /[A-Z]/.test(newPassword) &&
                            /[a-z]/.test(newPassword) &&
                            /\d/.test(newPassword) &&
                            /[_!@#$%^&*(),.?":{}|<>]/.test(newPassword))}
                        variant="contained" color="primary" endIcon={<LoginIcon/>}
                        onClick={updatePasswordClick}
                    >Update password
                    </Button>

                </div>


            </div>
        </>
    );
}

export default RecoverPasswordPage;