export interface IMeetingType {
  id?: number;
  name?: string;
  description?: string | null;
}

export const defaultValue: Readonly<IMeetingType> = {};
