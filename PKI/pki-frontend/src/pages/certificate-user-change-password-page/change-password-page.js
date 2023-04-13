import React from 'react';

import "../page.css"
import ChangePassword from "../../components/change-password/change-password";

function ChangePasswordPage() {
    return (
        <div className="page">
            <h1>Change the password</h1>
            <h2>Set new password using the old one sent to you via e-mail</h2>
            <ChangePassword></ChangePassword>

        </div>
    );
}

export default ChangePasswordPage;