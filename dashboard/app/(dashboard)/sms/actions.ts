'use server';
import SMS from '@/api/sms';
export async function getYearlySms(startYear: number, endYear: number) {
  return SMS.getYearlySms(startYear, endYear);
}

export async function getMonthlySms(year: number) {
  return SMS.getMonthlySms(year);
}

export async function getSmsStatus(year: number, month: number) {
  return SMS.getSmsStatus(year, month);
}

export async function getSmsDistribution(year: number, month: number) {
  return SMS.getSmsDistribution(year, month);
}
