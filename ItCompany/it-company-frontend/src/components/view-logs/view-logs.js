import React, {useEffect, useState} from 'react';
import {Paper, styled, Table, TableBody, TableCell, tableCellClasses, TableContainer, TableRow} from "@mui/material";
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
                                {logs.map((item, idx) => {
                                    let color = 'red';
                                    if (item.includes('| INFO  |')) {
                                        color = 'lightblue';
                                    } else if (item.includes('| WARN')) {
                                        color = 'orange';
                                    } else if (item.includes('| DEBUG')) {
                                        color = 'purple';
                                    } else if (item.includes('| ERROR')) {
                                        color = 'red';
                                    }

                                    return (
                                        <React.Fragment key={`${idx}-row`}>
                                            <StyledTableRow>
                                                <StyledTableCell>
                                                    <span style={{color}}>{item}</span>
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