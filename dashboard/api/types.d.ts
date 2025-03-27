export interface BaseResponse<T> {
  code: number;
  data: T;
  message: string;
}

export namespace AuthResponse {
  interface login {
    id: number;
    email: string;
    createdAt: string
  }
}
