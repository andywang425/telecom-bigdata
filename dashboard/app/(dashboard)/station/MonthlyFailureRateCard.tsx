import { Card, CardActions, CardContent } from '@mui/material';
import Typography from '@mui/material/Typography';
import * as React from 'react';
import { useEffect } from 'react';
import { StationResponse } from '@/api/types';
import { DatePicker } from '@mui/x-date-pickers-pro';
import { DateTime } from 'luxon';
import { getMonthlyFailureRate } from '@/app/(dashboard)/station/actions';
import MonthlyFailureRateChart from '@/components/station/MonthlyFailureRateChart';

export default function MonthlyFailureRateCard() {
  const [year, setYear] = React.useState<number | null>(2022);
  const [failureRateMonthlyData, setFailureRateMonthlyData] = React.useState<StationResponse.MonthlyFailureRate[]>([]);

  useEffect(() => {
    async function fetchData() {
      const res = await getMonthlyFailureRate(year!);
      if (res.code === 0) {
        setFailureRateMonthlyData(res.data);
      }
    }

    if (year) {
      fetchData();
    }
  }, [year]);

  const handleYearChange = (newYear: DateTime<boolean> | null) => {
    const year = newYear ? newYear.year : null;
    setYear(year);
  };

  return (
    <Card variant="outlined" sx={{ p: 2 }}>
      <Typography variant="h6" gutterBottom>
        月基站故障率
      </Typography>
      <CardContent>
        <MonthlyFailureRateChart data={failureRateMonthlyData} />
      </CardContent>
      <CardActions>
        <DatePicker
          label={'年份'}
          views={['year']}
          value={year ? DateTime.fromObject({ year }) : null}
          onChange={handleYearChange}
          minDate={DateTime.fromObject({ year: 2000 })}
          disableFuture
        />
      </CardActions>
    </Card>
  );
}
