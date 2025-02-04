import Typography from '@mui/material/Typography';

export default function ListItemText({ text }) {
    return <Typography 
                variant="h5"
                color='text.secondary'
                sx={{
                    fontFamily: '"Play", serif',
                    fontWeight: 400,
                    fontStyle: 'normal'
                }}
            >{text}
            </Typography>
}