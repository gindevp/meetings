import { IMeeting } from 'app/shared/model/meeting.model';

export interface IAgendaItem {
  id?: number;
  itemOrder?: number;
  topic?: string;
  presenterName?: string | null;
  durationMinutes?: number | null;
  note?: string | null;
  meeting?: IMeeting;
}

export const defaultValue: Readonly<IAgendaItem> = {};
