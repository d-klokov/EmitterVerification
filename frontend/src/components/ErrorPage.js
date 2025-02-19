import { Container, Typography, Box } from '@mui/material';
import { useLocation } from 'react-router-dom';

export default function ErrorPage() {
    const location = useLocation();

    return (
        <Container maxWidth="lg">
            <Box sx={{ bgcolor: '#cfe8fc', height: '80vh' }}>
                {/* <Typography variant='h2'>Ошибка {location.state.status}</Typography> */}
                <Typography variant='h4' color='error.main'>{location.state.message}</Typography>
            </Box>
        </Container>
    );
}