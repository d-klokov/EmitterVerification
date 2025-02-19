import { useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import OwnerService from "../../services/OwnerService";

export default function DeleteOwner() {
    const navigate = useNavigate();

    let params = useParams();

    useEffect(() => {
        deleteOwnerById(params.id);
    }, []);

    const deleteOwnerById = (id) => {
        OwnerService.deleteOwner(id)
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

    return (<></>)
}