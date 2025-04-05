import { Card, CardActions, CardContent } from '@mui/material';
import Typography from '@mui/material/Typography';
import * as React from 'react';
import { useEffect } from 'react';
import { CallResponse } from '@/api/types';
import { getMonthlyCalls } from '@/app/(dashboard)/call/actions';
import MonthlyCallsChart from '@/components/MonthlyCallsChart';
import { DatePicker } from '@mui/x-date-pickers-pro';
import { DateTime } from 'luxon';

export default function MonthlyCallsCard() {
  const [year, setYear] = React.useState<number | null>(2022);
  const [callMonthlyData, setCallMonthlyData] = React.useState<CallResponse.MonthlyCalls[]>([]);

  useEffect(() => {
    async function fetchData() {
      const res = await getMonthlyCalls(year!);
      if (res.code === 0) {
        setCallMonthlyData(res.data);
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
        月通话数据
      </Typography>
      <CardContent>
        <MonthlyCallsChart data={callMonthlyData} />
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
