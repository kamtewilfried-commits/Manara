import { User } from './user.model';

export interface Notification {
  id: number;
  user?: User;
  userId?: number;
  userFullName?: string;
  userEmail?: string;
  subject: string;
  message: string;
  type: string;
  isRead: boolean;
  emailSent: boolean;
  sentAt: string;
}