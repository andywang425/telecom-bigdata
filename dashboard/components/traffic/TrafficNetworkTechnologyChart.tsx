import React from 'react';
import { PieChart, PieValueType } from '@mui/x-charts-pro';
import { TrafficResponse } from '@/api/types';

export interface TrafficNetworkTechnologyChartProps {
  data: TrafficResponse.NetworkTechnology[];
  valueField: 'sessionCount' | 'totalUpstreamDataVolume' | 'totalDownstreamDataVolume';
}

export default function TrafficNetworkTechnologyChart({ data, valueField }: TrafficNetworkTechnologyChartProps) {
  const chartData: PieValueType[] = data.map(item => ({
    id: item.networkTechnology,
    value: item[valueField],
    label: item.networkTechnology,
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
