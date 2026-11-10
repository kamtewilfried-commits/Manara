import { User } from "./user.model";

export interface LoginResponse {
  success: boolean;
  requiresOtp: boolean;
  message: string;

  user?: User; 
  userId?: number;
  role?: string;
}