import React from 'react';
import { BarChart } from '@mui/x-charts-pro';
import { CallResponse } from '@/api/types';

interface MonthlyCallsChartProps {
  data: CallResponse.MonthlyCalls[];
}

export default function MonthlyCallsChart({ data }: MonthlyCallsChartProps) {
  const valueFormatter = (value: number) => `${value}月`;

  return (
    <BarChart
      dataset={data}
      xAxis={[{ scaleType: 'band', dataKey: 'month', label: '月份', valueFormatter }]}
      series={[
        { dataKey: 'totalCalls', label: '通话数量' },
        { dataKey: 'totalCallDuration', label: '通话时长（分钟）' },
      ]}
      height={400}
    />
  );
}
