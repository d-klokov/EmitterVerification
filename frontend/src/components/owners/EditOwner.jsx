import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import { Container, Paper, Stack } from "@mui/material";
import OwnerService from "../../services/OwnerService";
import HeadingText from "../common/HeadingText";

export default function EditOwner() {
    const initialEditRequest = {
        name: ""
    }

    const initialOwner = {
        id: "",
        name: ""
    }

    const [request, setRequest] = useState(initialEditRequest)
    const [owner, setOwner] = useState(initialOwner)

    useEffect(() => {
        getOwnerById(params.id);
    }, []);

    const navigate = useNavigate();

    let params = useParams();

    const getOwnerById = (id) => {
        OwnerService
            .getOwnerById(id)
            .then((response) => {
                setOwner(response)
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

    const editOwner = () => {
        OwnerService
            .editOwner(params.id, request)
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
                <HeadingText text={'Редактировать владельца'} />

                <Paper elevation={1} sx={{ p: 3 }}>
                    <Stack spacing={3}>
                        <TextField id="name" label="Имя" defaultValue={owner.name} variant="outlined"
                            onChange={(event) => setRequest({ ...request, name: event.target.value })} />
                        <Stack direction={"row"} justifyContent="end">
                            <Button variant="contained" onClick={editOwner}>Готово</Button>
                        </Stack>
                    </Stack>
                </Paper>
            </Stack>
        </Container>
    )
}