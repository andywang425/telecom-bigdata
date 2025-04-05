import { Card, CardActions, CardContent } from '@mui/material';
import Typography from '@mui/material/Typography';
import * as React from 'react';
import { useEffect } from 'react';
import { SmsResponse } from '@/api/types';
import { DatePicker } from '@mui/x-date-pickers-pro';
import { DateTime } from 'luxon';
import MonthlySmsChart from '@/components/MonthlySmsChart';
import { getMonthlySms } from '@/app/(dashboard)/sms/actions';

export default function MonthlySmsCard() {
  const [year, setYear] = React.useState<number | null>(2022);
  const [smsMonthlyData, setSmsMonthlyData] = React.useState<SmsResponse.MonthlySms[]>([]);

  useEffect(() => {
    async function fetchData() {
      const res = await getMonthlySms(year!);
      if (res.code === 0) {
        setSmsMonthlyData(res.data);
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
        月短信数据
      </Typography>
      <CardContent>
        <MonthlySmsChart data={smsMonthlyData} />
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
