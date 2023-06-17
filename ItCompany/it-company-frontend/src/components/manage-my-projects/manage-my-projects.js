import React, {useEffect, useState} from 'react';
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

    const [showUpdateProjectDialog, setShowUpdateProjectDialog] = React.useState(false)
    const handleUpdateProjectClick = (item) => {
        setSelectedProject(item);
        setShowUpdateProjectDialog(true);

    };

    const [newProject, setNewProject] = React.useState({
        name: "",
        duration: {
            startDate: dayjs(),
            endDate: dayjs().add(1, 'day')
        }
    });

    const handleCloseUpdatingProjectDialog = () => {
        setShowUpdateProjectDialog(false);
        setNewProject({
            name: "",
            duration: {
                startDate: dayjs(),
                endDate: dayjs().add(1, 'day')
            }
        });
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


    const handleUpdatingProject = () => {


        var sendData = {
            endDate: new Date(newProject.duration.endDate),
            name: newProject.name
        }

        interceptor.put("project/" + selectedProject.project.id + "/update-project", sendData).then((res) => {
            getAllProjects();
            handleCloseUpdatingProjectDialog();
        }).catch((err) => {
            console.log(err)
        })

    };

    const viewCV = (item) => {
        interceptor.get('sw-engineer/cv/' + item.swEngineer.id, {responseType: 'blob'}).then((res) => {
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

    const [errorDialogShow, setErrorDialogShow] = useState(false)
    const [errorMessage, setErrorMessage] = useState("")
    const handleErrorClose = () => {
        setErrorDialogShow(false)
    };


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


            <Dialog onClose={handleCloseUpdatingProjectDialog} open={showUpdateProjectDialog}>
                <DialogTitle>Project</DialogTitle>
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
                        <Button onClick={handleCloseUpdatingProjectDialog}
                                variant="contained">
                            Close
                        </Button>
                        <Box m={1}>
                            <Button
                                disabled={newProject.name === "" || newProject.name.length >= 255}
                                variant="contained" color="success"
                                onClick={handleUpdatingProject}
                            >Update project</Button>
                        </Box>
                    </Flex>
                </DialogActions>
            </Dialog>


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
                                        <React.Fragment key={`${item.swEngineer.id}-row`}>
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
                                                    <Box m={1}>
                                                        <Button fullWidth variant="outlined" color="warning"

                                                                onClick={() => {
                                                                    viewCV(item)
                                                                }}
                                                        >
                                                            View CV
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
                                                    height: 150,
                                                    overflowY: 'auto'
                                                }}>
                                                    <li>Name: {item.project.name}</li>
                                                    <li>Id: {item.project.id}</li>
                                                    <li>Start
                                                        date: {new Date(item.project.duration.startDate).toLocaleDateString('en-US', {hour12: false})}</li>
                                                    <li>End
                                                        date: {new Date(item.project.duration.endDate).toLocaleDateString('en-US', {hour12: false})}</li>
                                                    <Box mt={2}>
                                                        <Button variant="contained" color="warning"
                                                                onClick={() => handleUpdateProjectClick(item)}>

                                                            Update project
                                                        </Button>
                                                    </Box>
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