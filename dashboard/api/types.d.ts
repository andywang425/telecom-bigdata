export interface BaseResponse<T> {
  code: number;
  data: T;
  message: string;
}

export namespace AuthResponse {
  interface Login {
    id: number;
    email: string;
    createdAt: string;
    accessToken: string;
    expiresAt: string;
    refreshToken: string;
  }

  interface RefreshToken {
    accessToken: string;
    expiresAt: string;
    refreshToken: string;
  }
}

export namespace CallResponse {
  interface YearlyCalls extends Record<string, number> {
    year: number;
    totalCalls: number;
    totalCallDuration: number;
  }

  interface MonthlyCalls extends Record<string, number> {
    month: number;
    totalCalls: number;
    totalCallDuration: number;
  }

  interface CallStatus extends Record<string, number | string> {
    callCount: number;
    callStatus: string;
  }

  interface CallDistribution extends Record<string, number> {
    hour: number;
    callCount: number;
  }
}

export namespace SmsResponse {
  interface YearlySms extends Record<string, number> {
    year: number;
    totalCount: number;
    totalLength: number;
  }

  interface MonthlySms extends Record<string, number> {
    month: number;
    totalCount: number;
    totalLength: number;
  }

  interface SmsStatus extends Record<string, number | string> {
    sendStatus: number;
    smsCount: number;
  }

  interface SmsDistribution extends Record<string, number> {
    hour: number;
    smsCount: number;
  }
}

export namespace TrafficResponse {
  interface YearlyTraffic extends Record<string, number> {
    year: number;
    totalSessions: number;
    totalDuration: number;
    totalUpstream: number;
    totalDownstream: number;
  }

  interface MonthlyTraffic extends Record<string, number> {
    month: number;
    totalSessions: number;
    totalDuration: number;
    totalUpstream: number;
    totalDownstream: number;
  }

  interface ApplicationType extends Record<string, number | string> {
    applicationType: string;
    sessionCount: number;
    totalUpstreamDataVolume: number;
    totalDownstreamDataVolume: number;
  }

  interface NetworkTechnology extends Record<string, number | string> {
    networkTechnology: string;
    sessionCount: number;
    totalUpstreamDataVolume: number;
    totalDownstreamDataVolume: number;
  }

  interface TrafficDistribution extends Record<string, number> {
    hour: number;
    sessionCount: number;
    totalUpstream: number;
    totalDownstream: number;
  }
}

export namespace StationResponse {
  interface YearlyFailureRate extends Record<string, number> {
    year: number;
    callFailureRate: number;
    smsFailureRate: number;
  }

  interface MonthlyFailureRate extends Record<string, number> {
    month: number;
    callFailureRate: number;
    smsFailureRate: number;
  }
}

export namespace UserResponse {
  interface UserCluster extends Record<string, string | number> {
    phone: string;
    cluster: number;
    pcaX: number;
    pcaY: number;
  }

  interface UserClusterCount extends Record<string, number> {
    cluster: number;
    count: number;
    percentage: number;
  }
}
