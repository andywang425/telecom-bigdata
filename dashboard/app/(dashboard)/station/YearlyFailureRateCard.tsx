import { Card, CardActions, CardContent } from '@mui/material';
import Typography from '@mui/material/Typography';
import YearRangePicker from '@/components/YearRangePicker';
import * as React from 'react';
import { useEffect } from 'react';
import { StationResponse } from '@/api/types';
import { getYearlyFailureRate } from '@/app/(dashboard)/station/actions';
import YearlyFailureRateChart from '@/components/station/YearlyFailureRateChart';

export default function YearlyFailureRateCard() {
  const [startYear, setStartYear] = React.useState<number | null>(2021);
  const [endYear, setEndYear] = React.useState<number | null>(2024);
  const [failureRateYearlyData, setFailureRateYearlyData] = React.useState<StationResponse.YearlyFailureRate[]>([]);

  useEffect(() => {
    async function fetchData() {
      const res = await getYearlyFailureRate(startYear!, endYear!);
      if (res.code === 0) {
        setFailureRateYearlyData(res.data);
      }
    }

    if (startYear && endYear) {
      fetchData();
    }
  }, [startYear, endYear]);

  return (
    <Card variant="outlined" sx={{ p: 2 }}>
      <Typography variant="h6" gutterBottom>
        年基站故障率
      </Typography>
      <CardContent>
        <YearlyFailureRateChart data={failureRateYearlyData} />
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
