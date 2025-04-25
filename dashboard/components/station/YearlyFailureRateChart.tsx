import React from 'react';
import { LineChart } from '@mui/x-charts-pro';
import { StationResponse } from '@/api/types';

export interface YearlyFailureRateChartProps {
  data: StationResponse.YearlyFailureRate[];
}

export default function YearlyFailureRateChart({ data }: YearlyFailureRateChartProps) {
  const xAxisValueFormatter = (value: number) => `${value}年`;
  const seriesValueFormatter = (value: number | null) => `${value}%`;

  return (
    <LineChart
      dataset={data}
      xAxis={[{ dataKey: 'year', label: '年', scaleType: 'point', valueFormatter: xAxisValueFormatter }]}
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
