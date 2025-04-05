import React from 'react';
import { LineChart } from '@mui/x-charts-pro';
import { CallResponse } from '@/api/types';

interface CallDayDistributionChartProps {
  data: CallResponse.CallDistribution[];
}

export default function CallDayDistributionChart({ data }: CallDayDistributionChartProps) {
  const valueFormatter = (value: number) => `${value}时`;

  return (
    <LineChart
      dataset={data}
      xAxis={[{ dataKey: 'hour', label: '小时', valueFormatter }]}
      series={[
        {
          label: '通话数量',
          dataKey: 'callCount',
        },
      ]}
      height={200}
    />
  );
}
