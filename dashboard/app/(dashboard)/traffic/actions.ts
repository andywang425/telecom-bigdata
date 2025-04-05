'use server';
import TRAFFIC from '@/api/traffic';
export async function getYearlyTraffic(startYear: number, endYear: number) {
  return TRAFFIC.getYearlyTraffic(startYear, endYear);
}

export async function getMonthlyTraffic(year: number) {
  return TRAFFIC.getMonthlyTraffic(year);
}
//
// export async function getSmsStatus(year: number, month: number) {
//   return SMS.getSmsStatus(year, month);
// }
//
// export async function getSmsDistribution(year: number, month: number) {
//   return SMS.getSmsDistribution(year, month);
// }
