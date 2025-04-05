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
import { getNetworkTechnology } from '@/app/(dashboard)/traffic/actions';
import { TrafficApplicationTypeChartProps } from '@/components/traffic/TrafficApplicationTypeChart';
import TrafficNetworkTechnologyChart from '@/components/traffic/TrafficNetworkTechnologyChart';

export default function TrafficNetworkTechnologyCard() {
  const [date, setDate] = React.useState<DateTime | null>(DateTime.fromObject({ year: 2021, month: 1 }));
  const [valueField, setValueField] = React.useState<TrafficApplicationTypeChartProps['valueField']>('sessionCount');
  const [trafficNetworkTechData, setTrafficNetworkTechData] = React.useState<TrafficResponse.NetworkTechnology[]>([]);

  useEffect(() => {
    async function fetchData() {
      const res = await getNetworkTechnology(date!.year, date!.month);
      if (res.code === 0) {
        setTrafficNetworkTechData(res.data);
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
    setValueField(event.target.value as TrafficApplicationTypeChartProps['valueField']);
  };

  return (
    <Card variant="outlined" sx={{ p: 2 }}>
      <Typography variant="h6" gutterBottom>
        流量网络技术分布
      </Typography>
      <CardContent>
        <TrafficNetworkTechnologyChart data={trafficNetworkTechData} valueField={valueField} />
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
            <MenuItem value={'sessionCount'}>会话数量</MenuItem>
            <MenuItem value={'totalUpstreamDataVolume'}>上行流量（KB）</MenuItem>
            <MenuItem value={'totalDownstreamDataVolume'}>下行流量（KB）</MenuItem>
          </Select>
        </FormControl>
      </CardActions>
    </Card>
  );
}
