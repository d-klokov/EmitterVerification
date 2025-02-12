import { useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import EmitterTypeService from "../../services/EmitterTypeService";

export default function DeleteEmitterType() {
    const navigate = useNavigate();

    let params = useParams();

    useEffect(() => {
        deleteEmitterTypeById(params.id);
    }, [params.id]);

    const deleteEmitterTypeById = (id) => {
        EmitterTypeService.deleteEmitterType(id)
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

    return (<></>)
}