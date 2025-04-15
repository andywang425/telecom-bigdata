import { Card, CardContent } from '@mui/material';
import Typography from '@mui/material/Typography';
import * as React from 'react';
import { useEffect } from 'react';
import { UserResponse } from '@/api/types';
import { getClusterCount } from '@/app/(dashboard)/user/actions';
import UserCountChart from '@/components/user/UserCountChart';

export default function SmsStatusCard() {
  const [userClusterCountData, setUserClusterCountData] = React.useState<UserResponse.UserClusterCount[]>([]);

  useEffect(() => {
    async function fetchData() {
      const res = await getClusterCount();
      if (res.code === 0) {
        setUserClusterCountData(res.data);
      }
    }

    fetchData();
  }, []);

  return (
    <Card variant="outlined" sx={{ p: 2 }}>
      <Typography variant="h6" gutterBottom>
        各类用户占比
      </Typography>
      <CardContent>
        <UserCountChart data={userClusterCountData} />
      </CardContent>
    </Card>
  );
}
