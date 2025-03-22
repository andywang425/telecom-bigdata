'use client';
import * as React from 'react';
import Link from '@mui/material/Link';
import Alert from '@mui/material/Alert';
import { SignInPage } from '@toolpad/core/SignInPage';
import { providerMap } from '@/auth';
import signIn from './actions';

function ForgotPasswordLink() {
  return (
    <span>
      <Link fontSize="0.75rem" href="/auth/forgot-password">
        忘记密码?
      </Link>
    </span>
  );
}

function SignUpLink() {
  return (
    <span style={{ fontSize: '0.8rem' }}>
      没有账号?&nbsp;<Link href="/auth/signup">注册</Link>
    </span>
  );
}

function DemoInfo() {
  return (
    <Alert severity="info">
      可用账号 <strong>admin@telecom.com</strong> 和密码 <strong>admin123</strong> 登录
    </Alert>
  );
}

export default function SignIn() {
  return (
    <SignInPage
      providers={providerMap}
      signIn={signIn}
      slots={{
        forgotPasswordLink: ForgotPasswordLink,
        signUpLink: SignUpLink,
        subtitle: DemoInfo,
      }}
    />
  );
}
