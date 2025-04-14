import { Card, CardContent } from '@mui/material';
import Typography from '@mui/material/Typography';
import * as React from 'react';
import { UserResponse } from '@/api/types';
import UserClusterChart from '@/components/user/UserClusterChart';
import { getCluster } from '@/app/(dashboard)/user/actions';
import { useEffect } from 'react';

export default function UserClusterCard() {
  const [userClusterData, setUserClusterData] = React.useState<UserResponse.UserCluster[]>([]);

  useEffect(() => {
    async function fetchData() {
      const res = await getCluster();
      if (res.code === 0) {
        setUserClusterData(res.data);
      }
    }

    fetchData();
  }, []);

  return (
    <Card variant="outlined" sx={{ p: 2 }}>
      <Typography variant="h6" gutterBottom>
        用户聚类结果
      </Typography>
      <CardContent>
        <UserClusterChart data={userClusterData} />
      </CardContent>
    </Card>
  );
}
