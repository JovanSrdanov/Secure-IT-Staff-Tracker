import React from 'react';

import "../page.css"
import CreateCertificate from "../../components/create-certificate/create-certificate";

function CreateCertificatePage() {
    return (
        <div className="page">
            <h1>Create certificate</h1>
            <CreateCertificate></CreateCertificate>
        </div>
    );
}

export default CreateCertificatePage;