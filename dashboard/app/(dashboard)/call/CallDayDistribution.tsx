import { Card, CardActions, CardContent } from '@mui/material';
import Typography from '@mui/material/Typography';
import * as React from 'react';
import { useEffect } from 'react';
import { CallResponse } from '@/api/types';
import { getCallDistribution } from '@/app/(dashboard)/call/actions';
import { DatePicker } from '@mui/x-date-pickers-pro';
import { DateTime } from 'luxon';
import CallDayDistributionChart from '@/components/CallDayDistributionChart';

export default function CallDayDistribution() {
  const [date, setDate] = React.useState<DateTime | null>(DateTime.fromObject({ year: 2021, month: 1 }));
  const [callDistribution, setCallDistribution] = React.useState<CallResponse.CallDistribution[]>([]);

  useEffect(() => {
    async function fetchData() {
      const res = await getCallDistribution(date!.year, date!.month);
      if (res.code === 0) {
        setCallDistribution(res.data);
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
        通话日分布
      </Typography>
      <CardContent>
        <CallDayDistributionChart data={callDistribution} />
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
