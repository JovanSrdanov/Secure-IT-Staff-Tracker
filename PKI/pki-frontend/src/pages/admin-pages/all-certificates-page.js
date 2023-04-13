import React from 'react';

import "../page.css"
import AllCertificates from "../../components/all-certificate/all-certificates";

function AllCertificatesPage() {
    return (
        <div className="page">
            <h1>All certificates </h1>
            <AllCertificates></AllCertificates>
        </div>
    );
}

export default AllCertificatesPage;