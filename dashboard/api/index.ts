import axios from '@/axios';
import { AuthResponse, BaseResponse } from '@/api/types';

const AUTH = {
  async login(email: string, password: string) {
    const res = await axios.post<BaseResponse<AuthResponse.login>>(`/api/auth/login`, {
      email,
      password,
    });
    return res.data;
  },
};

export { AUTH };
