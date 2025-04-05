import React from 'react';
import { BarChart } from '@mui/x-charts-pro';
import { TrafficResponse } from '@/api/types';

interface MonthlyTrafficChartProps {
  data: TrafficResponse.MonthlyTraffic[];
}

export default function MonthlyTrafficChart({ data }: MonthlyTrafficChartProps) {
  const valueFormatter = (value: number) => `${value}月`;

  return (
    <BarChart
      dataset={data}
      xAxis={[{ scaleType: 'band', dataKey: 'month', label: '月份', valueFormatter }]}
      series={[
        { dataKey: 'totalSessions', label: '会话数量' },
        { dataKey: 'totalDuration', label: '会话时长（分钟）' },
        { dataKey: 'totalUpstream', label: '上行流量（MB）' },
        { dataKey: 'totalDownstream', label: '下行流量（MB）' },
      ]}
      height={400}
    />
  );
}
