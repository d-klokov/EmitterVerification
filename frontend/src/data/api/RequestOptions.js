const getOptions = () => ({
  method: "GET",
  headers: {
    "Content-type": "application/json",
  },
});

const postOptions = (body) => ({
  method: "POST",
  headers: {
    "Content-type": "application/json",
  },
  body: JSON.stringify(body),
});

const putOptions = (body) => ({
  method: "PUT",
  headers: {
    "Content-type": "application/json",
  },
  body: JSON.stringify(body),
});

const deleteOptions = () => ({
  method: "DELETE",
  headers: {
    "Content-type": "application/json",
  },
});

export { getOptions, postOptions, putOptions, deleteOptions };
