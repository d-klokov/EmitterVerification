import Typography from '@mui/material/Typography';

export default function HeadingText({ text }) {
    return <Typography 
                variant="h3"
                color='primary.main'
                align="center"
                sx={{
                    fontFamily: '"Play", serif',
                    fontWeight: 400,
                    fontStyle: 'normal'
                }}
            >{text}
            </Typography>
}