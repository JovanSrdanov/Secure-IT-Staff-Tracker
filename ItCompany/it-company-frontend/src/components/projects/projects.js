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
import {DatePicker, LocalizationProvider} from "@mui/x-date-pickers";
import {AdapterDayjs} from "@mui/x-date-pickers/AdapterDayjs";
import dayjs from "dayjs";

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

    const [allProjects, setAllProjects] = React.useState(null);
    const [selectedProject, setSelectedProject] = React.useState(null);


    const getAllProjects = () => {
        interceptor.get("project").then(res => {
            setAllProjects(res.data)

        }).catch(err => {
            console.log(err)
        })
    }

    useEffect(() => {
        getAllProjects();
    }, []);
    

    const [showCreateNewProjectDialog, setShowCreateNewProjectDialog] = React.useState(false);
    const [newProject, setNewProject] = React.useState({
        name: "",
        duration: {
            startDate: dayjs(),
            endDate: dayjs().add(1, 'day')
        }
    });

    const handleCloseCreateNewProjectDialog = () => {
        setShowCreateNewProjectDialog(false);
        setNewProject({
            name: "",
            duration: {
                startDate: dayjs(),
                endDate: dayjs().add(1, 'day')
            }
        });
    };
    const handleCreateNewProject = () => {

        let sendData = newProject;
        sendData.duration.startDate = new Date(sendData.duration.startDate);
        sendData.duration.endDate = new Date(sendData.duration.endDate);

        interceptor.post("project", sendData).then((res) => {

            getAllProjects();
            handleCloseCreateNewProjectDialog();
        }).catch((err) => {
            console.log(err)
        })

    };

    const handleNameChange = (event) => {
        setNewProject((prevProject) => ({
            ...prevProject,
            name: event.target.value,
        }));
    };

    const handleEndDateChange = (date) => {
        setNewProject((prevProject) => ({
            ...prevProject,
            duration: {
                ...prevProject.duration,
                endDate: date,
            },
        }));
    };

    const [viewAllProjectManagersOnProjectDialogShow, setViewAllProjectManagersOnProjectDialogShow] = React.useState(false);
    const [viewAllEngineersOnProjectDialogShow, setViewAllEngineersOnProjectDialogShow] = React.useState(false);

    const [viewAvailableEngineersOnProjectDialogShow, setViewAvailableEngineersOnProjectDialogShow] = React.useState(false);
    const [viewAvailableProjectManagersOnProjectDialogShow, setViewAvailableProjectManagersOnProjectDialogShow] = React.useState(false);

    const [workDescirption, setWorkDescirption] = React.useState("")
    const [workDescirptionDialog, setWorkDescirptionDialog] = React.useState(false)

    const [selectedEngineer, setSelectedEngineer] = React.useState(null)

    const handleCloseViewAllProjectManagersDialog = () => {
        setViewAllProjectManagersOnProjectDialogShow(false);
        setAllProjectManagers(null)
        setSelectedProject(null)
    };

    const handleCloseViewAllEngineersDialog = () => {
        setViewAllEngineersOnProjectDialogShow(false);
        setAllEngineers(null)
        setSelectedProject(null)
    };
    const handleCloseViewAvailableProjectManagersDialog = () => {
        setViewAvailableProjectManagersOnProjectDialogShow(false);
    };

    const handleCloseViewAvailableEngineersDialog = () => {
        setViewAvailableEngineersOnProjectDialogShow(false);
    };


    const handleViewAllProjectManagers = (item) => {
        setViewAllProjectManagersOnProjectDialogShow(true);
        setSelectedProject(item)
        interceptor.get("project/" + item.id + "/pr-managers").then((res) => {
            setAllProjectManagers(res.data)
        }).catch((err) => {
            console.log(err)
        })
    };

    const handleViewAllEngineers = (item) => {
        setViewAllEngineersOnProjectDialogShow(true);
        setSelectedProject(item)
        interceptor.get("project/" + item.id + "/sw-engineers").then((res) => {
            setAllEngineers(res.data)
        }).catch((err) => {
            console.log(err)
        })
    };

    const handleAddAvailableProjectManagers = (item) => {
        setViewAvailableProjectManagersOnProjectDialogShow(true);
        setSelectedProject(item)
        interceptor.get("employee/unemployed-pr-manager/" + item.id).then((res) => {
            setAvailablePRManager(res.data)
        }).catch((err) => {
            console.log(err)
        })
    };

    const handleAddAvailableEngineers = (item) => {
        setViewAvailableEngineersOnProjectDialogShow(true);
        setSelectedProject(item)
        interceptor.get("employee/unemployed-engineer/" + item.id).then((res) => {
            setAvailableEngineers(res.data)
        }).catch((err) => {
            console.log(err)
        })
    };


    const [allProjectManagers, setAllProjectManagers] = React.useState(null);
    const [allEngineers, setAllEngineers] = React.useState(null);
    const [availableEngineers, setAvailableEngineers] = React.useState(null);
    const [availablePRManager, setAvailablePRManager] = React.useState(null);


    const addEngineerToProject = (item) => {
        setSelectedEngineer(item);
        setWorkDescirptionDialog(true);

    };


    const addPrManagerToProject = (item) => {
        interceptor.patch("project/" + selectedProject.id + "/add-pr-manager", {prManagerId: item.employeeId})
            .then((res) => {
                interceptor.get("employee/unemployed-pr-manager/" + selectedProject.id).then((res) => {
                    setAvailablePRManager(res.data)
                }).catch((err) => {
                    console.log(err)
                })
            }).catch((err) => {
            console.log(err)
        })
    };


    const removePrManagerFromProject = (item) => {
        interceptor.patch("project/" + selectedProject.id + "/dismiss-pr-manager", {workerId: item.prManager.id})
            .then((res) => {
                interceptor.get("project/" + selectedProject.id + "/pr-managers").then((res) => {
                    setAllProjectManagers(res.data)
                }).catch((err) => {
                    console.log(err)
                })
            }).catch((err) => {
            console.log(err)
        })

    };
    const addWithDescription = () => {

        interceptor.patch("project/" + selectedProject.id + "/add-sw-engineer", {
            swEngineerId: selectedEngineer.employeeId,
            jobDescription: workDescirption
        }).then((res) => {
            interceptor.get("employee/unemployed-engineer/" + selectedProject.id).then((res) => {
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


        interceptor.patch("project/" + selectedProject.id + "/dismiss-sw-engineer", {workerId: item.swEngineer.id})
            .then((res) => {
                interceptor.get("project/" + selectedProject.id + "/sw-engineers").then((res) => {
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


            <Dialog open={viewAllProjectManagersOnProjectDialogShow} onClose={handleCloseViewAllProjectManagersDialog}>
                <DialogTitle>View All Project Managers</DialogTitle>
                <DialogContent>
                    {allProjectManagers != null && allProjectManagers.length > 0 && (
                        <TableContainer component={Paper}
                                        sx={{maxHeight: 500, height: 500, overflowY: 'scroll'}}>
                            <Table>
                                <TableBody>
                                    {allProjectManagers.map((item) => (
                                        <React.Fragment key={`${item.employeeId}-row`}>
                                            <StyledTableRow>
                                                <StyledTableCell>
                                                    <Box m={1} sx={{
                                                        overflowX: 'auto',
                                                        width: 300,
                                                        height: 150,
                                                        overflowY: 'auto'
                                                    }}>
                                                        <li>Name: {item.prManager.name}</li>
                                                        <li>Surname: {item.prManager.surname}</li>
                                                        <li>Phone number: {item.prManager.phoneNumber}</li>
                                                        <li>Profession: {item.prManager.profession}</li>
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
                                                                    removePrManagerFromProject(item)
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
                    <Button onClick={handleCloseViewAllProjectManagersDialog}>Close</Button>
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

            <Dialog open={viewAvailableProjectManagersOnProjectDialogShow}
                    onClose={handleCloseViewAvailableProjectManagersDialog}>
                <DialogTitle>View Available Project Managers</DialogTitle>
                <DialogContent>
                    {availablePRManager != null && availablePRManager.length > 0 && (
                        <TableContainer component={Paper}
                                        sx={{maxHeight: 500, height: 500, overflowY: 'scroll'}}>
                            <Table>
                                <TableBody>
                                    {availablePRManager.map((item) => (
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
                                                                    addPrManagerToProject(item)
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
                    <Button onClick={handleCloseViewAvailableProjectManagersDialog}>Close</Button>
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


            <Dialog onClose={handleCloseCreateNewProjectDialog} open={showCreateNewProjectDialog}>
                <DialogTitle>New project:</DialogTitle>
                <DialogContent>
                    <Box m={2}>
                        <TextField
                            fullWidth
                            label="Project name"
                            variant="filled"
                            value={newProject.name}
                            onChange={handleNameChange}
                        />
                    </Box>
                    <Box m={2}>
                        Project start date: {(new Date()).toLocaleDateString()}
                    </Box>
                    <Box m={2}>
                        <LocalizationProvider dateAdapter={AdapterDayjs}>
                            <DatePicker
                                label="End date"
                                minDate={dayjs().add(1, 'day')}

                                value={newProject.duration.endDate}
                                onChange={handleEndDateChange}
                            />
                        </LocalizationProvider>
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Flex flexDirection="row" justifyContent="center" alignItems="center">
                        <Button onClick={handleCloseCreateNewProjectDialog}
                                variant="contained">
                            Close
                        </Button>
                        <Box m={1}>
                            <Button
                                disabled={newProject.name === "" || newProject.name.length >= 255}
                                variant="contained" color="success"
                                onClick={handleCreateNewProject}
                            >Create project</Button>
                        </Box>
                    </Flex>
                </DialogActions>
            </Dialog>


            <div className="wrapper">
                <Flex flexDirection="column" justifyContent="center" alignItems="center">
                    <Button variant="contained"
                            color="success"
                            onClick={() => {
                                setShowCreateNewProjectDialog(true)
                            }}
                    >Create new project
                    </Button>
                </Flex>

                {allProjects != null && allProjects.length > 0 && (
                    <TableContainer component={Paper}
                                    sx={{maxHeight: 500, height: 500, overflowY: 'scroll'}}>
                        <Table>
                            <TableBody>
                                {allProjects.map((item) => (
                                    <React.Fragment key={`${item.id}-row`}>
                                        <StyledTableRow>
                                            <StyledTableCell>
                                                <Box m={1} sx={{
                                                    overflowX: 'auto',
                                                    width: 300,
                                                    height: 100,
                                                    overflowY: 'auto'
                                                }}>
                                                    <li>Name: {item.name}</li>
                                                    <li>Id: {item.id}</li>
                                                    <li>Start
                                                        date: {new Date(item.duration.startDate).toLocaleDateString('en-US', {hour12: false})}</li>
                                                    <li>End
                                                        date: {new Date(item.duration.endDate).toLocaleDateString('en-US', {hour12: false})}</li>
                                                </Box>
                                            </StyledTableCell>
                                            <StyledTableCell>
                                                <Box m={1}>
                                                    <Box m={1}>
                                                        <Button fullWidth variant="outlined" color="info"
                                                                onClick={() => handleViewAllProjectManagers(item)}>
                                                            View project managers
                                                        </Button>
                                                    </Box>
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
                                                                onClick={() => handleAddAvailableProjectManagers(item)}>
                                                            Add project managers
                                                        </Button>
                                                    </Box>
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