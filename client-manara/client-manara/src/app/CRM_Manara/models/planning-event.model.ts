export interface PlanningEvent {
  activityName?: string;
  childName?: string;
  location?: string;
  status?: string;
  slot?: string;
  day?: string;

  startDate: string;
  endDate: string;
}