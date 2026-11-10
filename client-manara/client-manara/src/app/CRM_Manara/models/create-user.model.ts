export interface CreateUser {
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  password: string;
  role: 'ANIMATEUR' | 'ADMIN';
  active: boolean;
}