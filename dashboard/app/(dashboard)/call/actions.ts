'use server';
import CALL from '@/api/call';
export async function getYearlyCalls(startYear: number, endYear: number) {
  return CALL.getYearlyCalls(startYear, endYear);
}

export async function getMonthlyCalls(year: number) {
  return CALL.getMonthlyCalls(year);
}

export async function getCallStatus(year: number, month: number) {
  return CALL.getCallStatus(year, month);
}

export async function getCallDistribution(year: number, month: number) {
  return CALL.getCallDistribution(year, month);
}
