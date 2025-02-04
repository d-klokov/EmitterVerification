import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import { Stack } from '@mui/material';
import NavbarLink from './NavbarLink';

export default function Navbar() {
    return (
        <Box sx={{ flexGrow: 1 }}>
            <AppBar position="static">
                <Toolbar>
                    <Stack direction={'row'} spacing={3}>
                        <NavbarLink label={'Главная'} to={'/'}></NavbarLink>

                        <Stack direction={'row'} spacing={3}>
                            <NavbarLink label={'Типы излучателей'} to={'/types-list'}></NavbarLink>
                            <NavbarLink label={'Владельцы'} to={'/owners-list'}></NavbarLink>
                            <NavbarLink label={'Излучатели'} to={'/emitters-list'}></NavbarLink>
                        </Stack>

                    </Stack>
                </Toolbar>
            </AppBar>
        </Box>
    );
}
