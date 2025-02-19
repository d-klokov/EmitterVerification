import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Container, Paper, Stack, List, ListItem, ListItemText, IconButton, Typography, Button, Pagination, Divider } from "@mui/material";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import OwnerService from "../../services/OwnerService";
import HeadingText from "../common/HeadingText";
import RegularText from "../common/RegularText";

const OwnersList = () => {
    const [ownersList, setOwnersList] = useState([]);
    const [pageNumber, setPageNumber] = useState(1);
    const [totalPages, setTotalPages] = useState(1);

    const handlePageChange = (event, value) => {
        setPageNumber(value);
    };

    const navigate = useNavigate();

    useEffect(() => {
        getOwnersList();
    }, [pageNumber]);

    function getOwnersList() {
        OwnerService
            .getOwnersList(pageNumber)
            .then(response => {
                setTotalPages(response.totalPages);
                setOwnersList(response.content);
            })
            .catch(error => {
                console.log(error);
                if (error && error.status === '400') {
                    navigate("/error", {
                        state: {
                            status: error.status,
                            message: error.message
                        }
                    })
                }
            })
    }

    return (
        <Container maxWidth="md" sx={{ p: 3 }}>
            <Stack spacing={3}>
                <HeadingText text={'Владельцы'} />

                <Paper elevation={1}>
                    <List sx={{ p: 3 }}>
                        <Divider />
                        {ownersList.map(item => (
                            <div key={item.id}>
                                <ListItem

                                    secondaryAction={
                                        <Stack direction={"row"} spacing={1}>
                                            <IconButton component={Link} edge="end" aria-label="edit" to={`/edit-owner/${item.id}`}>
                                                <EditIcon fontSize="medium" color="warning" />
                                            </IconButton>

                                            <IconButton component={Link} edge="end" aria-label="delete" to={`/delete-owner/${item.id}`}>
                                                <DeleteIcon fontSize="medium" color="error" />
                                            </IconButton>
                                        </Stack>
                                    }
                                >
                                    <ListItemText>
                                        <RegularText text={item.name} />
                                    </ListItemText>
                                </ListItem>
                                <Divider />
                            </div>
                        ))}
                    </List>

                    <Stack direction="row" display={"flex"} justifyContent={"space-between"} alignItems={"center"} sx={{ p: 3 }}>
                        <Pagination size="medium" count={totalPages} page={pageNumber} onChange={handlePageChange} />
                        <Button size="large" variant="contained" color="primary" href="/create-owner">Добавить</Button>
                    </Stack>
                </Paper>
            </Stack>
        </Container>
    );
}

export default OwnersList