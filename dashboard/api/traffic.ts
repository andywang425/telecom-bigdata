import axios from '@/api/axios';
import { BaseResponse, TrafficResponse } from './types';

const TRAFFIC = {
  async getYearlyTraffic(startYear: number, endYear: number) {
    const res = await axios.get<BaseResponse<TrafficResponse.YearlyTraffic[]>>(`/api/traffic/summary/yearly`, {
      params: {
        startYear,
        endYear,
      },
    });
    return res.data;
  },

  async getMonthlyTraffic(year: number) {
    const res = await axios.get<BaseResponse<TrafficResponse.MonthlyTraffic[]>>(`/api/traffic/summary/monthly`, {
      params: {
        year,
      },
    });
    return res.data;
  },

  async getApplicationType(year: number, month: number) {
    const res = await axios.get<BaseResponse<TrafficResponse.ApplicationType[]>>(`/api/traffic/app`, {
      params: {
        year,
        month,
      },
    });
    return res.data;
  },

  async getNetworkTechnology(year: number, month: number) {
    const res = await axios.get<BaseResponse<TrafficResponse.NetworkTechnology[]>>(`/api/traffic/technology`, {
      params: {
        year,
        month,
      },
    });
    return res.data;
  },

  async getTrafficDistribution(year: number, month: number) {
    const res = await axios.get<BaseResponse<TrafficResponse.TrafficDistribution[]>>(`/api/traffic/distribution`, {
      params: {
        year,
        month,
      },
    });
    return res.data;
  },
};

export default TRAFFIC;
