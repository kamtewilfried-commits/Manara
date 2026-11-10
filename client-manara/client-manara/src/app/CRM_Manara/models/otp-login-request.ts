export interface OtpLoginRequest {
  userId: number;
  key: string;
  otp: string;
}