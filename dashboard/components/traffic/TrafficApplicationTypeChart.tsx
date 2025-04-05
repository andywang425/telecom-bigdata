import React from 'react';
import { PieChart, PieValueType } from '@mui/x-charts-pro';
import { TrafficResponse } from '@/api/types';

export interface TrafficApplicationTypeChartProps {
  data: TrafficResponse.ApplicationType[];
  valueField: 'sessionCount' | 'totalUpstreamDataVolume' | 'totalDownstreamDataVolume';
}

export default function TrafficApplicationTypeChart({ data, valueField }: TrafficApplicationTypeChartProps) {
  const statusToText: Record<string, string> = {
    ONLINE_VIDEO: '在线视频',
    SOCIAL_MEDIA: '社交媒体',
    WEB_BROWSING: '网上冲浪',
  };

  const chartData: PieValueType[] = data.map(item => ({
    id: item.applicationType,
    value: item[valueField],
    label: statusToText[item.applicationType] ?? item.applicationType,
  }));

  return (
    <PieChart
      series={[
        {
          data: chartData,
        },
      ]}
      height={250}
    />
  );
}
