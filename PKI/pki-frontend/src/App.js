import ParticlesBg from 'particles-bg'
import "./particles.css"
import {AppBar, Box, Button, Toolbar} from "@mui/material";
import {createTheme, ThemeProvider} from '@mui/material/styles';
import KeyIcon from '@mui/icons-material/Key';
import Typography from '@mui/material/Typography';

const darkTheme = createTheme({
    palette: {
        mode: 'dark',
    },
});


function App() {
    return (
        <ThemeProvider theme={darkTheme}>
            <div className="App">
                <ParticlesBg color="#00FFFF" type="cobweb" num={200} bg={true}/>
                <Box>
                    <AppBar position="static">
                        <Toolbar>
                            <KeyIcon sx={{display: {xs: 'none', md: 'flex'}, mr: 1}}/>
                            <Typography
                                variant="h6"
                                noWrap
                                component="a"
                                href="/"
                                sx={{
                                    mr: 2,
                                    display: {xs: 'none', md: 'flex'},
                                    fontFamily: 'monospace',
                                    fontWeight: 700,
                                    letterSpacing: '.3rem',
                                    color: 'inherit',
                                    textDecoration: 'none',

                                }}
                            > PKI
                            </Typography>
                            <Button sx={{color: 'inherit'}}>All certificates</Button>
                            <Button sx={{color: 'inherit'}}>Create certificate</Button>
                            <Button sx={{color: 'inherit', marginLeft: 'auto'}}>Log out</Button>
                        </Toolbar>
                    </AppBar>
                    {/*OVde ce ici ruter*/}
                </Box>
            </div>
        </ThemeProvider>
    );
}

export default App;
