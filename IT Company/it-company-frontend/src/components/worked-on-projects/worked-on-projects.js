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

function WorkedOnProjects(props) {

    const [ManageMyProjects, setManageMyProjects] = React.useState(null);
    const [selectedProject, setSelectedProject] = React.useState(null);


    const getAllProjects = () => {
        interceptor.get("project/sw-engineer").then(res => {
            setManageMyProjects(res.data)

        }).catch(err => {
            console.log(err)
        })
    }

    useEffect(() => {
        getAllProjects();
    }, []);


    const [workDescirption, setWorkDescirption] = React.useState("")
    const [workDescirptionDialog, setWorkDescirptionDialog] = React.useState(false)


    const handleCloseDescriptionDialog = () => {
        setWorkDescirptionDialog(false);
        setWorkDescirption("");

    };
    const changeDescriptionDialogHandler = (item) => {
        setWorkDescirptionDialog(true)
        setSelectedProject(item);
    };
    const changeDescription = () => {
        interceptor.patch("project/" + selectedProject.project.id + "/sw-engineer", {newJobDescription: workDescirption}).then((res) => {
            getAllProjects();
            handleCloseDescriptionDialog();
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
                                onClick={changeDescription}
                                disabled={workDescirption === "" || workDescirption.length >= 255}
                                variant="contained" color="success">Change</Button>
                        </Box>
                    </Flex>


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
                                            <StyledTableCell>
                                                <Box m={1}>
                                                    <Box m={1}>
                                                        <Button fullWidth variant="outlined" color="info"
                                                                disabled={item.workingPeriod.endDate !== null}
                                                                onClick={() => changeDescriptionDialogHandler(item)}>
                                                            Change description
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

export default WorkedOnProjects;