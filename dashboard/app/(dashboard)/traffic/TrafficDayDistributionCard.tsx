import {
  Card,
  CardActions,
  CardContent,
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  SelectChangeEvent,
} from '@mui/material';
import Typography from '@mui/material/Typography';
import * as React from 'react';
import { useEffect } from 'react';
import { TrafficResponse } from '@/api/types';
import { DatePicker } from '@mui/x-date-pickers-pro';
import { DateTime } from 'luxon';
import { getTrafficDistribution } from '@/app/(dashboard)/traffic/actions';
import TrafficDayDistributionChart, {
  TrafficDayDistributionChartProps,
} from '@/components/traffic/TrafficDayDistributionChart';

export default function TrafficDayDistributionCard() {
  const [date, setDate] = React.useState<DateTime | null>(DateTime.fromObject({ year: 2021, month: 1 }));
  const [valueField, setValueField] = React.useState<TrafficDayDistributionChartProps['valueField']>('dataVolume');
  const [trafficDistribution, setTrafficDistribution] = React.useState<TrafficResponse.TrafficDistribution[]>([]);

  useEffect(() => {
    async function fetchData() {
      const res = await getTrafficDistribution(date!.year, date!.month);
      if (res.code === 0) {
        setTrafficDistribution(res.data);
      }
    }

    if (date) {
      fetchData();
    }
  }, [date]);

  const handleDateChange = (newDate: DateTime | null) => {
    setDate(newDate);
  };

  const handleValueFieldChange = (event: SelectChangeEvent) => {
    setValueField(event.target.value as TrafficDayDistributionChartProps['valueField']);
  };

  return (
    <Card variant="outlined" sx={{ p: 2 }}>
      <Typography variant="h6" gutterBottom>
        流量日分布
      </Typography>
      <CardContent>
        <TrafficDayDistributionChart data={trafficDistribution} valueField={valueField} />
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
        <FormControl sx={{ minWidth: 200 }}>
          <InputLabel id="select-label">数据类型</InputLabel>
          <Select labelId={'select-label'} value={valueField} label="数据类型" onChange={handleValueFieldChange}>
            <MenuItem value={'session'}>会话</MenuItem>
            <MenuItem value={'dataVolume'}>流量</MenuItem>
          </Select>
        </FormControl>
      </CardActions>
    </Card>
  );
}
