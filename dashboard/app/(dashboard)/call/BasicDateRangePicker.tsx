'use client'
import * as React from 'react';
import { LocalizationProvider } from '@mui/x-date-pickers-pro/LocalizationProvider';
import { AdapterLuxon } from '@mui/x-date-pickers-pro/AdapterLuxon';
import { DateRangePicker } from '@mui/x-date-pickers-pro/DateRangePicker';

export default function BasicDateRangePicker() {
    return (
        <LocalizationProvider dateAdapter={AdapterLuxon}>
        <DateRangePicker localeText={{ start: 'Check-in', end: 'Check-out' }} />
    </LocalizationProvider>
);
}