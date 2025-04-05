import React from 'react';
import { BarChart } from '@mui/x-charts-pro';
import { TrafficResponse } from '@/api/types';

interface YearlyTrafficChartProps {
  data: TrafficResponse.YearlyTraffic[];
}

export default function YearlyTrafficChart({ data }: YearlyTrafficChartProps) {
  const valueFormatter = (value: number) => `${value}年`;

  return (
    <BarChart
      dataset={data}
      xAxis={[{ scaleType: 'band', dataKey: 'year', label: '年份', valueFormatter }]}
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
