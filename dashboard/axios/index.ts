import axios from 'axios';
import { auth } from '@/auth';

const instance = axios.create({
  baseURL: process.env.BACKEND_URL,
  timeout: 3000,
});

instance.interceptors.request.use(
  async config => {
    const session = await auth();
    if (session?.access_token) {
      config.headers.Authorization = `Bearer ${session.access_token}`;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  },
);

export default instance;
