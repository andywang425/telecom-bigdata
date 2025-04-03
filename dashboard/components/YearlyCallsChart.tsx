import React from 'react';
import { BarChart } from '@mui/x-charts-pro';
import { CallResponse } from '@/api/types';

interface YearlyCallsChartProps {
  data: CallResponse.YearlyCalls[];
}

export default function YearlyCallsChart({ data }: YearlyCallsChartProps) {
  const valueFormatter = (value: number) => `${value}`;

  return (
    <BarChart
      dataset={data}
      xAxis={[{ scaleType: 'band', dataKey: 'year', label: '年份', valueFormatter }]}
      series={[
        { dataKey: 'totalCalls', label: '通话数量' },
        { dataKey: 'totalCallDuration', label: '通话时长（分钟）' },
      ]}
      height={400}
    />
  );
}
