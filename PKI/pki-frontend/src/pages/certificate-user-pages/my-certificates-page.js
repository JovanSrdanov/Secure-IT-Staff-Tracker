import React from 'react';

import "../page.css"
import MyCertificates from "../../components/my-certificates/my-certificates";

function MyCertificatesPage() {
    return (
        <div className="page">
            <h1>My certificates</h1>
            <MyCertificates></MyCertificates>
        </div>
    );
}

export default MyCertificatesPage;