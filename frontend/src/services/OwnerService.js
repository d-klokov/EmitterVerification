import ApiRequest from "../components/ApiRequest"

const PATH = 'http://localhost:8080/api/v1/owners'

class OwnerService {
    getOwnersList(pageNumber) {
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

    getOwnerById(id) {
        const url = new URL(PATH + `/${id}`);
        
        const getOptions = {
            method: 'GET',
            headers: {
                'Content-type': 'application/json'
            }
        }

        return ApiRequest(url, getOptions);
    }

    createOwner(ownerRequest) {
        const url = new URL(PATH);
        
        const postOptions = {
            method: 'POST',
            headers: {
                'Content-type': 'application/json'
            },
            body: JSON.stringify(ownerRequest)
        }

        return ApiRequest(url, postOptions);
    }

    editOwner(id, ownerRequest) {
        const url = new URL(PATH + `/${id}`);
        
        const putOptions = {
            method: 'PUT',
            headers: {
                'Content-type': 'application/json'
            },
            body: JSON.stringify(ownerRequest)
        }

        return ApiRequest(url, putOptions);
    }

    deleteOwner(id) {
        const url = new URL(PATH + `/${id}`);
        
        const deleteOptions = {
            method: 'DELETE',
            headers: {
                'Content-type': 'application/json'
            }
        }

        return ApiRequest(url, deleteOptions);
    }
}

export default new OwnerService()