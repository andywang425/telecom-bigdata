import { Card, CardActions, CardContent } from '@mui/material';
import Typography from '@mui/material/Typography';
import * as React from 'react';
import { useEffect } from 'react';
import { CallResponse } from '@/api/types';
import { getCallStatus } from '@/app/(dashboard)/call/actions';
import CallStatusChart from '@/components/CallStatusChart';
import { DatePicker } from '@mui/x-date-pickers-pro';
import { DateTime } from 'luxon';

export default function MonthlyCalls() {
  const [date, setDate] = React.useState<DateTime | null>(DateTime.fromObject({ year: 2021, month: 1 }));
  const [callStatusData, setCallStatusData] = React.useState<CallResponse.CallStatus[]>([]);

  useEffect(() => {
    async function fetchData() {
      const res = await getCallStatus(date!.year, date!.month);
      if (res.code === 0) {
        setCallStatusData(res.data);
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
        通话状态
      </Typography>
      <CardContent>
        <CallStatusChart data={callStatusData} />
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
