import { IUser } from 'app/shared/model/user.model';
import { IMeeting } from 'app/shared/model/meeting.model';
import { ParticipantRole } from 'app/shared/model/enumerations/participant-role.model';
import { AttendanceStatus } from 'app/shared/model/enumerations/attendance-status.model';

export interface IMeetingParticipant {
  id?: number;
  role?: keyof typeof ParticipantRole;
  isRequired?: boolean;
  attendance?: keyof typeof AttendanceStatus;
  absentReason?: string | null;
  user?: IUser;
  meeting?: IMeeting;
}

export const defaultValue: Readonly<IMeetingParticipant> = {
  isRequired: false,
};
