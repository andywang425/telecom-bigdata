'use client';
import * as React from 'react';
import { Box } from '@mui/material';
import Grid from '@mui/material/Grid';
import { AdapterLuxon } from '@mui/x-date-pickers-pro/AdapterLuxon';
import { LocalizationProvider } from '@mui/x-date-pickers-pro/LocalizationProvider';
import YearlyTrafficCard from '@/app/(dashboard)/traffic/YearlyTrafficCard';
import MonthlyTrafficCard from '@/app/(dashboard)/traffic/MonthlyTrafficCard';
import TrafficApplicationTypeCard from '@/app/(dashboard)/traffic/TrafficApplicationTypeCard';

export default function TrafficContent() {
  return (
    <LocalizationProvider dateAdapter={AdapterLuxon} adapterLocale={'zh-cn'}>
      <Box>
        <Grid container spacing={3}>
          <Grid size={6}>
            <YearlyTrafficCard />
          </Grid>
          <Grid size={6}>
            <MonthlyTrafficCard />
          </Grid>

          <Grid size={6}>
            <TrafficApplicationTypeCard />
          </Grid>
          <Grid size={6}></Grid>
        </Grid>
      </Box>
    </LocalizationProvider>
  );
}
