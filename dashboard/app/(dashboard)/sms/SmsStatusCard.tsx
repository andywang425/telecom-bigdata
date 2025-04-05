import { Card, CardActions, CardContent } from '@mui/material';
import Typography from '@mui/material/Typography';
import * as React from 'react';
import { useEffect } from 'react';
import { SmsResponse } from '@/api/types';
import { DatePicker } from '@mui/x-date-pickers-pro';
import { DateTime } from 'luxon';
import SmsStatusChart from '@/components/SmsStatusChart';
import { getSmsStatus } from '@/app/(dashboard)/sms/actions';

export default function SmsStatusCard() {
  const [date, setDate] = React.useState<DateTime | null>(DateTime.fromObject({ year: 2021, month: 1 }));
  const [smsStatusData, setSmsStatusData] = React.useState<SmsResponse.SmsStatus[]>([]);

  useEffect(() => {
    async function fetchData() {
      const res = await getSmsStatus(date!.year, date!.month);
      if (res.code === 0) {
        setSmsStatusData(res.data);
      }
    }

    if (date) {
      fetchData();
    }
  }, [date]);

  const handleDateChange = (newDate: DateTime | null) => {
    setDate(newDate);
  };

  return (
    <Card variant="outlined" sx={{ p: 2 }}>
      <Typography variant="h6" gutterBottom>
        短信状态
      </Typography>
      <CardContent>
        <SmsStatusChart data={smsStatusData} />
      </CardContent>
      <CardActions>
        <DatePicker
          label={'月份 年份'}
          views={['year', 'month']}
          value={date}
          onChange={handleDateChange}
          minDate={DateTime.fromObject({ year: 2000 })}
          disableFuture
        />
      </CardActions>
    </Card>
  );
}
