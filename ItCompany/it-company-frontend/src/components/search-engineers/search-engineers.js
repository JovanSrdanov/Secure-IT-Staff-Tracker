import React, {useState} from 'react';
import {Flex} from "reflexbox";
import {
    Box,
    Button,
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
import {DatePicker, LocalizationProvider} from "@mui/x-date-pickers";
import {AdapterDayjs} from "@mui/x-date-pickers/AdapterDayjs";
import dayjs from "dayjs";
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

function SearchEngineers(props) {
    const [searchResults, setSearchResults] = useState(null);

    const [searchParams, setSearchParams] = useState({
        email: "",
        name: "",
        surname: "",
        employmentDateRange: {
            startDate: dayjs(),
            endDate: dayjs(),
        },
    });

    const handleInputChange = (e) => {
        const {name, value} = e.target;
        setSearchParams((prevSearchParams) => ({
            ...prevSearchParams,
            [name]: value,
        }));
    };

    const handleStartDateChange = (date) => {

        setSearchParams((prevSearchParams) => ({
            ...prevSearchParams,
            employmentDateRange: {
                ...prevSearchParams.employmentDateRange,
                startDate: date,
            },
        }));
    };

    const handleEndDateChange = (date) => {

        setSearchParams((prevSearchParams) => ({
            ...prevSearchParams,
            employmentDateRange: {
                ...prevSearchParams.employmentDateRange,
                endDate: date,
            },
        }));
    };


    const handleSearchClick = () => {
        let sendData = searchParams
        sendData.employmentDateRange.startDate = new Date(sendData.employmentDateRange.startDate);
        sendData.employmentDateRange.endDate = new Date(sendData.employmentDateRange.endDate);
        interceptor.post("sw-engineer/search", sendData).then((res) => {

            setSearchResults(res.data)
        }).catch((err) => {
            setSearchResults(null)
        })
    };


    return (
        <>
            <div className="wrapper">
                <Flex flexDirection="column" alignItems="center" justifyContent="center">
                    <Flex>
                        <Box width={1 / 3} m={1}>
                            <TextField
                                fullWidth
                                variant="filled"
                                label="Name"
                                name="name"
                                value={searchParams.name}
                                onChange={handleInputChange}
                            />
                        </Box>
                        <Box width={1 / 3} m={1}>
                            <TextField
                                fullWidth
                                variant="filled"
                                label="Surname"
                                name="surname"
                                value={searchParams.surname}
                                onChange={handleInputChange}
                            />
                        </Box>
                        <Box width={1 / 3} m={1}>
                            <TextField
                                fullWidth
                                variant="filled"
                                label="Email"
                                name="email"
                                value={searchParams.email}
                                onChange={handleInputChange}
                            />
                        </Box>

                    </Flex>
                    <Flex alignItems="center" justifyContent="center">
                        <Box width={1 / 3} m={1}>
                            <LocalizationProvider dateAdapter={AdapterDayjs}>
                                <DatePicker
                                    label="Start date"
                                    value={searchParams.employmentDateRange.startDate}
                                    maxDate={dayjs()}
                                    onChange={handleStartDateChange}
                                />
                            </LocalizationProvider>
                        </Box>

                        <Box width={1 / 3} m={1}>
                            <LocalizationProvider dateAdapter={AdapterDayjs}>
                                <DatePicker
                                    label="End date"
                                    value={searchParams.employmentDateRange.endDate}
                                    maxDate={dayjs()}
                                    onChange={handleEndDateChange}

                                />
                            </LocalizationProvider>
                        </Box>
                    </Flex>
                    <Flex>
                        <Box width={1 / 2} m={1}>
                            <Button
                                onClick={handleSearchClick}
                                disabled={(!searchParams.employmentDateRange.endDate || !searchParams.employmentDateRange.startDate || dayjs(searchParams.employmentDateRange.endDate).isBefore(searchParams.employmentDateRange.startDate))}
                                variant="contained">
                                Search
                            </Button>
                        </Box>
                    </Flex>
                </Flex>
            </div>
            {searchResults != null && searchResults.length > 0 && (
                <div className="wrapper" style={{marginTop: 10}}>
                    <TableContainer component={Paper}
                                    sx={{maxHeight: 500, height: 400, overflowY: 'scroll'}}>
                        <Table>
                            <TableBody>
                                {searchResults.map((item) => (
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
                                                    <li>Account
                                                        status: {item.isBlocked ? 'BLOCKED' : 'NOT BLOCKED'}</li>
                                                    <li>Date of
                                                        employment: {dayjs(item.dateOfEmployment).format('DD.MM.YYYY')}</li>
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

                                        </StyledTableRow>
                                    </React.Fragment>
                                ))}
                            </TableBody>
                        </Table>
                    </TableContainer>
                </div>
            )}

        </>
    );
}

export default SearchEngineers;
