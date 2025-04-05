'use client';
import * as React from 'react';
import { Box } from '@mui/material';
import Grid from '@mui/material/Grid';
import YearlyCalls from '@/app/(dashboard)/call/YearlyCalls';
import MonthlyCalls from '@/app/(dashboard)/call/MonthlyCalls';
import CallStatus from '@/app/(dashboard)/call/CallStatus';
import { AdapterLuxon } from '@mui/x-date-pickers-pro/AdapterLuxon';
import { LocalizationProvider } from '@mui/x-date-pickers-pro/LocalizationProvider';
import CallDayDistribution from '@/app/(dashboard)/call/CallDayDistribution';

export default function CallContent() {
  return (
    <LocalizationProvider dateAdapter={AdapterLuxon} adapterLocale={'zh-cn'}>
      <Box>
        <Grid container spacing={3}>
          <Grid size={6}>
            <YearlyCalls />
          </Grid>
          <Grid size={6}>
            <MonthlyCalls />
          </Grid>

          <Grid size={4}>
            <CallStatus />
          </Grid>
          <Grid size={8}>
            <CallDayDistribution />
          </Grid>
        </Grid>
      </Box>
    </LocalizationProvider>
  );
}
