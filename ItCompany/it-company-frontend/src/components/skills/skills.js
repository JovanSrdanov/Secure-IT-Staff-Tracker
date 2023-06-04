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
import {Flex} from "reflexbox";
import interceptor from "../../interceptor/interceptor";
import PictureAsPdfIcon from '@mui/icons-material/PictureAsPdf';

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

function Skills(props) {
    const [mySkills, setMySkills] = React.useState(null);
    const [seniority, setSeniority] = React.useState(null);


    const [CV, setCV] = React.useState(null);

    const [showAddNewSkillDialog, setShowAddNewSkillDialog] = React.useState(false);
    const [skillName, setSkillName] = React.useState("");
    const [skillLevel, setSkillLevel] = React.useState(1);

    const handleCloseAddNewSkillsDialog = () => {
        setShowAddNewSkillDialog(false)
        setSkillName("")
        setSkillLevel(1)
    };

    const getMySkills = () => {
        interceptor.get("sw-engineer/skill").then(res => {
            setMySkills(res.data)

        }).catch(err => {
            console.log(err)
        })

    }


    const getMySeniority = () => {
        interceptor.get("sw-engineer/seniority").then(res => {
            setSeniority(res.data)
        }).catch(err => {
            console.log(err)
        })
    };
    useEffect(() => {
        getMySkills();
        getMySeniority();
    }, []);


    const handleAddingSkill = () => {
        interceptor.post("sw-engineer/skill", {name: skillName, level: parseInt(skillLevel)}).then((res) => {
            getMySkills();
            handleCloseAddNewSkillsDialog();

        }).catch((err) => {
            console.log(err)
        })


    };
    const handleRemoveSkill = (id) => {
        interceptor.delete("sw-engineer/skill/" + id).then((res) => {
            getMySkills();

        }).catch((err) => {
            console.log(err)
        })
    };

    const handleFileChange = (event) => {
        const file = event.target.files[0];
        const allowedTypes = ['application/pdf'];

        if (file && allowedTypes.includes(file.type)) {
            setCV(file);
        }
    };

    const handleCVUpload = () => {
        if (CV) {
            const formData = new FormData();
            formData.append('cv', CV);

            interceptor
                .post('sw-engineer/cv', formData, {
                    headers: {
                        'Content-Type': 'multipart/form-data',
                    },
                })
                .then((res) => {
                    console.log(res.data);
                })
                .catch((err) => {
                    console.log(err);
                });
        }
    };
    return (
        <>
            <Dialog onClose={handleCloseAddNewSkillsDialog} open={showAddNewSkillDialog}>
                <DialogTitle>Reason for rejection:</DialogTitle>
                <DialogContent>
                    <Box m={1}>
                        <TextField
                            label="Skill"
                            variant="filled"
                            value={skillName}
                            onChange={(event) => setSkillName(event.target.value)}
                        />
                    </Box>
                    <Box m={1}>
                        <TextField
                            variant="filled"
                            type="number"
                            label="Skill level"
                            InputProps={{inputProps: {min: 1, max: 5}}}
                            name="skillLevel"
                            value={skillLevel}
                            onChange={(event) => setSkillLevel(event.target.value)}
                        />
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Flex flexDirection="row" justifyContent="center" alignItems="center">
                        <Button onClick={handleCloseAddNewSkillsDialog}
                                variant="contained"

                        >
                            Close
                        </Button>

                        <Box m={1}>
                            <Button
                                disabled={skillName === "" || skillName.length >= 255}
                                variant="contained" color="error"
                                onClick={handleAddingSkill}

                            >Add skill</Button>
                        </Box>
                    </Flex>


                </DialogActions>
            </Dialog>


            <div className="wrapper">
                <Flex flexDirection="column" justifyContent="center" alignItems="center">
                    {seniority != null && (
                        <>
                            <Box m={1}>
                                Seniority: {seniority.seniority}
                            </Box>

                            <Box m={1}>
                                Date Of
                                Employment: {new Date(seniority.dateOfEmployment).toLocaleString('en-US', {hour12: false})}
                            </Box>
                        </>
                    )}

                </Flex>
                {mySkills != null && mySkills.length > 0 && (
                    <TableContainer component={Paper}
                                    sx={{maxHeight: 500, height: 250, overflowY: 'scroll'}}>
                        <Table>
                            <TableBody>
                                {mySkills.map((item) => (
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
                                            <StyledTableCell>
                                                <Button fullWidth variant="outlined"
                                                        color="error"
                                                        onClick={() => {
                                                            handleRemoveSkill(item.id)
                                                        }}

                                                >Remove skill
                                                </Button>
                                            </StyledTableCell>
                                        </StyledTableRow>
                                    </React.Fragment>
                                ))}
                            </TableBody>
                        </Table>
                    </TableContainer>
                )}
                <Button fullWidth variant="contained"
                        color="success"
                        onClick={() => {
                            setShowAddNewSkillDialog(true)
                        }}
                >Add skill
                </Button>

                <Flex justifyContent="center">
                    <Box m={1}>
                        <Button fullWidth variant="outlined"
                                color="warning"
                        >Download current CV
                        </Button>
                    </Box>
                    <Box m={1}>

                        <Button variant="outlined" color="info" component="label"
                                endIcon={<PictureAsPdfIcon/>}>
                            Import new CV
                            <input
                                type="file"
                                accept=".pdf"
                                onChange={handleFileChange}
                                style={{display: 'none'}}
                            />
                        </Button>
                    </Box>
                    <Box m={1}>
                        <Button fullWidth variant="outlined"
                                color="success"
                                disabled={CV === null}
                                onClick={handleCVUpload}
                        >Confirm upload
                        </Button>
                    </Box>
                </Flex>

            </div>

        </>
    );
}

export default Skills;