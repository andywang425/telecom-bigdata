import axios from '@/axios';
import { BaseResponse, AuthResponse } from './types';

const AUTH = {
  async login(email: string, password: string) {
    const res = await axios.post<BaseResponse<AuthResponse.Login>>(`/api/auth/login`, {
      email,
      password,
    });
    return res.data;
  },

  async refreshToken(refreshToken: string) {
    const res = await axios.post<BaseResponse<AuthResponse.RefreshToken>>(`/api/auth/refresh-token`, {
      refreshToken,
    });
    return res.data;
  },
};

export default AUTH;
