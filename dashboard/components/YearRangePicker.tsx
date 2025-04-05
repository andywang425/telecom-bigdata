'use client';
import * as React from 'react';
import { DatePicker } from '@mui/x-date-pickers-pro';
import Typography from '@mui/material/Typography';
import { Box } from '@mui/material';
import theme from '@/theme';
import { DateTime } from 'luxon';

interface YearRangePickerProps {
  startYear: number | null;
  endYear: number | null;
  onChangeAction: (startYear: number | null, endYear: number | null) => void;
}

export default function YearRangePicker({ startYear, endYear, onChangeAction }: YearRangePickerProps) {
  const handleStartYearChange = (newStartYear: DateTime<boolean> | null) => {
    const year = newStartYear ? newStartYear.year : null;
    onChangeAction(year, endYear);
  };

  const handleEndYearChange = (newEndYear: DateTime<boolean> | null) => {
    const year = newEndYear ? newEndYear.year : null;
    onChangeAction(startYear, year);
  };

  return (
    <Box display={'flex'} sx={{ gap: 2, alignItems: 'center' }}>
      <DatePicker
        label={'起始年份'}
        views={['year']}
        value={startYear ? DateTime.fromObject({ year: startYear }) : null}
        onChange={handleStartYearChange}
        minDate={DateTime.fromObject({ year: 2000 })}
        maxDate={endYear ? DateTime.fromObject({ year: endYear }) : undefined}
      />
      <Typography sx={{ color: theme.palette.text.primary }}> – </Typography>
      <DatePicker
        label={'终止年份'}
        views={['year']}
        value={endYear ? DateTime.fromObject({ year: endYear }) : null}
        onChange={handleEndYearChange}
        minDate={startYear ? DateTime.fromObject({ year: startYear }) : undefined}
        disableFuture
      />
    </Box>
  );
}
