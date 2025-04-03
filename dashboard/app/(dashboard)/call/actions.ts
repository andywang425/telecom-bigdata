'use server';
import CALL from '@/api/call';
export async function getYearlyCalls(startYear: number, endYear: number) {
  return CALL.getYearlyCalls(startYear, endYear);
}
