'use server';
import USER from '@/api/user';

export async function getCluster() {
  return USER.getCluster();
}

export async function getClusterCount() {
  return USER.getClusterCount();
}
