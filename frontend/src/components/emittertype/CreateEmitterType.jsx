import { useState } from "react";
import { useNavigate } from "react-router-dom";
import Button from '@mui/material/Button';
import { Container, Paper, Stack, TextField } from "@mui/material";
import EmitterTypeService from "../../services/EmitterTypeService";
import HeadingText from "../common/HeadingText";

const CreateEmitterType = () => {
    const initialCreateRequest = {
        name: ""
    }

    const [request, setRequest] = useState(initialCreateRequest)

    const navigate = useNavigate();

    const createEmitterType = () => {
        EmitterTypeService.createEmitterType(request)
            .then(() => {
                navigate("/types-list");
            })
            .catch(error => {
                console.log(error);
                if (error) {
                    navigate("/error", {
                        state: {
                            status: error.response.status,
                            message: error.response.message
                        }
                    })
                }
            })
    }

    return (
        <Container maxWidth="md" sx={{ p: 3, mt: 5 }}>
            <Stack spacing={3}>
                <Paper elevation={1} sx={{ pt: 2 }}>
                    <HeadingText text={'Добавить тип излучателя'} />
                    <Stack spacing={3} padding={3}>
                        <TextField id="name" label="Имя" variant="outlined" value={request.name}
                            onChange={(event) => setRequest({ ...request, name: event.target.value })} />
                        <Stack direction={"row"} justifyContent="end">
                            <Button variant="contained" onClick={createEmitterType}>Добавить</Button>
                        </Stack>
                    </Stack>
                </Paper>
            </Stack>
        </Container>
    )
}

export default CreateEmitterType