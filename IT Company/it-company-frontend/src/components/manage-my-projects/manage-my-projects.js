import React, {useEffect} from 'react';
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
    TextField
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
    '&:nth-of-type(odd)': {
        backgroundColor: theme.palette.action.focusOpacity,
    }
}));

function Projects(props) {

    const [ManageMyProjects, setManageMyProjects] = React.useState(null);
    const [selectedProject, setSelectedProject] = React.useState(null);


    const getAllProjects = () => {
        interceptor.get("project/pr-manager").then(res => {
            setManageMyProjects(res.data)

        }).catch(err => {
            console.log(err)
        })
    }

    useEffect(() => {
        getAllProjects();
    }, []);


    const [viewAllEngineersOnProjectDialogShow, setViewAllEngineersOnProjectDialogShow] = React.useState(false);

    const [viewAvailableEngineersOnProjectDialogShow, setViewAvailableEngineersOnProjectDialogShow] = React.useState(false);

    const [workDescirption, setWorkDescirption] = React.useState("")
    const [workDescirptionDialog, setWorkDescirptionDialog] = React.useState(false)

    const [selectedEngineer, setSelectedEngineer] = React.useState(null)


    const handleCloseViewAllEngineersDialog = () => {
        setViewAllEngineersOnProjectDialogShow(false);
        setAllEngineers(null)
        setSelectedProject(null)
    };

    const handleCloseViewAvailableEngineersDialog = () => {
        setViewAvailableEngineersOnProjectDialogShow(false);
    };


    const handleViewAllEngineers = (item) => {
        setViewAllEngineersOnProjectDialogShow(true);
        setSelectedProject(item)
        interceptor.get("project/" + item.project.id + "/sw-engineers").then((res) => {
            setAllEngineers(res.data)
        }).catch((err) => {
            console.log(err)
        })
    };


    const handleAddAvailableEngineers = (item) => {
        setViewAvailableEngineersOnProjectDialogShow(true);
        setSelectedProject(item)
        interceptor.get("employee/unemployed-engineer/" + item.project.id).then((res) => {
            setAvailableEngineers(res.data)
        }).catch((err) => {
            console.log(err)
        })
    };


    const [allEngineers, setAllEngineers] = React.useState(null);
    const [availableEngineers, setAvailableEngineers] = React.useState(null);

    const addEngineerToProject = (item) => {
        setSelectedEngineer(item);
        setWorkDescirptionDialog(true);

    };


    const addWithDescription = () => {
        interceptor.patch("project/" + selectedProject.project.id + "/add-sw-engineer", {
            swEngineerId: selectedEngineer.employeeId,
            jobDescription: workDescirption
        }).then((res) => {
            interceptor.get("employee/unemployed-engineer/" + selectedProject.project.id).then((res) => {
                setAvailableEngineers(res.data)
                handleCloseDescriptionDialog();
            }).catch((err) => {
                console.log(err)
            })
        }).catch((err) => {
            console.log(err)
        })

    };
    const handleCloseDescriptionDialog = () => {
        setWorkDescirptionDialog(false);
        setWorkDescirption("");
        setSelectedEngineer(null);
    };


    const removeEngineerFromProject = (item) => {

        interceptor.patch("project/" + selectedProject.project.id + "/dismiss-sw-engineer", {workerId: item.swEngineer.id})
            .then((res) => {
                interceptor.get("project/" + selectedProject.project.id + "/sw-engineers").then((res) => {
                    setAllEngineers(res.data)
                }).catch((err) => {
                    console.log(err)
                })
            }).catch((err) => {
            console.log(err)
        })


    };
    return (
        <>
            <Dialog onClose={handleCloseDescriptionDialog} open={workDescirptionDialog}>
                <DialogTitle>Work description:</DialogTitle>
                <DialogContent>
                    <TextField
                        sx={{width: 500}}
                        id="filled-textarea"
                        label="Work description..."
                        multiline
                        rows={5}
                        variant="filled"
                        value={workDescirption}
                        onChange={(event) => setWorkDescirption(event.target.value)}
                    />
                </DialogContent>
                <DialogActions>
                    <Flex flexDirection="row" justifyContent="center" alignItems="center">
                        <Button onClick={handleCloseDescriptionDialog}
                                variant="contained"

                        >
                            Close
                        </Button>
                        <Box m={1}>
                            <Button
                                onClick={addWithDescription}
                                disabled={workDescirption === "" || workDescirption.length >= 255}
                                variant="contained" color="success">Add</Button>
                        </Box>
                    </Flex>


                </DialogActions>
            </Dialog>


            <Dialog open={viewAllEngineersOnProjectDialogShow} onClose={handleCloseViewAllEngineersDialog}>
                <DialogTitle>View All Engineers</DialogTitle>
                <DialogContent>
                    {allEngineers != null && allEngineers.length > 0 && (
                        <TableContainer component={Paper}
                                        sx={{maxHeight: 500, height: 500, overflowY: 'scroll'}}>
                            <Table>
                                <TableBody>
                                    {allEngineers.map((item) => (
                                        <React.Fragment key={`${item.employeeId}-row`}>
                                            <StyledTableRow>
                                                <StyledTableCell>
                                                    <Box m={1} sx={{
                                                        overflowX: 'auto',
                                                        width: 300,
                                                        height: 200,
                                                        overflowY: 'auto'
                                                    }}>

                                                        <li>Name: {item.swEngineer.name}</li>
                                                        <li>Surname: {item.swEngineer.surname}</li>
                                                        <li>Phone number: {item.swEngineer.phoneNumber}</li>
                                                        <li>Profession: {item.swEngineer.profession}</li>
                                                        <li>Job description: {item.jobDescription}</li>
                                                        <li>Working period:</li>

                                                        <li>Start
                                                            date: {new Date(item.workingPeriod.startDate).toLocaleDateString('en-US', {hour12: false})}</li>
                                                        <li>
                                                            End
                                                            date: {item.workingPeriod.endDate ? new Date(item.workingPeriod.endDate).toLocaleDateString('en-US', {hour12: false}) : ''}
                                                        </li>

                                                    </Box>

                                                </StyledTableCell>
                                                <StyledTableCell>
                                                    <Box m={1}>
                                                        <Button fullWidth variant="contained" color="error"
                                                                disabled={item.workingPeriod.endDate !== null}
                                                                onClick={() => {
                                                                    removeEngineerFromProject(item)
                                                                }}
                                                        >
                                                            Remove from project
                                                        </Button>
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
                    <Button onClick={handleCloseViewAllEngineersDialog}>Close</Button>
                </DialogActions>
            </Dialog>


            <Dialog open={viewAvailableEngineersOnProjectDialogShow} onClose={handleCloseViewAvailableEngineersDialog}>
                <DialogTitle>View Available Engineers</DialogTitle>
                <DialogContent>
                    {availableEngineers != null && availableEngineers.length > 0 && (
                        <TableContainer component={Paper}
                                        sx={{maxHeight: 500, height: 500, overflowY: 'scroll'}}>
                            <Table>
                                <TableBody>
                                    {availableEngineers.map((item) => (
                                        <React.Fragment key={`${item.employeeId}-row`}>
                                            <StyledTableRow>
                                                <StyledTableCell>
                                                    <Box m={1} sx={{
                                                        overflowX: 'auto',
                                                        width: 300,
                                                        height: 150,
                                                        overflowY: 'auto'
                                                    }}>
                                                        <li>Email: {item.mail}</li>
                                                        <li>Name: {item.name}</li>
                                                        <li>Surname: {item.surname}</li>
                                                        <li>Phone number: {item.phoneNumber}</li>
                                                        <li>Profession: {item.profession}</li>
                                                    </Box>
                                                </StyledTableCell>
                                                <StyledTableCell>
                                                    <Box m={1}>
                                                        <Button fullWidth variant="contained" color="success"
                                                                onClick={() => {
                                                                    addEngineerToProject(item)
                                                                }}
                                                        >
                                                            Add to project
                                                        </Button>
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
                    <Button onClick={handleCloseViewAvailableEngineersDialog}>Close</Button>
                </DialogActions>
            </Dialog>


            <div className="wrapper">
                {ManageMyProjects != null && ManageMyProjects.length > 0 && (
                    <TableContainer component={Paper}
                                    sx={{maxHeight: 500, height: 500, overflowY: 'scroll'}}>
                        <Table>
                            <TableBody>
                                {ManageMyProjects.map((item) => (
                                    <React.Fragment key={`${item.id}-row`}>
                                        <StyledTableRow>
                                            <StyledTableCell>
                                                <Box m={1} sx={{
                                                    overflowX: 'auto',
                                                    width: 300,
                                                    height: 100,
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
                                            <StyledTableCell>
                                                <Box m={1}>

                                                    <Box m={1}>
                                                        <Button fullWidth variant="outlined" color="info"
                                                                onClick={() => handleViewAllEngineers(item)}>
                                                            View engineers
                                                        </Button>
                                                    </Box>
                                                </Box>
                                            </StyledTableCell>
                                            <StyledTableCell>
                                                <Box m={1}>

                                                    <Box m={1}>
                                                        <Button fullWidth variant="contained" color="success"
                                                                onClick={() => handleAddAvailableEngineers(item)}>
                                                            Add engineers
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

export default Projects;