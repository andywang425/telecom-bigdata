import React from 'react';
import { LineChart } from '@mui/x-charts-pro';
import { StationResponse } from '@/api/types';

export interface MonthlyFailureRateChartProps {
  data: StationResponse.MonthlyFailureRate[];
}

export default function MonthlyFailureRateChart({ data }: MonthlyFailureRateChartProps) {
  const xAxisValueFormatter = (value: number) => `${value}月`;
  const seriesValueFormatter = (value: number | null) => `${value}%`;

  return (
    <LineChart
      dataset={data}
      xAxis={[{ dataKey: 'month', label: '月', scaleType: 'point', valueFormatter: xAxisValueFormatter }]}
      series={[
        {
          label: '通话故障率',
          dataKey: 'callFailureRate',
          valueFormatter: seriesValueFormatter,
        },
        {
          label: '短信故障率',
          dataKey: 'smsFailureRate',
          valueFormatter: seriesValueFormatter,
        },
      ]}
      height={250}
    />
  );
}
