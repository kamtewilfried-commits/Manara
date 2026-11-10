export interface NotificationForm {
  recipientType: string;
  type: string;
  activityId: number | null;
  parentId: number | null;
  subject: string;
  message: string;
}