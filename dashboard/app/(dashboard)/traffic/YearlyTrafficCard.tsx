import { Card, CardActions, CardContent } from '@mui/material';
import Typography from '@mui/material/Typography';
import YearRangePicker from '@/components/YearRangePicker';
import * as React from 'react';
import { useEffect } from 'react';
import { TrafficResponse } from '@/api/types';
import { getYearlyTraffic } from '@/app/(dashboard)/traffic/actions';
import YearlyTrafficChart from '@/components/traffic/YearlyTrafficChart';

export default function YearlyTrafficCard() {
  const [startYear, setStartYear] = React.useState<number | null>(2021);
  const [endYear, setEndYear] = React.useState<number | null>(2024);
  const [trafficYearlyData, setTrafficYearlyData] = React.useState<TrafficResponse.YearlyTraffic[]>([]);

  useEffect(() => {
    async function fetchData() {
      const res = await getYearlyTraffic(startYear!, endYear!);
      if (res.code === 0) {
        setTrafficYearlyData(res.data);
      }
    }

    if (startYear && endYear) {
      fetchData();
    }
  }, [startYear, endYear]);

  return (
    <Card variant="outlined" sx={{ p: 2 }}>
      <Typography variant="h6" gutterBottom>
        年流量数据
      </Typography>
      <CardContent>
        <YearlyTrafficChart data={trafficYearlyData} />
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
