export class NetworkError extends Error {
  constructor(message, cause) {
    super(message);
    this.name = "NetworkError";
    this.statusCode = 503;
    this.cause = cause;
  }
}

export class ApiError extends Error {
  constructor(message, statusCode, cause) {
    super(message);
    this.name = "ApiError";
    this.statusCode = statusCode;
    this.cause = cause;
  }
}
