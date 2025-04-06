import axios from '@/api/axios';
import { BaseResponse, StationResponse } from './types';

const STATION = {
  async getYearlyFailureRate(startYear: number, endYear: number) {
    const res = await axios.get<BaseResponse<StationResponse.YearlyFailureRate[]>>(`/api/station/failure/yearly`, {
      params: {
        startYear,
        endYear,
      },
    });
    return res.data;
  },

  async getMonthlyFailureRate(year: number) {
    const res = await axios.get<BaseResponse<StationResponse.MonthlyFailureRate[]>>(`/api/station/failure/monthly`, {
      params: {
        year,
      },
    });
    return res.data;
  },
};

export default STATION;
