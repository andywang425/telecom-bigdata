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

  async getCallStatus(year: number, month: number) {
    const res = await axios.get<BaseResponse<CallResponse.CallStatus[]>>(`/api/call/status`, {
      params: {
        year,
        month,
      },
    });
    return res.data;
  },

  async getCallDistribution(year: number, month: number) {
    const res = await axios.get<BaseResponse<CallResponse.CallDistribution[]>>(`/api/call/distribution`, {
      params: {
        year,
        month,
      },
    });
    return res.data;
  },
};

export default CALL;
