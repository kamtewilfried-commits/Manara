import { Activity } from './activity.model';

export type SessionStatus =
  | 'PLANNED'
  | 'IN_PROGRESS'
  | 'COMPLETED'
  | 'CANCELLED';

export interface Session {
  id?: number;

  startDate: string;
  endDate: string;

  capacity: number;
  availableSpots?: number;

  location: string;
  status: SessionStatus;

  prixSession?: number;

  activityId?: number;
  activityName?: string;
  activity?: Activity;

  animateurId?: number;
  animateurName?: string;
}