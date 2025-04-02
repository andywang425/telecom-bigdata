import NextAuth from 'next-auth';
import Credentials from 'next-auth/providers/credentials';
import Keycloak from 'next-auth/providers/keycloak';
import type { Provider } from 'next-auth/providers';
import AUTH from '@/api/auth';
import { DateTime } from 'luxon';
import type { Session, User } from 'next-auth';
import type { JWT } from 'next-auth/jwt';

let isRefreshingToken = false;

const providers: Provider[] = [
  Credentials({
    credentials: {
      email: { label: 'Email Address', type: 'email' },
      password: { label: 'Password', type: 'password' },
    },
    async authorize(c) {
      const res = await AUTH.login(<string>c.email, <string>c.password);

      if (res.code === 0) {
        const user = res.data;

        return {
          id: `${user.id}`,
          name: user.email.split('@').shift(),
          email: user.email,
          image: (await import('@/public/image/avatar.jpg')).default.src,
          accessToken: user.accessToken,
          expiresAt: user.expiresAt,
          refreshToken: user.refreshToken,
        };
      }

      return null;
    },
  }),

  Keycloak({
    clientId: process.env.KEYCLOAK_CLIENT_ID,
    clientSecret: process.env.KEYCLOAK_CLIENT_SECRET,
    issuer: process.env.KEYCLOAK_ISSUER,
  }),
];

export const providerMap = providers.map(provider => {
  if (typeof provider === 'function') {
    const providerData = provider();
    return { id: providerData.id, name: providerData.name };
  }
  return { id: provider.id, name: provider.name };
});

export const { handlers, auth, signIn, signOut } = NextAuth({
  providers,
  secret: process.env.AUTH_SECRET,
  pages: {
    signIn: '/auth/signin',
  },
  callbacks: {
    authorized({ auth: session, request: { nextUrl } }) {
      const isLoggedIn = !!session?.user;
      const isPublicPage = nextUrl.pathname.startsWith('/public');

      if (isPublicPage || (isLoggedIn && !session.error)) {
        return true;
      }

      return false; // Redirect unauthenticated users to login page
    },
    async jwt({ token, user, trigger }) {
      // console.log('jwt token', token, 'now', new Date(), 'user', user, 'trigger', trigger);
      if (trigger === 'signIn' && user && user.accessToken) {
        token.access_token = user.accessToken;
        token.expires_at = user.expiresAt;
        token.refresh_token = user.refreshToken;
      } else if (token && token.refresh_token && DateTime.now() > DateTime.fromISO(token.expires_at)) {
        if (isRefreshingToken) {
          // 避免重复刷新token
          return token;
        }
        isRefreshingToken = true;

        // access_token过期，使用refresh_token刷新
        const res = await AUTH.refreshToken(token.refresh_token!);
        console.log('AUTH.refreshToken response', res);

        if (res.code === 0) {
          token.access_token = res.data.accessToken;
          token.expires_at = res.data.expiresAt;
          token.refresh_token = res.data.refreshToken;
        } else {
          console.error('Error refreshing access_token', res.message);
          token.error = 'RefreshTokenError';
        }

        isRefreshingToken = false;
      }

      return token;
    },

    async session({ session, token }) {
      session.access_token = token.access_token;
      session.error = token.error;
      return session;
    },
  },
  session: {
    strategy: 'jwt',
  },
});

declare module 'next-auth' {
  interface Session {
    error?: 'RefreshTokenError';
    access_token: string;
  }

  interface User {
    accessToken: string;
    expiresAt: string;
    refreshToken: string;
  }
}

declare module 'next-auth/jwt' {
  interface JWT {
    access_token: string;
    expires_at: string;
    refresh_token?: string;
    error?: 'RefreshTokenError';
  }
}
