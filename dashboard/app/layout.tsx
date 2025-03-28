import * as React from 'react';
import { NextAppProvider } from '@toolpad/core/nextjs';
import { AppRouterCacheProvider } from '@mui/material-nextjs/v15-appRouter';
import DashboardIcon from '@mui/icons-material/Dashboard';
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';

import type { Navigation } from '@toolpad/core/AppProvider';
import { SessionProvider, signIn, signOut } from 'next-auth/react';
import { auth } from '@/auth';
import theme from '@/theme';
import {Metadata} from "next";
import Image from "next/image";
import logo from '@/public/image/logo.svg'

export const metadata: Metadata = {
  title: "电信数据可视化",
  description: "毕业设计",
};

const NAVIGATION: Navigation = [
  {
    kind: 'header',
    title: 'Main items',
  },
  {
    segment: '',
    title: 'Dashboard',
    icon: <DashboardIcon />,
  },
  {
    segment: 'orders',
    title: 'Orders',
    icon: <ShoppingCartIcon />,
  },
];

const AUTHENTICATION = {
  signIn,
  signOut,
};

const BRANDING = {
  logo: (
      <Image src={logo} alt={'logo'} width={24}  />
  )
};

export default async function RootLayout(props: { children: React.ReactNode }) {
  const session = await auth();

  return (
    <html lang="en" data-toolpad-color-scheme="light" suppressHydrationWarning>
      <body>
        <SessionProvider session={session}>
          <AppRouterCacheProvider options={{ enableCssLayer: true }}>
            <NextAppProvider navigation={NAVIGATION} session={session} authentication={AUTHENTICATION} branding={BRANDING} theme={theme}>
              {props.children}
            </NextAppProvider>
          </AppRouterCacheProvider>
        </SessionProvider>
      </body>
    </html>
  );
}
