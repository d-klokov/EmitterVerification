import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import { Container, Paper, Stack, Typography } from "@mui/material";
import EmitterTypeService from "../../services/EmitterTypeService";
import HeadingText from "../common/HeadingText";

export default function EditEmitterType() {
    const initialEditRequest = {
        name: ""
    }

    const initialType = {
        id: "",
        name: ""
    }

    const [request, setRequest] = useState(initialEditRequest)
    const [type, setType] = useState(initialType)

    useEffect(() => {
        getEmitterTypeById(params.id);
    }, []);

    const navigate = useNavigate();

    let params = useParams();

    const getEmitterTypeById = (id) => {
        EmitterTypeService
            .getEmitterTypeById(id)
            .then((response) => {
                setType(response)
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

    const editEmitterType = () => {
        EmitterTypeService
            .editEmitterType(params.id, request)
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
        <Container maxWidth="md" sx={{ p: 3 }}>
            <Stack spacing={3}>
                <Paper elevation={1} sx={{ pt: 2 }}>
                    <HeadingText text={'Редактировать владельца'} />
                    <Stack spacing={3} padding={3}>
                        <TextField id="name" label="Имя" defaultValue={type.name} variant="outlined"
                            onChange={(event) => setRequest({ ...request, name: event.target.value })} />
                        <Stack direction={"row"} justifyContent="end">
                            <Button variant="contained" onClick={editEmitterType}>Готово</Button>
                        </Stack>
                    </Stack>
                </Paper>
            </Stack>
        </Container>
    )
}