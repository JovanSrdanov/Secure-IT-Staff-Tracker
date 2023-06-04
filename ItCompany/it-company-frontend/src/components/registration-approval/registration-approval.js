import React, {useEffect} from 'react';
import {
    Box,
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    Paper,
    styled,
    Table,
    TableBody,
    TableCell,
    tableCellClasses,
    TableContainer,
    TableRow,
    TextField
} from "@mui/material";
import interceptor from "../../interceptor/interceptor";
import {Flex} from "reflexbox";


const StyledTableCell = styled(TableCell)(({theme}) => ({
    [`&.${tableCellClasses.head}`]: {
        backgroundColor: theme.palette.common.black,
        color: theme.palette.common.white,
    },
    [`&.${tableCellClasses.body}`]: {
        fontSize: 14,
    },
}));

const StyledTableRow = styled(TableRow)(({theme}) => ({
    '&:nth-of-type(odd)': {
        backgroundColor: theme.palette.action.focusOpacity,
    }
}));


function RegistrationApproval(props) {

    const [pendingAccounts, setPendingAccounts] = React.useState(null);
    const [selectedAccountEmail, setSelectedAccountEmail] = React.useState(null);
    const [showRejectDialog, setShowRejectDialog] = React.useState(false);
    const [rejectionText, setRejectionText] = React.useState("");

    const getAllPendingAccounts = () => {
        interceptor.get("account/pending").then(res => {
            setPendingAccounts(res.data)

        }).catch(err => {
            console.log(err)
        })

    }


    useEffect(() => {
        getAllPendingAccounts();
    }, []);

    const handleReject = (email) => {
        setSelectedAccountEmail(email)
        setShowRejectDialog(true)
    };
    const handleAccept = (email) => {
        interceptor.get("auth/accept-registration/" + email)
            .then(res => {
                    getAllPendingAccounts();
                }
            ).catch(err => {
                console.log(err)
            }
        )

    };
    const handleCloseRejectDialog = () => {
        setShowRejectDialog(false)
        setSelectedAccountEmail(null)
    };
    const rejectAccount = () => {
        var sendData = {
            mail: selectedAccountEmail,
            reason: rejectionText,
        }


        interceptor.post("auth/reject-registration", sendData).then(res => {
                getAllPendingAccounts();
                handleCloseRejectDialog();
            }
        ).catch(err => {
                console.log(err)
            }
        )
    };
    return (
        <>
            <Dialog onClose={handleCloseRejectDialog} open={showRejectDialog}>
                <DialogTitle>Reason for rejection:</DialogTitle>
                <DialogContent>
                    <TextField
                        sx={{width: 500}}
                        id="filled-textarea"
                        label="Reason..."
                        multiline
                        rows={5}
                        variant="filled"
                        value={rejectionText}
                        onChange={(event) => setRejectionText(event.target.value)}
                    />


                </DialogContent>
                <DialogActions>
                    <Flex flexDirection="row" justifyContent="center" alignItems="center">
                        <Button onClick={handleCloseRejectDialog}
                                variant="contained"

                        >
                            Close
                        </Button>

                        <Box m={1}>
                            <Button
                                onClick={rejectAccount}
                                disabled={rejectionText === "" || rejectionText.length >= 255}
                                variant="contained" color="error">Reject</Button>
                        </Box>
                    </Flex>
                </DialogActions>
            </Dialog>

            <div className="wrapper">
                {pendingAccounts != null && pendingAccounts.length > 0 && (
                    <TableContainer component={Paper}
                                    sx={{maxHeight: 500, height: 500, overflowY: 'scroll'}}>
                        <Table>
                            <TableBody>
                                {pendingAccounts.map((item) => (
                                    <React.Fragment key={`${item.email}-row`}>
                                        <StyledTableRow>
                                            <StyledTableCell>
                                                <Box m={1} sx={{
                                                    overflowX: 'auto',
                                                    width: 300,
                                                    height: 100,
                                                    overflowy: 'auto'
                                                }}>
                                                    <li>Email: {item.email}</li>
                                                    <li>Role: {item.role.replace(/^ROLE_/, '').replace(/_/g, ' ')}</li>
                                                </Box>
                                            </StyledTableCell>
                                            <StyledTableCell>
                                                <Box m={1} sx={{
                                                    overflowX: 'auto',
                                                    width: 300,
                                                    height: 100,
                                                    overflowy: 'auto'
                                                }}>
                                                    <li>Name: {item.name}</li>
                                                    <li>Surname: {item.surname}</li>
                                                    <li>Phone number: {item.phoneNumber}</li>
                                                    <li>Profession: {item.profession}</li>
                                                </Box>
                                            </StyledTableCell>
                                            <StyledTableCell>
                                                <Box m={1} sx={{
                                                    overflowX: 'auto',
                                                    width: 300,
                                                    height: 100,
                                                    overflowy: 'auto'
                                                }}>
                                                    <li>Address:</li>
                                                    <li>{item.address.city}, {item.address.country}</li>
                                                    <li>{item.address.street}, {item.address.streetNumber}</li>
                                                </Box>
                                            </StyledTableCell>
                                            <StyledTableCell>
                                                <Box m={1}>

                                                    <Box m={1}>
                                                        <Button fullWidth variant="contained"
                                                                color="success"
                                                                onClick={() => {
                                                                    handleAccept(item.email)
                                                                }}

                                                        >Accept
                                                        </Button>
                                                    </Box>
                                                    <Box m={1}>
                                                        <Button fullWidth variant="outlined"
                                                                onClick={() => {
                                                                    handleReject(item.email)
                                                                }}
                                                                color="error"
                                                        >Reject
                                                        </Button>
                                                    </Box>
                                                </Box>
                                            </StyledTableCell>
                                        </StyledTableRow>
                                    </React.Fragment>
                                ))}
                            </TableBody>
                        </Table>
                    </TableContainer>
                )}

            </div>

        </>
    );
}

export default RegistrationApproval;