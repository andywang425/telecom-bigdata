import { Card, CardActions, CardContent } from '@mui/material';
import Typography from '@mui/material/Typography';
import * as React from 'react';
import { useEffect } from 'react';
import { SmsResponse } from '@/api/types';
import { DatePicker } from '@mui/x-date-pickers-pro';
import { DateTime } from 'luxon';
import { getSmsDistribution } from '@/app/(dashboard)/sms/actions';
import SmsDayDistributionChart from '@/components/sms/SmsDayDistributionChart';

export default function SmsDayDistributionCard() {
  const [date, setDate] = React.useState<DateTime | null>(DateTime.fromObject({ year: 2021, month: 1 }));
  const [smsDistribution, setSmsDistribution] = React.useState<SmsResponse.SmsDistribution[]>([]);

  useEffect(() => {
    async function fetchData() {
      const res = await getSmsDistribution(date!.year, date!.month);
      if (res.code === 0) {
        setSmsDistribution(res.data);
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
        短信日分布
      </Typography>
      <CardContent>
        <SmsDayDistributionChart data={smsDistribution} />
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
