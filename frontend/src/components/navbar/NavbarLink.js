import { Link } from 'react-router';
import Typography from '@mui/material/Typography';

export default function NavbarLink({ label, to }) {
    return <Typography 
                variant="h5" 
                component={Link} 
                to={to} 
                color='white'
                sx={{ 
                    flexGrow: 1, 
                    textDecoration: 'none', 
                    "&:hover": { color: "primary.light" }, 
                    fontFamily: '"Play", serif',
                    fontWeight: 400,
                    fontStyle: 'normal'
                }}
            >{label}
            </Typography>
}