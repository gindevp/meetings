import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';
import { IMeeting } from 'app/shared/model/meeting.model';
import { ApprovalDecision } from 'app/shared/model/enumerations/approval-decision.model';

export interface IMeetingApproval {
  id?: number;
  step?: number;
  decision?: keyof typeof ApprovalDecision;
  reason?: string | null;
  decidedAt?: dayjs.Dayjs;
  decidedBy?: IUser;
  meeting?: IMeeting;
}

export const defaultValue: Readonly<IMeetingApproval> = {};
