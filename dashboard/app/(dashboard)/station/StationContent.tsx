'use client';
import * as React from 'react';
import { Box } from '@mui/material';
import Grid from '@mui/material/Grid';
import { AdapterLuxon } from '@mui/x-date-pickers-pro/AdapterLuxon';
import { LocalizationProvider } from '@mui/x-date-pickers-pro/LocalizationProvider';
import YearlyFailureRateCard from '@/app/(dashboard)/station/YearlyFailureRateCard';
import MonthlyFailureRateCard from '@/app/(dashboard)/station/MonthlyFailureRateCard';

export default function StationContent() {
  return (
    <LocalizationProvider dateAdapter={AdapterLuxon} adapterLocale={'zh-cn'}>
      <Box>
        <Grid container spacing={3}>
          <Grid size={6}>
            <YearlyFailureRateCard />
          </Grid>
          <Grid size={6}>
            <MonthlyFailureRateCard />
          </Grid>

          <Grid size={4}></Grid>
          <Grid size={8}></Grid>
        </Grid>
      </Box>
    </LocalizationProvider>
  );
}
