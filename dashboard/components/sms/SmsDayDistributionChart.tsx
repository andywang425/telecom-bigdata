import React from 'react';
import { LineChart } from '@mui/x-charts-pro';
import { SmsResponse } from '@/api/types';

interface SmsDayDistributionChartProps {
  data: SmsResponse.SmsDistribution[];
}

export default function SmsDayDistributionChart({ data }: SmsDayDistributionChartProps) {
  const valueFormatter = (value: number) => `${value}时`;

  return (
    <LineChart
      dataset={data}
      xAxis={[{ dataKey: 'hour', label: '小时', valueFormatter }]}
      series={[
        {
          label: '短信数量',
          dataKey: 'smsCount',
        },
      ]}
      height={200}
    />
  );
}
