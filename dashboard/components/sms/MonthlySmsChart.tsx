import React from 'react';
import { BarChart } from '@mui/x-charts-pro';
import { SmsResponse } from '@/api/types';

interface MonthlySmsChartProps {
  data: SmsResponse.MonthlySms[];
}

export default function MonthlySmsChart({ data }: MonthlySmsChartProps) {
  const valueFormatter = (value: number) => `${value}月`;

  return (
    <BarChart
      dataset={data}
      xAxis={[{ scaleType: 'band', dataKey: 'month', label: '月份', valueFormatter }]}
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
