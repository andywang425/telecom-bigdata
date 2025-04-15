import axios from '@/api/axios';
import { BaseResponse, UserResponse } from './types';

const USER = {
  async getCluster() {
    const res = await axios.get<BaseResponse<UserResponse.UserCluster[]>>(`/api/user/cluster`);
    return res.data;
  },

  async getClusterCount() {
    const res = await axios.get<BaseResponse<UserResponse.UserClusterCount[]>>(`/api/user/count`);
    return res.data;
  },
};

export default USER;
