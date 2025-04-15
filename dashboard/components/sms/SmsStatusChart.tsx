import React from 'react';
import { PieChart, PieValueType } from '@mui/x-charts-pro';
import { SmsResponse } from '@/api/types';

interface SmsStatusChartProps {
  data: SmsResponse.SmsStatus[];
}

export default function SmsStatusChart({ data }: SmsStatusChartProps) {
  const statusToText: Record<string, string> = {
    FAILED_TO_RECEIVE: '接收失败',
    FAILED_TO_SEND: '发送失败',
    SUCCESSFUL: '成功',
  };

  const statusToColor: Record<string, string> = {
    FAILED_TO_RECEIVE: '#ffa500',
    FAILED_TO_SEND: '#ef5f5f',
    SUCCESSFUL: '#2e96ff',
  };

  const chartData: PieValueType[] = data.map(item => ({
    id: item.sendStatus,
    value: item.smsCount,
    label: statusToText[item.sendStatus] ?? item.sendStatus,
    color: statusToColor[item.sendStatus],
  }));

  return (
    <PieChart
      series={[
        {
          highlightScope: { fade: 'global', highlight: 'item' },
          data: chartData,
        },
      ]}
      height={200}
    />
  );
}
