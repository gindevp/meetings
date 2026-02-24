import dayjs from 'dayjs';
import { IMeetingType } from 'app/shared/model/meeting-type.model';
import { IMeetingLevel } from 'app/shared/model/meeting-level.model';
import { IDepartment } from 'app/shared/model/department.model';
import { IRoom } from 'app/shared/model/room.model';
import { IUser } from 'app/shared/model/user.model';
import { MeetingMode } from 'app/shared/model/enumerations/meeting-mode.model';
import { MeetingStatus } from 'app/shared/model/enumerations/meeting-status.model';

export interface IMeeting {
  id?: number;
  title?: string;
  startTime?: dayjs.Dayjs;
  endTime?: dayjs.Dayjs;
  mode?: keyof typeof MeetingMode;
  onlineLink?: string | null;
  objectives?: string | null;
  note?: string | null;
  status?: keyof typeof MeetingStatus;
  createdAt?: dayjs.Dayjs;
  submittedAt?: dayjs.Dayjs | null;
  approvedAt?: dayjs.Dayjs | null;
  canceledAt?: dayjs.Dayjs | null;
  type?: IMeetingType;
  level?: IMeetingLevel;
  organizerDepartment?: IDepartment;
  room?: IRoom | null;
  requester?: IUser;
  host?: IUser;
}

export const defaultValue: Readonly<IMeeting> = {};
