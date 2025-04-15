import React from 'react';
import { PieChart, PieItemId, PieValueType } from '@mui/x-charts-pro';
import { UserResponse } from '@/api/types';

interface UserCountChartProps {
  data: UserResponse.UserClusterCount[];
}

export default function UserCountChart({ data }: UserCountChartProps) {
  const clusterToText: Record<string, string> = {
    0: '夜猫子',
    1: '正常人',
  };

  const chartData: PieValueType[] = [];
  const percentages: Record<PieItemId, number> = {};

  for (const item of data) {
    chartData.push({
      id: item.cluster,
      value: item.count,
      label: clusterToText[item.cluster] ?? item.cluster,
    });

    percentages[item.cluster] = item.percentage;
  }

  return (
    <PieChart
      series={[
        {
          arcLabel: item => `${percentages[item.id]}%`,
          highlightScope: { fade: 'global', highlight: 'item' },
          data: chartData,
        },
      ]}
      height={400}
    />
  );
}
