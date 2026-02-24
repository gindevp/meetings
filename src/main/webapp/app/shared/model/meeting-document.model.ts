import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';
import { IMeeting } from 'app/shared/model/meeting.model';

export interface IMeetingDocument {
  id?: number;
  docType?: string;
  fileName?: string;
  contentType?: string | null;
  fileContentType?: string | null;
  file?: string | null;
  uploadedAt?: dayjs.Dayjs;
  uploadedBy?: IUser;
  meeting?: IMeeting;
}

export const defaultValue: Readonly<IMeetingDocument> = {};
