'use server';
import STATION from '@/api/station';
export async function getYearlyFailureRate(startYear: number, endYear: number) {
  return STATION.getYearlyFailureRate(startYear, endYear);
}

export async function getMonthlyFailureRate(year: number) {
  return STATION.getMonthlyFailureRate(year);
}
