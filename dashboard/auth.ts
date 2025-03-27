import NextAuth from 'next-auth';
import Credentials from 'next-auth/providers/credentials';
import Keycloak from 'next-auth/providers/keycloak';
import type { Provider } from 'next-auth/providers';
import { AUTH } from '@/api';

const providers: Provider[] = [
  Credentials({
    credentials: {
      email: { label: 'Email Address', type: 'email' },
      password: { label: 'Password', type: 'password' },
    },
    async authorize(c) {
      const res = await AUTH.login(<string>c.email, <string>c.password);

      if (res.code === 0) {
        const user = res.data

        return {
          id: `${user.id}`,
          name: user.email.split('@').shift(),
          email: user.email,
          image: (await import('@/public/image/avatar.jpg')).default.src
        }
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

      if (isPublicPage || isLoggedIn) {
        return true;
      }

      return false; // Redirect unauthenticated users to login page
    },
  },
});
