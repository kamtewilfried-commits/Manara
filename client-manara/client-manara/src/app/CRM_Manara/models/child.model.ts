import { Registration } from './registration.model';
import { User } from './user.model';


export interface Child {
  id: number;
  firstName: string;
  lastName: string;
  birthDate: string;
  parent: User;
  registrations: Registration[];
  allergies: string;
  medicalNotes: string;
}