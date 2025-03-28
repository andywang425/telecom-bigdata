'use client';
import * as React from 'react';
import { SignInPage } from '@toolpad/core/SignInPage';
import { providerMap } from '@/auth';
import signIn from './actions';
import Typography from "@mui/material/Typography";
import theme from "@/theme";

function Title() {
  return (
      <Typography variant="h5" sx={{
          my: 1,
          fontWeight: theme.typography.fontWeightBold,
          color: theme.palette.text.primary
      }} >电信数据可视化</Typography>
  )
}

export default function SignIn() {
  return (
    <SignInPage
      providers={providerMap}
      signIn={signIn}
      slots={{
        title: Title
      }}
      slotProps={{ emailField: { autoFocus: false }, form: { noValidate: true } }}
      sx={{
        '& .MuiInputLabel-root': {
          fontSize: '0.75rem',
        },
      }}
    />
  );
}
