import * as React from 'react';
import {useEffect} from 'react';
import Tabs from '@mui/material/Tabs';
import Tab from '@mui/material/Tab';
import {AppBar, Box, Button, Card, Grid} from '@mui/material';
import Checkbox from '@mui/material/Checkbox';
import CardHeader from '@mui/material/CardHeader';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import PropTypes from 'prop-types';
import interceptor from "../../interceptor/interceptor";
import SwipeableViews from 'react-swipeable-views';
import {useTheme} from '@mui/material/styles';


function TabPanel(props) {
    const {children, value, index, ...other} = props;

    return (
        <div
            role="tabpanel"
            hidden={value !== index}
            id={`full-width-tabpanel-${index}`}
            aria-labelledby={`full-width-tab-${index}`}
            {...other}
        >
            {value === index && (
                <Box sx={{p: 3}}>
                    {children}
                </Box>
            )}
        </div>
    );
}

TabPanel.propTypes = {
    children: PropTypes.node,
    index: PropTypes.number.isRequired,
    value: PropTypes.number.isRequired,
};

function a11yProps(index) {
    return {
        id: `full-width-tab-${index}`,
        'aria-controls': `full-width-tabpanel-${index}`,
    };
}

function Permissions(props) {
    const [tabValue, setTabValue] = React.useState(0);
    const handleChangeTab = (event, newValue) => {
        setTabValue(newValue);
    };

    function not(a, b) {
        return a.filter((value) => b.indexOf(value) === -1);
    }

    function intersection(a, b) {
        return a.filter((value) => b.indexOf(value) !== -1);
    }

    function union(a, b) {
        return [...a, ...not(b, a)];
    }

    const handleToggleEngineer = (value) => () => {
        const currentIndex = checkedEngineer.indexOf(value);
        const newChecked = [...checkedEngineer];

        if (currentIndex === -1) {
            newChecked.push(value);
        } else {
            newChecked.splice(currentIndex, 1);
        }

        setCheckedEngineer(newChecked);
    };
    const numberOfCheckedEngineer = (items) => intersection(checkedEngineer, items).length;
    const handleToggleAllEngineer = (items) => () => {
        if (numberOfCheckedEngineer(items) === items.length) {
            setCheckedEngineer(not(checkedEngineer, items));
        } else {
            setCheckedEngineer(union(checkedEngineer, items));
        }
    };
    const handleCheckedRightEngineer = () => {
        setRightEngineer(rightEngineer.concat(leftCheckedEngineer));
        setLeftEngineer(not(leftEngineer, leftCheckedEngineer));
        setCheckedEngineer(not(checkedEngineer, leftCheckedEngineer));

    };
    const handleCheckedLeftEngineer = () => {
        setLeftEngineer(leftEngineer.concat(rightCheckedEngineer));
        setRightEngineer(not(rightEngineer, rightCheckedEngineer));
        setCheckedEngineer(not(checkedEngineer, rightCheckedEngineer));

    };
    const customListEngineer = (title, items) => (
        <Card>
            <CardHeader
                avatar={
                    <Checkbox
                        onClick={handleToggleAllEngineer(items)}
                        checked={numberOfCheckedEngineer(items) === items.length && items.length !== 0}
                        indeterminate={numberOfCheckedEngineer(items) !== items.length && numberOfCheckedEngineer(items) !== 0}
                        disabled={items.length === 0}
                        inputProps={{
                            'aria-label': 'all items selected',
                        }}
                    />
                }
                title={title}
                subheader={`${numberOfCheckedEngineer(items)}/${items.length} selected`}
            />
            <List
                sx={{
                    width: 350,
                    height: 300,
                    overflow: 'auto',
                }}
                dense
                component="div"
                role="list">
                {items.map((value) => {
                    const labelId = `transfer-list-all-item-${value}-label`;
                    return (
                        <ListItem key={value} role="listitem" button onClick={handleToggleEngineer(value)}>
                            <ListItemIcon>
                                <Checkbox
                                    checked={checkedEngineer.indexOf(value) !== -1}
                                    tabIndex={-1}
                                    disableRipple
                                    inputProps={{
                                        'aria-labelledby': labelId,
                                    }}
                                />
                            </ListItemIcon>
                            <ListItemText id={labelId} primary={` ${value}`}/>
                        </ListItem>
                    );
                })}
            </List>
        </Card>
    );

    const [leftEngineer, setLeftEngineer] = React.useState([]);
    const [rightEngineer, setRightEngineer] = React.useState([]);
    const [checkedEngineer, setCheckedEngineer] = React.useState([]);
    const leftCheckedEngineer = intersection(checkedEngineer, leftEngineer);
    const rightCheckedEngineer = intersection(checkedEngineer, rightEngineer);

    const handleToggleProjectManager = (value) => () => {
        const currentIndex = checkedProjectManager.indexOf(value);
        const newChecked = [...checkedProjectManager];

        if (currentIndex === -1) {
            newChecked.push(value);
        } else {
            newChecked.splice(currentIndex, 1);
        }

        setCheckedProjectManager(newChecked);
    };
    const numberOfCheckedProjectManager = (items) => intersection(checkedProjectManager, items).length;
    const handleToggleAllProjectManager = (items) => () => {
        if (numberOfCheckedProjectManager(items) === items.length) {
            setCheckedProjectManager(not(checkedProjectManager, items));
        } else {
            setCheckedProjectManager(union(checkedProjectManager, items));
        }
    };
    const handleCheckedRightProjectManager = () => {
        setRightProjectManager(rightProjectManager.concat(leftCheckedProjectManager));
        setLeftProjectManager(not(leftProjectManager, leftCheckedProjectManager));
        setCheckedProjectManager(not(checkedProjectManager, leftCheckedProjectManager));
        updateProjectManager();
    };
    const handleCheckedLeftProjectManager = () => {
        setLeftProjectManager(leftProjectManager.concat(rightCheckedProjectManager));
        setRightProjectManager(not(rightProjectManager, rightCheckedProjectManager));
        setCheckedProjectManager(not(checkedProjectManager, rightCheckedProjectManager));
        updateProjectManager();
    };
    const customListProjectManager = (title, items) => (
        <Card>
            <CardHeader
                avatar={
                    <Checkbox
                        onClick={handleToggleAllProjectManager(items)}
                        checked={numberOfCheckedProjectManager(items) === items.length && items.length !== 0}
                        indeterminate={numberOfCheckedProjectManager(items) !== items.length && numberOfCheckedProjectManager(items) !== 0}
                        disabled={items.length === 0}
                        inputProps={{
                            'aria-label': 'all items selected',
                        }}
                    />
                }
                title={title}
                subheader={`${numberOfCheckedProjectManager(items)}/${items.length} selected`}
            />

            <List
                sx={{
                    width: 350,
                    height: 300,
                    overflow: 'auto',
                }}
                dense
                component="div"
                role="list">
                {items.map((value) => {
                    const labelId = `transfer-list-all-item-${value}-label`;
                    return (
                        <ListItem key={value} role="listitem" button onClick={handleToggleProjectManager(value)}>
                            <ListItemIcon>
                                <Checkbox
                                    checked={checkedProjectManager.indexOf(value) !== -1}
                                    tabIndex={-1}
                                    disableRipple
                                    inputProps={{
                                        'aria-labelledby': labelId,
                                    }}
                                />
                            </ListItemIcon>
                            <ListItemText id={labelId} primary={` ${value}`}/>
                        </ListItem>
                    );
                })}
            </List>
        </Card>
    );

    const [leftProjectManager, setLeftProjectManager] = React.useState([]);
    const [rightProjectManager, setRightProjectManager] = React.useState([]);
    const [checkedProjectManager, setCheckedProjectManager] = React.useState([]);
    const leftCheckedProjectManager = intersection(checkedProjectManager, leftProjectManager);
    const rightCheckedProjectManager = intersection(checkedProjectManager, rightProjectManager);
    const handleToggleHrManager = (value) => () => {
        const currentIndex = checkedHrManager.indexOf(value);
        const newChecked = [...checkedHrManager];

        if (currentIndex === -1) {
            newChecked.push(value);
        } else {
            newChecked.splice(currentIndex, 1);
        }

        setCheckedHrManager(newChecked);
    };
    const numberOfCheckedHrManager = (items) => intersection(checkedHrManager, items).length;
    const handleToggleAllHrManager = (items) => () => {
        if (numberOfCheckedHrManager(items) === items.length) {
            setCheckedHrManager(not(checkedHrManager, items));
        } else {
            setCheckedHrManager(union(checkedHrManager, items));
        }
    };
    const handleCheckedRightHrManager = () => {
        setRightHrManager(rightHrManager.concat(leftCheckedHrManager));
        setLeftHrManager(not(leftHrManager, leftCheckedHrManager));
        setCheckedHrManager(not(checkedHrManager, leftCheckedHrManager));
        updateHrManager();
    };
    const handleCheckedLeftHrManager = () => {
        setLeftHrManager(leftHrManager.concat(rightCheckedHrManager));
        setRightHrManager(not(rightHrManager, rightCheckedHrManager));
        setCheckedHrManager(not(checkedHrManager, rightCheckedHrManager));
        updateHrManager();
    };


    const customListHrManager = (title, items) => (
        <Card>
            <CardHeader
                avatar={
                    <Checkbox
                        onClick={handleToggleAllHrManager(items)}
                        checked={numberOfCheckedHrManager(items) === items.length && items.length !== 0}
                        indeterminate={numberOfCheckedHrManager(items) !== items.length && numberOfCheckedHrManager(items) !== 0}
                        disabled={items.length === 0}
                        inputProps={{
                            'aria-label': 'all items selected',
                        }}
                    />
                }
                title={title}
                subheader={`${numberOfCheckedHrManager(items)}/${items.length} selected`}
            />

            <List
                sx={{
                    width: 350,
                    height: 300,
                    overflow: 'auto',
                }}
                dense
                component="div"
                role="list">
                {items.map((value) => {
                    const labelId = `transfer-list-all-item-${value}-label`;
                    return (
                        <ListItem key={value} role="listitem" button onClick={handleToggleHrManager(value)}>
                            <ListItemIcon>
                                <Checkbox
                                    checked={checkedHrManager.indexOf(value) !== -1}
                                    tabIndex={-1}
                                    disableRipple
                                    inputProps={{
                                        'aria-labelledby': labelId,
                                    }}
                                />
                            </ListItemIcon>
                            <ListItemText id={labelId} primary={` ${value}`}/>
                        </ListItem>
                    );
                })}
            </List>
        </Card>
    );

    const [leftHrManager, setLeftHrManager] = React.useState([]);
    const [rightHrManager, setRightHrManager] = React.useState([]);
    const [checkedHrManager, setCheckedHrManager] = React.useState([]);
    const leftCheckedHrManager = intersection(checkedHrManager, leftHrManager);
    const rightCheckedHrManager = intersection(checkedHrManager, rightHrManager);


    const getAndSetAllData = async () => {
        try {
            
            const res1 = await interceptor.get("/privilege/all");
            const names = res1.data.map((item) => item.name);

            const res2 = await interceptor.get("/privilege/all/ROLE_ENGINEER");
            const names2 = res2.data.map((item) => item.name);
            setLeftEngineer(names2);
            setRightEngineer(names.filter((permission) => !names2.includes(permission)));

            const res3 = await interceptor.get("/privilege/all/ROLE_HR_MANAGER");
            const names3 = res3.data.map((item) => item.name);
            setLeftHrManager(names3);
            setRightHrManager(names.filter((permission) => !names3.includes(permission)));

            const res4 = await interceptor.get("/privilege/all/ROLE_PROJECT_MANAGER");
            const names4 = res4.data.map((item) => item.name);
            setLeftProjectManager(names4);
            setRightProjectManager(names.filter((permission) => !names4.includes(permission)));
        } catch (e) {
            console.log(e);
        }
    };

    useEffect(() => {
        (async () => {
            await getAndSetAllData();
        })();
    }, []);

    const theme = useTheme();
    const [value, setValue] = React.useState(0);

    const handleChange = (event, newValue) => {
        setValue(newValue);
    };

    const handleChangeIndex = (index) => {
        setValue(index);
    };

    const updateHrManager = () => {
        var sendData = {
            roleName: "ROLE_HR_MANAGER",
            privileges: leftHrManager
        }
        interceptor.post("privilege/update", sendData).then(res => {
        }).catch(err => {
            console.log(err)
        })
    }

    const updateProjectManager = () => {
        var sendData = {
            roleName: "ROLE_PROJECT_MANAGER",
            privileges: leftProjectManager
        }
        interceptor.post("privilege/update", sendData).then(res => {
        }).catch(err => {
            console.log(err)
        })
    }

    const updateEngineer = () => {
        var sendData = {
            roleName: "ROLE_ENGINEER",
            privileges: leftEngineer
        }
        interceptor.post("privilege/update", sendData).then(res => {
        }).catch(err => {
            console.log(err)
        })
    }


    return (
        <>
            <div className="wrapper">
                <Box>
                    <AppBar position="static">
                        <Tabs
                            value={value}
                            onChange={handleChange}

                            textColor="inherit"
                            variant="fullWidth"
                            aria-label="full width tabs example"
                        >
                            <Tab label="Engineer" {...a11yProps(0)} />
                            <Tab label="HR Manager" {...a11yProps(1)} />
                            <Tab label="Project Manager" {...a11yProps(2)} />
                        </Tabs>
                    </AppBar>
                    <SwipeableViews
                        axis={theme.direction === 'rtl' ? 'x-reverse' : 'x'}
                        index={value}
                        onChangeIndex={handleChangeIndex}
                    >
                        <TabPanel value={value} index={0} dir={theme.direction}>

                            <Grid container spacing={1}>
                                <Grid item>{customListEngineer('Has permissions ', leftEngineer)}</Grid>
                                <Grid item>
                                    <Grid container direction="column" alignItems="center">
                                        <Button sx={{my: 0.5}} variant="contained" size="small"
                                                onClick={handleCheckedRightEngineer}
                                                disabled={leftCheckedEngineer.length === 0}>
                                            &gt;
                                        </Button>
                                        <Button sx={{my: 0.5}} variant="contained" size="small"
                                                onClick={handleCheckedLeftEngineer}
                                                disabled={rightCheckedEngineer.length === 0}>
                                            &lt;
                                        </Button>

                                    </Grid>
                                </Grid>
                                <Grid item>{customListEngineer('Available permissions', rightEngineer)}</Grid>
                            </Grid>
                            <Button onClick={updateEngineer}>Save changes</Button>

                        </TabPanel>
                        <TabPanel value={value} index={1} dir={theme.direction}>
                            <Grid container spacing={1}>
                                <Grid item>{customListHrManager('Has permissions ', leftHrManager)}</Grid>
                                <Grid item>
                                    <Grid container direction="column" alignItems="center">
                                        <Button sx={{my: 0.5}} variant="contained" size="small"
                                                onClick={handleCheckedRightHrManager}
                                                disabled={leftCheckedHrManager.length === 0}>
                                            &gt;
                                        </Button>
                                        <Button sx={{my: 0.5}} variant="contained" size="small"
                                                onClick={handleCheckedLeftHrManager}
                                                disabled={rightCheckedHrManager.length === 0}>
                                            &lt;
                                        </Button>
                                    </Grid>
                                </Grid>
                                <Grid item>{customListHrManager('Available permissions', rightHrManager)}</Grid>
                            </Grid>
                            <Button onClick={updateHrManager}>Save changes</Button>
                        </TabPanel>
                        <TabPanel value={value} index={2} dir={theme.direction}>
                            <Grid container spacing={1}>
                                <Grid item>{customListProjectManager('Has permissions ', leftProjectManager)}</Grid>
                                <Grid item>
                                    <Grid container direction="column" alignItems="center">
                                        <Button sx={{my: 0.5}} variant="contained" size="small"
                                                onClick={handleCheckedRightProjectManager}
                                                disabled={leftCheckedProjectManager.length === 0}>
                                            &gt;
                                        </Button>
                                        <Button sx={{my: 0.5}} variant="contained" size="small"
                                                onClick={handleCheckedLeftProjectManager}
                                                disabled={rightCheckedProjectManager.length === 0}>
                                            &lt;
                                        </Button>
                                    </Grid>
                                </Grid>
                                <Grid
                                    item>{customListProjectManager('Available permissions', rightProjectManager)}</Grid>
                            </Grid>
                            <Button onClick={updateProjectManager}>Save changes</Button>
                        </TabPanel>
                    </SwipeableViews>
                </Box>
            </div>
        </>
    );
}

export default Permissions;
