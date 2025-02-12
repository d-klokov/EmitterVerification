const ApiRequest = async (url = '', options = null) => {
    let data = null;
    try {
        const response = await fetch(url, options);

        if (!response.ok) throw Error(response.json.message);

        data = await response.json();
    } catch (error) {
        data = error.message;
    } 
    return data;
}

export default ApiRequest;