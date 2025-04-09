import React from 'react';
import { LineChart } from '@mui/x-charts-pro';
import { TrafficResponse } from '@/api/types';

export interface TrafficDayDistributionChartProps {
  data: TrafficResponse.TrafficDistribution[];
  valueField: 'session' | 'dataVolume';
}

export default function TrafficDayDistributionChart({ data, valueField }: TrafficDayDistributionChartProps) {
  const valueFormatter = (value: number) => `${value}时`;

  const yAxis = valueField === 'dataVolume' ? [{ id: 'down' }, { id: 'up' }] : undefined;
  const rightAxis = valueField === 'dataVolume' ? 'up' : undefined;
  const series =
    valueField === 'dataVolume'
      ? [
          {
            label: '下行流量（KB）',
            dataKey: 'totalDownstream',
            yAxisId: 'down',
          },
          {
            label: '上行流量（KB）',
            dataKey: 'totalUpstream',
            yAxisId: 'up',
          },
        ]
      : [
          {
            label: '会话数量',
            dataKey: 'sessionCount',
          },
        ];

  return (
    <LineChart
      dataset={data}
      xAxis={[{ dataKey: 'hour', label: '小时', scaleType: 'point', valueFormatter }]}
      yAxis={yAxis}
      rightAxis={rightAxis}
      series={series}
      height={250}
    />
  );
}
