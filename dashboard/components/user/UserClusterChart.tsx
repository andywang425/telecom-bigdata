import React from 'react';
import { ScatterChartPro } from '@mui/x-charts-pro';
import { UserResponse } from '@/api/types';

interface UserClusterChartProps {
  data: UserResponse.UserCluster[];
}

interface DataPoint extends Record<string, string | number | undefined> {
  phone: string;
  pcaX0?: number;
  pcaY0?: number;
  pcaX1?: number;
  pcaY1?: number;
}

export default function UserClusterChart({ data }: UserClusterChartProps) {
  const dataset: DataPoint[] = data.map(i =>
    i.cluster === 0
      ? {
          phone: i.phone,
          pcaX0: i.pcaX,
          pcaY0: i.pcaY,
        }
      : {
          phone: i.phone,
          pcaX1: i.pcaX,
          pcaY1: i.pcaY,
        },
  );

  return (
    <ScatterChartPro
      dataset={dataset}
      series={[
        { datasetKeys: { id: 'phone', x: 'pcaX0', y: 'pcaY0' }, label: '夜猫子' },
        { datasetKeys: { id: 'phone', x: 'pcaX1', y: 'pcaY1' }, label: '正常人' },
      ]}
      xAxis={[{ label: 'PCA X', zoom: true }]}
      yAxis={[{ label: 'PCA Y', zoom: true }]}
      height={400}
    />
  );
}
