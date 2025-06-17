import { NetworkError, ApiError } from "../error/Errors";

const ApiRequest = async (url = "", options = {}) => {
  try {
    const response = await fetch(url, options);

    const contentType = response.headers.get("content-type");

    let data;
    if (contentType?.includes("application/json")) {
      data = await response.json();
    } else {
      data = await response.text();
    }

    if (!response.ok) {
      const error = new Error(data.message || "Ошибка сервера!");
      error.status = response.status;
      error.data = data;
      throw error;
    }

    return data;
  } catch (error) {
    if (error.name === "AbortError") throw error;

    if (error instanceof TypeError && error.message === "Failed to fetch") {
      throw new NetworkError("Сервер недоступен!", error);
    }

    throw new ApiError(error.message, error.statusCode || 500, error);
  }
};

export default ApiRequest;
