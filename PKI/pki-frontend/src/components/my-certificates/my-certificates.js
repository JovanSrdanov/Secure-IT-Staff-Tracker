import React, {useEffect, useState} from 'react';
import interceptor from "../../interceptor/interceptor";
import {Button} from "@mui/material";

function MyCertificates() {

    const [certificates, setCertificates] = useState(null);

    useEffect(() => {
        interceptor.get("certificate/loggedIn").then(res => {
            setCertificates(res.data)
        }).catch(err => {
        })


    }, []);

    const download = (item) => {
        interceptor.get("certificate/download/" + item.serialNumber, {
            responseType: 'blob',
        }).then(res => {

            const url = window.URL.createObjectURL(new Blob([res.data]));
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', 'certificate.crt');
            document.body.appendChild(link);
            link.click();
        }).catch(err => {
        })

    };


    return (
        <div>
            {certificates != null &&
                (<div className="scrollCertificates">
                        {certificates.map((item) => (<div className="certificateWrapper">
                            <p>Subject</p>
                            <li>Country code: {item.subjectInfo.country}</li>
                            <li>Organization unit: {item.subjectInfo.orgUnit}</li>
                            <li>Organization: {item.subjectInfo.organization}</li>
                            <li>Common name: {item.subjectInfo.commonName}</li>
                            <li>Surname: {item.subjectInfo.surname}</li>
                            <li>Given name: {item.subjectInfo.givenName}</li>
                            <p>Issuer</p>
                            <li>Country code: {item.issuerInfo.country}</li>
                            <li>Organization unit: {item.issuerInfo.orgUnit}</li>
                            <li>Organization: {item.issuerInfo.organization}</li>
                            <li>Common name: {item.issuerInfo.commonName}</li>
                            <li>Surname: {item.issuerInfo.surname}</li>
                            <li>Given name: {item.issuerInfo.givenName}</li>
                            <p>Certificate info</p>
                            <li>Valid not before: {item.startDate}</li>
                            <li>Valid not after: {item.endDate}</li>
                            <li>Alias: {item.alias}</li>
                            <li>Issuer serial number: {item.issuerSerialNumber}</li>
                            <li>Serial number: {item.serialNumber}</li>
                            <li>Is CA: {item.ca.toString()}</li>
                            <li>Revoked: {item.revoked.toString()}</li>
                            <Button variant="contained" onClick={() => download(item)}>Download</Button>

                        </div>))}
                    </div>
                )
            }
        </div>
    );
}

export default MyCertificates;