'use client';
import * as React from 'react';
import { Box, Paper } from '@mui/material';
import Grid from '@mui/material/Grid';
import YearlyCalls from '@/app/(dashboard)/call/YearlyCalls';
import MonthlyCalls from '@/app/(dashboard)/call/MonthlyCalls';
import CallStatus from '@/app/(dashboard)/call/CallStatus';
import { AdapterLuxon } from '@mui/x-date-pickers-pro/AdapterLuxon';
import { LocalizationProvider } from '@mui/x-date-pickers-pro/LocalizationProvider';

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
          <Grid size={4}>
            <Paper sx={{ p: 2, height: 250 }}>Chart D</Paper>
          </Grid>
          <Grid size={4}>
            <Paper sx={{ p: 2, height: 250 }}>Chart E</Paper>
          </Grid>
        </Grid>
      </Box>
    </LocalizationProvider>
  );
}
