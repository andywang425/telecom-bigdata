'use server';
import { AuthError } from 'next-auth';
import type { AuthProvider } from '@toolpad/core';
import { signIn } from '@/auth';

export default async function serverSignIn(provider: AuthProvider, formData: FormData, callbackUrl?: string) {
  try {
    return await signIn(provider.id, {
      ...(formData && { email: formData.get('email'), password: formData.get('password') }),
      redirectTo: callbackUrl ?? '/',
    });
  } catch (error) {
    if (error instanceof Error && error.message === 'NEXT_REDIRECT') {
      throw error;
    }
    if (error instanceof AuthError) {
      return {
        error: error.type === 'CredentialsSignin' ? '邮箱或密码错误' : 'Auth.js 内部错误，请联系管理员',
        type: error.type,
      };
    }
    return {
      error: '未知错误，请联系管理员',
      type: 'UnknownError',
    };
  }
}
