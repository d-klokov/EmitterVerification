import { useState } from "react";
import { useNavigate } from "react-router-dom";
import Button from '@mui/material/Button';
import { Container, Paper, Stack, TextField } from "@mui/material";
import OwnerService from "../../services/OwnerService";
import HeadingText from "../common/HeadingText";

const CreateOwner = () => {
    const initialCreateRequest = {
        name: ""
    }

    const [request, setRequest] = useState(initialCreateRequest)

    const navigate = useNavigate();

    const createOwner = () => {
        OwnerService.createOwner(request)
            .then(() => {
                navigate("/owners-list");
            })
            .catch(error => {
                console.log(error);
                if (error) {
                    navigate("/error", {
                        state: {
                            status: error.statusCode,
                            message: error.message
                        }
                    })
                }
            })
    }

    return (
        <Container maxWidth="md" sx={{ p: 3 }}>
            <Stack spacing={3}>
                <HeadingText text={'Добавить владельца'} />

                <Paper elevation={1} sx={{ p: 3 }}>
                    <Stack spacing={3}>
                        <TextField id="name" label="Имя" variant="outlined" value={request.name}
                            onChange={(event) => setRequest({ ...request, name: event.target.value })} />
                        <Stack direction={"row"} justifyContent="end">
                            <Button variant="contained" onClick={createOwner}>Добавить</Button>
                        </Stack>
                    </Stack>
                </Paper>
            </Stack>
        </Container>
    )
}

export default CreateOwner