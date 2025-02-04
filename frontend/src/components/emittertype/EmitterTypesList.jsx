import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Container, Paper, Stack, List, ListItem, ListItemText, IconButton, Typography, Button, Pagination, Divider } from "@mui/material";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import EmitterTypeService from "../../services/EmitterTypeService";
import HeadingText from "../common/HeadingText";
import RegularText from "../common/RegularText";

const EmitterTypesList = () => {
    const [emitterTypesList, setEmitterTypesList] = useState([]);
    const [pageNumber, setPageNumber] = useState(1);
    const [totalPages, setTotalPages] = useState(1);

    const handlePageChange = (event, value) => {
        setPageNumber(value);
    };

    const navigate = useNavigate();

    useEffect(() => {
        getEmitterTypesList();
    }, [pageNumber]);

    function getEmitterTypesList() {
        EmitterTypeService
            .getEmitterTypesList(pageNumber)
            .then(response => {
                setTotalPages(response.data.totalPages);
                setEmitterTypesList(response.data.content);
            })
            .catch(error => {
                console.log(error);
                if (error && error.status == '400') {
                    navigate("/error", {
                        state: {
                            status: error.data.status,
                            message: error.data.message
                        }
                    })
                }
            })
    }

    return (
        <Container maxWidth="md" sx={{ p: 3 }}>
            <Stack spacing={3}>


                <Paper elevation={1} sx={{ pt: 3 }}>
                    <HeadingText text={'Типы излучателей'} />

                    <List sx={{ p: 3 }}>
                        <Divider />
                        {emitterTypesList.map(item => (
                            <div key={item.id}>
                                <ListItem
                                    secondaryAction={
                                        <Stack direction={"row"} spacing={1}>
                                            <IconButton component={Link} edge="end" aria-label="edit" to={`/edit-type/${item.id}`}>
                                                <EditIcon fontSize="medium" color="warning" />
                                            </IconButton>

                                            <IconButton component={Link} edge="end" aria-label="delete" to={`/delete-type/${item.id}`}>
                                                <DeleteIcon fontSize="medium" color="error" />
                                            </IconButton>
                                        </Stack>
                                    }>
                                    <ListItemText>
                                        {/* <Typography variant="h5" color="text.primary">{item.name}</Typography> */}
                                        <RegularText text={item.name} />
                                    </ListItemText>
                                </ListItem>
                                <Divider />
                            </div>
                        ))}
                    </List>

                    <Stack direction="row" display={"flex"} justifyContent={"space-between"} alignItems={"center"} sx={{ p: 3 }}>
                        <Pagination size="medium" count={totalPages} page={pageNumber} onChange={handlePageChange} />
                        <Button size="large" variant="contained" color="primary" href="/create-type">Добавить</Button>
                    </Stack>
                </Paper>
            </Stack>
        </Container>
    );
}

export default EmitterTypesList