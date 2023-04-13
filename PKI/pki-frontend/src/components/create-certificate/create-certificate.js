import React, {useEffect, useState} from 'react';
import {
    Button,
    Checkbox,
    Dialog,
    DialogActions,
    DialogTitle,
    FormControlLabel,
    FormGroup,
    InputLabel,
    MenuItem,
    Radio,
    RadioGroup,
    Select,
    Step,
    StepLabel,
    Stepper,
    TextField
} from "@mui/material";
import {DatePicker, LocalizationProvider} from "@mui/x-date-pickers";
import {AdapterDayjs} from "@mui/x-date-pickers/AdapterDayjs";
import dayjs from "dayjs";
import {useNavigate} from "react-router-dom";
import interceptor from "../../interceptor/interceptor";
import "./create-certificate.css"


function CreateCertificate() {
    const navigate = useNavigate();
    const steps = ['Selfsigned / CA issued', 'Issuer', 'Subject', 'Valid not before', 'Valid not after', 'Extensions'];
    const [dialogOpen, setDialogOpen] = useState(false);
    const [activeStep, setActiveStep] = useState(0);
    const [typeOfCertificate, setTypeOfCertificate] = useState('selfsigned');
    const [subjectTypeSelected, setSubjectTypeSelected] = useState('existing');
    const [selectedSubjectEmail, setSelectedSubjectEmail] = useState(null);
    const [uniqueEmailForNewSubject, setUniqueEmailForNewSubject] = useState(false);
    const [uniqueEmailForNewIssuer, setUniqueEmailForNewIssuer] = useState(false);
    const [certificates, setCertificates] = useState(null);
    const [existingAccounts, setExistingAccounts] = useState(null);


    const [subjectInfo, setSubjectInfo] = useState({
        commonName: '',
        surname: '',
        givenName: '',
        organization: '',
        organizationUnitName: '',
        countryCode: '',
        email: '',

    });
    const [issuer, setIssuer] = useState({
        commonName: '',
        surname: '',
        givenName: '',
        organization: '',
        organizationUnitName: '',
        countryCode: '',
        email: '',
        isAccountNew: ''
    });
    const [startDate, setStartDate] = useState(null);
    const [endDate, setEndDate] = useState(null);
    const [selectedCertificate, setSelectedCertificate] = useState({
        startDate: '',
        endDate: '',
    });
    const [keyUsage, setKeyUsage] = useState('keyEncipherment');
    const handleChange = (event) => {
        setKeyUsage(event.target.value);
    };

    const [CAChecked, setCAChecked] = useState(false);
    const [subjectKeyIdentifierChecked, setSubjectKeyIdentifierChecked] = useState(false);


    const handleCAChange = (event) => {
        setCAChecked(event.target.checked);
    };

    const handleSubjectKeyIdentifierChange = (event) => {
        setSubjectKeyIdentifierChecked(event.target.checked);
    };


    const handleNext = () => {
        setActiveStep((prevActiveStep) => prevActiveStep + 1);
    };

    const handleBack = () => {
        setActiveStep((prevActiveStep) => prevActiveStep - 1);
    };

    const handleStartDateChange = (date) => {
        setStartDate(date);
    };

    const handleEndDateChange = (date) => {
        setEndDate(date);
    };
    const handleTypeOfCertificateChange = (event) => {
        setTypeOfCertificate(event.target.value);
    };

    const handleSubjectTypeSelectedChange = (event) => {
        setSubjectTypeSelected(event.target.value);
    };


    const handleSelectedCertificate = (certificate) => {
        setSelectedCertificate(certificate)

    };
    const isSelectedCertificate = (item) => {
        return item && selectedCertificate.alias === item.alias

    };

    const handleSelectedAccount = (email) => {
        setSelectedSubjectEmail(email)

    };

    const isSelectedAccount = (email) => {
        return email && email === selectedSubjectEmail

    };


    const handleSubjectInputChange = (event) => {

        const {name, value} = event.target;
        setSubjectInfo(prevState => ({
            ...prevState,
            [name]: value
        }));
    }

    const handelIssuerInputChange = (event) => {
        const {name, value} = event.target;
        setIssuer(prevState => ({
            ...prevState,
            [name]: value
        }));
    }

    const createCertificate = () => {
        let extensions = {}
        extensions.keyUsage = keyUsage;
        if (CAChecked) {
            extensions.basicConstraints = "CA";
        }
        if (subjectKeyIdentifierChecked) {
            extensions.subjectKeyIdentifier = ""
        }

        let subjectInfoCorrected = {};

        if (typeOfCertificate === "selfsigned") {
            subjectInfoCorrected = issuer;
            subjectInfoCorrected.isAccountNew = true;
        }
        if (typeOfCertificate === "caissued") {
            subjectInfoCorrected = subjectInfo;
            if (subjectTypeSelected === "new") {
                subjectInfoCorrected.isAccountNew = true;
            }
            if (subjectTypeSelected === "existing") {
                subjectInfoCorrected.isAccountNew = false;
                subjectInfoCorrected.email = selectedSubjectEmail;

            }
        }

        let dto = {
            subjectInfo: subjectInfoCorrected,
            startDate: startDate,
            endDate: endDate,
            issuingCertificateSerialNumber: selectedCertificate.issuerSerialNumber,
            extensions: extensions
        }


        interceptor.post("/certificate", dto).then(res => {
            setDialogOpen(true)
        }).catch(err => {
            alert("Unexpected error")
        })
    };


    useEffect(() => {
        interceptor.get("certificate/allCa").then(res => {
            setCertificates(res.data)
            console.log(certificates)
        }).catch(err => {
            alert("Unexpected error")
        })

        interceptor.get("account/all").then(res => {
            setExistingAccounts(res.data)
        }).catch(err => {
            alert("Unexpected error")
        })

    }, []);


    function dialogClose() {
        setDialogOpen(false)
        navigate("/all-certificates")

    }

    return (
        <>
            <div className="wrapper" style={{width: "90%"}}>
                <Stepper activeStep={activeStep}>
                    {steps.map((label) => (
                        <Step key={label}>
                            <StepLabel>{label}</StepLabel>
                        </Step>
                    ))}
                </Stepper>
                {activeStep === 0 && (
                    <div className="wrapper">
                        <h2>Choose if the new certificate is selfsigned or CA issued</h2>
                        <RadioGroup value={typeOfCertificate} onChange={handleTypeOfCertificateChange}>
                            <FormControlLabel value="selfsigned" control={<Radio/>} label="Selfsigned"/>
                            <FormControlLabel value="caissued" control={<Radio/>} label="CA issued"/>
                        </RadioGroup>
                    </div>
                )}
                {activeStep === 1 && (
                    <div className="wrapper">
                        {typeOfCertificate === "selfsigned" &&
                            (
                                <>
                                    <h2>Create new issuer</h2>
                                    <TextField
                                        fullWidth
                                        variant="filled"
                                        label="Common Name"
                                        type="text"
                                        name="commonName"
                                        value={issuer.commonName}
                                        onChange={handelIssuerInputChange}
                                    />
                                    <TextField
                                        fullWidth
                                        variant="filled"
                                        label="Surname"
                                        type="text"
                                        name="surname"
                                        value={issuer.surname}
                                        onChange={handelIssuerInputChange}
                                    />
                                    <TextField
                                        fullWidth
                                        variant="filled"
                                        label="Given name"
                                        type="text"
                                        name="givenName"
                                        value={issuer.givenName}
                                        onChange={handelIssuerInputChange}
                                    />
                                    <TextField
                                        fullWidth
                                        variant="filled"
                                        label="Organization"
                                        type="text"
                                        name="organization"
                                        value={issuer.organization}
                                        onChange={handelIssuerInputChange}
                                    />
                                    <TextField
                                        fullWidth
                                        variant="filled"
                                        label="Organization Unit Name"
                                        type="text"
                                        name="organizationUnitName"
                                        value={issuer.organizationUnitName}
                                        onChange={handelIssuerInputChange}
                                    />
                                    <TextField
                                        fullWidth
                                        variant="filled"
                                        label="Country code"
                                        type="text"
                                        name="countryCode"
                                        value={issuer.countryCode}
                                        onChange={handelIssuerInputChange}
                                    />
                                    <TextField
                                        fullWidth
                                        variant="filled"
                                        label="E-mail"
                                        type="text"
                                        name="email"
                                        value={issuer.email}
                                        onChange={handelIssuerInputChange}
                                    />
                                    <p>
                                        {uniqueEmailForNewIssuer && (<span>Email is unique</span>)}
                                        {!uniqueEmailForNewIssuer && (<span>Email is not unique</span>)}
                                    </p>
                                </>
                            )
                        }

                        {typeOfCertificate === "caissued" &&
                            (
                                <>
                                    <h2>Select certificate that will be the issuer</h2>
                                    {certificates != null &&
                                        (<div className="scrollCertificates">
                                                {certificates.map((item) => (<div className="certificateWrapper"
                                                                                  onClick={() => {
                                                                                      handleSelectedCertificate(item)
                                                                                  }}
                                                                                  style={{
                                                                                      backgroundColor: isSelectedCertificate(item) ? "var(--outlines)" : "inherit",

                                                                                  }}>
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
                                                    <li>Revoked: {item.revoked.toString()}</li>
                                                    <li>Is CA: {item.ca.toString()}</li>

                                                </div>))}

                                            </div>
                                        )
                                    }
                                </>
                            )
                        }


                    </div>
                )}
                {activeStep === 2 && (
                    <div className="wrapper">
                        {typeOfCertificate === "selfsigned" &&
                            (<h2>Issuer is the same as the subject. Proceed.</h2>)
                        }

                        {typeOfCertificate === "caissued" &&
                            (
                                <>
                                    <h2>Select subject or create a new subject</h2>
                                    <RadioGroup value={subjectTypeSelected} onChange={handleSubjectTypeSelectedChange}>
                                        <FormControlLabel value="existing" control={<Radio/>} label="Existing"/>
                                        <FormControlLabel value="new" control={<Radio/>} label="New"/>
                                    </RadioGroup>
                                    {subjectTypeSelected === "new" &&
                                        (
                                            <>
                                                <TextField
                                                    fullWidth
                                                    variant="filled"
                                                    label="Common Name"
                                                    type="text"
                                                    name="commonName"
                                                    value={subjectInfo.commonName}
                                                    onChange={handleSubjectInputChange}
                                                />
                                                <TextField
                                                    fullWidth
                                                    variant="filled"
                                                    label="Surname"
                                                    type="text"
                                                    name="surname"
                                                    value={subjectInfo.surname}
                                                    onChange={handleSubjectInputChange}
                                                />
                                                <TextField
                                                    fullWidth
                                                    variant="filled"
                                                    label="Given name"
                                                    type="text"
                                                    name="givenName"
                                                    value={subjectInfo.givenName}
                                                    onChange={handleSubjectInputChange}
                                                />
                                                <TextField
                                                    fullWidth
                                                    variant="filled"
                                                    label="Organization"
                                                    type="text"
                                                    name="organization"
                                                    value={subjectInfo.organization}
                                                    onChange={handleSubjectInputChange}
                                                />
                                                <TextField
                                                    fullWidth
                                                    variant="filled"
                                                    label="Organization Unit Name"
                                                    type="text"
                                                    name="organizationUnitName"
                                                    value={subjectInfo.organizationUnitName}
                                                    onChange={handleSubjectInputChange}
                                                />
                                                <TextField
                                                    fullWidth
                                                    variant="filled"
                                                    label="Country code"
                                                    type="text"
                                                    name="countryCode"
                                                    value={subjectInfo.countryCode}
                                                    onChange={handleSubjectInputChange}
                                                />
                                                <TextField
                                                    fullWidth
                                                    variant="filled"
                                                    label="E-mail"
                                                    type="text"
                                                    name="email"
                                                    value={subjectInfo.email}
                                                    onChange={handleSubjectInputChange}
                                                />
                                                <p>
                                                    {uniqueEmailForNewSubject && (<span>Email is unique</span>)}
                                                    {!uniqueEmailForNewSubject && (<span>Email is not unique</span>)}
                                                </p>

                                            </>
                                        )
                                    }

                                    {subjectTypeSelected === "existing" && (
                                        <>
                                            {existingAccounts != null &&
                                                (<div className="scrollAccounts">
                                                        {existingAccounts.map((item) => (
                                                            <div className="accountsWrapper"
                                                                 onClick={() => {
                                                                     handleSelectedAccount(item.email)
                                                                 }}
                                                                 style={{
                                                                     backgroundColor: isSelectedAccount(item.email) ? "var(--outlines)" : "inherit",

                                                                 }}>
                                                                <li>{item.email}</li>


                                                            </div>))}

                                                    </div>
                                                )
                                            }
                                        </>
                                    )
                                    }

                                </>
                            )
                        }
                    </div>
                )}
                {activeStep === 3 && (
                    <div className="wrapper">
                        {typeOfCertificate === "selfsigned" &&
                            (
                                <>
                                    <h2>Select date from when the certificate is valid that is not in the past</h2>
                                    <LocalizationProvider dateAdapter={AdapterDayjs}>
                                        <DatePicker label="Start date"
                                                    value={startDate}
                                                    minDate={dayjs()}
                                                    onChange={handleStartDateChange}
                                                    sx={{
                                                        width: "fit-content",
                                                        margin: "auto"
                                                    }}
                                        />
                                    </LocalizationProvider>
                                </>
                            )
                        }

                        {typeOfCertificate === "caissued" &&
                            (
                                <>
                                    <h2>Select date from when the current certificate is valid and it must be in between
                                        valid
                                        dates of
                                        issuing
                                        certificate</h2>
                                    <LocalizationProvider dateAdapter={AdapterDayjs}>
                                        <DatePicker label="End date"
                                                    value={startDate}
                                                    onChange={handleStartDateChange}
                                                    minDate={dayjs(selectedCertificate.startDate)}
                                                    maxDate={dayjs(selectedCertificate.endDate)}
                                                    sx={{
                                                        width: "fit-content",
                                                        margin: "auto"
                                                    }}
                                        />
                                    </LocalizationProvider>
                                </>

                            )
                        }
                    </div>
                )}
                {activeStep === 4 && (
                    <div className="wrapper">
                        {typeOfCertificate === "selfsigned" &&
                            (<>
                                    <h2>Select date until the certificate is valid and that date must be after
                                        certificate
                                        start
                                        date</h2>

                                    <LocalizationProvider dateAdapter={AdapterDayjs}>
                                        <DatePicker label="End date"
                                                    value={endDate}
                                                    minDate={startDate}
                                                    onChange={handleEndDateChange}
                                                    sx={{
                                                        width: "fit-content",
                                                        margin: "auto"
                                                    }}
                                        />
                                    </LocalizationProvider>

                                </>


                            )
                        }

                        {typeOfCertificate === "caissued" &&
                            (
                                <>
                                    <h2>Select date until when the certificate is valid and that date must be in between
                                        current certificate start date and issuing
                                        certificate end date</h2>
                                    <LocalizationProvider dateAdapter={AdapterDayjs}>
                                        <DatePicker label="End date"
                                                    value={endDate}
                                                    minDate={startDate}
                                                    onChange={handleEndDateChange}
                                                    maxDate={dayjs(selectedCertificate.endDate)}
                                                    sx={{
                                                        width: "fit-content",
                                                        margin: "auto"
                                                    }}
                                        />
                                    </LocalizationProvider>
                                </>

                            )


                        }


                    </div>
                )}
                {activeStep === 5 && (
                    <div className="wrapper">
                        <h2>Select extensions</h2>

                        <InputLabel id="key-usage-label">Key Usage</InputLabel>
                        <Select
                            labelId="key-usage-label"
                            id="key-usage-select"
                            value={keyUsage}
                            onChange={handleChange}
                        >
                            <MenuItem value="keyEncipherment" selected>Key Encipherment</MenuItem>
                            <MenuItem value="dataEncipherment">Data Encipherment</MenuItem>
                        </Select>
                        <FormGroup>
                            <FormControlLabel
                                control={<Checkbox checked={CAChecked} onChange={handleCAChange} name="CA"/>}
                                label="CA"
                            />

                            <FormControlLabel
                                control={<Checkbox checked={subjectKeyIdentifierChecked}
                                                   onChange={handleSubjectKeyIdentifierChange}
                                                   name="subjectKeyIdentifier"/>}
                                label="Subject Key Identifier"
                            />
                        </FormGroup>
                        <h2>Extension "Authority Key Identifier" will always be added.</h2>

                    </div>
                )}
                <div>
                    <Button disabled={activeStep === 0} onClick={handleBack}>
                        Back
                    </Button>
                    {activeStep !== steps.length - 1 ? (
                        <Button
                            onClick={handleNext}
                            disabled={
                                // TODO popravi ovo true u false
                                (activeStep === 1 && typeOfCertificate === "caissued" && selectedCertificate.issuerSerialNumber == null) ||
                                (activeStep === 1 && typeOfCertificate === "selfsigned" && uniqueEmailForNewIssuer === true) ||
                                (activeStep === 2 && typeOfCertificate === "caissued" && subjectTypeSelected === "new" && uniqueEmailForNewSubject === true) ||
                                (activeStep === 2 && typeOfCertificate === "caissued" && subjectTypeSelected === "existing" && selectedSubjectEmail == null) ||
                                (activeStep === 3 && startDate == null) ||
                                (activeStep === 4 && endDate == null)}
                        >
                            Next
                        </Button>
                    ) : (
                        <Button onClick={createCertificate}>Create certificate</Button>
                    )}
                </div>
            </div>
            <Dialog
                onClose={dialogClose} open={dialogOpen}>
                <DialogTitle id="alert-dialog-title">
                    {"Certificate created!"}
                </DialogTitle>
                <DialogActions>
                    <Button onClick={dialogClose}>Close</Button>
                </DialogActions>
            </Dialog>
        </>
    );
}

export default CreateCertificate;