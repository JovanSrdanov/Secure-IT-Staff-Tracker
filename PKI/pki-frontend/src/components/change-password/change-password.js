import {useNavigate} from "react-router-dom";
import React from "react";
import interceptor from "../../interceptor/interceptor";
import {Alert, Button, TextField} from "@mui/material";
import PublishedWithChangesIcon from '@mui/icons-material/PublishedWithChanges';

function ChangePassword() {
    const navigate = useNavigate();
    const [oldPassword, setOldPassword] = React.useState("");
    const [newPassword, setNewPassword] = React.useState("");
    const [showAlert, setShowAlert] = React.useState(false);
    const [showSuccess, setShowSuccess] = React.useState(false);

    const handleOldPasswordChange = (event) => {
        setOldPassword(event.target.value);
    };

    const handleNewPasswordChange = (event) => {
        setNewPassword(event.target.value);
    };
    const handleChangePassword = async () => {
        setShowAlert(false);
        interceptor.post('account/change-password', {
            oldPassword: oldPassword,
            newPassword: newPassword
        }).then(res => {

            setShowSuccess(true);
        }).catch(err => {

            setShowAlert(true);
        })

    };
    const handleAlertClose = () => {
        setShowAlert(false);
    };

    const handleSuccesClose = () => {
        setShowSuccess(false);
        localStorage.removeItem('jwt');
        navigate('/login')

    };

    return (
        <div>
            <div className="wrapper">

                <TextField
                    fullWidth
                    variant="filled"
                    label="Old password"
                    type="password"
                    value={oldPassword}
                    onChange={handleOldPasswordChange}
                />
                <TextField
                    fullWidth
                    variant="filled"
                    label="New password"
                    type="password"
                    value={newPassword}
                    onChange={handleNewPasswordChange}
                />
                <Button
                    variant="contained" endIcon={<PublishedWithChangesIcon/>}
                    onClick={handleChangePassword}
                >Change
                </Button>


            </div>
            {showAlert && (
                <Alert sx={{width: "fit-content", margin: "10px auto"}} severity="info" onClose={handleAlertClose}>
                    Old password is not correct
                </Alert>
            )}

            {showSuccess && (
                <Alert sx={{width: "fit-content", margin: "10px auto"}} severity="info" onClose={handleSuccesClose}>
                    Succesful password change! Click on the "X" so that you can be redirected to login page.
                </Alert>
            )}
        </div>
    );
}

export default ChangePassword;