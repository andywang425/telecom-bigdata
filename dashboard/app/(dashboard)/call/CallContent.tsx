'use client';
import * as React from 'react';
import YearlyCallsChart from '@/components/YearlyCallsChart';
import { Box, Card, CardActions, CardContent, Paper } from '@mui/material';
import Typography from '@mui/material/Typography';
import Grid from '@mui/material/Grid';
import YearRangePicker from '@/components/YearRangePicker';
import { useEffect } from 'react';

import { CallResponse } from '@/api/types';
import { getYearlyCalls } from '@/app/(dashboard)/call/actions';

export default function CallContent() {
  const [startYear, setStartYear] = React.useState<number | null>(2021);
  const [endYear, setEndYear] = React.useState<number | null>(2024);
  const [callYearlyData, setCallYearlyData] = React.useState<CallResponse.YearlyCalls[]>([]);

  useEffect(() => {
    if (startYear && endYear) {
      getYearlyCalls(startYear, endYear).then(res => {
        if (res.code === 0) {
          setCallYearlyData(res.data);
        }
      });
    }
  }, [startYear, endYear]);

  return (
    <Box>
      <Grid container spacing={3}>
        {/* Row 1 */}
        <Grid size={6}>
          <Card variant="outlined" sx={{ p: 2 }}>
            <Typography variant="h6" gutterBottom>
              年通话数量
            </Typography>
            <CardContent>
              <YearlyCallsChart data={callYearlyData} />
            </CardContent>
            <CardActions>
              <YearRangePicker
                startYear={startYear}
                endYear={endYear}
                onChangeAction={(newStartYear, newEndYear) => {
                  setStartYear(newStartYear);
                  setEndYear(newEndYear);
                }}
              />
            </CardActions>
          </Card>
        </Grid>
        <Grid size={6}>
          <Paper sx={{ p: 2, height: 300 }}>Chart B</Paper>
        </Grid>

        {/* Row 2 */}
        <Grid size={4}>
          <Paper sx={{ p: 2, height: 250 }}>Chart C</Paper>
        </Grid>
        <Grid size={4}>
          <Paper sx={{ p: 2, height: 250 }}>Chart D</Paper>
        </Grid>
        <Grid size={4}>
          <Paper sx={{ p: 2, height: 250 }}>Chart E</Paper>
        </Grid>
      </Grid>
    </Box>
  );
}
