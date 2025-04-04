import axios from '@/api/axios';
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

  async getMonthlyCalls(year: number) {
    const res = await axios.get<BaseResponse<CallResponse.MonthlyCalls[]>>(`/api/call/summary/monthly`, {
      params: {
        year,
      },
    });
    return res.data;
  },
};

export default CALL;
