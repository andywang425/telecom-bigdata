import { Card, CardActions, CardContent } from '@mui/material';
import Typography from '@mui/material/Typography';
import YearlyCallsChart from '@/components/YearlyCallsChart';
import YearRangePicker from '@/components/YearRangePicker';
import * as React from 'react';
import { CallResponse } from '@/api/types';
import { useEffect } from 'react';
import { getYearlyCalls } from '@/app/(dashboard)/call/actions';

export default function YearlyCallsCard() {
  const [startYear, setStartYear] = React.useState<number | null>(2021);
  const [endYear, setEndYear] = React.useState<number | null>(2024);
  const [callYearlyData, setCallYearlyData] = React.useState<CallResponse.YearlyCalls[]>([]);

  useEffect(() => {
    async function fetchData() {
      const res = await getYearlyCalls(startYear!, endYear!);
      if (res.code === 0) {
        setCallYearlyData(res.data);
      }
    }

    if (startYear && endYear) {
      fetchData();
    }
  }, [startYear, endYear]);

  return (
    <Card variant="outlined" sx={{ p: 2 }}>
      <Typography variant="h6" gutterBottom>
        年通话数据
      </Typography>
      <CardContent>
        <YearlyCallsChart data={callYearlyData} />
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
