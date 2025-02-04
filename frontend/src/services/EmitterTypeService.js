import axios from "axios"

const PATH = 'http://localhost:8080/api/v1/types'

class EmitterTypeService {
    getEmitterTypesList(pageNumber) {
        return axios
            .get(PATH, {
                headers: {
                    "Content-type": "application/json"
                },
                params: {
                    page: pageNumber
                }
            })
    }

    getEmitterTypeById(id) {
        return axios
            .get(PATH + `/${id}`, {
                headers: {
                    'Content-Type': 'application/json',
                }
            })
    }

    createEmitterType(emitterTypeRequest) {
        return axios
            .post(PATH, emitterTypeRequest, {
                headers: {
                    'Content-Type': 'application/json',
                }
            })
    }

    editEmitterType(id, emitterTypeRequest) {
        return axios
            .put(PATH + `/${id}`, emitterTypeRequest, {
                headers: {
                    'Content-Type': 'application/json',
                }
            })
    }

    deleteEmitterType(id) {
        return axios
            .delete(PATH + `/${id}`, {
                headers: {
                    'Content-Type': 'application/json',
                }
            })
    }
}

export default new EmitterTypeService()