import * as React from 'react';
import { NextAppProvider } from '@toolpad/core/nextjs';
import { AppRouterCacheProvider } from '@mui/material-nextjs/v15-appRouter';
import DashboardIcon from '@mui/icons-material/Dashboard';
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';

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
    icon: <DashboardIcon />,
  },
  {
    segment: 'sms',
    title: '短信数据',
    icon: <ShoppingCartIcon />,
  },
  {
    segment: 'traffic',
    title: '流量数据',
    icon: <DashboardIcon />,
  },
  {
    segment: 'station',
    title: '基站数据',
    icon: <ShoppingCartIcon />,
  },
];

const AUTHENTICATION = {
  signIn,
  signOut,
};

const BRANDING = {
  title: '电信数据可视化',
  logo: <Image src={logo} alt={'logo'} style={{ width: '100%', height: '100%' }} />,
};

export default async function RootLayout(props: { children: React.ReactNode }) {
  const session = await auth();

  return (
    <html lang="en" data-toolpad-color-scheme="light" suppressHydrationWarning>
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
              <MuiXLicense />
            </NextAppProvider>
          </AppRouterCacheProvider>
        </SessionProvider>
      </body>
    </html>
  );
}
