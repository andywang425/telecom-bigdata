import * as React from 'react';
import Typography from '@mui/material/Typography';
import { auth } from '@/auth';
import { Box, Divider, List, ListItemButton, ListItemIcon, ListItemText } from '@mui/material';
import CodeIcon from '@mui/icons-material/Code';
import NumbersIcon from '@mui/icons-material/Numbers';
import Grid from '@mui/material/Grid';
import packageInfo from '@/package.json';
import DevicesIcon from '@mui/icons-material/Devices';
import DvrIcon from '@mui/icons-material/Dvr';

export default async function HomePage() {
  const session = await auth();

  return (
    <Box>
      <Grid container spacing={1}>
        <Grid size={12} sx={{ mb: 1 }}>
          <Typography>欢迎访问电信数据可视化, {session?.user?.name || '用户'}!</Typography>
        </Grid>
        <Grid size={12}>
          <Typography variant={'h5'}>网站信息</Typography>
        </Grid>
        <Grid size={5}>
          <List disablePadding>
            <ListItemButton>
              <ListItemIcon>
                <NumbersIcon />
              </ListItemIcon>
              <ListItemText primary="版本：" />
              <ListItemText
                sx={{
                  textAlign: 'right',
                }}
                primary={packageInfo.version}
              />
            </ListItemButton>
            <Divider component="li" />
            <ListItemButton>
              <ListItemIcon>
                <CodeIcon />
              </ListItemIcon>
              <ListItemText primary="是否为正式版：" />
              <ListItemText
                sx={{
                  textAlign: 'right',
                }}
                primary={process.env.NODE_ENV === 'production' ? '是' : '否'}
              />
            </ListItemButton>
          </List>
        </Grid>
        <Grid size={12}>
          <Typography variant={'h5'}>依赖信息</Typography>
        </Grid>
        <Grid
          size={5}
          sx={{
            mr: 5,
          }}
        >
          <Typography variant={'h6'}>dependencies</Typography>
          <List disablePadding>
            {Object.keys(packageInfo.dependencies).map(key => (
              <React.Fragment key={key}>
                <ListItemButton>
                  <ListItemIcon>
                    <DevicesIcon />
                  </ListItemIcon>
                  <ListItemText primary={key} />
                  <ListItemText
                    sx={{
                      textAlign: 'right',
                    }}
                    primary={(packageInfo.dependencies as Record<string, string>)[key]}
                  />
                </ListItemButton>
                <Divider component="li" />
              </React.Fragment>
            ))}
          </List>
        </Grid>
        <Grid size={5}>
          <Typography variant={'h6'}>devDependencies</Typography>
          <List disablePadding>
            {Object.keys(packageInfo.devDependencies).map(key => (
              <React.Fragment key={key}>
                <ListItemButton>
                  <ListItemIcon>
                    <DvrIcon />
                  </ListItemIcon>
                  <ListItemText primary={key} />
                  <ListItemText
                    sx={{
                      textAlign: 'right',
                    }}
                    primary={(packageInfo.devDependencies as Record<string, string>)[key]}
                  />
                </ListItemButton>
                <Divider component="li" />
              </React.Fragment>
            ))}
          </List>
        </Grid>
      </Grid>
    </Box>
  );
}
