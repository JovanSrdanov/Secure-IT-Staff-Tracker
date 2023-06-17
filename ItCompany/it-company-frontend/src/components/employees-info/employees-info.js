import React, {useEffect, useState} from "react";
import interceptor from "../../interceptor/interceptor";
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
} from "@mui/material";
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
    "&:nth-of-type(odd)": {
        backgroundColor: theme.palette.action.focusOpacity,
    },
}));

function EmployeesInfo(props) {
    const [allEmployees, setAllEmployees] = useState(null);


    const [viewSkillsDialog, setViewSkillsDialog] = useState(false);
    const [skills, setSkills] = useState(null);


    const [viewPrManagerProjectsDialog, setViewPrManagerProjectsDialog] = useState(false);
    const [viewEngineerProjectsDialog, setviewEngineerProjectsDialog] = useState(false);

    const [projects, setProjects] = useState(null);


    const getAllEmployees = () => {
        interceptor
            .get("employee")
            .then((res) => {
                setAllEmployees(res.data);
            })
            .catch((err) => {
                console.log(err);
            });
    };

    useEffect(() => {
        getAllEmployees();
    }, []);

    const handleCloseSkillsDialog = () => {
        setViewSkillsDialog(false)
        setSkills(null);
    };
    const handleViewSkills = (item) => {
        interceptor.get("sw-engineer/skill/" + item.employeeId).then((res) => {
            setSkills(res.data)
            setViewSkillsDialog(true)
            console.log(res.data)
        }).catch((err) => {
            console.log(err)
        })
    };


    const handleClosePrManagerProjectDialog = () => {
        setViewPrManagerProjectsDialog(false)
        setProjects(null);
    };
    const handleRedirectToProjects = (item) => {

        if (item.role === "Software engineer") {
            setviewEngineerProjectsDialog(true)
            interceptor.get("project/sw-engineer/" + item.employeeId).then((res) => {
                setProjects(res.data)

            }).catch((err) => {
                console.log(err)
            })

        } else {
            setViewPrManagerProjectsDialog(true)
            interceptor.get("project/pr-manager/" + item.employeeId).then((res) => {
                setProjects(res.data)

            }).catch((err) => {
                console.log(err)
            })
        }


    };
    const handleCloseEngineerProjectDialog = () => {
        setviewEngineerProjectsDialog(false);
        setProjects(null);

    };

    const viewCV = (item) => {
        interceptor.get('sw-engineer/cv/' + item.employeeId, {responseType: 'blob'}).then((res) => {
            const blob = new Blob([res.data], {type: 'application/pdf'});
            const url = URL.createObjectURL(blob);

            // Download the file
            const link = document.createElement('a');
            link.href = url;
            link.download = 'cv.pdf';
            link.style.display = 'none';
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);

            // Open the file in a new tab
            window.open(url, '_blank');

        }).catch((err) => {
            console.log(err);
            setErrorMessage("No available CV at the moment")
            setErrorDialogShow(true)
        });
    };


    const handleErrorClose = () => {
        setErrorDialogShow(false)
    };
    const [errorDialogShow, setErrorDialogShow] = useState(false)
    const [errorMessage, setErrorMessage] = useState("")

    return (
        <>

            <Dialog onClose={handleErrorClose} open={errorDialogShow}>
                <DialogTitle>{errorMessage}</DialogTitle>
                <DialogActions>
                    <Button onClick={handleErrorClose}
                            variant="contained"
                    >
                        Close
                    </Button>
                </DialogActions>
            </Dialog>


            <Dialog onClose={handleCloseSkillsDialog} open={viewSkillsDialog}>
                <DialogTitle>Skills:</DialogTitle>
                <DialogContent>
                    {skills != null && skills.length > 0 && (
                        <TableContainer component={Paper}
                                        sx={{maxHeight: 500, height: 500, overflowY: 'scroll'}}>
                            <Table>
                                <TableBody>
                                    {skills.map((item) => (
                                        <React.Fragment key={`${item.id}-row`}>
                                            <StyledTableRow>
                                                <StyledTableCell>
                                                    <Box m={1} sx={{
                                                        width: 150,
                                                        height: 50,
                                                        overflowX: 'auto',
                                                        overflowy: 'auto'
                                                    }}>
                                                        <li>Skill name: {item.name}</li>
                                                        <li>Level: {item.level}</li>
                                                    </Box>
                                                </StyledTableCell>

                                            </StyledTableRow>
                                        </React.Fragment>
                                    ))}
                                </TableBody>
                            </Table>
                        </TableContainer>
                    )}
                </DialogContent>
                <DialogActions>
                    <Flex flexDirection="row" justifyContent="center" alignItems="center">
                        <Button onClick={handleCloseSkillsDialog}
                                variant="contained"
                        >
                            Close
                        </Button>
                    </Flex>
                </DialogActions>
            </Dialog>

            <Dialog onClose={handleClosePrManagerProjectDialog} open={viewPrManagerProjectsDialog}>
                <DialogTitle>Projects:</DialogTitle>
                <DialogContent>
                    {projects != null && projects.length > 0 && (
                        <TableContainer component={Paper}
                                        sx={{maxHeight: 500, height: 500, overflowY: 'scroll'}}>
                            <Table>
                                <TableBody>
                                    {projects.map((item) => (
                                        <React.Fragment key={`${item.id}-row`}>
                                            <StyledTableRow>
                                                <StyledTableCell>
                                                    <Box m={1} sx={{
                                                        overflowX: 'auto',
                                                        width: 300,
                                                        height: 150,
                                                        overflowY: 'auto'
                                                    }}>
                                                        <li>Name: {item.project.name}</li>
                                                        <li>Id: {item.project.id}</li>
                                                        <li>Start
                                                            date: {new Date(item.project.duration.startDate).toLocaleDateString('en-US', {hour12: false})}</li>
                                                        <li>End
                                                            date: {new Date(item.project.duration.endDate).toLocaleDateString('en-US', {hour12: false})}</li>

                                                    </Box>
                                                </StyledTableCell>
                                            </StyledTableRow>
                                        </React.Fragment>
                                    ))}
                                </TableBody>
                            </Table>
                        </TableContainer>
                    )}
                </DialogContent>
                <DialogActions>
                    <Flex flexDirection="row" justifyContent="center" alignItems="center">
                        <Button onClick={handleClosePrManagerProjectDialog}
                                variant="contained"
                        >
                            Close
                        </Button>
                    </Flex>
                </DialogActions>
            </Dialog> <


            Dialog onClose={handleCloseEngineerProjectDialog} open={viewEngineerProjectsDialog}>
            <DialogTitle>Projects:</DialogTitle>
            <DialogContent>
                {projects != null && projects.length > 0 && (
                    <TableContainer component={Paper}
                                    sx={{maxHeight: 500, height: 500, overflowY: 'scroll'}}>
                        <Table>
                            <TableBody>
                                {projects.map((item) => (
                                    <React.Fragment key={`${item.id}-row`}>
                                        <StyledTableRow>
                                            <StyledTableCell>
                                                <Box m={1} sx={{
                                                    overflowX: 'auto',
                                                    width: 400,
                                                    height: 150,
                                                    overflowY: 'auto'
                                                }}>

                                                    <li>Job description : {item.jobDescription}</li>
                                                    <li>Start the job on:
                                                        date: {new Date(item.workingPeriod.startDate).toLocaleDateString('en-US', {hour12: false})}</li>
                                                    <li>
                                                        Removed from project on:
                                                        {item.workingPeriod.endDate ? new Date(item.workingPeriod.endDate).toLocaleDateString('en-US', {hour12: false}) : ''}
                                                    </li>

                                                    <li>Name: {item.project.name}</li>
                                                    <li>Id: {item.project.id}</li>
                                                    <li>Project start
                                                        date: {new Date(item.project.duration.startDate).toLocaleDateString('en-US', {hour12: false})}</li>
                                                    <li>Project end
                                                        date: {new Date(item.project.duration.endDate).toLocaleDateString('en-US', {hour12: false})}</li>
                                                </Box>
                                            </StyledTableCell>
                                        </StyledTableRow>
                                    </React.Fragment>
                                ))}
                            </TableBody>
                        </Table>
                    </TableContainer>
                )}
            </DialogContent>
            <DialogActions>
                <Flex flexDirection="row" justifyContent="center" alignItems="center">
                    <Button onClick={handleCloseEngineerProjectDialog}
                            variant="contained"
                    >
                        Close
                    </Button>
                </Flex>
            </DialogActions>
        </Dialog>


            <div className="wrapper">
                {allEmployees != null && allEmployees.length > 0 && (
                    <TableContainer
                        component={Paper}
                        sx={{maxHeight: 500, height: 500, overflowY: "scroll"}}
                    >
                        <Table>
                            <TableBody>
                                {allEmployees.map((item) => (
                                    <React.Fragment key={`${item.employeeId}-row`}>
                                        <StyledTableRow>
                                            <StyledTableCell>
                                                <Box
                                                    m={1}
                                                    sx={{
                                                        overflowX: "auto",
                                                        width: 300,
                                                        height: 100,
                                                        overflowy: "auto",
                                                    }}
                                                >
                                                    <li>Email: {item.mail}</li>
                                                    <li>
                                                        Role:{" "}
                                                        {item.role.replace(/^ROLE_/, "").replace(/_/g, " ")}
                                                    </li>
                                                </Box>
                                            </StyledTableCell>
                                            <StyledTableCell>
                                                <Box
                                                    m={1}
                                                    sx={{
                                                        overflowX: "auto",
                                                        width: 300,
                                                        height: 100,
                                                        overflowy: "auto",
                                                    }}
                                                >
                                                    <li>Name: {item.name}</li>
                                                    <li>Surname: {item.surname}</li>
                                                    <li>Phone number: {item.phoneNumber}</li>
                                                    <li>Profession: {item.profession}</li>
                                                </Box>
                                            </StyledTableCell>
                                            <StyledTableCell>
                                                <Box
                                                    m={1}
                                                    sx={{
                                                        overflowX: "auto",
                                                        width: 300,
                                                        height: 100,
                                                        overflowy: "auto",
                                                    }}
                                                >
                                                    <li>Address:</li>
                                                    <li>
                                                        {item.address.city}, {item.address.country}
                                                    </li>
                                                    <li>
                                                        {item.address.street}, {item.address.streetNumber}
                                                    </li>
                                                </Box>
                                            </StyledTableCell>
                                            <StyledTableCell>
                                                <Box m={1}>
                                                    {item.role !== "HR manager" && (
                                                        <Box m={1}>
                                                            <Button fullWidth variant="contained" color="info"
                                                                    onClick={() => {
                                                                        handleRedirectToProjects(item)
                                                                    }}
                                                            >
                                                                View projects
                                                            </Button>
                                                        </Box>
                                                    )}
                                                    {item.role === "Software engineer" && (
                                                        <>
                                                            <Box m={1}>
                                                                <Button fullWidth variant="contained" color="primary"
                                                                        onClick={() => {
                                                                            handleViewSkills(item)
                                                                        }}
                                                                >
                                                                    View Skills
                                                                </Button>
                                                            </Box>
                                                            <Box m={1}>
                                                                <Button fullWidth variant="outlined" color="warning"
                                                                        onClick={() => {
                                                                            viewCV(item)
                                                                        }}
                                                                >
                                                                    Download cv
                                                                </Button>
                                                            </Box>
                                                        </>
                                                    )}
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

export default EmployeesInfo;
