import React, {useEffect, useState} from 'react';
import {
    Box,
    Paper,
    styled,
    Table,
    TableBody,
    TableCell,
    tableCellClasses,
    TableContainer,
    TableRow
} from "@mui/material";
import interceptor from "../../interceptor/interceptor";

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

function ViewLogs(props) {
    const [logs, setLogs] = useState(null);

    const getLogs = () => {
        interceptor.get("logs/get-all-logs").then((res) => {
            setLogs(res.data)
        }).catch((err) => {
            console.log(err)
        })

    }

    useEffect(() => {
        // Call getLogs on component mount
        getLogs();

        // Call getLogs every two seconds
        const interval = setInterval(getLogs, 2000);

        // Clean up the interval on component unmount
        return () => clearInterval(interval);
    }, []);

    return (
        <>
            {logs != null && logs.length > 0 && (
                <div className="wrapper">
                    <TableContainer component={Paper} sx={{maxHeight: 700, height: 700, overflowY: 'scroll'}}>
                        <Table>
                            <TableBody>
                                {logs.map((item) => {
                                    const parts = item.split('|');
                                    const text = parts[2].trim(); // Get the text between the second and third '|'

                                    let color = 'white';
                                    if (text.includes('INFO')) {
                                        color = 'lightblue';
                                    } else if (text.includes('WARN')) {
                                        color = 'orange';
                                    } else if (text.includes('DEBUG')) {
                                        color = 'purple';
                                    } else if (text.includes('ERROR')) {
                                        color = 'red';
                                    }

                                    return (
                                        <React.Fragment key={`${item}-row`}>
                                            <StyledTableRow>
                                                <StyledTableCell>
                                                    <Box m={1} sx={{overflowX: 'auto', overflowY: 'auto'}}>
                                                        <li>
                                                            <span style={{color}}>{item}</span>
                                                        </li>
                                                    </Box>
                                                </StyledTableCell>
                                            </StyledTableRow>
                                        </React.Fragment>
                                    );
                                })}
                            </TableBody>
                        </Table>
                    </TableContainer>
                </div>
            )}
        </>
    );
}

export default ViewLogs;