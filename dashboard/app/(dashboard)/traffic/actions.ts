'use server';
import TRAFFIC from '@/api/traffic';

export async function getYearlyTraffic(startYear: number, endYear: number) {
  return TRAFFIC.getYearlyTraffic(startYear, endYear);
}

export async function getMonthlyTraffic(year: number) {
  return TRAFFIC.getMonthlyTraffic(year);
}

export async function getApplicationType(year: number, month: number) {
  return TRAFFIC.getApplicationType(year, month);
}

export async function getNetworkTechnology(year: number, month: number) {
  return TRAFFIC.getNetworkTechnology(year, month);
}

export async function getTrafficDistribution(year: number, month: number) {
  return TRAFFIC.getTrafficDistribution(year, month);
}
