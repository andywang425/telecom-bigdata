import * as React from 'react';
import { NextAppProvider } from '@toolpad/core/nextjs';
import { AppRouterCacheProvider } from '@mui/material-nextjs/v15-appRouter';
import DashboardIcon from '@mui/icons-material/Dashboard';
import CallIcon from '@mui/icons-material/Call';
import SmsIcon from '@mui/icons-material/Sms';
import FiveGIcon from '@mui/icons-material/FiveG';
import CellTowerIcon from '@mui/icons-material/CellTower';

import type { Navigation } from '@toolpad/core/AppProvider';
import { SessionProvider, signIn, signOut } from 'next-auth/react';
import { auth } from '@/auth';
import theme from '@/theme';
import { Metadata } from 'next';
import Image from 'next/image';
import logo from '@/public/image/logo.svg';
import MuiXLicense from '@/components/MuiXLicense';

export const metadata: Metadata = {
  title: '电信数据可视化',
  description: '毕业设计',
};

const NAVIGATION: Navigation = [
  {
    segment: '',
    title: '首页',
    icon: <DashboardIcon />,
  },
  {
    kind: 'header',
    title: '可视化图表',
  },
  {
    segment: 'call',
    title: '通话数据',
    icon: <CallIcon />,
  },
  {
    segment: 'sms',
    title: '短信数据',
    icon: <SmsIcon />,
  },
  {
    segment: 'traffic',
    title: '流量数据',
    icon: <FiveGIcon />,
  },
  {
    segment: 'station',
    title: '基站数据',
    icon: <CellTowerIcon />,
  },
];

const AUTHENTICATION = {
  signIn,
  signOut,
};

const BRANDING = {
  title: '电信数据可视化',
  logo: <Image src={logo} alt={'logo'} width={24} style={{ height: '100%' }} />,
};

export default async function RootLayout(props: { children: React.ReactNode }) {
  const session = await auth();

  return (
    <html lang="en" data-toolpad-color-scheme="light" suppressHydrationWarning>
      <MuiXLicense />
      <body>
        <SessionProvider session={session}>
          <AppRouterCacheProvider options={{ enableCssLayer: true }}>
            <NextAppProvider
              navigation={NAVIGATION}
              session={session}
              authentication={AUTHENTICATION}
              branding={BRANDING}
              theme={theme}
            >
              {props.children}
            </NextAppProvider>
          </AppRouterCacheProvider>
        </SessionProvider>
      </body>
    </html>
  );
}
