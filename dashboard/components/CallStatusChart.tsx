import React from 'react';
import { PieChart, PieValueType } from '@mui/x-charts-pro';
import { CallResponse } from '@/api/types';

interface MonthlyCallsChartProps {
  data: CallResponse.CallStatus[];
}

export default function CallStatusChart({ data }: MonthlyCallsChartProps) {
  const statusToText: Record<string, string> = {
    BUSY: '忙线',
    CONNECTED: '接通',
    FAILED: '失败',
    MISSED: '未接',
  };

  const statusToColor: Record<string, string> = {
    BUSY: '#ffa500',
    CONNECTED: '#2e96ff',
    FAILED: '#ef5f5f',
    MISSED: '#60009b',
  };

  const chartData: PieValueType[] = data.map(item => ({
    id: item.callStatus,
    value: item.callCount,
    label: statusToText[item.callStatus] ?? item.callStatus,
    color: statusToColor[item.callStatus],
  }));

  return (
    <PieChart
      series={[
        {
          data: chartData,
        },
      ]}
      height={200}
    />
  );
}
