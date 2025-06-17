import ApiRequest from "../api/ApiRequest";
import {
  getOptions,
  postOptions,
  putOptions,
  deleteOptions,
} from "../api/RequestOptions";

const ApiService = (path) => {
  return {
    getAll: async () => {
      const url = new URL(path + "/all");

      try {
        return await ApiRequest(url, getOptions());
      } catch (error) {
        throw error;
      }
    },

    getPage: async (page, size, signal) => {
      const url = new URL(path);

      url.searchParams.append("page", page);
      url.searchParams.append("size", size);

      try {
        return await ApiRequest(url, { ...getOptions(), signal });
      } catch (error) {
        throw error;
      }
    },

    getById: async (id, signal) => {
      const url = new URL(path + `/${id}`);

      try {
        return await ApiRequest(url, { ...getOptions(), signal });
      } catch (error) {
        throw error;
      }
    },

    create: async (request, signal) => {
      const url = new URL(path);

      try {
        return await ApiRequest(url, { ...postOptions(request), signal });
      } catch (error) {
        throw error;
      }
    },
  };
};

export default ApiService;
