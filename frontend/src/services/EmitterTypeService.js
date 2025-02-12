import axios from "axios"
import ApiRequest from "../components/ApiRequest"

const PATH = 'http://localhost:8080/api/v1/types'

class EmitterTypeService {
    getEmitterTypesList(pageNumber) {
        const url = new URL(PATH);
        url.searchParams.append('page', pageNumber);
        
        const getOptions = {
            method: 'GET',
            headers: {
                'Content-type': 'application/json'
            }
        }

        return ApiRequest(url, getOptions);
    }

    getEmitterTypeById(id) {
        const url = new URL(PATH + `/${id}`);
        
        const getOptions = {
            method: 'GET',
            headers: {
                'Content-type': 'application/json'
            }
        }

        return ApiRequest(url, getOptions);
    }

    createEmitterType(emitterTypeRequest) {
        const url = new URL(PATH);
        
        const postOptions = {
            method: 'POST',
            headers: {
                'Content-type': 'application/json'
            },
            body: JSON.stringify(emitterTypeRequest)
        }

        return ApiRequest(url, postOptions);
    }

    editEmitterType(id, emitterTypeRequest) {
        const url = new URL(PATH + `/${id}`);
        
        const putOptions = {
            method: 'PUT',
            headers: {
                'Content-type': 'application/json'
            },
            body: JSON.stringify(emitterTypeRequest)
        }

        return ApiRequest(url, putOptions);
    }

    deleteEmitterType(id) {
        const url = new URL(PATH + `/${id}`);
        
        const deleteOptions = {
            method: 'DELETE',
            headers: {
                'Content-type': 'application/json'
            }
        }

        return ApiRequest(url, deleteOptions);
        // return axios
        //     .delete(PATH + `/${id}`, {
        //         headers: {
        //             'Content-Type': 'application/json',
        //         }
        //     })
    }
}

export default new EmitterTypeService()