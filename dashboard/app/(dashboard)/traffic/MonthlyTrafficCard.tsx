import { Card, CardActions, CardContent } from '@mui/material';
import Typography from '@mui/material/Typography';
import * as React from 'react';
import { useEffect } from 'react';
import { TrafficResponse } from '@/api/types';
import { DatePicker } from '@mui/x-date-pickers-pro';
import { DateTime } from 'luxon';
import { getMonthlyTraffic } from '@/app/(dashboard)/traffic/actions';
import MonthlyTrafficChart from '@/components/traffic/MonthlyTrafficChart';

export default function MonthlyTrafficCard() {
  const [year, setYear] = React.useState<number | null>(2022);
  const [trafficMonthlyData, setTrafficMonthlyData] = React.useState<TrafficResponse.MonthlyTraffic[]>([]);

  useEffect(() => {
    async function fetchData() {
      const res = await getMonthlyTraffic(year!);
      if (res.code === 0) {
        setTrafficMonthlyData(res.data);
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
        月流量数据
      </Typography>
      <CardContent>
        <MonthlyTrafficChart data={trafficMonthlyData} />
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
