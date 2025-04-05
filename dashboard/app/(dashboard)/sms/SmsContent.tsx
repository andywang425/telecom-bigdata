'use client';
import * as React from 'react';
import { Box } from '@mui/material';
import Grid from '@mui/material/Grid';
import { AdapterLuxon } from '@mui/x-date-pickers-pro/AdapterLuxon';
import { LocalizationProvider } from '@mui/x-date-pickers-pro/LocalizationProvider';
import YearlySmsCard from '@/app/(dashboard)/sms/YearlySmsCard';
import MonthlySmsCard from '@/app/(dashboard)/sms/MonthlySmsCard';
import SmsStatusCard from '@/app/(dashboard)/sms/SmsStatusCard';
import SmsDayDistributionCard from '@/app/(dashboard)/sms/SmsDayDistributionCard';

export default function SmsContent() {
  return (
    <LocalizationProvider dateAdapter={AdapterLuxon} adapterLocale={'zh-cn'}>
      <Box>
        <Grid container spacing={3}>
          <Grid size={6}>
            <YearlySmsCard />
          </Grid>
          <Grid size={6}>
            <MonthlySmsCard />
          </Grid>

          <Grid size={4}>
            <SmsStatusCard />
          </Grid>
          <Grid size={8}>
            <SmsDayDistributionCard />
          </Grid>
        </Grid>
      </Box>
    </LocalizationProvider>
  );
}
