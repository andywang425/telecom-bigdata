import axios from '@/axios';
import { BaseResponse, CallResponse } from './types';

const CALL = {
  async getYearlyCalls(startYear: number, endYear: number) {
    const res = await axios.get<BaseResponse<CallResponse.YearlyCalls[]>>(`/api/call/summary/yearly`, {
      params: {
        startYear,
        endYear,
      },
    });
    return res.data;
  },
};

export default CALL;
