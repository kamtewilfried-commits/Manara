export interface Registration {
  id: number;
  status: string;
  waitlist: boolean;
  present: boolean;
  createdAt: string;

  childId?: number;
  childFirstName?: string;
  childLastName?: string;
  childFullName?: string;
  childAllergies?: string;

  sessionId?: number;
  sessionLocation?: string;
  sessionStartDate?: string;
  sessionEndDate?: string;

  parentId?: number;
  parentFullName?: string;
  parentEmail?: string;
  parentPhone?: string;

  confirmedById?: number;
  confirmedByFullName?: string;
}