const ApiRequest = async (url = '', options = null) => {
    const response = await fetch(url, options);

    if (!response.ok) throw await response.json();

    return response.json();
}

export default ApiRequest;