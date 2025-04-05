import axios from '@/api/axios';
import { BaseResponse, SmsResponse } from './types';

const SMS = {
  async getYearlySms(startYear: number, endYear: number) {
    const res = await axios.get<BaseResponse<SmsResponse.YearlySms[]>>(`/api/sms/summary/yearly`, {
      params: {
        startYear,
        endYear,
      },
    });
    return res.data;
  },

  async getMonthlySms(year: number) {
    const res = await axios.get<BaseResponse<SmsResponse.MonthlySms[]>>(`/api/sms/summary/monthly`, {
      params: {
        year,
      },
    });
    return res.data;
  },

  async getSmsStatus(year: number, month: number) {
    const res = await axios.get<BaseResponse<SmsResponse.SmsStatus[]>>(`/api/sms/status`, {
      params: {
        year,
        month,
      },
    });
    return res.data;
  },

  async getSmsDistribution(year: number, month: number) {
    const res = await axios.get<BaseResponse<SmsResponse.SmsDistribution[]>>(`/api/sms/distribution`, {
      params: {
        year,
        month,
      },
    });
    return res.data;
  },
};

export default SMS;
