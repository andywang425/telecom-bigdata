'use client';
import * as React from 'react';
import YearlyCallsChart from '@/components/YearlyCallsChart';
import { Box, Paper, Stack } from '@mui/material';
import Typography from '@mui/material/Typography';
import Grid from '@mui/material/Grid2';

export default function CallContent() {
  return (
    <Box>
      <Grid container spacing={3}>
        {/* Row 1 */}
        <Grid size={6}>
          <Paper sx={{ p: 2, height: 300 }}>Chart A</Paper>
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
