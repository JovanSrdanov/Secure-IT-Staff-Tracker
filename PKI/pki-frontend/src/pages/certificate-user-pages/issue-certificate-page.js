import React from 'react';

import "../page.css"
import IssueCertificate from "../../components/issue-certificate/issue-certificate";

function IssueCertificatePage() {
    return (
        <div className="page">
            <h1>Issue Certificate</h1>
            <IssueCertificate></IssueCertificate>
        </div>
    );
}

export default IssueCertificatePage;