import * as React from 'react';
import Typography from '@mui/material/Typography';
import { auth } from '@/auth';
import { Box } from '@mui/material';

export default async function HomePage() {
  const session = await auth();

  return (
    <Box>
      <Typography>欢迎访问电信数据可视化, {session?.user?.name || 'User'}!</Typography>
    </Box>
  );
}
