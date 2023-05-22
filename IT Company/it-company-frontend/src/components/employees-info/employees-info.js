import React, { useEffect, useState } from "react";
import interceptor from "../../interceptor/interceptor";
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
} from "@mui/material";

const StyledTableCell = styled(TableCell)(({ theme }) => ({
  [`&.${tableCellClasses.head}`]: {
    backgroundColor: theme.palette.common.black,
    color: theme.palette.common.white,
  },
  [`&.${tableCellClasses.body}`]: {
    fontSize: 14,
  },
}));

const StyledTableRow = styled(TableRow)(({ theme }) => ({
  "&:nth-of-type(odd)": {
    backgroundColor: theme.palette.action.focusOpacity,
  },
}));

function EmployeesInfo(props) {
  const [allEmployees, setAllEmployees] = useState(null);

  const getAllEmployees = () => {
    interceptor
      .get("employee")
      .then((res) => {
        console.log(res.data);
        setAllEmployees(res.data);
      })
      .catch((err) => {
        console.log(err);
      });
  };

  useEffect(() => {
    getAllEmployees();
  }, []);

  return (
    <>
      <div className="wrapper">
        {allEmployees != null && allEmployees.length > 0 && (
          <TableContainer
            component={Paper}
            sx={{ maxHeight: 500, height: 500, overflowY: "scroll" }}
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
                          <Box m={1}>
                            <Button fullWidth variant="outlined" color="error">
                              Block
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

export default EmployeesInfo;
