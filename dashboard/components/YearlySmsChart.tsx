import React from 'react';
import { BarChart } from '@mui/x-charts-pro';
import { SmsResponse } from '@/api/types';

interface YearlySmsChartProps {
  data: SmsResponse.YearlySms[];
}

export default function YearlySmsChart({ data }: YearlySmsChartProps) {
  const valueFormatter = (value: number) => `${value}年`;

  return (
    <BarChart
      dataset={data}
      xAxis={[{ scaleType: 'band', dataKey: 'year', label: '年份', valueFormatter }]}
      yAxis={[{ id: 'count' }, { id: 'length' }]}
      rightAxis={'length'}
      series={[
        { dataKey: 'totalCount', label: '短信数量', yAxisId: 'count' },
        { dataKey: 'totalLength', label: '短信字数', yAxisId: 'length' },
      ]}
      height={400}
    />
  );
}
