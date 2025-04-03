import React from 'react';
import { BarChart } from '@mui/x-charts-pro';
import { CallResponse } from '@/api/types';

interface YearlyCallsChartProps {
  data: CallResponse.YearlyCalls[];
}

export default function YearlyCallsChart({ data }: YearlyCallsChartProps) {
  const years = data.map(item => item.year);
  const totalCalls = data.map(item => item.totalCalls);

  return (
    <BarChart
      xAxis={[{ scaleType: 'band', data: years }]}
      series={[{ data: totalCalls, label: '通话数量' }]}
      height={400}
    />
  );
}
