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

    const createNewProject = () => {
        interceptor.post("project", newProject).then(res => {
            getAllProjects();
        }).catch(err => {
            console.log(err)
        })
    }

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

    return (
        <>
            <Dialog onClose={handleCloseCreateNewProjectDialog} open={showCreateNewProjectDialog}>
                <DialogTitle>Reason for rejection:</DialogTitle>
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
                                disabled={newProject.name === ""}
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
                                                    <li>Star
                                                        date: {new Date(item.duration.startDate).toLocaleDateString('en-US', {hour12: false})}
                                                    </li>
                                                    <li>End
                                                        date: {new Date(item.duration.endDate).toLocaleDateString('en-US', {hour12: false})}</li>

                                                </Box>
                                            </StyledTableCell>
                                            <StyledTableCell>
                                                <Box m={1}>
                                                    <Box m={1}>
                                                        <Button fullWidth variant="outlined"
                                                                color="info"
                                                        >View project managers
                                                        </Button>
                                                    </Box>
                                                    <Box m={1}>
                                                        <Button fullWidth variant="outlined"
                                                                color="info"
                                                        >View engineers
                                                        </Button>
                                                    </Box>
                                                </Box>
                                            </StyledTableCell>
                                            <StyledTableCell>
                                                <Box m={1}>

                                                    <Box m={1}>
                                                        <Button fullWidth variant="contained"
                                                                color="success"
                                                        >Add project managers
                                                        </Button>
                                                    </Box>
                                                    <Box m={1}>
                                                        <Button fullWidth variant="contained"
                                                                color="success"
                                                        >Add engineers
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