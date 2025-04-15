'use client';
import * as React from 'react';
import { Box } from '@mui/material';
import Grid from '@mui/material/Grid';
import { AdapterLuxon } from '@mui/x-date-pickers-pro/AdapterLuxon';
import { LocalizationProvider } from '@mui/x-date-pickers-pro/LocalizationProvider';
import UserClusterCard from './UserClusterCard';
import UserCountCard from './UserCountCard';

export default function UserContent() {
  return (
    <LocalizationProvider dateAdapter={AdapterLuxon} adapterLocale={'zh-cn'}>
      <Box>
        <Grid container spacing={3}>
          <Grid size={6}>
            <UserClusterCard />
          </Grid>
          <Grid size={6}>
            <UserCountCard />
          </Grid>
        </Grid>
      </Box>
    </LocalizationProvider>
  );
}
