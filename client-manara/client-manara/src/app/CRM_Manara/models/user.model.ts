export type UserRole = 'PARENT' | 'ANIMATEUR' | 'ADMIN';

export interface User {
  id: number;

  firstName: string;
  lastName: string;
  email: string;

  phone?: string;

  role: UserRole;
password: string;
  active: boolean;

  createdAt: string|Date;
}