'use server';
import CALL from '@/api/call';
export async function getYearlyCalls(startYear: number, endYear: number) {
  return CALL.getYearlyCalls(startYear, endYear);
}

export async function getMonthlyCalls(year: number) {
  return CALL.getMonthlyCalls(year);
}
