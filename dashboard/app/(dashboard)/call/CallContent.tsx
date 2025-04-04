'use client';
import * as React from 'react';
import { Box, Paper } from '@mui/material';
import Grid from '@mui/material/Grid';
import YearlyCalls from '@/app/(dashboard)/call/YearlyCalls';
import MonthlyCalls from '@/app/(dashboard)/call/MonthlyCalls';

export default function CallContent() {
  return (
    <Box>
      <Grid container spacing={3}>
        {/* Row 1 */}
        <Grid size={6}>
          <YearlyCalls />
        </Grid>
        <Grid size={6}>
          <MonthlyCalls />
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
