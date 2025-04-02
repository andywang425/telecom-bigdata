import React from 'react';
import { BarChart } from '@mui/x-charts-pro';
import { Card, CardContent, Typography } from '@mui/material';
import CALL from '@/api/call';

const YearlyCallsChart = async () => {
  const yearlyCalls = await CALL.getYearlyCalls(2020, 2024);
  const data = yearlyCalls.data;

  const years = data.map(item => item.year.toString());
  const totalCalls = data.map(item => item.totalCalls);

  return (
    <Card variant="outlined" sx={{ p: 2 }}>
      <Typography variant="h6" gutterBottom>
        Total Calls per Year
      </Typography>

      <CardContent>
        <BarChart
          xAxis={[{ scaleType: 'band', data: years }]}
          series={[{ data: totalCalls, label: 'Total Calls' }]}
          width={600}
          height={400}
        />
      </CardContent>
    </Card>
  );
};

export default YearlyCallsChart;
