import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';
import { IMeeting } from 'app/shared/model/meeting.model';
import { TaskType } from 'app/shared/model/enumerations/task-type.model';
import { TaskStatus } from 'app/shared/model/enumerations/task-status.model';

export interface IMeetingTask {
  id?: number;
  type?: keyof typeof TaskType;
  title?: string;
  description?: string | null;
  dueAt?: dayjs.Dayjs | null;
  status?: keyof typeof TaskStatus;
  remindBeforeMinutes?: number | null;
  assignee?: IUser | null;
  assignedBy?: IUser | null;
  meeting?: IMeeting;
}

export const defaultValue: Readonly<IMeetingTask> = {};
