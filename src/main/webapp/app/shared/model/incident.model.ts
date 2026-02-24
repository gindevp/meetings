import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';
import { IMeeting } from 'app/shared/model/meeting.model';

export interface IIncident {
  id?: number;
  title?: string;
  description?: string | null;
  reportedAt?: dayjs.Dayjs;
  severity?: string | null;
  status?: string | null;
  reportedBy?: IUser;
  meeting?: IMeeting;
}

export const defaultValue: Readonly<IIncident> = {};
