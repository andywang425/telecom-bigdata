'use server';
import USER from '@/api/user';
export async function getCluster() {
  return USER.getCluster();
}
