import { Session } from './session.model';
import { User } from './user.model';

export interface Activity {
  id?: number;
  name: string;
  description?: string;
  categorie?: string;
   photo?: string;
  active: boolean;
 prixHoraire?: number;
  animateurId?: number;
  animateurFullName?: string;
  animateurEmail?: string;

  sessionCount?: number;
  sessions?: Session[];
}