import { Container, Typography, Box } from '@mui/material';

export default function HomePage() {
    return (
        <Container maxWidth="lg">
            <Box sx={{ bgcolor: '#cfe8fc', height: '80vh' }}>
                <Typography variant='h3'>Главная страница</Typography>
            </Box>
        </Container>
    );
}