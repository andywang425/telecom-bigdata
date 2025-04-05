import { Card, CardActions, CardContent } from '@mui/material';
import Typography from '@mui/material/Typography';
import YearRangePicker from '@/components/YearRangePicker';
import * as React from 'react';
import { useEffect } from 'react';
import { SmsResponse } from '@/api/types';
import YearlySmsChart from '@/components/YearlySmsChart';
import { getYearlySms } from '@/app/(dashboard)/sms/actions';

export default function YearlySmsCard() {
  const [startYear, setStartYear] = React.useState<number | null>(2021);
  const [endYear, setEndYear] = React.useState<number | null>(2024);
  const [smsYearlyData, setSmsYearlyData] = React.useState<SmsResponse.YearlySms[]>([]);

  useEffect(() => {
    async function fetchData() {
      const res = await getYearlySms(startYear!, endYear!);
      if (res.code === 0) {
        setSmsYearlyData(res.data);
      }
    }

    if (startYear && endYear) {
      fetchData();
    }
  }, [startYear, endYear]);

  return (
    <Card variant="outlined" sx={{ p: 2 }}>
      <Typography variant="h6" gutterBottom>
        年短信数据
      </Typography>
      <CardContent>
        <YearlySmsChart data={smsYearlyData} />
      </CardContent>
      <CardActions>
        <YearRangePicker
          startYear={startYear}
          endYear={endYear}
          onChangeAction={(newStartYear, newEndYear) => {
            setStartYear(newStartYear);
            setEndYear(newEndYear);
          }}
        />
      </CardActions>
    </Card>
  );
}
