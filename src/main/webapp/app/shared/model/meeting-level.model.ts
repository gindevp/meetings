export interface IMeetingLevel {
  id?: number;
  name?: string;
  description?: string | null;
}

export const defaultValue: Readonly<IMeetingLevel> = {};
