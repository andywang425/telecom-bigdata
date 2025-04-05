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
